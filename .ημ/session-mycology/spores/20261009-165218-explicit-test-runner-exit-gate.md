---
status: incubating
created: 2026-10-09T21:52:18.531658887Z
source-session: /home/err/spaces/foresight
source-task: Restore and review OpenHax personal portfolio
p-efficiency: 0.60
p-friction: 0.85
p-skill-candidate: 0.79
promoted-to: ""
rejected-reason: ""
---

## Problem
A shadow-cljs compile with autorun printed failing assertions but still returned success; a build child-process error with status zero could likewise pass.

## Pattern
Compiler or process-wrapper success can hide failure in the effect that the command is supposed to qualify.

## Candidate skill outline
- Name suggestion
- Trigger phrases
- Key steps or rules
- Anti-patterns to avoid

## Better path
Execute the generated test runner as its own process and propagate its exit status. Treat child-process error as failure before considering status. Prove each boundary with one deliberately failing assertion or injected spawn error, then run the real suite.

## Receipt refs
- 2026-10-09T21:13:54.095983704Z,2026-10-09T21:27:27.021674805Z,2026-10-09T21:36:23.251739309Z
