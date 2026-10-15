---
uuid: "cce5ffb1-b348-484f-bad9-155a56baeb5f"
title: "Recognize native Codex review state without losing negative or scope guards"
status: "incoming"
type: "task"
priority: "P1"
points: "3"
labels: "review, codex, canonical-skills"
dependency: []
category: "kanban"
write-id: "1792101387575-0.tjw3ox0rl08hohi8p7m"
created_at: "2026-10-15T21:56:27.575Z"
---

# Recognize native Codex review state without losing negative or scope guards

## Context

The canonical PR-flow collector does not recognize two observed native Codex
formats. This prevents completed reviews from being assessed consistently while
newer negative and pending states must continue to revoke older passing evidence.
The problem affects the Website/CMS deployment prerequisite review chain.

Dated native observations from October 15, 2026 are retained as full inputs:

- [ETA24 panel6201584645](https://github.com/riatzukiza/eta-mu/pull/24#issuecomment-6201584645)
  at head `b3971e6e2844645863c699d605d5dbee69f2ccbb`: completed code
  review, false/false finding flags, actual completion20:46:34.510287UTC.
- [Knoxx18 panel6202516315](https://github.com/riatzukiza/knoxx/pull/18#issuecomment-6202516315)
  at historical head `62a4017a4f341921f749a4b8f1c2a283cac539f1`:
  completed with true/true flags at21:25:02.472309UTC, accompanied by five
  actual findings in review5540129138. A completed panel is not necessarily
  passing. This mutable public panel may later describe another head; qualify
  the retained full native snapshot rather than transferring its old state.
- [F41 receipt6200788648](https://github.com/riatzukiza/foresight/pull/41#issuecomment-6200788648)
  at `7736be78ed03c3ca3ac2870564edc740d4e7a133`: an initial native
  `codex-review-receipt:v1` HTML comment and blank line precede the already
  accepted classic sentence and Reviewed commit marker. Collector hydration
  tests the raw prefix; the law strips the comment but retains a leading newline.

The structured panels use `codex-pull-request-review-summary` and exactly one
`codex-review-state:v1` payload. Their observed publisher is the existing
configured `chatgpt-codex-connector[bot]`, native Bot199175422 /
`BOT_kgDOC98s_g`, performed-via App1144995. Both observed security objects have
all fields null; active security-review semantics are outside this story.

Fresh native Rheos searches for codex, format and review, and the full personal
canonical PR inventory found no equivalent implementation owner. Existing
`fork-review-setup` owns App/protection setup; `e1-10-reviewer-start-window-protection`
owns trusted required-gate rollout; ETA's `opencode-mimo-evidence-review-agent`
owns the anonymous OpenCode/MiMo route. Preserve those owners, estimates and state.

## Outcome

One bounded canonical `.agents` adapter recognizes these actual native formats
and shares their qualified facts across review evidence, completion, availability
and request deduplication. It preserves negative, scope, chronology, identity,
settlement, required-check and convergence guards. The implementation is reviewed
and protected-merged in the canonical repository before a separate qualified
catalog adoption; this planning card supplies no review or deployment credit.

## Scope

This is a standalone provisional P1/3 owner coordinated on the Foresight board.
Implementation belongs to `riatzukiza/.agents`, on an isolated branch from its
fresh accepted main. The recovered base is protected
`8497afdd329c3dcaefb78eec36d8e629a59d9eb2`; recheck it before implementation.
Record the eventual canonical source PR here through Rheos. Do not edit the
shared installed `~/.agents` checkout or the Foresight `.agents` consolidation
input. Do not invent a formal parent, dependency or new epic.

Prospective canonical paths are:

1. `skills/pr-flow/scripts/pr_flow/law.cljc`: a shared pure bounded decoder and
   decisive-head, completion, availability and deduplication integration.
2. `skills/pr-flow/scripts/pr.cljs`: shared classic recognition for commit
   hydration and complete native Bot/App/source custody; no new effect surface.
3. `skills/pr-flow/scripts/test_policy.cljs`: complete relevant native positive,
   negative, temporal, scope and cohort fixtures and causal controls.
4. `skills/pr-flow/scripts/test_cli.cljs`: collector/API contracts showing all
   consumers use the same qualified facts without extra writes or requests.
5. `skills/pr-flow/SKILL.md`: document these literal channels and limits while
   preserving the existing classic sentence policy and global policy pointer.
6. `skills/pr-flow/CONTRACT.edn`: record the new bounded admission invariants
   and contract version consistently with the reviewed behavior. Activation,
   identities, quorum and non-override authority remain unchanged.

The contract path specializes the earlier five-path proposal because the
canonical repository requires its automated governance contract to match a
materially revised skill. Keep historical evidence and ledgers append-only.

## Non-goals

A general JSON or Markdown parser, schema compatibility beyond observed forms,
active security-review interpretation, generic multi-invocation reconciliation,
new identities or configuration, quorum/round/settlement exceptions, synthetic
GitHub APPROVED states, model/credential/App changes, paid reviews, a second
board engine, project-local review policy, automatic catalog installation and
Website/CMS application or deployment work are outside this story.

## Acceptance criteria

- [ ] Require the literal standalone marker pair at native body start, exactly
  one complete bounded v1 payload, exactly the observed two nested objects and
  known fields/types/order. Use a narrow whole-payload recognizer with explicit
  bounds and whitespace support; do not build a general parser or rely on
  `JSON.parse` after duplicate keys have been lost. Reject quoted/fenced examples,
  duplicate markers/keys, malformed/oversized/trailing payloads, unknown versions,
  unsupported ordering and non-null/unknown security state as passing evidence.
- [ ] Preserve raw body, native comment ID, configured Bot login/type plus the
  observed numeric/node/App tuple, creation/edit times and full40hex code head.
  Require one matching native Code Review row, compatible short rendering and
  actual completion timestamp bounded by native comment times. Disagreement or
  missing custody refuses completion; an unrelated later edit cannot retime an
  old code review into a newer stage/request. New identity aliases are forbidden.
- [ ] Completed false/false at the exact current head may supply a separately
  named native issue-state no-findings verdict, subject to unchanged actual
  incomplete/omitted-input and complete-corpus guards. Completed true/true or
  true/false is nonpassing. Later current-head pending, negative, incomplete,
  formal CHANGES_REQUESTED or dismissal revokes older passing evidence under
  unchanged chronology rules. Finding-bearing completion may complete a full
  review without approving. Flags never remove roots, body findings or unsettled
  conversations, and no GitHub APPROVED review is fabricated.
- [ ] Status, completion, availability and request deduplication consume the
  same shared qualified facts. Actual completed automatic review can restore
  availability after older quota without approving or finishing a newer manual
  request. Pending native work remains pending and deduplicated. Preserve native
  source IDs and the existing available-agent and mandatory-reviewer semantics.
- [ ] Without a native invocation ID, count at most one automatic Codex
  completion per exact head and actual User stage, using the code completion time
  strictly after that stage boundary. Give no extra round if a qualifying
  classic/formal representation already exists for that provider/head/stage.
  A genuinely preceding manual request uses its existing request key; a newer
  request is never completed by an older timestamp. Similar prose or nearby
  timestamps cannot identify independent invocations or multiply provider votes.
- [ ] Classic hydration and the law use one recognizer for the optional exact
  initial `codex-review-receipt:v1` comment plus whitespace before existing
  accepted prose. Retain exactly one unambiguous Reviewed commit marker resolved
  through GitHub to the full current SHA. Reject quoted/fenced/duplicate variants;
  existing unwrapped classic fixtures and accepted sentence policy remain intact.
- [ ] Preserve the existing five-round/unanimous convergence, native User
  stage boundaries, required checks, full-scope rules and every settlement law.
  A locally passing test, producer job, panel title, quota or unsupported format
  cannot bootstrap approval of this implementation or its planning source.
- [ ] The exact implementation source has genuine causal RED/GREEN, passing
  registered canonical law/policy/CLI suites and required hosted checks with
  complete outputs, real counters, cleanup and source custody. Obtain independent
  source review and protected canonical merge before separate catalog adoption.
  Recheck fresh native corpus/source/stage during adoption; preserve installed
  device history and foreign dirty inputs. This card remains planning until
  actual lawful Rheos readiness and subsequent implementation evidence.

## Verification

On unchanged canonical source, register tests using the retained complete ETA24
positive, Knoxx18 negative plus five roots, and wrapped F41 classic inputs.
Demonstrate the missing positive/completion/pending/availability and classic
hydration behavior fail for the real source gap; merely asserting old rejection
is not causal RED. Then implement the shared pure layer and outer collector.

Controls cover edited positive to negative/pending; stale or conflicting heads;
wrong native Bot/numeric/node/App; duplicate/quoted/fenced/malformed/unknown or
oversized markers/payloads; unsupported security; bad timestamps; later formal
negative; actual omitted-input prose; unresolved findings; old completion versus
new request or stage; quota recovery without approval; and summary/classic/formal
deduplication. The retained negative Knoxx18 remains nonpassing with all five
findings visible. Include genuine unwrapped classic regressions.

Run the existing `test_law.cljs`, `test_policy.cljs` and `test_cli.cljs` via the
canonical documented NBB classpath and all other registered required checks.
CLI controls exercise actual status/gate/request input contracts, no duplicate
invitation while native work is pending, and refusal under required failure or
unresolved conversations. Recognition itself creates no GitHub or ledger writes.
Keep required effectful observations separate from the pure recognition result.

Planning verification is native dedup/admission/readback, source and artifact
custody, explicit ownership/estimate review, diff hygiene and append-only
provenance. No implementation tests, model execution, review approval, source
adoption, service or deployment result has been produced by this plan.

## Risks and estimate

Three points is provisional for one provider's two observed formats and the
shared six-path adapter/tests/contract/documentation boundary. If implementation
needs a general parser, new global identity/configuration mechanism, generic
multi-invocation reconciliation or active security semantics, stop and propose
a split through planning review. The observed private format is not a public
compatibility guarantee. A stronger native invocation identifier is unproven;
the conservative at-most-one rule may undercount repeated automatic reviews.

Review and catalog adoption remain separate from source preparation; do not use
this candidate's decoder to authorize its own protected merge. Required checks,
actual findings and incomplete inputs remain obligations. The existing F41 MiMo
attempt and all application/deployment prerequisites keep their own ownership.
