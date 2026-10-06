---
category: "kanban"
labels: "contract-language, session, conformance, 3sp"
type: "story"
story_id: "ANW1.02"
points: "3"
title: "ANW1.02 — Specify the six-form coordinator fixture and failure obligations"
priority: "P1"
status: "incoming"
epic: "a94c47bb-0a93-415c-ba3b-f9fddd2ea8fb"
parent: "a94c47bb-0a93-415c-ba3b-f9fddd2ea8fb"
dependency: "63b6320a-a4fb-43c4-9923-e5e8e970ca1d"
uuid: "1889fdc5-87f0-40d5-a02c-39cc7abf1498"
---

# ANW1.02 — Specify the six-form coordinator fixture and failure obligations

## Context

Consume the reviewed owner crosswalk from [ANW1.01](anw1-01-canonical-session-owner-crosswalk.md),
UUID `63b6320a-a4fb-43c4-9923-e5e8e970ca1d`, under this bounded epic. Original
issue77/PR78 is predecessor evidence, not an admitted executable fixture.
CS1.02 `preserve-raw-causal-event-graph` and CS1.06
`purpose-roles-bounded-authority` in [personal9](https://github.com/riatzukiza/foresight/pull/9)
at `91a02f8547cda726bea30c288e4df5aa1db899b2` are overlap references; reuse
their causal/authority concerns without duplicating the research program.

## Outcome

A provider-neutral contract profile and fixture specification make the first
coordinator proof small enough to estimate and implement in the owning repos.
The artifact remains proposed composition until explicit acceptance; the
fixture description is not interpreter, admission or replay execution evidence.

## Scope

Describe only six forms: `:session/started`, `:resource/read`, `:agent/invoke`,
`:session/await`, `:artifact/emit` and `:session/complete`. Each form names:

- input/output schemas and typed reference rules;
- capability identity and explicit policy admission boundary;
- finite resolver obligation, pending/success/failure result and error shape;
- immutable event expansion using Clio's canonical envelope;
- Receipt River references and structured session result requirements;
- deterministic projection/replay expectations, separated from effect execution.

Specify one positive abstract fixture: a coordinator starts with explicit
current/desired world data, reads exactly two bounded resources, invokes two
children with narrowed role/context/output contracts, awaits structured causal
bundles, emits one synthesized artifact and completes. Expected references tie
every child result and artifact to its causal events and admission receipts.
Define expected world/session projection data after replay and the fields a
later context renderer must consume; do not implement that renderer here.

The parent bundle names session/root-event/status, causal event refs, resources,
child refs/results, artifact refs, receipt refs and projection differences.
Explain its mapping to Sol runtime run persistence without making that mutable
run record the authoritative event history. Effects are not repeated by replay.

## Non-goals

Implementing AST/schema evaluation, a resolver/runtime, Clio replay, policy
admission, a prompt compiler, provider profiles, live children, Python/pickle
codecs, a receipt writer or Rheos state operations; changing identity/authority
or promoting proposals; estimating the remaining fourteen-workstream kernel.

## Acceptance criteria

- [ ] GIVEN any of six forms WHEN its contract is read THEN all listed
  boundary, result, evidence, failure and replay obligations are explicit.
- [ ] GIVEN the positive fixture WHEN the expected causal graph is inspected
  THEN two resource reads and two children have exact lineage into one artifact
  and parent completion; vector/file order never substitutes for causality.
- [ ] GIVEN an unknown form, invalid/missing reference, malformed child
  result or denied capability WHEN specified THEN the expected typed failure
  and evidence are explicit, with no successful completion inferred.
- [ ] GIVEN a child requesting broader authority WHEN specified THEN a
  contextual role cannot grant it and the denial remains visible to the parent.
- [ ] GIVEN exact canonical events WHEN replay expectations are compared
  THEN the same projection is specified without replaying external effects;
  missing causes or conflicting event identity cannot count as canonical input.
- [ ] Every proposed field and owner mapping traces to ANW1.01 evidence or an
  explicitly proposed gap; conflicts with old78 remain visible.
- [ ] The output lists separately estimated next implementation cards per
  owner and maps untouched issue77 requirements to deferred scope, without
  claiming the original issue complete.

## Verification

Independent planning/specification review checks the six-form obligation
matrix, expected fixture graph, negative cases and owner evidence. The later
implementation PRs must write meaningful failing laws/tests before green code
and execute the shared fixtures at canonical boundaries. This story specifies
those obligations and makes no executable conformance claim. Rheos admits and
moves reviewed cards; Markdown authoring alone does not prove readiness.

## Risks

Overloading one story with interpreter implementation would invalidate the
three-point estimate. Keep it documentary and finite; split any independent
new outcome. Cross-host identity, pending semantics and context narrowing can
hide authority changes: enumerate unresolved decisions instead of choosing
new semantics silently. Existing optional review unavailability is not approval.
