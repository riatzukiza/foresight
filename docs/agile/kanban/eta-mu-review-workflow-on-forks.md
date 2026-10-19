---
category: "kanban"
labels: "ci, review"
parent: "eta-mu-baseline-review"
type: "task"
points: "3"
title: "Run the eta-mu review workflow on every riatzukiza fork"
priority: "P1"
status: "incoming"
uuid: "eta-mu-review-workflow-on-forks"
created_at: "2026-10-19T20:30:00.000Z"
---

# Run the eta-mu review workflow on every riatzukiza fork

## Outcome

Each fork's default branch carries the current `eta-mu-review.yml` (repaired MiMo evidence workflow) and its secrets, so a PR there receives an eta-mu review verdict.

## Acceptance criteria

- [ ] GIVEN each fork in `shx`, `chat-ui`, `clio`, `kanban-orchestrator`, `osmos`, `session-mycology` WHEN its default branch is inspected THEN `eta-mu-review.yml` exists, taken from Foresight's reviewed copy, with no repository-local reimplementation of Rheos semantics.
- [ ] GIVEN `axxium`, `sol`, `mojomast-opencode` THEN their differently named workflows are reconciled with, or deliberately kept beside, `eta-mu-review.yml`, with the decision recorded.
- [ ] GIVEN forks already carrying `eta-mu-review.yml` THEN each is compared with Foresight's current copy and updated where it predates the repaired MiMo evidence workflow.
- [ ] GIVEN a probe PR on at least one fork per workflow variant WHEN the review runs THEN an eta-mu verdict is bound to the exact head and the required secrets resolve. Pending or skipped runs are recorded as such, never as approvals.
- [ ] GIVEN `riatzukiza/opencode` cannot exist THEN the `opencode` entry in the fork map is `riatzukiza/mojomast-opencode`, recorded in the fork-org map card.

## Verification

```bash
gh secret list --repo riatzukiza/<fork> | grep ETA_MU_APP
gh api repos/riatzukiza/<fork>/contents/.github/workflows -q 'map(.name)'
nbb -cp ~/.agents/skills/pr-flow/scripts ~/.agents/skills/pr-flow/scripts/pr.cljs status riatzukiza/<fork> <probe-pr>
```
