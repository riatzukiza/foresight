---
category: "kanban"
labels: "contract-language, session, ownership, 2sp"
type: "story"
story_id: "ANW1.01"
points: "2"
title: "ANW1.01 — Map the closed session profile to canonical survivor contracts"
priority: "P1"
status: "incoming"
epic: "a94c47bb-0a93-415c-ba3b-f9fddd2ea8fb"
parent: "a94c47bb-0a93-415c-ba3b-f9fddd2ea8fb"
uuid: "63b6320a-a4fb-43c4-9923-e5e8e970ca1d"
---

# ANW1.01 — Map the closed session profile to canonical survivor contracts

## Context

Start with the bounded [epic](epic-agent-native-coordinator-session-profile.md),
issue77 and the four original78 proposal artifacts at
`0c0fcab33c7a2b685a0ecf5ff6138a3173e0a31e`. The original accepted-card UUID
`8b4c1abb-6599-4bdf-a2aa-2adeec62e821` is a source reference only. CS1.02
`preserve-raw-causal-event-graph` and CS1.06 `purpose-roles-bounded-authority`
in [personal9](https://github.com/riatzukiza/foresight/pull/9) at
`91a02f8547cda726bea30c288e4df5aa1db899b2` address intersecting research
outcomes; neither is a hard prerequisite for this documentary crosswalk.

## Outcome

A revision-pinned comparison identifies exactly which existing declarations
can support the six-form session profile, which require adapter mappings and
which semantics remain proposed gaps. ANW1.02 consumes this comparison.

## Scope

Limit the comparison to `:session/started`, `:resource/read`, `:agent/invoke`,
`:session/await`, `:artifact/emit`, `:session/complete`, their session-result
bundle and directly referenced event, receipt, identity and admission evidence.

Inspect the actual schemas/laws and tests at the accepted root's direct pins:

| Owner | Full pin | Initial evidence seam |
| --- | --- | --- |
| Katamorph | `3bd4cf26e68dc88fbe67f831baa4bc389e3363e7` | `src/cljc/katamorph/schema/action.cljc`, `step.cljc`, manifest/schema registry, policy and action interpreter |
| Clio | `788cdd3434615a7932b924e68520dbf7f88408c2` | README event envelope; schema revision, canonicalization and replay laws |
| Sol | `1276955c86ff46936cd1ce7d81fbc790f7068e1e` | `AGENTS.md`; `src/cljs/open_hax/sol/shape/session_persistence.cljs`; session/run boundary |
| Axxium | `2439d4d6b8e546cda276f09f5c96db59226ecad6` | identity/actor/auth boundaries; issuer/delegation evidence must be demonstrated rather than assumed |
| Knoxx | `fb08a10a8aa32a594cc97ae11b820113de4cf386` | product/runtime contracts and session/context consumers |
| Receipt River | `7c7c62343fea0241ac545e37f14e57ab344bfa30` | receipt construction, schema and compatibility authority |
| Rheos | `11811264a308d406cb612aefa1dad40818675e5e` | board boundary only; session profile must not replace lifecycle authority |

The old proposal uses tuple event refs and lacks the canonical Clio schema
revision/stream envelope. Sol persists a runtime run shape with a different
status and result vocabulary. Record both as observed mismatches requiring
explicit mappings; do not reject source data or silently claim compatibility.
Katamorph already has portable typed action ports and step references. Use
that evidence before proposing another grammar/schema authority. Muse host
translation and Proxx provider access remain effect/compatibility consumers.

## Non-goals

Auditing every child feature or all twenty proposed kernel shapes; implementing
new contracts or adapters; changing child source/pins; executing services;
choosing a global identity authority by name; promoting recovered claims;
rewriting old proposal data or established cards.

## Acceptance criteria

- [ ] GIVEN a proposed form WHEN mapped THEN the row names its input/output,
  admission, event, receipt and result seam with repository, full revision,
  path and the inspected schema/law/test evidence.
- [ ] GIVEN an existing contract WHEN compared THEN compatible fields,
  conflicting identity/status vocabularies and required mappings are explicit.
- [ ] GIVEN a missing contract WHEN reported THEN the evidence distinguishes
  inspected absence from uncertainty and names a bounded owning-repo follow-up.
- [ ] GIVEN issuer/delegation requirements WHEN matched against Axxium THEN
  auth sessions or actor registration alone do not prove offline authority replay.
- [ ] GIVEN a composition statement WHEN classified THEN observed child fact,
  proposed composition/lift and explicitly accepted common law stay distinct.
- [ ] The comparison specifies what ANW1.02 may reference without introducing
  a second schema, event, receipt or board authority.

## Verification

Independently review each mapping against pinned primary source and relevant
fixtures. Keep source excerpts/references and mismatches inspectable in the
subsequent specification PR. This story needs no runtime or service execution.
Native planning review and lawful Rheos readiness are prerequisites for doing
the story; source inspection does not prove board validation.

## Risks

README summaries may lag code. Prefer inspected schemas/laws and report that
conflict. Similar names do not prove equal semantics or authority. Two points
assumes this finite interface inventory, not a full repository audit; split
new independent outcomes instead of growing it invisibly.
