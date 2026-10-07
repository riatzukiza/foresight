---
uuid: "593ec22f-7390-4663-9800-77f64a450783"
title: "LA1: Define typed derivation and admission for provisional lineage"
status: "incoming"
type: "epic"
priority: "P1"
points: "8"
labels: "lineage, admission, planning, issue-66"
category: "kanban"
---

# LA1: Define typed derivation and admission for provisional lineage

## Context

[Foresight issue #66](https://github.com/open-hax/foresight/issues/66) distinguishes
pinned direct observations from six stronger provisional Promethean interpretations.
This is a proposal on personal sync PR #3 at `96a6dca24cb7a14b041bdd6e3e7922c568238da9`,
which contains accepted upstream `fcfc2d17f28640203066ddfe0a22f87db7372a32` ancestry
plus personal synchronization metadata. Neither the sync nor this proposal is qualified.


## Outcome

A typed, total, owner-authorized relation separates immutable supporting evidence,
interpretation admission, rejection/supersession, and explicit durable promotion.
The existing six Promethean claims stay provisional until a separately authorized
promotion succeeds; every historic observation stays inspectable.

## Scope

The complete eight-criterion issue outcome is proposed as three stories, with
estimates 3 + 3 + 2 = 8. The epic is not directly implementable. Portable `.cljc`
shapes/laws precede outer filesystem/Git/Clio adapters; a replayable projection
cannot become authority independent of its retained events.

## Acceptance criteria — complete original issue

- Define typed identities for observations, derivations, admissions, rejections, and promotions.
- Bind every derivation to immutable source/descendant evidence and an explicit rule or human decision.
- Distinguish evidence support from interpretation admission.
- Require an owner-authorized durable event before a provisional interpretation becomes accepted.
- Preserve rejection and supersession history without rewriting earlier observations.
- Make malformed or missing relations fail closed and keep validation total.
- Add laws and adversarial tests for unknown identities, cycles, stale evidence, conflicting admissions, and attempted promotion without authority.
- Document how existing provisional Promethean claims remain lawful before any promotion relation exists.

## Proposed decomposition

- LA1.01 `4ce826b1-74aa-40da-827b-17af91722431` — typed evidence/derivation contracts and total graph validation (3).
- LA1.02 `16edcfc1-b65c-441e-b527-ef50dc5c290a` — scoped authority, durable admission/rejection/promotion and replay (3), depends on LA1.01.
- LA1.03 `abc9f258-4328-4a15-8a91-5c74c6687300` — adversarial composition and unchanged provisional inventory compatibility (2), depends on both preceding stories.

## Verification

The design at `docs/notes/design/lineage-admission-planning.md` maps every original
criterion to a story and real future positive/negative proof. Require current root
checks, dual-host pure contract tests, an isolated actual ledger append/reopen
fixture and no-authority/conflict controls. Native planning review/convergence,
required checks and lawful Rheos readiness must qualify each implementation slice.
No implementation command or repair qualification is supplied by this proposal.

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
