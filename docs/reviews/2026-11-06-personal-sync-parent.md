# Personal Foresight synchronization parent: review disposition

## Scope and evidence

This record accompanies the repair of [personal PR #3](https://github.com/riatzukiza/foresight/pull/3), the named base branch of 20 open personal PRs in the November 6, 2026 snapshot. It records source findings and changes; it does not resolve hosted review threads, grant an independent approval, or establish child-module execution readiness.

The pinned inputs are PR head `cf57e8988f32ea3a8adc2dfaab5993dd5875c622`, personal main `a8dff342cd69d27d621fe8d2c0b02ba3da624927`, and canonical main `cb73d35b0945e9961745e119dd0c1d9c8dc7dee2`. All 17 observed review threads were open at collection. The older `base.sha` values in the PR queue are API snapshot metadata; the live named branch head is the source for integration.

`clobber/` is identical between the pinned PR head and canonical main. Its [provenance](../../clobber/PROVENANCE.md) records whole-source recovery for inventory and extraction, including known nonworking behavior. The synchronization repair preserves that payload. This disposition does not claim any reported runtime defect is fixed. Execution remains ineligible under the root workspace contract.

## Per-thread disposition

| Finding | Verified concern | Source | Disposition in this repair |
| --- | --- | --- | --- |
| [1](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366274) | RPC schema and examples disagree | `clobber/docs/notes/infrastructure/pm2-clj-dsl-compose-helpers.md` | Preserved recovery material; defect remains open for its executable successor. |
| [2](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366286) | RPC handler assumes Promise | `clobber/docs/notes/infrastructure/pm2-clj-dsl-compose-helpers.md` | Preserved recovery material; defect remains open for its executable successor. |
| [3](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366296) | committed ecosystem export is empty | `clobber/ecosystem.config.cjs` | Preserved recovery material; defect remains open for its executable successor. |
| [4](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366308) | token placeholders are literal | `clobber/ecosystems/stacks/combined.open-skull.pm2.edn` | Preserved recovery material; defect remains open for its executable successor. |
| [5](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366320) | package test scripts reference absent files | `clobber/package.json` | Preserved recovery material; defect remains open for its executable successor. |
| [6](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366336) | extracted notes lack link definitions | `clobber/spec/notes-extracted/infrastructure--pm2-clj-dsl-cli.md` | Unresolved references preserved; exact historical link targets not recovered. |
| [7](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366345) | compiled clobber render cannot evaluate | `clobber/src/clobber/cli.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [8](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366354) | defprofile evaluates body before capture | `clobber/src/clobber/macro.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [9](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366370) | env-var reads JS object incorrectly | `clobber/src/clobber/macro.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [10](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366426) | nested app set/unset drops path | `clobber/src/pm2_clj/cli.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [11](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366440) | nbb failures ignored and ns aliases lost | `clobber/src/pm2_clj/eval.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [12](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366452) | extension dispatch rejects .pm2.clj | `clobber/src/pm2_clj/eval.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [13](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366460) | removal sentinels differ and remove shadows core | `clobber/src/pm2_clj/merge.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [14](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366469) | app merges duplicate names and scramble order | `clobber/src/pm2_clj/merge.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [15](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366481) | export! skips environment defaults | `clobber/src/pm2_clj/runtime.cljs` | Preserved recovery material; defect remains open for its executable successor. |
| [16](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366490) | acceptance omits later review workflow pin | `docs/migrations/eta-mu-breakdown/acceptance.md` | Corrected in acceptance history; preserve PR #121’s original execution scope. |
| [17](https://github.com/riatzukiza/foresight/pull/3#discussion_r4196366496) | README omits clobber from refusal sentence | `README.md` | Corrected the refusal sentence to include all three consolidation roots. |

The source checks confirm unresolved reference usages in the extracted CLI, eval and SCI notes. Exact original targets have not been established; the compose note’s reported labels require a fuller historical-source comparison. No replacement URLs were guessed.

## Preconditions for an executable successor

An executable extraction needs explicit contracts and failing behavioral tests for synchronous and asynchronous RPC results; module exports; environment lookup; multi-form evaluation and subprocess failures; complete app keypaths; deferred profile bodies; stable unique app merges; shared removal sentinels; and environment-sensitive materialization. Restoring package test commands requires their missing test sources. Preserving an archival defect does not satisfy any of those gates.

## Receipt reconciliation

The accepted personal store freezes `.ημ/receipts.edn` and writes one-record segments. The original PR authenticates a canonical stream, an imported stream, and documentary corrections by immutable Git revision, exact bytes and original physical line. The reconciliation preserves the frozen file and migrates PR-only rows byte-identically, reusing an already identical segment. An explicit source anchor retains the original stream’s authenticated coordinates; it does not turn new invalid receipts into historical exceptions. Newly introduced canonical records must pass current validation after any authenticated documentary projection. The same admitted view is required for verification, promotion and working append.

The original PR’s plain prefix verifier fails against the shared `fcfc2d17` base because its fork-origin row was inserted within that stream. The original source remains unchanged in Git; the new segment layout addresses storage reconciliation without reserializing those receipts. Full validation and independent candidate review are recorded separately in the execution run.

## Candidate review findings

The first frozen repair candidate, `39748164665e36f0a720ebd926571dee7fbbd2a0`,
preserves all three input heads. Its independent receipt review and skeptic
identified two compatibility regressions in the integration adapter:

| Finding | Reproduced behavior | Required repair |
| --- | --- | --- |
| R1-RECEIPT-001 | The reader rejected verification of the unchanged original PR head because its historical declarations predate the new binding file. | Reauthenticate unchanged committed historical snapshots without allowing new unbound declarations or working writes. |
| R1-RECEIPT-002 | Source replay applied the new timestamp rule to historical documentary corrections that the original reader accepted. | Retain literal authenticated historical timestamps while validating every genuinely new canonical and imported occurrence under current rules. |

The first round remains a failed result. Its separate integration review and
skeptic found no additional demonstrated defects in ancestry, byte preservation,
project mapping or these thread dispositions. The complete local CLI run had
153 tests and 918 assertions, with zero failures and five errors caused by
unavailable Linux process-custody access. That result is not a full CLI pass.
The subsequent repair and its independently frozen review are recorded as new
attempts; they do not replace the original findings or test receipts.
