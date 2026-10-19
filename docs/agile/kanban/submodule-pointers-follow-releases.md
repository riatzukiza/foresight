---
category: "kanban"
labels: "workspace, release, submodules"
parent: "eta-mu-baseline-review"
type: "task"
points: "2"
title: "Advance Foresight submodule pointers when member projects are published or deployed"
priority: "P1"
status: "incoming"
uuid: "submodule-pointers-follow-releases"
dependency: "eta-mu-release-2-0-1"
created_at: "2026-10-19T20:30:00.000Z"
---

# Advance Foresight submodule pointers when member projects are published or deployed

## Outcome

Changes to member projects are not finished until Foresight's gitlink names them. After eta-mu#360 merges and 2.0.1 is published, the `eta-mu` and `receipt-river` pointers (and any other member released in this epic) advance to the exact merged or published commits, via a Foresight integration PR requalified at its head.

## Acceptance criteria

- [ ] GIVEN a member project change that is merged, deployed or published WHEN the work is closed THEN the Foresight gitlink equals that commit, and the card names the SHA.
- [ ] GIVEN this epic THEN `eta-mu` (now at an old commit on `device/stealth`) and the standalone `receipt-river` submodule are reviewed for pointer advancement, each pointer targets a commit reachable from the approved destination branch, and any member left behind is listed with the reason.
- [ ] GIVEN `AGENTS.md` THEN the rule that a published or deployed member change requires a pointer bump is recorded once, in the instruction file, not copied into other documents.

## Verification

```bash
git submodule status eta-mu receipt-river
git -C eta-mu merge-base --is-ancestor <pointer> origin/main && echo reachable
```
