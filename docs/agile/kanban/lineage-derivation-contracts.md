---
uuid: "4ce826b1-74aa-40da-827b-17af91722431"
title: "LA1.01: Define typed derivation contracts and total relation validation"
status: "incoming"
type: "story"
priority: "P1"
points: "3"
labels: "lineage, admission, planning, issue-66"
category: "kanban"
epic: "593ec22f-7390-4663-9800-77f64a450783"
parent: "593ec22f-7390-4663-9800-77f64a450783"
---

# LA1.01: Define typed derivation contracts and total relation validation

## Context

[Foresight issue #66](https://github.com/open-hax/foresight/issues/66) distinguishes
pinned direct observations from six stronger provisional Promethean interpretations.
This is a proposal on personal sync PR #3 at `96a6dca24cb7a14b041bdd6e3e7922c568238da9`,
which contains accepted upstream `fcfc2d17f28640203066ddfe0a22f87db7372a32` ancestry
plus personal synchronization metadata. Neither the sync nor this proposal is qualified.


## Outcome

Pure data contracts identify observations, derivations and decision records without
mistaking a supported interpretation for an admitted one.

## Scope

Propose portable observation/derivation/decision IDs, explicit source repository,
commit/path/blob/hash and descendant references, version-bound rule or exact human
decision references, and a typed relation graph. Reuse root inventory identities
without changing their tier. Effect adapters acquire/verify bytes outside the pure law.

## Acceptance criteria

- All five record kinds have unambiguous typed identity and reference contracts; equal IDs with different canonical payloads conflict.
- Every derivation resolves immutable source/descendant evidence and a named version-bound rule or durable human decision; missing/unavailable/mismatched evidence refuses.
- Supporting evidence never alone admits or promotes an interpretation.
- Total validation returns stable structured diagnostics for nil/scalars/wrong-shaped collections, malformed records, unknown identities, dangling refs, self/multi-node cycles, and stale bindings; it never throws on untrusted domain data.
- Freshness means equality to the declared immutable revision/content/rule/decision context, not wall-clock recency or the latest branch name. Historic evidence remains valid for its historic scope.
- Pure `.cljc` validation contains no native objects, I/O, random identity generation or hidden clock; unchanged source observations remain immutable.

## Verification

Future red fixtures fail against missing proposed contracts, then green implementation
passes JVM/Babashka-compatible pure and NBB tests with deterministic ordered diagnostics.
Exercise missing evidence, unknown/ref-kind mismatches, duplicate/conflicting IDs,
cycles, mutated hashes/rule versions and exact historic positive controls. Follow the
design's acceptance matrix and root warning gates; these commands have not run here.

## Non-goals

No child code transplant, common-law promotion, relabeling the historical README,
actual acceptance of the six claims, live provider/service call, deployment, new
board engine, status/event mutation, or rewrite of observed evidence. PR #63's
post-merge correctness repairs remain separate. This planning diff implements no
relation model; later red/green work requires qualified planning and lawful Rheos readiness.


## Risks

Authority, evidence integrity, and supported interpretation are distinct obligations.
A self-declared reviewer/owner field is not authorization. The reviewed contract must
name the trusted decision producer and exact scope, not invent authentication policy.
These estimates are provisional: review may require further lawful breakdown while
retaining the whole issue outcome. Unknown/stale evidence cannot become empty success.
