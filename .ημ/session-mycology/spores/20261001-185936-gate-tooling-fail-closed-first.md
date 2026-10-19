---
status: incubating
created: 2026-10-01T23:59:36.361504424Z
source-session: /home/err/spaces/foresight
source-task: Author the pr-flow merge gate and run it on live PRs
p-efficiency: 0.6
p-friction: 0.55
p-skill-candidate: 0.6
promoted-to: ""
rejected-reason: ""
---

## Problem
The first gate passed on a skipped CodeRabbit run, counted settlement replies as review coverage, re-fetched the head before merging, and let Handled clear P0/P1; four fail-open bugs found only by live use and review

## Pattern
Gate/check tooling written against the happy path; every non-happy review state (skipped, rate-limited, failed, stale head, truncated page, missing reviewer) defaults to pass

## Candidate skill outline
- Name suggestion
- Trigger phrases
- Key steps or rules
- Anti-patterns to avoid

## Better path
Before first live use, enumerate every state the upstream can report and write one failing test per state asserting the gate blocks; treat unknown or partial data as BLOCK; pin decisions to the evaluated input (head SHA) instead of re-reading it

## Receipt refs
- riatzukiza/.agents#8@730924f
