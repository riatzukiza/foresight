---
category: "kanban"
labels: "evidence, isolation, security"
parent: "760f7f1e-a086-4e0a-82a5-71d2a761073d"
type: "task"
write-id: "1788047365904-0.n5p2yw44zo7zdd4mci"
title: "Isolate exact-commit evidence execution"
priority: "P1"
status: "todo"
uuid: "c9cfd94c-4c18-4469-acf4-d79fbe44f659"
created_at: "2026-08-29T23:48:38.409Z"
---

# Isolate exact-commit evidence execution

The stronger local-execution threat model and acceptance live in
[Foresight issue #58](https://github.com/open-hax/foresight/issues/58).
The current descriptor checks detect ordinary drift; this card owns isolated
materialization against adversarial pathname replacement.

## Exit

Merge bounded exact-commit worktree, sandbox, or immutable-mount execution with
fail-closed platform behavior, adversarial path/revision regressions, safe
cleanup, exact evidence retention, and independent review.

---
Projected from Foresight #58 as planned strengthening. Preserve the current documented threat boundary until isolated exact-commit execution lands.
---

## Planning refinement — issue58 hostile-runner boundary (2026-10-06)

### Context and admission

[Issue58](https://github.com/open-hax/foresight/issues/58) and its
[Eta/Mu299 adversarial expansion](https://github.com/open-hax/foresight/issues/58#issuecomment-5464548446)
own the stronger guarantee. This refinement retains this task's UUID, original
frontmatter, complete original text and historical comments. Recorded
`incoming → accepted → breakdown → ready → todo` events remain observed history;
this changed proposal still needs native planning review and Rheos admission
before implementation. The installed Rheos1.1.1 read returned `todo`.

### Outcome

Untrusted catalog commands consume the exact reviewed child source while being
unable to modify their executable inputs or the trusted verifier, reviewer,
publication credentials and other jobs' state. Passing observations require
trusted CI attestation and the proved isolation boundary. A fresh same-user
worktree plus clean pre/post snapshots does not establish that guarantee.

### Scope and non-goals

Design immutable exact-object materialization, a separate unprivileged execution
identity/container boundary, trusted evidence admission and bounded cleanup.
Retain Foresight's exact catalog argument vector and direct-child ownership.
Portable `.cljc` law validates supplied identity, capability and evidence facts;
outer adapters establish those facts and implement isolation. An immutable
mount alone does not protect verifier/runner state: every admitted platform must
also prove that boundary. The proposed implementation breakdown and owner seams
are in the [design note](../../notes/design/hostile-runner-execution-boundary.md).

This PR implements no runner, container, UID provisioning, workflow, artifact
protocol, board engine, secret/configuration change, deployment or accepted
common-law lift. It neither closes issue58 nor changes this task's status.
Receipt-helper path confinement in personal Foresight PR13 and foreign review
publisher restoration are separate scopes. Their existence supplies no hostile
execution qualification; their source and ownership stay untouched.

### Acceptance criteria retained from issue58

- Materialize each selected child from its exact catalog/reviewed-root gitlink
  into a fresh isolated worktree, sandbox, or immutable source mount.
- Reject replacement objects, alternate/untrusted object sources, dirty or
  untracked inputs, submodule drift, and target revision mismatch.
- Execute the exact catalog command from the isolated path and retain observed
  source identity.
- Tear down only the exact temporary target, with bounded and recoverable cleanup.
- Add integration regressions for pathname replacement, moved HEAD, dirty source,
  symlinked roots/components, and restored-after-execution swaps.
- Document platform support and fail closed where the isolation primitive is
  unavailable.
- Treat trusted CI attestation as the authority against hostile runner state;
  do not overclaim the local pre/post snapshot model.

### Additional adversarial acceptance

- Index flags (`assume-unchanged`, `skip-worktree`) cannot hide mutated tracked
  bytes/modes. A corrupt/replaced index or failed producer is refused visibly.
- A transient executable-input mutation followed by restoration cannot execute
  substitute bytes or create admitted evidence; containment must hold during
  execution, not only at its endpoints.
- The candidate cannot affect real runner command files/environment, verifier
  binaries/configuration, another job's storage, publication/signing credentials,
  or a shared writable cache. Test attempted writes against disposable canaries.
- Review agents run from an isolated trusted project root with explicitly pinned
  machinery. Candidate agent/plugin/config autoload attempts do not execute.
- Every cross-job artifact has an explicit path allowlist, digest/manifest,
  observed producer revision and fresh destination. Reject preseeded, conflicting,
  missing, symlinked, duplicate or wrong-producer inputs before privileged use.
- The gate runs under a separate unprivileged identity/container with immutable
  executable inputs and credentials/verifier outside it. Unavailable isolation,
  resource/timeout refusal or uncertain cleanup cannot become a passing result.

### Verification and risks

Review the note's complete red/green matrix, ownership seams, proposed estimates
and platform decision before any operational transition or implementation.
Implementation must falsify each criterion at the actual owning host boundary,
retain failure/output/source identity and run applicable portable-law and adapter
checks. This planning PR only verifies native readback, immutable prefixes,
source/refusal evidence and diff hygiene; it supplies no passing sandbox or
Windows/macOS execution evidence. Unsupported platforms fail closed.

The parent task remains open. Proposed smaller implementation units are review
inputs, not newly created cards or admitted estimates. Reviewer agreement, source
integrity and current-head deterministic results remain distinct obligations.
