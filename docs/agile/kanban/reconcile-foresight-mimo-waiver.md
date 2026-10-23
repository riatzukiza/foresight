---
uuid: 8d29ca35-ad1f-4ccb-be2f-f410a212371a
title: Reconcile Foresight MiMo waiver without weakening deterministic checks
status: incoming
labels: review, policy, ci
---

## Outcome

Native current-head CodeRabbit and Codex approvals qualify Foresight's review
cohort under the recorded MiMo waiver; deterministic failures still block.

## Scope

Canonical PR-flow exact repository policy, eta-mu reusable gate boundary,
and Foresight immutable caller pin. See ../../notes/foresight-mimo-waiver.md.
Manual planning input only; no Rheos transition or validation is claimed.

## Non-goals

No waiver for other repositories, fabricated review/round credit, skipped
findings, global reviewer removal, deployment activation or protection bypass.

## Acceptance criteria

- Require both CodeRabbit and Codex approvals at the current full head SHA.
- Exclude only MiMo from the current cohort; mandatory overrides win.
- Retain every deterministic gate invocation, success and revision guard.
- Failed/skipped/pending deterministic dependencies keep the terminal gate red.
- Preserve actual MiMo result and all findings; retain historical PR47 failures.

## Verification

Canonical pure/isolated CLI tests and fresh PR47 read-only assessment; upstream
actual gate execution tests; unchanged caller deterministic script comparison.
Hosted current-head qualification and independent review precede adoption.
