---
uuid: "6eb77110-8cf4-44d5-a242-f495d25d6aaf"
title: "Admit changing outside encounters into the character's memory graph"
status: incoming
priority: P1
points: 3
labels: "cephalon, encounters, social, memory, provenance"
epic: "63a0e4ff-353f-4c90-ab8a-7241d958d54c"
parent: "63a0e4ff-353f-4c90-ab8a-7241d958d54c"
---

## Context

The maker is dominated by repeated production tasks and its own outputs. Knoxx
already has feed, notification and thread adapters; the live maker's tools do
not currently expose that discovery loop. Outside material must become durable
observations with real source identity, rather than ambient prompt authority.

## Outcome

A bounded intake facet continuously supplies actual changing encounters between
creative clock opportunities. The graph retains people, topics, replies and
sources with original identities, observation times and visibility.

## Scope

Authenticated home feed, replies/mentions, selected follows and a configured
small external-source set through existing adapters. Persist cursors, external
IDs and content hashes through existing event/memory stores; retain author and
relationship links. Intake is cheaper than a maker turn and does not require
publication or a model call for every item.

## Non-goals

Scrape arbitrary private content, infer read sources from output destinations,
invent encounters, echo every self-post as fresh outside novelty, introduce a
second gateway owner, or replace the native creative clock.

## Acceptance criteria

1. The same external item across pagination, retry, reconnect and two facets is
   admitted once by its canonical external identity. Changed content is a new
   attributed revision, not a silent rewrite of historical evidence.
2. A successful cursor is durable; interrupted batches resume without missing
   or duplicating admitted observations. Empty, malformed, failed and partial
   feeds remain distinguishable and cannot advance a failed page's cursor.
3. Self-output, outside authors, replies and explicit operator input retain
   separate provenance. Source/account/visibility/actual UTC observation time
   and author/topic relationships reach the existing graph projection.
4. A configured exploration budget admits a changing source beyond current
   interests, with its selection seed/reason recorded. Repeated own output alone
   cannot satisfy the outside-input test.
5. External text cannot alter permissions, model routing or effect admission.
   Intake queues, per-source requests, bytes and retention have explicit bounds.
6. The fifteen-minute maker reads changed admitted state without intake creating
   a second creative timer, clock owner or unbounded LLM invocation stream.

## Verification

Red adapter fixtures cover duplicate IDs, edits, pagination, disconnect/retry,
self-posts, invalid content and private-source denial. Observe capture and graph
projection separately. A later live read records actual cursors and source IDs
without replaying posts or exposing tokens/content from private sources.

## Risks

API pagination drift, source outages, stale deleted content, bot feedback,
sensitive graph context and starvation of quieter sources.

License: GPL-3.0-or-later.
