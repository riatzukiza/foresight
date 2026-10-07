---
uuid: "a1e9d6af-0233-4dcb-9677-5c76fa9a2701"
title: "Hydrate the creative character with authorized traced graph memory"
status: incoming
priority: P1
points: 5
labels: "cephalon, memory, graph, identity, laws"
epic: "63a0e4ff-353f-4c90-ab8a-7241d958d54c"
parent: "63a0e4ff-353f-4c90-ab8a-7241d958d54c"
---

## Context

Served Knoxx hydrates memory using vector search and then session authorization.
The later API search was not a controlled reproduction of the maker. The current
scheduled maker records actor/role but no organization, membership or user; the
turn's synthesis adds no cross-session memory grant. Visibility rejects a
non-admin context lacking the required matching scope or grant. Graph retrieval
is a separate REST seam even when vectors use the direct Mongo client.

## Outcome

A scheduled character obtains its authorized memories through graph-mediated
recall and includes useful traced context. Missing authority and unavailable
graph transport remain observable outcomes. An unrelated actor's private
memory stays inaccessible at seeds, traversal, returned context and feedback.

## Scope

Knoxx trusted event actor-context resolution, memory visibility decisions,
candidate/filter/inclusion diagnostics, bounded passive graph recall and existing
OpenPlanner graph seams. Resolve actual actor authority through the existing
identity/policy boundary; never infer grants from a display role. Review any
needed graph scope/feedback extension in OpenPlanner's personal fork.

## Non-goals

Make context nil or administrator to bypass filtering, grant arbitrary
cross-session access, accept payload-chosen credentials, replace vector storage,
call a feedback-writing query a pure read, or require an LLM for this fixture.

## Acceptance criteria

1. Freeze query, corpus and authenticated maker principal. The prior path fails
   to include an authorized stored encounter; the repaired path includes it.
   Retain candidate IDs, exclusion reasons and included IDs with visibility-safe
   operator diagnostics. State the proven cause separately from historical clues.
2. A scheduled actor obtains only its stored authorized scope through the
   existing resolver. Missing, revoked and mismatched bindings fail closed;
   a payload cannot choose a role, organization or permission grant.
3. Scope is enforced before graph expansion and reinforcement. A private node,
   connecting edge or hidden intermediate cannot leak through path text, scores,
   metadata or persisted trails. Public and explicitly shared memories obey
   their actual admission policy, not an invented role-based exception.
4. Recall includes a memory reached through an authorized graph edge beyond the
   semantic seed, with node IDs, traversal path, reasons, graph/field revision
   and explicit inclusion budget. A vector-only result cannot satisfy this case.
5. Empty authorized corpus, denied candidates, indexing delay, transport error,
   timeout and budget exhaustion are distinct results. A failed graph path does
   not silently become a successful empty recall.
6. Duplicate recall cannot multiply reinforcement; unadmitted retrieval cannot
   reinforce hidden nodes. Deliberate feedback is attributed to this recall.
7. Existing legacy request contexts and privacy negatives retain behavior. The
   conversation head uses a bounded shared snapshot outside its acknowledgment
   path; heavy recall cannot consume the reserved head lane.

## Verification

Red tests use the actual automatic and tool/API adapters with identical corpus
and principal, held provider boundaries and real graph result shapes. Include
negative authority and hidden-path cases, a graph-neighbor inclusion case and
transport failures. Run the relevant Knoxx and OpenPlanner gates separately;
bind proofs to both revisions. A live passive turn is observed only after
qualified deployment; do not invoke the active maker to prepare fixtures.

## Risks

Existing actor contexts may omit organization on purpose; repair requires actual
stored authority rather than guessed grants. Current graph-memory reinforces
edges and persists trails; output filtering after those writes is insufficient.
Scope-aware expansion may require an independently reviewed OpenPlanner change.

License: GPL-3.0-or-later.
