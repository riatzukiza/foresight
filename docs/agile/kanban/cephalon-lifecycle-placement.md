---
uuid: "25e3a688-09b5-4e9a-8765-9b13944b1a05"
title: "Report truthful lifecycle health and qualify independent cloud placement"
status: incoming
priority: P1
points: 5
labels: "knoxx, cephalon, services, lifecycle, placement"
epic: "25e3a688-09b5-4e9a-8765-9b13944b1a00"
parent: "25e3a688-09b5-4e9a-8765-9b13944b1a00"
dependency: "25e3a688-09b5-4e9a-8765-9b13944b1a02"
---

## Context

The local consumer cannot create during host sleep. Current API lifecycle data
includes hardcoded `running: true`, and the older cloud consumer depends on
operator-laptop services. Network reachability does not establish availability.

## Outcome

Native lifecycle reports reflect actual state, and one configured cloud target
has independently usable provider, memory/search and artifact dependencies.
Readiness can be verified with the candidate gateway and clock disabled.

## Scope

Native lifecycle/dependency health plus Services-owned declarations and a
placement runbook for one cloud target. Reuse existing model, embedding, voice,
vector-search and artifact surfaces. This separates former recovery AC4–5 from
owner/fencing/retry AC1–3, which remain in the recovery story.

## Proposed estimate and relationships

The independent local-agent sprint assessment proposes **5 points**: lifecycle
reporting and a real dependency qualification span Knoxx and Services, with
hidden laptop dependencies and false health as the main risks. This remains
provisional incoming authoring metadata, without operational admission.

The creative-cycle predecessor supplies observable successful/failed outcomes.
This story supplies a proposed readiness predecessor for recovery cutover;
it does not require recovery to activate an owner first. Rheos relationship
retention/admission is an upstream prerequisite, not supplied by this document.

## Non-goals

Implement a fleet scheduler, activate another gateway, migrate unrelated data,
change unrelated PM2 services or claim that all mesh devices remain available.

## Acceptance criteria

1. Replace constant lifecycle claims with actual enabled, disabled and stopped
   states. Verify each independently, including native gateway/clock ownership,
   recent successful creative cycles and usable dependencies; an HTTP listener
   alone cannot qualify health. Alerts stay quiet while unchanged/non-actionable.
2. One configured cloud target has independently available provider,
   memory/vector search and artifact dependencies, without operator-laptop
   availability. Verify actual provider responses, an actual vector query and
   persisted owned artifact data; reachability alone is insufficient.
3. Readiness inspection does not activate a candidate gateway/clock. Keep those
   disabled until the recovery story's qualified cutover; preserve one owner.
4. Record measured host/dependency availability, response timing and remaining
   mesh limitations. A missing provider, failed query or unusable artifact store
   reports a real unhealthy state with evidence, never a synthetic success.
5. Publish a reproducible human readiness/runbook artifact with exact
   image/contract identities, read-only defaults, explicit owned write probes,
   cleanup and reversible candidate placement.

## Verification

Laws/tests for lifecycle and dependency failure before implementation, full
source/build gates in each owner, and real identity-bound provider/vector/store
probes. Include an unavailable laptop/dependency case and prove unhealthy
reporting while candidate gateway/clock remain disabled. Recovery separately
verifies ownership transfer and artifact/publication replay safety.

## Risks

Cloud capacity, vector-index readiness, hidden laptop routing, credentials,
false lifecycle state and overstated host/mesh availability.
