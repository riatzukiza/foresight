---
uuid: "5aa55b2b-6bfd-442a-a083-f537435c656d"
parent: "0d73c22a-b018-4b8c-92fe-560127f53294"
title: "Consume qualified merge admission before personal fork PR readiness"
type: "task"
status: "incoming"
priority: "P1"
points: "3"
labels: "review, personal-forks, merge, consumption"
---

# Consume qualified merge admission before personal fork PR readiness

## Outcome

Personal development PRs can become ready without an inherited eager caller
merging an unqualified head. Consume the reviewed owner fix tracked in
[Proxx #454](https://github.com/open-hax/proxx/issues/454), preserving canonical
review admission and separate release/deploy integration into origin.

## Scope

Proxx personal PR1 and Axxium personal PR1 currently stay draft because their
inherited same-repository, non-draft auto-merge callers use floating Eta-Mu
workflow references and squash merging. Their jobs were actually skipped while
draft. A disabled repository auto-merge option is not trusted exact-head
admission evidence. Manual CodeRabbit review can proceed separately.

Reuse the existing canonical merge gate and trusted publication seams. The
owning repositories qualify their caller/configuration changes through personal
development PRs; Foresight records consumption, exact revisions and bounded
readiness fixtures. Retain stronger repository requirements and distinguish
staging retargeting from default-branch policy and deployment admission.

## Non-goals

No Foresight merge-gate implementation, automatic policy/settings activation,
waived checks, candidate-controlled authorization, force push, squash merge,
live deployment or draft-to-ready workaround. This manual incoming card does
not admit a transition or authorize merging any active PR.

## Acceptance criteria

- Trusted caller code is pinned outside candidate control and never executes
  candidate code with signing or deployment authority.
- A readiness event alone cannot authorize merge; absent/pending/failed review,
  unresolved findings, failed required checks and stale head/base reject it.
- The gate uses verified native reviewer identity and full current head, preserves
  required contexts, and performs a guarded merge commit only when authorized.
- Private owner fixtures cover draft/ready, same-repository/fork, changed head,
  changed base, skipped jobs and staging retargeting without touching protected
  main, real service configuration or deployment.
- Only after verified consumption can the affected development PRs become ready
  lawfully; final origin integration remains its separate release/deploy PR.

## Verification

Current evidence is the actual skipped auto-merge jobs and owner issue #454.
Retain exact caller revisions, base-policy read-back, fixture receipts and
native review records for qualification. A generic GitHub blocker label does
not change a Rheos card. No new operational admission is claimed here.
