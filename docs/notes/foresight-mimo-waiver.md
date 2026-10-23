# Foresight review qualification under the MiMo waiver

License: GPL-3.0-or-later.

Err reaffirmed on October 22, 2026: reconcile the MiMo waiver so current-head
CodeRabbit and Codex approvals qualify the review, without weakening the
deterministic checks. The [October 21 PR #47 merge record](https://github.com/riatzukiza/foresight/pull/47#issuecomment-6328083797)
records the earlier exception. PR #47 was merged at
`6cf607cb4c86c8bed5c8de9b421eba52284c2716`; its failed historical gate is
retained as failed evidence.

## Authority and scope

The canonical [PR-flow skill](https://github.com/riatzukiza/.agents/tree/main/skills/pr-flow)
owns review admission. Its candidate `:review/repository-policy` entry applies
only to `riatzukiza/foresight`: both trusted native CodeRabbit and Codex
approvals must bind the current full head SHA. Only MiMo is excluded from the
current review cohort. No approval, availability, historical round or finding
is fabricated. Explicit mandatory overrides still apply. This does not activate
the broader proposed Promethean branch-protection or deployment process.

## Hosted boundary

The caller pins the eta-mu source that adds `require_model_review: false`.
That option is rejected for other repositories or an explicit model selector;
the runner's existing empty selector still resolves the free MiMo route.
MiMo continues to run with its actual result visible. The terminal gate keeps
all deterministic job/output success, expected/executed/completion SHA,
exact-head, clean-tree and context-compilation obligations. Only the waived
model's success and model execution bindings leave that aggregate requirement.
The aggregate gate remains a deterministic check in canonical PR-flow.

Every existing `run_gate` invocation is preserved byte-for-byte. There is no
`continue-on-error` wrapper, required-check removal, identity substitution,
review-finding waiver, paid model route or branch-protection bypass.

## Qualification and adoption

This branch is a source candidate. Before adoption, independently qualify the
canonical PR-flow policy and eta-mu source, then qualify this caller at its
current head using the reviewed immutable pin. Install the canonical skill
only from its qualified merged source. Require a fresh successful hosted
aggregate; old failures cannot be relabeled. Both CodeRabbit and Codex reviews
must be native, complete and current, with every finding settled.

The waiver remains explicit until revoked in the trusted catalog and hosted
caller together. A MiMo failure or recovery never silently changes policy.
