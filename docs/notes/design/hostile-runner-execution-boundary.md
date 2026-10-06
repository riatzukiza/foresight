<!-- SPDX-License-Identifier: GPL-3.0-or-later -->
# Hostile-runner execution boundary — issue58 planning proposal

This note refines existing task `c9cfd94c-4c18-4469-acf4-d79fbe44f659` under
parent `760f7f1e-a086-4e0a-82a5-71d2a761073d`. It is proposed design, not a
closed threat boundary, live workflow, platform attestation or implementation
readiness. [Issue58](https://github.com/open-hax/foresight/issues/58) and
[comment5464548446](https://github.com/open-hax/foresight/issues/58#issuecomment-5464548446)
remain the full scope; no criterion is satisfied merely by this document.

## Observed source and ownership

At synchronization base `96a6dca24cb7a14b041bdd6e3e7922c568238da9`:

- `scripts/evidence.clj` owns local orchestration. `spawn-result` invokes the
  catalog command directly in the selected checkout, without a shell.
  `run-local-gate!` checks path/revision/cleanliness before and after that call.
  These checks detect ordinary drift; they cannot observe a restored transient
  substitution or prevent same-user runner-state writes.
- `src/foresight/evidence.cljc` owns portable evidence consistency; supplied
  capability or digest data is not self-authenticating host proof.
  `config/quality-gates.edn` and the reviewed root gitlinks bind commands/source.
  Child roots retain their own package-manager and executable policies.
- `README.md` explicitly records the stronger adversarial boundary as issue58;
  descriptor-bound Receipt River append is a distinct confinement contract.
- `.github/workflows/eta-mu-review.yml` supplies read-only token scopes and pins
  provider `45ec644c2d15ed511e9bc1e797d1b4073b63dbfc`, Muse
  `0b9a91492c8355e6933dc2164d35668cb76d9e60` and agents
  `7fd3252e7663ad5e68be5e90429d126aa66c38c8`.
  Static provider inspection finds exact/clean checks and pinned context, but
  review and App-token publication occur in one job. This is source evidence,
  not a proved hostile-runner split. This planning PR changes none of those files.

Foresight owns root catalog/result admission and the child execution adapter.
Canonical Eta/Mu/Sol/Muse owners retain reusable worker, isolated agent-context
and publication machinery; any change there needs its own reviewed child PR.
Foreign publisher restoration is not this lane's implementation authority.
Clio/Receipt River retain immutable event/receipt authority; Rheos alone owns
board admission and transitions. No alternative engine is proposed.

## Proposed trust boundary and portable facts

The trusted coordinator resolves a reviewed root commit and selected direct
child gitlink, catalog bytes/digest and exact argv from trusted Git objects. It
materializes verified bytes/modes in a new private object store/source tree,
without borrowing mutable object alternates or invoking candidate hooks, filters,
install scripts or agent configuration during preparation. Reject replacement
objects, untrusted object databases/configuration, unresolved source, dirty or
untracked source, hidden tracked changes, executable-mode drift, nested/submodule
revision drift and producer errors. Do not trust `git status` alone. Any Git
materialization/filter policy must be explicit and reviewed for supported repos.

Proposed portable input facts include root/child commit, tree and content/mode
identities; catalog digest and argv; isolated instance/source identity; platform
capabilities and immutable-source proof; verifier/publisher producer revisions;
attempt identity; result and artifact identities; and cleanup/quarantine outcome.
The pure law can reject absent/inconsistent facts and unknown capabilities. It
cannot mint a trusted producer or attest that a mount/container actually isolates.
No new wire schema or API is declared accepted by this plan.

A platform adapter runs the exact argv as a separate unprivileged identity or in
an equivalently proved container boundary, with immutable executable inputs,
private writable outputs/cache/temp, bounded resources and no access to host
runner command files, sockets, sibling workspaces or verifier storage. No Docker
socket, privileged mode, host-control capability or publication/signing secret
may enter the candidate boundary. Read-only tokens, if genuinely required, are
explicit least-privilege inputs rather than ambient inherited credentials.
Command hooks remain untrusted work inside that boundary.

The verifier/reviewer uses immutable pinned machinery from its own trusted root.
Candidate-local `.opencode`, AGENTS, plugins, skills and environment files remain
review data, never autoloaded machinery. Candidate output is untrusted data.
Cross-job transfer uses an explicit allowlist, digest/manifest, exact observed
producer revision and fresh destination. The trusted consumer rejects aliases,
path escape, preseed, extra/duplicate/conflicting files, missing output and
producer/revision mismatch before parsing or publication. Digests prove content
identity; trusted CI producer binding establishes authority.

Publication/signing credentials remain in a separate trusted job/process that
never executes candidate code, loads candidate project configuration or trusts a
candidate-selected publisher path. Admission binds source, catalog, isolated
attempt, complete required gate outcomes and artifacts. Source cleanup alone
cannot authorize publication. This is a required interface, not a replacement
publisher implementation or permission to alter foreign publisher work.

Cleanup targets only the held isolated instance and its exact recorded identity.
A moved/replaced/symlinked cleanup path refuses deletion and records quarantine;
no broad glob, parent-directory deletion or global cache cleanup is admitted.
Retain logs, attempted status, source identities, input/artifact digests and
bounded recovery instructions outside candidate control before teardown.

## Proposed breakdown and platform decision for review

Existing `todo` metadata and earlier ready history remain authoritative. This
expanded refinement needs planning qualification before execution. These units
are proposed estimates and ordering within the existing task, not new cards,
status transitions or accepted dependencies:

| Proposed unit | Points | Outcome and owner |
| --- | ---: | --- |
| Exact source and admission facts | 3 | Foresight pure-law contract and trusted object/materialization adapter; reject unverifiable input before candidate launch. |
| Host execution and recovery proof | 5 | Owning worker adapter proves immutable input, unprivileged isolation, state/resource exclusion and exact cleanup with adversarial integration fixtures. |
| Trusted review/transfer qualification | 5 | Foresight consumption plus separately reviewed canonical worker/publisher seams prove agent isolation and artifact/producer admission through publication. |

The thirteen-point aggregate requires lawful breakdown into reviewed smaller
implementation stories before implementation; do not silently implement it as
one admitted task. Do not create duplicate issue58/umbrella cards. The owning
Rheos operations and reviewed child linkage will author the actual breakdown.
Units build on source admission, then host proof, then end-to-end trusted
consumption. Reference existing adjacent PRs as overlap evidence only, not fake
UUID dependencies or approval grants.

Proposed first supported platform is an isolated Linux CI worker with immutable
inputs and separate identity/container boundary. The actual primitive, image/
tool pins, allowed network/resources, dependency acquisition, availability and
trust roots remain planning decisions requiring review. No local UID changes,
container/service provisioning or host experiment occurs in this PR. Windows,
macOS and any Linux runner lacking the accepted primitive return an explicit
unavailable/refusal outcome. A future platform needs its own proof; worktree
creation or descriptor checks alone cannot qualify it.

## Red/green verification contract

After reviewed planning and lawful Rheos admission, establish RED at the actual
old boundary using only disposable fixtures/canaries; then GREEN at the accepted
boundary. Never target real runner files, another job or real credentials.
Retain actual attempts and distinguish refused, failed, unavailable and passed.

| Case | Required observable proof |
| --- | --- |
| Index flags and corrupt/replaced index | Hidden tracked byte/mode mutation or producer error cannot supply admitted source/evidence. |
| Path replacement, symlink root/component and moved HEAD | Materialization/launch refuses untrusted aliases or the executing immutable source remains exactly bound; no substituted program runs. |
| Dirty/untracked source, replace objects, alternate DB and nested drift | Refusal precedes launch; positive clean exact-source case still runs exact catalog argv. |
| Transient mutate/run/restore | Substitute executable bytes never run; final clean snapshot alone cannot pass this fixture. |
| Runner command-file/env/job-state poisoning | Disposable protected canaries and trusted job environment remain unchanged during/after execution; attempted write is denied or isolated. |
| Agent/plugin/config autoload | Candidate marker/plugin never executes; only pinned trusted machinery loads from the independent root. |
| Artifact preseed/ambiguity | Fresh consumer refuses preseeded, missing, extra, alias, duplicate/conflict, digest or producer mismatch before privileged consumption. |
| Parallel attempts and cancellation | Distinct private identities/stores/cache/output; cancellation/resource failure does not alter sibling evidence or promote a pass. |
| Cleanup replacement or interrupted teardown | Only exact owned target can be removed; uncertain identity is quarantined with recoverable evidence. |
| Unsupported capability/platform | Explicit fail-closed refusal, no downgraded same-user/pre-post substitute. |
| Positive end-to-end case | Exact root/child/catalog/argv/attempt binding, complete required outcomes and artifact manifests survive trusted verification and retained attestation. |

Use portable-law positive/negative tests on available declared runtimes, then
owning adapter integration and isolated CI attestation. Independently review the
new head, all findings and full required-check evidence before qualification.
Planning checks here are source/readback/prefix/hygiene verification only; no
RED exploit, GREEN containment, JVM/sandbox suite or production isolation pass
is claimed.

## Evidence and decision limits

[Planning evidence](../../../.ημ/verification/issue58-hostile-runner-planning-20261006/README.md)
retains exact issue/comment/read-task bytes, hashes, observed scope and prefix
proof. The installed Rheos read reports `todo`; historical events record the
original ready-to-todo path. Neither that history nor this plan admits the
changed design. Missing primitive, trusted publication separation or portable
admission capability remains an owning implementation blocker, not permission
to weaken the threat model. Current observer/producer authentication work in
issue57 and measured coverage in issue59 remain separately owned and open.
