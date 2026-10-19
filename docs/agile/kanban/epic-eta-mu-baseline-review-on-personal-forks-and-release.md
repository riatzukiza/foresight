---
category: "kanban"
labels: "workspace, ci, review, release"
type: "epic"
points: "8"
title: "EPIC: eta-mu is the always-available baseline reviewer on every riatzukiza fork, and its releases reach Foresight"
priority: "P1"
status: "incoming"
uuid: "eta-mu-baseline-review"
created_at: "2026-10-19T20:30:00.000Z"
---

# EPIC: eta-mu baseline review on personal forks, and release propagation

## Outcome

Every `riatzukiza/*` fork of a Foresight submodule (and `riatzukiza/foresight`) runs the eta-mu evidence review on pull requests, even when CodeRabbit lags the agents or Codex has run out of reviews. eta-mu may use weaker models; its job is to be available. Whenever a member project is merged, deployed or published, the Foresight submodule pointer is advanced to the exact released commit, and the epic records which pointers still lag.

## Context

- 2026-10-19: `ETA_MU_APP_ID` and `ETA_MU_APP_PRIVATE_KEY` were set as repository Actions secrets on `riatzukiza/foresight` and 23 submodule forks (`riatzukiza/mojomast-opencode` stands in for `opencode`; `riatzukiza/opencode` cannot be created because that fork network is already held). Secrets are necessary, not sufficient: the review workflow must also exist on each fork's default branch.
- Workflow gaps observed 2026-10-19 via `gh api`: no review workflow on `shx`, `chat-ui`, `clio`, `kanban-orchestrator`, `osmos`, `session-mycology`; differently named ones on `axxium`, `sol`, `mojomast-opencode`; the rest carry `eta-mu-review.yml` copies that may predate the repaired MiMo evidence workflow.
- eta-mu 2.0.0 and `@eta-mu/{fork-tax,session-mycology,receipt-river,rheos,sol}` were published on 2026-10-19. 2.0.0 stamps `1.1.1` as producer version and `eta-mu receipt` ignores `.ημ/receipts.edn`; the fix is `open-hax/eta-mu#360`.
- Related: `fork-review-setup` (apps, branch protection and exact-head gate on every fork) is a sibling concern under `fork-dev-origins`; this epic owns the eta-mu workflow and secrets, not branch protection.

## Children

- `eta-mu-review-workflow-on-forks` — the review workflow exists and runs on every fork
- `eta-mu-release-2-0-1` — `open-hax/eta-mu#360` merged and 2.0.1 published
- `submodule-pointers-follow-releases` — Foresight pointers advance to released commits

## Definition of done

A probe PR on each fork produces an eta-mu review verdict on its exact head, `eta-mu@2.0.1` is published from merged `main`, and every Foresight submodule pointer that has a deployed or published change points at that change.
