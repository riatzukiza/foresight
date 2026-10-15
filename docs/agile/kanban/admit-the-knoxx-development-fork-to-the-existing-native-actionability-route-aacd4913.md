---
category: "kanban"
labels: "ci, review, actionability, 3sp"
dependency: []
parent: "fork-review-setup"
type: "task"
write-id: "1792053902800-0.zrcb008bnci1e589zqa"
points: "3"
title: "Admit the Knoxx development fork to the existing native actionability route"
priority: "P1"
status: "breakdown"
uuid: "aa39770d-7adb-4171-bb75-b244aacd4913"
created_at: "2026-10-15T08:44:23.217Z"
---

# Admit the Knoxx development fork to the existing native actionability route

## Context

The existing Foresight owner is `fork-review-setup` (explicit canonical UUID; incoming, P1, 3 points), beneath `fork-dev-origins`. Its parent body names `create-dev-forks` as predecessor; that broad card is also incoming. The specific public fork already exists as `riatzukiza/knoxx`, database ID `1401249297`, node ID `R_kgDOU4VeEQ`, and its ordinary review workflow has actually run. This story admits only the missing scoped assessment capability for that observed fork. It does not complete the all-forks predecessor or parent.

The protected `open-hax/eta-mu` actionability workflow currently accepts only `open-hax/*` targets and scopes its App tokens to owner `open-hax`. Its host entrypoint independently rejects other owners. Canonical pr-flow actionability policy admits the existing ETA App principal only for `open-hax/knoxx` and `open-hax/proxx`; the personal fork is not enrolled. Ordinary reviews, author prose, secret-name presence and passing credential prechecks do not supply a scoped independent assessment.

## Outcome

A fresh central `open-hax/eta-mu` run can assess one genuine author-only Knoxx fork context using unchanged canonical actionability protocol, complete native inputs/source/history, anonymous dynamically discovered OpenCode MiMo and the existing independently authenticated ETA App. It publishes at most one native issue comment, preserves readback/uncertain outcomes, and supplies no review vote, approval, round, automatic resolution or finding settlement.

## Scope

- Keep the central protected workflow, per-target/PR publication lock, native collector, invocation verifier and one-shot publisher.
- Admit only the additional exact public fork identity above in the workflow's early/native target guards and host input guard. Preserve existing `open-hax/*` behavior. Derive App token owner `riatzukiza` and repository `knoxx` only after native name/ID/public-visibility checks; assert the retained node ID for this additional fork. All three target-read/publication token actions consume the validated owner and single repository outputs.
- Add only `["riatzukiza/knoxx" 1401249297]` to canonical `:review/actionability :repository-identities` with the existing principal `eta-mu-ai[bot]`, numeric ID `270021952`, node `BOT_kgDOEBg1QA`. Preserve the current two-field enrollment schema and all independent judgment, chronology, revocation and no-credit laws.
- Review and adopt the canonical source first; then pin that exact accepted canonical commit in both ETA workflow and `dependency-pins`, with matching fixture expectations. No provisional future SHA is invented.
- Qualify the actual central native route after protected adoption. Existing fork secret names are context only; the central route uses its existing protected `ETA_MU_APP_ID` / `ETA_MU_APP_PRIVATE_KEY` references. It does not read or copy secret values from Knoxx.

## Non-goals

No paragraph PR15 source/workflow edits, broad fork rollout, new reviewer identity/alias, second vote, ordinary full-review admission, rejection of Bot findings, automatic conversation resolution, change to quorum or required checks, OpenRouter/provider/account changes, new App installation or credentials, or copying a signing secret into a candidate workflow. A separate canonical rejection route is required for an actual Bot finding; author-context actionability cannot settle it.

## Acceptance criteria

- [ ] Native-shaped tests execute the actual authored workflow guards and host config: exact public `riatzukiza/knoxx`/`1401249297`/`R_kgDOU4VeEQ` reaches the existing collector; another personal repository, same name with wrong ID, aliases, malformed owner/repository inputs, wrong node, private/unknown visibility and failed metadata reads refuse before target checkout or App token creation. The wrong-node check belongs to the new workflow native-identity gate; canonical policy remains its existing name/database-ID pair schema.
- [ ] Every target token action scopes to the single validated owner/repository. Native publisher proof must still authenticate App metadata, installation grant, actual token repository access and native Bot tuple. No model process receives signing/publication credentials. A failed token/grant read is retained and makes no publication; credential-name/precheck success supplies no grant proof.
- [ ] Canonical pure tests admit the exact additional repository pair with the existing ETA principal and refuse wrong name, numeric/string ID, Bot ID/node/type or missing enrollment. Existing global OpenCode and current org enrollments retain behavior. Fresh assessments still produce zero approval/coverage/round/settlement credit; unresolved contexts still block merge. Changed heads/context/proposals, incomplete native/source/history, withdrawals and adverse/uncertain judgments retain existing refusals.
- [ ] The existing assessment suites and canonical law/policy/CLI/informational suites pass at the reviewed heads. After protected source adoption, retain one genuine fresh native assessment invocation, complete inputs and source digests, actual discovered model route, token/principal proof with no secret values, sole publication outcome/readback and canonical disposition. No positive result is required: a genuine adverse or uncertain assessment remains adverse/uncertain. No retry of an uncertain publication or job rerun reusing old evidence.

## Verification

Canonical repository: `nbb -cp skills/pr-flow/scripts skills/pr-flow/scripts/test_law.cljs`, `test_policy.cljs`, `test_cli.cljs`, and `test_informational.cljs` (each separate invocation with the same classpath). ETA source: `bb scripts/test-assessments.bb`; run the relevant CLI/lint gates required by its AGENTS if implementation touches CLI code. Retain the RED exact-fork guard/enrollment failures before implementation and GREEN results afterwards. Native qualification is a later protected exact-source run, never a synthetic fixture pass.

## Risks

The App's effective installation grant on the fork is still UNKNOWN: MiMo run `38703545217` failed before token creation/publication despite a successful credential-name check. The reviewed central route must observe the existing publisher proof rather than infer access or expand permissions. The canonical source and ETA consumer are separate repositories, so policy publication precedes consumer pin adoption. The broad predecessor/parent remain incomplete; selected fork capability does not activate the entire fork promotion/protection scheme. The context and head must be collected again after any push; earlier native IDs are provenance, not reusable current authorization.