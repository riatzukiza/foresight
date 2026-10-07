---
uuid: "16edcfc1-b65c-441e-b527-ef50dc5c290a"
title: "LA1.02: Admit lineage interpretations through owner-authorized durable events"
status: "incoming"
type: "story"
priority: "P1"
points: "3"
labels: "lineage, admission, planning, issue-66"
category: "kanban"
epic: "593ec22f-7390-4663-9800-77f64a450783"
parent: "593ec22f-7390-4663-9800-77f64a450783"
dependency: ["4ce826b1-74aa-40da-827b-17af91722431"]
---

# LA1.02: Admit lineage interpretations through owner-authorized durable events

## Context

[Foresight issue #66](https://github.com/open-hax/foresight/issues/66) distinguishes
pinned direct observations from six stronger provisional Promethean interpretations.
This is a proposal on personal sync PR #3 at `96a6dca24cb7a14b041bdd6e3e7922c568238da9`,
which contains accepted upstream `fcfc2d17f28640203066ddfe0a22f87db7372a32` ancestry
plus personal synchronization metadata. Neither the sync nor this proposal is qualified.


## Outcome

Interpretation admission, rejection, supersession and promotion preserve history and
require trusted owner-scoped authorization over the exact proposed record/evidence.

## Scope

Pure admission decisions consume explicit trusted authorization evidence; outer
adapters authenticate the existing producer boundary, verify immutable inputs and
append/read accepted events through the owning Clio contract. Use one declared
canonical stream/provenance path; do not add a competing event-store authority.

## Acceptance criteria

- Admission and promotion are distinct typed acts; support or a matching owner name cannot supply authority.
- An owner-authorized durable event bound to the claim, exact derivation/evidence, authority scope and contract version is required before accepted projection; failed append cannot advance state.
- Missing/wrong-owner/revoked-or-out-of-scope authorization, missing durable decision or attempted promotion without admitted evidence fails closed with structured diagnostics.
- Rejections and supersessions append immutable facts; they retain earlier observations/decisions and identify the replaced decision rather than editing it.
- Exact equal-payload retries are idempotent; reused decision/request identity with a changed payload conflicts. Conflicting concurrent admissions never silently overwrite or pick the last writer.
- Replay/reopen derives the same accepted/provisional/rejected view from validated retained history; supplied event order/causal identities and authorization provenance remain inspectable.

## Verification

Future red/green tests use fictional authorized/unauthorized producer contexts and
an actual private temporary Clio-backed append/read/reopen boundary, with failed
append, exact retry, changed retry, competing admission and stale decision controls.
Mock authorization alone proves decision logic, not authenticated producer identity
or durable append. Missing native ledger dependencies are visible blockers.

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
