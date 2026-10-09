# Character graph recall: observed feedback and scope boundary

License: GPL-3.0-or-later.

## Status

This is an investigation of unchanged committed OpenPlanner source, with
synthetic in-memory I/O. It is neither a live authorization test nor a formal
implementation RED gate. The character-loop design and its incoming recall
story retain their canonical planning and Rheos readiness requirements.

## Source and method

The freshly fetched `open-hax/openplanner` main is
`07085d6557b75834ce6f50e6c54b8ca47e1c7c08`. Its memory handler retains the
relevant behavior found in donor checkout `582efb7`. The probe reads committed
blobs, selects the actual `/graph/memory` callback and named helper declarations
using the TypeScript AST, transpiles those unchanged selections, and executes
them with a frozen clock, synthetic native vector seed, synthetic graph rows,
and recording Mongo boundaries. It starts no Fastify server or database and
does not call the live endpoint.

- [Probe](../../.ημ/review-evidence/cephalon-character/20261007-openplanner-memory-boundary-probe.cjs)
- [Executed observations](../../.ημ/review-evidence/cephalon-character/20261007-openplanner-memory-boundary-observations.json)

The output retains source blob hashes, callback/selection hashes, runtime and
compiler versions, all four scenarios, read/write plans, results and limits.
It is actual probe stdout, not a raw REST response or provider approval.

## Observed behavior

| Scenario | Executed selected-handler outcome |
| --- | --- |
| `persistDaimoiTrails: false` | No trail write, but one semantic-edge reinforcement write containing `$inc: {reinforcement_count: 1}`. This flag does not make graph recall read-only. |
| Same synthetic recall ID, query, clock and graph repeated | Both invocations schedule a semantic-edge increment. The callback does not consume the synthetic recall ID or deduplicate those effects. |
| Semantic feedback writer throws | The callback logs the failed write, leaves the reply status at 200, and returns normal recall with `semanticReinforcements: 1` and no explicit feedback error. That count records selected reinforcement operations, not verified persistence. |
| Trail persistence enabled | One semantic-edge write and two attributed-path trail upserts are scheduled. Both collections are isolated fixture boundaries. |

In all four fixtures the graph crosses from the seed to a same-lake neighbor
outside the fixture's expected accessible set. The attached synthetic tenant
context is not consulted by the selected callback. Lake filtering checks ID
prefixes; it cannot by itself establish a character's memory permissions.
The existing API-key and tenant plugins were inspected separately, but were
not executed by this fixture. These observations do **not** prove a complete
deployed authorization exploit, HTTP exposure, or the visibility of actual
private content. They demonstrate the missing scope-consumption seam in the
handler the character would need to use.

## Consequence for the first repair

The owning repair spans Knoxx's trusted stored-principal resolution and
OpenPlanner's graph-memory boundary. Adding `graph_query` to the maker's
allowlist, or filtering its returned text afterward, cannot satisfy the
incoming [scoped recall story](../agile/kanban/cephalon-character-recall.md).
Expansion, compacted views, trail/force influence and every feedback effect
must obey the same authorized visibility snapshot before they contribute to
recall. A denied connecting node must not influence returned paths or feedback.

The reviewed graph contract must distinguish retrieval from deliberately
admitted feedback, bind feedback to a stable recall/effect identity, and report
attempted, completed, partial and failed persistence truthfully. Compatibility
for existing consumers needs an explicit decision; this note does not silently
change the existing HTTP contract or invent permission grants.

Fresh repository inspection found no open personal OpenPlanner PR and no
matching scope/feedback repair in the targeted upstream PR search. The personal
fork's main is `f95a53a4da8ed90588b0be320ba8e86c6a03668d`, an ancestor 423
commits behind the inspected upstream main; their cumulative difference is
2,041 files. Its much older graph route is not the audited handler. A future
personal implementation therefore needs an independently qualified source
integration boundary as well as the scoped repair. These counts are an
observed integration constraint, not approval transfer, a reason to omit review
inputs, or permission to reset personal main. No new branch, PR, review request,
card transition, source implementation or deployment was created here.

## Limits

This narrows the first upstream work. It does not repair automatic hydration,
select a physical kernel, implement mood or personality, prove a graph's physical
effect on choices, establish social engagement, or complete the character loop.
All retained donor and production files remain unchanged.
