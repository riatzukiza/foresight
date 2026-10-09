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

The [2026-10-07 source audit](../../notes/2026-10-07-cephalon-character-memory-graph.md#2026-10-07--bounded-hydration-source-audit)
binds these facts to S `2644fc6c51bbbcda599964a5a674415b58344c3b` and C
`366e72ab68c8afae708d3f6d5546faee808ebc07`, with source paths and immutable
line anchors. Search takes k before visibility and does not refill; the memory
event counts `:results` despite a `:hits` payload. The latter is an accounting
bug distinct from the eight actual persisted empty arrays. Detached indexing,
different API gates and the sessionless SDK fixture remain separate test inputs,
not an asserted historical cause or a new execution/installation claim.

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

Retain the [existing graph/projection boundary](../../notes/2026-10-07-cephalon-graph-recall-boundary.md)
and its separately pinned OpenPlanner observations: trail persistence flags do
not disable all feedback, selected reinforcement counts do not prove writes,
and scope must cover compacted views, trail/force influence and feedback before
they affect recall. The parent epic still requires the complete encounter ->
physical field -> independent mood -> graph recall -> choice -> observed outcome
loop, including physical replay, continuity and social obligations.

## Non-goals

Make context nil or administrator to bypass filtering, grant arbitrary
cross-session access, accept payload-chosen credentials, replace vector storage,
call a feedback-writing query a pure read, or require an LLM for this fixture.

## Acceptance criteria

1. Freeze query, corpus and authenticated maker principal. The prior path fails
   to include an authorized stored encounter; the repaired path includes it.
   Retain candidate IDs, exclusion reasons and included IDs with visibility-safe
   operator diagnostics. State the proven cause separately from historical clues.
   Include the bounded k=6/fetch-18 case with six denied leading candidates and
   an authorized seventh candidate after quality ordering. Preserve the search
   budget while preventing that candidate from being lost before visibility.
2. A scheduled actor obtains only its stored authorized scope through the
   existing resolver. Missing, revoked and mismatched bindings fail closed;
   a payload cannot choose a role, organization or permission grant.
3. Scope is enforced before graph expansion and reinforcement. A private node,
   connecting edge or hidden intermediate cannot leak through path text, scores,
   metadata or persisted trails. Public and explicitly shared memories obey
   their actual admission policy, not an invented role-based exception.
   Hidden nodes/edges, compacted members, trails and forces cannot causally alter
   authorized ranking, path selection or feedback even when their text is omitted.
4. Recall includes a memory reached through an authorized graph edge beyond the
   semantic seed, with node IDs, traversal path, reasons, graph/field revision
   and explicit inclusion budget. A vector-only result cannot satisfy this case.
   Inspect the actual assembled prompt: the authorized encounter and traversal
   context must reach the scheduled character, not only a returned result map.
5. Empty authorized corpus, denied candidates, indexing delay, transport error,
   timeout and budget exhaustion are distinct results. A failed graph path does
   not silently become a successful empty recall.
6. Duplicate recall cannot multiply reinforcement; unadmitted retrieval cannot
   reinforce hidden nodes. Deliberate feedback is attributed to this recall.
7. Existing legacy request contexts and privacy negatives retain behavior. The
   conversation head uses a bounded shared snapshot outside its acknowledgment
   path; heavy recall cannot consume the reserved head lane.
8. Memory event counts use the memory hit payload and agree with its persisted
   hit count. Report actual prompt inclusion separately when its budget reduces
   context; candidate, authorized-hit and included-hit counts need not be equal.
   Distinguish raw candidates, quality survivors, authorized hits and included
   hits. Feedback distinguishes attempted, completed, partial and failed writes;
   a selected-operation count or HTTP 200 cannot attest completed persistence.

## Verification

Red tests use the actual automatic and tool/API adapters with identical corpus
and principal, held provider boundaries and real graph result shapes. Include
negative authority and hidden-path cases, a graph-neighbor inclusion case and
transport failures. Run the relevant Knoxx and OpenPlanner gates separately;
bind proofs to both revisions. A live passive turn is observed only after
qualified deployment; do not invoke the active maker to prepare fixtures.

The following are prospective RED scenarios; none was authored or executed by
the static audit or this planning update. Use the source revisions/paths in the
linked audit and retain fixture query, corpus/index snapshot, candidate IDs and
order, quality labels, principal/grants, limits and selected transport.

- **Authorized lower candidate:** feed the real search/visibility adapters the
  same frozen nested vector result with six quality-eligible denied candidates
  before an authorized seventh, k=6 and fetch-18. The predecessor loses the
  authorized candidate; the acceptance test requires bounded inclusion without
  admitting the denied leading candidates or silently broadening permissions.
- **Matched inputs and endpoint gates:** compare automatic hydration and the
  memory tool/API with the full stored query, candidate set, options and trusted
  principal held fixed. Verify API memory-read and cross-session gates explicitly;
  a correct route refusal need not equal passive hydration's result. Do not add
  a grant, nil context or admin role solely to force endpoint parity.
- **Strict denial and decoding:** missing/revoked/ambiguous/mismatched bindings,
  foreign/private sessions, session-fetch rejection and missing session metadata
  remain denied. Keep legacy policy controls explicit. Supply a separate positive
  nested-array fixture with session metadata and matching stored ownership rows;
  the current canned SDK hit/session lookup cannot serve as that positive proof.
- **Graph scope and causal isolation:** include an authorized neighbor beyond
  the seed, then vary only hidden connecting nodes/edges, compacted members and
  trail/force samples under a fixed seed/config/scope snapshot. Authorized paths,
  rankings, prompt context and feedback must not change because of denied data.
  Enforce scope before expansion/projection influence and writes, not afterward.
- **Transport and readiness:** hold vector and REST graph boundaries separately.
  Local Mongo vector success with unavailable/failed/timed-out graph transport
  must remain an explicit graph failure. Hold detached indexing completion apart
  from event acceptance; freeze candidates before/after fixture readiness and
  never infer earlier availability from a later API result.
- **Telemetry and prompt inclusion:** exercise the actual turn assembly and
  publication boundary with positive authorized memory and a graph path. Require
  the event's memory count to match the `:hits` payload, preserve the resource
  array, and inspect the prompt actually passed onward. Empty/denied/failure and
  inclusion-budget cases must keep their distinct counts and outcomes.
- **Deliberate feedback:** replay one stable recall/effect identity and inject
  partial/failed writes. Verify no duplicate or unauthorized reinforcement,
  truthful persistence outcomes and explicit consumer compatibility. A trail-off
  flag or successful retrieval response alone cannot establish no effects.

Reuse the existing C hydration outage, semantic nested-result/request, SDK
conversion, hydration-publication and actor-membership fixture seams listed in
the audit. Their current coverage is not a positive authorized graph recall
proof. Graph boundary observations remain isolated synthetic I/O evidence;
upstream integration and each owning implementation's qualification are separate.

## Risks

Existing actor contexts may omit organization on purpose; repair requires actual
stored authority rather than guessed grants. Current graph-memory reinforces
edges and persists trails; output filtering after those writes is insufficient.
Scope-aware expansion may require an independently reviewed OpenPlanner change.

License: GPL-3.0-or-later.
