---
uuid: 43b4f065-5971-4bf0-bc38-7cd470436088
title: Reconcile legacy archaeology EDN admission without rewriting history
status: incoming
priority: P1
story_points: 3
parent: 0d73c22a-b018-4b8c-92fe-560127f53294
labels: archaeology,clio,planning,integrity
---

## Outcome

Resolve [Foresight135](https://github.com/open-hax/foresight/issues/135): current CI-pinned NBB1.4.207 rejects numeric-leading relation keyword names in immutable archaeology history. Preserve historical bytes, provenance and identity while establishing a reviewed portable reconciliation and preventing new unreadable records.

## Scope

Trace the responsible writer and Clio admission boundary. Specify the relationship between retained raw historical evidence and any appended corrective facts before implementing it. Consume Clio-owned codec or admission behavior if the gap belongs upstream. Include the current reader version and exact failing inputs in the verification contract. The PR115 and116 preservation candidates retain unchanged records and fail current admission; their older1.3.204 passes are historical observations.

## Non-goals

No rewriting, renaming or deleting historical events; no weakened reader, obsolete runtime pin, silent normalization, fabricated read success, duplicate parser, or second ledger authority in Foresight. No service deployment or branch-policy changes.

## Acceptance criteria

- Planning review settles the portable representation and reconciliation contract, including preserved original identifiers and explicit causal/provenance links.
- Rheos admits this authored incoming card to ready before implementation.
- Laws/tests fail under the actual CI pin for the demonstrated defect and pass only after the reviewed fix.
- Complete historical byte prefixes and affected raw artifacts remain unchanged.
- Both the target records and the complete declared archaeology union have truthful current-reader results.
- New writers cannot emit invalid EDN identifiers; semantic authority remains with Clio and the existing domain boundary.

## Verification

PR115 observations:29resources,2schema revisions,145ledger reads,127readable/18failed underNBB1.4.207. Target relation `:relation/03f0493a-consumes-08b-route-pressure` fails at line1/column694. PR116 target `:relation/2fd1f2d9-continues-261aa432-inventory` fails at line1/column695. Logs and byte hashes are in `.ημ/verification/pr115-personal-migration/`. Issue135 is the shared blocker; incoming Markdown is planning input, not operational admission or a transition event.
