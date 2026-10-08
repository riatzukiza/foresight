// Executes selected unchanged committed donor source, with no live effects.
// This is an investigation fixture, not a new graph implementation or RED gate.
const fs = require('node:fs');
const path = require('node:path');
const cp = require('node:child_process');
const crypto = require('node:crypto');
const vm = require('node:vm');

const repo = process.argv[2];
const revision = process.argv[3];
if (!repo || !/^[a-f0-9]{40}$/.test(revision || '')) {
  throw new Error('usage: node PROBE.cjs DONOR_REPO FULL_COMMITTED_SHA');
}
const ts = require(path.join(repo, 'node_modules/typescript'));
const sourceRecords = [];
function committedSource(relative) {
  const bytes = cp.execFileSync('git', ['show', revision + ':' + relative], {cwd: repo});
  sourceRecords.push({path: relative, bytes: bytes.length,
    sha256: crypto.createHash('sha256').update(bytes).digest('hex')});
  return ts.createSourceFile(relative, bytes.toString('utf8'), ts.ScriptTarget.Latest, true);
}
const graph = committedSource('src/routes/v1/graph.ts');
const embedding = committedSource('packages/openplanner-sdk/src/embedding-text.ts');
const helperNames = ['escapeRegex', 'matchesNodeType', 'clampConfidence',
  'semanticChargeFromSimilarity', 'semanticCircuitConductance', 'hashHex',
  'stableUnitInterval', 'fadeNoise', 'lerp', 'simplexTrailNoise',
  'decayedTrailInfluence', 'undirectedEdgeKey', 'sortGraphMemorySeedScores',
  'filterGraphMemorySeedScores', 'resolveGraphMemorySeedNodes'];
function selectFunction(source, name) {
  const matches = source.statements.filter(n => ts.isFunctionDeclaration(n) && n.name?.text === name);
  if (matches.length !== 1) throw new Error('ambiguous/missing committed helper: ' + name);
  return matches[0].getText(source).replace(/^export\s+/, '');
}
const routeMatches = [];
function visit(node) {
  if (ts.isCallExpression(node) && ts.isPropertyAccessExpression(node.expression)
      && node.expression.expression.getText(graph) === 'app'
      && node.expression.name.text === 'post'
      && node.arguments[0] && ts.isStringLiteral(node.arguments[0])
      && node.arguments[0].text === '/graph/memory') routeMatches.push(node);
  ts.forEachChild(node, visit);
}
visit(graph);
if (routeMatches.length !== 1) throw new Error('ambiguous/missing committed memory route');
const callback = routeMatches[0].arguments[1].getText(graph);
const selected = helperNames.map(n => selectFunction(graph, n)).join('\n') + '\n'
  + ['expandEscapedNewlines', 'formatEmbeddingQueryText'].map(n => selectFunction(embedding, n)).join('\n')
  + '\nconst EDGE_CLAIM_ACTIVE_PROJECTABLE_STATUSES = ["supported", "active"];\n'
  + '\nconst handler = ' + callback + ';\n';
const compiled = ts.transpileModule(selected, {compilerOptions: {
  target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.None}, reportDiagnostics: true});
const errors = (compiled.diagnostics || []).filter(d => d.category === ts.DiagnosticCategory.Error);
if (errors.length) throw new Error('selected source transpilation failed');

const seed = 'knoxx-session:run:fixture-allowed:user';
const neighbor = 'knoxx-session:run:fixture-outside-scope:user';
function cursor(rows) {
  return {limit() {return this;}, async toArray() {return structuredClone(rows);}};
}
async function scenario(label, options) {
  const writes = [];
  const warnings = [];
  const reads = [];
  const results = [];
  const now = Date.parse('2026-10-07T15:40:00.000Z');
  class FixtureDate extends Date {
    constructor(...args) {super(...(args.length ? args : [now]));}
    static now() {return now;}
  }
  const readCollection = (name, rows) => ({find(filter) {
    reads.push({collection: name, filter}); return cursor(rows);
  }});
  const app = {
    embeddingRuntime: {hot: {getEmbeddingFunctionForModel: () => ({generate: async () => [[1, 0]]})}},
    mongo: {
      graphNodeEmbeddings: {aggregate(pipeline) {
        reads.push({collection: 'graphNodeEmbeddings', pipeline});
        return cursor([{node_id: seed, project: 'knoxx-session', score: 0.9}]);
      }},
      graphViewNodes: readCollection('graphViewNodes', []),
      graphSemanticForceSamples: readCollection('graphSemanticForceSamples', []),
      graphEdgeClaims: readCollection('graphEdgeClaims', []),
      graphSemanticEdges: {
        ...readCollection('graphSemanticEdges', [{source_node_id: seed, target_node_id: neighbor, similarity: 0.9}]),
        async bulkWrite(operations) {
          writes.push({collection: 'graphSemanticEdges', operations, outcome: options.failWrites ? 'failed' : 'completed'});
          if (options.failWrites) throw new Error('fixture feedback write unavailable');
          return {modifiedCount: operations.length};
        }
      },
      graphDaimoiTrails: {...readCollection('graphDaimoiTrails', []), async bulkWrite(operations) {
        writes.push({collection: 'graphDaimoiTrails', operations, outcome: 'completed'});
        return {upsertedCount: operations.length};
      }},
      events: readCollection('events', [])
    }
  };
  const context = vm.createContext({app, createHash: crypto.createHash, Date: FixtureDate,
    process: {env: {EMBED_PROVIDER_MODEL: 'fixture-embedding'}},
    fallbackGraphMemorySeedSearch: () => {throw new Error('unexpected fallback; fixture exercises native seeds');}});
  new vm.Script(compiled.outputText, {filename: 'selected-committed-memory-handler.js'}).runInContext(context);
  const handler = vm.runInContext('handler', context);
  for (let index = 0; index < (options.repeat || 1); index++) {
    // This attached fixture scope is not a real tenant/principal authentication proof.
    const request = {
      body: {q: 'synthetic fixture encounter', lakes: ['knoxx-session'], k: 1,
        maxCost: 2, maxNodes: 10, includeText: false, useCompactView: false,
        persistDaimoiTrails: options.persistTrails, simplexNoiseGain: 0,
        recallId: 'synthetic-same-recall-id'},
      tenantContext: {tenant: {tenant_id: 'fixture-allowed-tenant'}, roles: [], scopes: []},
      log: {info() {}, warn(bindings, message) {warnings.push(message);}}
    };
    const reply = {statusCode: 200, status(code) {this.statusCode = code; return this;}, send(value) {return value;}};
    const value = await handler(request, reply);
    results.push({statusCode: reply.statusCode, nodeIds: value.nodes?.map(n => n.id),
      edgeEndpoints: value.edges?.map(e => [e.source, e.target]),
      trails: value.daimoi?.map(d => d.trail), stats: value.stats,
      explicitFeedbackError: Object.hasOwn(value, 'error') || Object.hasOwn(value, 'feedbackError')});
  }
  return {label, fixture: {seed, neighbor, attachedScope: 'fixture-allowed-tenant',
      expectedAccessibleNodeIds: [seed], persistDaimoiTrails: options.persistTrails,
      repeat: options.repeat || 1, failWrites: Boolean(options.failWrites)},
    results, reads, writes, warnings};
}

(async () => {
  const observations = await Promise.all([
    scenario('trail-persistence-disabled', {persistTrails: false}),
    scenario('same-recall-repeated', {persistTrails: false, repeat: 2}),
    scenario('feedback-write-failure', {persistTrails: false, failWrites: true}),
    scenario('trail-persistence-enabled', {persistTrails: true})
  ]);
  console.log(JSON.stringify({
    evidenceKind: 'executed selected unchanged committed handler with isolated in-memory effect boundaries; not live HTTP, formal RED gate or provider review',
    observedAt: new Date().toISOString(), repositoryPath: repo, revision,
    nodeVersion: process.version, typescriptVersion: ts.version, sourceRecords,
    helperNames, callbackSha256: crypto.createHash('sha256').update(callback).digest('hex'),
    selectedSourceSha256: crypto.createHash('sha256').update(selected).digest('hex'), observations,
    limits: [
      'No network, HTTP server, live database, graph feedback, maker, credential, social action or donor working-file mutation.',
      'The actual committed callback, native seed filter/resolver and listed pure helpers run; embedding and Mongo boundaries are synthetic.',
      'The graph callback is isolated from authentication/tenant plugins. Attached fixture scope proves only whether this callback consumes it, not deployment exposure or a complete authorization exploit.',
      'Synthetic outside-scope classification is an external fixture expectation, not a newly implemented authorization law.',
      'TranspileModule removes TypeScript annotations; this is not typechecking or a complete package test.',
      'Date and graph data are held fixed; no physical solver, mood, character continuity, successful publication or end-to-end loop is admitted.'
    ]
  }, null, 2));
})().catch(error => {console.error(error.stack); process.exitCode = 1;});
