---
uuid: "25e3a688-09b5-4e9a-8765-9b13944b1a02"
title: "Each unattended creative cycle saves an original inspectable artifact"
status: incoming
priority: P1
points: 5
labels: "knoxx, cephalon, artifacts, contracts"
epic: "25e3a688-09b5-4e9a-8765-9b13944b1a00"
parent: "25e3a688-09b5-4e9a-8765-9b13944b1a00"
dependency: "25e3a688-09b5-4e9a-8765-9b13944b1a01"
---

## Context

The current social scan may do nothing. Native music, workspace, image and voice
tools exist; autonomous creation needs verifiable outputs and bounded lifetimes.

## Outcome

An enabled maker cycle produces an original artifact or records an explicit
failure, with job identity and a discoverable persisted result.

## Scope

Executable agent/trigger resources, an explicit cadence and deadline, one
outstanding cycle per maker, existing worker delegation, output validation and
addressable result/receipt data at the existing persistence boundaries.

This story implements a ten-minute deadline **per attempt**, at most three
attempts per cycle identity, and one-minute then five-minute backoffs. The whole
cycle's execution/backoff budget is at most 36 minutes from first admission,
including retries, backoffs and later admission delays; each attempt is also
bounded by the remaining cycle budget. Later clock ticks coalesce or are refused
while its maker reservation is held. Budget expiry does not establish terminal
ownership: unresolved work remains quarantined until verified termination or
fencing prevents both artifact persistence and publication. The recovery story
supplies these same limits, together with the publication admission/reconciliation
contracts specified in the publication story, before its cycles can pass AC3.
Those later stories verify outlet adapters and restart/placement behavior; their
ordering does not postpone the limits needed by this cycle runtime.

## Non-goals

Invent a synthesis engine, require paid image generation, enforce a fixed artistic
style, or claim every generated piece must be published.

## Acceptance criteria

1. Three consecutive unattended cycles produce validated saved artifacts or
   explicit failed outcomes. At least one cycle succeeds with validated saved
   artifacts; successful outputs cover at least two media. The remaining cycles
   may fail explicitly, with their real reasons.
2. Music has a playable non-empty audio file and its composition specification;
   visual work has a valid image/SVG; lyrics/MIDI have parseable inspectable data.
3. The maker reservation lasts until its run and delegated work have terminal
   outcomes, not merely until dispatch returns an acceptance receipt. Hold a
   delegated job nonterminal after dispatch returns; later ticks must be
   coalesced or refused with an observable result until that job terminates.
   A provider failure yields the retry/backoff limits in the recovery story.
   After exhaustion, only an authorized operator retry or the next scheduled
   cycle creates a fresh cycle identity; operator retries link the exhausted
   predecessor and preserve its immutable attempts/outcome. The recovery story
   defines the new identity's cap and retains publication/reconciliation gates.
4. Delegated work has parent/job identities and a terminal outcome. The head
   does not await a maker before responding to conversation.
5. Completion is derived from verified outputs, not the model's prose. Stored
   artifacts and provenance survive backend recreation.

## Verification

Failing state/output laws, native-tool boundary tests and backend gates. Run
three bounded real cycles, inspect files and persisted receipts, document model
and served revision. Missing providers are failures, not synthetic successes.
Advance a controlled clock through attempt deadlines and both backoffs; verify
the three-attempt and 36-minute budgets, coalesced ticks and retained quarantine
when the old attempt or delegate remains able to persist or publish.

## Risks

Expensive idle work, repeated motifs, tool hallucination, media decoding failures,
disk growth and a schedule dispatch lock that ends before job completion.
