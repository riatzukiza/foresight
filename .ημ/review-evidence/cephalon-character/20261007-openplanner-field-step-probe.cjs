const fs=require('node:fs');
const path=require('node:path');
const crypto=require('node:crypto');
const cp=require('node:child_process');
const repo=process.argv[2], scratch=process.argv[3];
const ts=require(path.join(repo,'node_modules/typescript'));
const revision=cp.execFileSync('git',['rev-parse','HEAD'],{cwd:repo,encoding:'utf8'}).trim();
const sourceFiles=['sim.ts','quadtree.ts','types.ts'];
const files=[];
for (const name of sourceFiles) {
 const relative='packages/graph/eros-eris-field/src/'+name;
 const source=fs.readFileSync(path.join(repo,relative));
 const committed=cp.execFileSync('git',['show',revision+':'+relative],{cwd:repo});
 if (!source.equals(committed)) throw new Error('selected source differs from committed revision: '+relative);
 files.push({path:relative,bytes:source.length,sha256:crypto.createHash('sha256').update(source).digest('hex'),matchesCommittedHead:true});
 const js=ts.transpileModule(source.toString('utf8'),{compilerOptions:{target:ts.ScriptTarget.ES2022,module:ts.ModuleKind.CommonJS}}).outputText;
 fs.writeFileSync(path.join(scratch,name.replace(/\.ts$/,'.js')),js,{flag:'wx'});
}
const {stepField}=require(path.join(scratch,'sim.js'));
const config={theta:0.6,repulsionStrength:0,localRepulsionRadius:0,localRepulsionStrength:0,localRepulsionPower:2,softening:0.01,damping:0.8,maxSpeed:100,minSeparation:0,separationStrength:0,semanticAttractAbove:0.7,semanticRepelBelow:0.2,semanticAttractStrength:0,semanticRepelStrength:0,semanticRepelRadius:0,semanticRestLength:1,semanticBreakDistance:10,targetRadius:0,boundaryThickness:0,boundaryPressure:0,boundaryEdgeFraction:0.1};
const initial=[{id:'a',x:-1,y:0,vx:1,vy:0,mass:1},{id:'b',x:1,y:0,vx:-1,vy:0,mass:1}];
const encode=v=>typeof v==='number'&&!Number.isFinite(v)?String(v):Array.isArray(v)?v.map(encode):v&&typeof v==='object'?Object.fromEntries(Object.entries(v).map(([k,x])=>[k,encode(x)])):v;
function observe(label,dts) {
 const particles=structuredClone(initial);
 let error=null;
 try {for (const dt of dts) stepField({particles,dt,config});} catch(e) {error=String(e);}
 return {label,dts:encode(dts),error,state:encode(particles),finite:particles.every(p=>[p.x,p.y,p.vx,p.vy,p.mass].every(Number.isFinite)),changed:JSON.stringify(encode(particles))!==JSON.stringify(initial)};
}
console.log(JSON.stringify({evidenceKind:'actual selected committed source fixture execution; not full package test, character solver qualification or live runtime proof',observedAt:new Date().toISOString(),repositoryPath:repo,revision,nodeVersion:process.version,typescriptVersion:ts.version,files,config,initial,observations:[observe('zero_elapsed_time',[0]),observe('negative_elapsed_time',[-0.1]),observe('nonfinite_nan_elapsed_time',[NaN]),observe('nonfinite_infinite_elapsed_time',[Infinity]),observe('one_step_0.1',[0.1]),observe('two_substeps_0.05',[0.05,0.05])],limits:['No graph app, HTTP service, database, maker, social effect, package build or donor source mutation.','TypeScript transpileModule emitted temporary CommonJS without typecheck; actual pure stepField and its quadtree dependency were loaded from selected committed bytes.','Configuration is an isolated fixture, not an accepted kernel contract; no RNG, authorization, collision, mood or full recall coupling tested.']},null,2));
