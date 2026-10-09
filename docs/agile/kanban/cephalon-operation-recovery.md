---
uuid: "25e3a688-09b5-4e9a-8765-9b13944b1a04"
title: "Supervise creative work and recover one cephalon owner across restarts"
status: incoming
priority: P1
points: 5
labels: "knoxx, cephalon, services, recovery"
epic: "25e3a688-09b5-4e9a-8765-9b13944b1a00"
parent: "25e3a688-09b5-4e9a-8765-9b13944b1a00"
dependency: "25e3a688-09b5-4e9a-8765-9b13944b1a03"
---

## Context

A recovered owner must preserve accepted jobs and external-effect identity
across restart or cutover. Dependency readiness and cloud availability are a
separate slice: `25e3a688-09b5-4e9a-8765-9b13944b1a05`.

## Outcome

One admitted owner recovers durable work without repeating artifact persistence
or publication. Optional makers may disappear without unsafe re-admission.

## Scope

Owner cutover, persisted ownership inspection, stale-owner fencing and bounded
job recovery. Reuse the cycle story's attempt/backoff implementation and the
publication story's reconciliation contracts; this slice verifies them across
restart and ownership loss. Lifecycle reporting and cloud dependency readiness
belong to `25e3a688-09b5-4e9a-8765-9b13944b1a05`.

## Proposed estimate and prerequisites

Independent local-agent sprint assessment proposes **8 points** for this slice,
with **5 points** as a conditional alternative only if existing durable fencing
and outlet reconciliation are demonstrably reusable. Fencing both artifact and
publication writes, including delegates, is the principal uncertainty. The
existing frontmatter's 5 points is retained as unadmitted initial metadata, not
an established estimate; the unsupported native Rheos points setter must be
resolved upstream before operational point updates. No local setter is used.

Retain the declared publication predecessor. The proposed placement-readiness
predecessor is `25e3a688-09b5-4e9a-8765-9b13944b1a05`; it may verify dependencies
without enabling a successor gateway. Rheos must retain/admit this additional
edge before readiness. It is not silently added to established frontmatter.

## Non-goals

Deploy a new mesh scheduler, migrate unrelated database users, change every PM2
process, or make cloud worker availability depend on an operator laptop service.

## Acceptance criteria

1. Document and verify one active gateway/clock owner; relocation disables the
   old owner persistently before enabling its successor.
2. Recreate the backend with a pending job. Within 60 seconds, the recovered
   owner must inspect persisted job identity and previous ownership. Re-admit
   pending work only after the previous owner and its delegated work are
   confirmed terminated, or verified fencing prevents them from persisting
   artifacts and publishing. Ten-minute attempt or 36-minute cycle budget
   expiry alone is insufficient:
   after expiry, test an old owner that still attempts both effects and prove
   rejection before re-admission. Ambiguous termination or fencing keeps the
   work quarantined with an explicit outcome. Do not cancel a live owner within
   its valid execution deadline. Preserve artifact identity. Reconcile any
   unknown publication outcome against the
   native outlet before retrying; if unresolved, mark the attempt ambiguous
   and require explicit retry rather than republishing. Observe three cycles.
3. Each attempt has a ten-minute deadline, with at most three total attempts per
   cycle identity. Failed attempts wait one minute, then five minutes before
   the remaining attempts; no immediate retry loop. The whole cycle has at most
   36 minutes of execution/backoff budget from its first admission, including
   later admission delays. Each attempt is capped by the remaining budget;
   no retry is admitted after budget expiry. Re-admission still requires AC2's
   verified termination or fencing of the old attempt and its delegates.
   Expiry does not release unresolved ownership: it stays quarantined, even
   beyond 36 minutes, until terminal evidence or verified fencing permits it.
   Exhaustion yields a persisted failed outcome and a meaningful alert, then waits for the
   next scheduled cycle or an authorized explicit operator retry. That retry
   creates a fresh cycle identity linked to the exhausted predecessor, with its
   own three-attempt cap; the predecessor's failed outcome and attempt history
   are immutable. A fresh identity does not bypass maker reservation, publication
   deduplication or ambiguous-outlet quarantine. Test exhaustion followed by
   operator retry and verify both identities, limits and retained history.
   Inject unavailable provider and disappearing maker cases and inspect those
   outcomes. The head remains reachable throughout; healthy-provider response
   timing is measured separately from maker availability.

## Verification

Failing ownership, fencing, replay and retry-history laws first; source/build
gates in each owning repository and an artifact/publication recovery drill.
Verify stale parent/delegate writes are blocked at both boundaries before
re-admission, and quarantine remains on ambiguous termination. Ship a human
restart/cutover runbook with exact images/contracts and reversible ownership.
Cloud provider/vector-query readiness is verified in the placement slice.

## Risks

Worker leases, stale delegates, duplicate consumers, ambiguous external effects,
immutable retry history and durable ownership consistency.
