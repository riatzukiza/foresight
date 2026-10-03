---
uuid: "b94fce31-907a-420a-bd3d-0a8f8942c787"
title: "Admit verified zero-finding full reviews in pr-flow"
type: "story"
category: "tooling"
status: "incoming"
priority: "P2"
points: 5
labels: "pr-flow, review-evidence, verification, follow-up"
---

# Admit verified zero-finding full reviews in pr-flow

## Context

On 2026-10-03, the canonical `~/.agents/skills/pr-flow/scripts/pr.cljs`
`fetch-heads` adapter read only formal GitHub pull-request reviews. CodeRabbit
completed explicitly requested full reviews with zero new actionable findings,
but published completion in mutable issue-summary comments instead of formal
reviews. The CLI therefore reported missing full review coverage despite the
available completion evidence.

The observed examples are:

| Summary | Run ID | Complete reviewed range |
| --- | --- | --- |
| [Rheos PR 1](https://github.com/riatzukiza/rheos/pull/1#issuecomment-5966686355) | `56a1d690-bf04-4205-bc72-826e5fb68b4e` | `11811264a308d406cb612aefa1dad40818675e5e` to `dda73f1003f6fc0ba804544d4c2a556eafe246bf` |
| [Foresight PR 2](https://github.com/riatzukiza/foresight/pull/2#issuecomment-5966549469) | `be0fbe82-b4af-4542-8c76-d2b47fbfc53e` | `4927ab7d439ac7e1093c574096a2d5a6d6241391` to `b09cda73d47b5b9bce20f12cf9fc42ef209ecffa` |
| [Epiphany PR 1](https://github.com/riatzukiza/epiphany/pull/1#issuecomment-5966394176) | `60a9a582-8701-4f26-ad06-53f6b70dac0a` | `643be698ea0d841dd19385506b272872306e456e` to `ff5fc0e2a42b478ecda14163fcdbb4f61e317909` |

Each observed summary has a bounded `recent_review_start/end` section containing
the zero-finding sentence, a Run ID, selected files and the complete base-to-head
range. Its `final_review_risk_coverage` JSON names the same head as both
`sourceCommitId` and `coveredCommitId`, with `kind` equal to `reviewed`.
The [Rheos full-review request](https://github.com/riatzukiza/rheos/pull/1#issuecomment-5967175076)
was posted at 08:27:24 UTC; that summary was updated at 08:31:09 UTC.

This is standalone initial intake for deferred compatibility work. It is not
accepted runtime semantics, a child of the content preparation epic, or permission
to merge a PR whose checks or settlements are incomplete. These comment URLs
remain mutable; the observed runs and revisions above bound the finding.

## Outcome

`pr-flow` recognizes a proven zero-finding full review of the current head and
base without admitting an acknowledgement, incremental review, stale review,
skipped run or rate-limit response as completion. The CLI can report the admitted
evidence and why a candidate was rejected.

## Scope

- Implementation owner: `riatzukiza/.agents`, canonical `skills/pr-flow`.
- Resolve publishing ownership first. The local `pr-flow` and sibling `pr-*`
  skill folders were untracked when inspected on 2026-10-03, alongside unrelated
  modified skills. Establish a reviewable baseline and an isolated owned diff;
  do not blanket-stage the catalog or assume these local skills exist on main.
- Keep GitHub collection and JSON decoding at the adapter boundary. Express
  evidence acceptance over portable Clojure maps in `.cljc` where practical.
- Preserve the existing formal-review path. Add only the narrowly verified
  zero-finding full-summary path, with paginated issue-comment collection.
- Preserve provenance: repository, PR, head, base, summary ID/URL, summary
  update time, request ID/URL and effective time, Run ID and coverage values.
- Reuse the existing merge law, CI evaluation, settlement requirements and
  required-reviewer coverage. Admission supplies evidence; it grants no override.

## Non-goals

- No weaker CI, branch protection, required reviewers or settlement rules.
- No automatic merge, administrator bypass, repository-setting changes or
  additional review requests merely to manufacture a formal review record.
- No acceptance based only on a green badge, optimistic prose, a request marker,
  a checkbox, paid usage or absence of inline findings.
- No generic HTML parser, provider API integration or alternate board authority.
- No mutation of the global skill checkout as part of recording this card.

## Acceptance criteria

- Collect current head and base together. Refuse a candidate if either no longer
  matches at the decision boundary, or if collection is incomplete.
- Accept issue-summary candidates only from the verified exact
  `coderabbitai[bot]` GitHub author with Bot user type; a substring login match
  or user-authored copy is insufficient.
- Require exactly one complete bounded recent-review section with the observed
  zero-finding completion statement, a valid Run ID, positive selected-file
  count and an explicit complete range from the current base to current head.
- Decode a unique, well-formed coverage JSON marker at the adapter boundary.
  Require `sourceCommitId` and `coveredCommitId` to equal the current head and
  `kind` to equal `reviewed`. Missing, contradictory or ambiguous evidence fails
  closed.
- Correlate an actual non-bot `@coderabbitai full review` request containing the
  exact `pr-flow-full-review-head:<SHA>` marker for this head. The summary update
  must follow the request's effective timestamp, including edits; a request is
  never itself completion evidence.
- Require successful current CodeRabbit status explicitly indicating review
  completion. Pending, skipped, failed, rate-limited, absent or unknown status
  cannot admit a summary, even if an older completion section remains visible.
- Require independently verifiable provider evidence linking that successful
  status to the summary's Run ID, exact head and correlated full-review request.
  Its completion timestamp must follow the request's effective time; head and
  timestamp matches alone are insufficient. If the provider exposes no evidence
  establishing the same run, fail closed and report missing correlation.
  Reject a newer pending same-head request even when an old successful status
  and an unrelated edit of the mutable summary remain visible. Retain explicit
  bot full-review-finished corroboration when the provider exposes it.
- Do not reject completed paid explicit runs solely because their summary
  retains a global automatic-review pause banner or exhausted included-allowance
  footer. Bound the match to the actual recent run and current completion status;
  Foresight and Epiphany above exhibit this distinction.
- CLI output reports an admitted candidate's preserved provenance and each
  rejected candidate's specific refusal reason. Missing run correlation must be
  distinguishable from pending, skipped, stale and malformed evidence; an
  unexplained generic pass or failure does not meet the outcome.
- Add only the proven head to CodeRabbit's reviewed-head set. All applicable CI,
  thread settlement, body-response and additional-reviewer requirements still
  determine whether the merge gate passes.

## Verification

Retain sanitized fixtures from all three observed summaries and matching
requests/status data, with retrieval time and IDs, before those mutable comments
change. Treat them as observed candidate evidence, not automatically positive
fixtures: admit them only if the provider's run-level status correlation can be
established independently. Demonstrate the current compatibility failure, then
qualify the corrected adapter and pure acceptance function without weakening
that correlation requirement to accommodate these examples.

Exercise negative cases for stale head; changed base with unchanged head;
incremental previous-head-to-current-head range; mismatched coverage SHAs or
kind; missing, malformed or duplicate section/coverage markers; malformed JSON;
missing Run ID; empty file selection; bare zero-finding prose; acknowledgements;
human/lookalike bot copies; completion older than the request; and requests
edited after completion. Also reject pending, skipped, rate-limited, failed,
absent and unknown current status with otherwise valid summary data.
Include an old successful current-head status plus an unrelated summary edit
after a newer same-head request that is still pending; that combination must
not become a completed full review. Include a fresh successful status from a
different Run ID on the same head after the request; mismatched or absent
run-level correlation must still refuse the older summary.

Verify rendered CLI reporting for both an admitted candidate's provenance and
rejected candidates' specific refusal reasons. Exercise the positive
retained-pause-banner and exhausted-included-allowance cases with actual
completed paid full reviews whose run-level status linkage is independently
verified. Integrate acceptance with the unchanged merge gate and prove that failing CI, unsettled or contested threads,
unanswered body findings, truncated collection and uncovered additional required
reviewers remain blockers. Run the canonical shipped law tests and relevant
adapter fixtures at the implementation revision; the observed baseline was
13 tests and 66 assertions passing, not evidence for this unimplemented change.
Record exact revisions, commands, outcomes and review dispositions. Use Rheos
for board reads and any future transitions.

## Risks

CodeRabbit summaries are mutable and their private comment grammar can change.
Unknown formats must remain visible as missing evidence, rather than becoming
passes through permissive matching. A stale recent-review section may coexist
with a new pending request or a global pause banner. Correlating bounded content,
exact revisions, effective timestamps and current status prevents those cases
from silently becoming full coverage. If available provider APIs do not expose
provable run-level correlation, this path stays blocked; record an upstream
evidence gap instead of accepting chronological coincidence.

The initial five-point estimate covers this one evidence adapter and adversarial
qualification. If publishing the untracked skill baseline requires broader
governance work, split that prerequisite before readiness. This Foresight card
routes work to the owning skill repository; it does not promote its matcher into
Foresight law or authorize copying the implementation here.
