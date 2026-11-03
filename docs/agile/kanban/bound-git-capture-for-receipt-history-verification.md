---
category: "kanban"
labels: "evidence, regression, security"
dependency: []
type: "task"
write-id: "1793731241611-0.vesqcgprlzrhgim76xc"
points: "3"
title: "Bound Git capture for receipt-history verification"
priority: "P1"
status: "done"
uuid: "bound-git-capture-for-receipt-history-verification"
created_at: "2026-11-03T17:17:48.098Z"
---

## Context

The immutable receipt-history gate fails with `spawnSync git ENOBUFS` even when unchanged trusted base `e31b43a57765a394521d6c49aafd068f5c4d5ad5` is verified against itself. Its 1,275,072-byte ledger exceeds Node spawnSync's implicit 1 MiB capture limit. Starting HEAD b9ac3141 has 1,280,738 bytes and fails identically. Both text `git-capture!` and binary `git-buffer!` omit maxBuffer; binary immutable ledger loading is the observed gate failure path.

## Outcome

Restore bounded, exact receipt-history verification for supported ledger sizes without weakening immutable history or security validation. Implementation is blocked until planning review and a lawful Rheos ready transition; this incoming card is not readiness or a gate waiver.

## Scope

- Follow `docs/specs/receipt-git-buffer.md`: one shared finite 16 MiB maxBuffer in both Git capture option maps, keeping text and Buffer behavior distinct.
- Add red/green regressions using existing evidence CLI temporary Git fixtures, including >1 MiB ledgers, text metadata and bounded overflow.
- After admission, move committed-ledger capture into the target-owned try in `open-append-reservation!` (or an equivalent narrow ownership scope) so every post-open/pre-reservation error closes the owned target exactly once. Preserve outer lock/parent cleanup, cleanup-error behavior, successful ownership transfer and unverified-write quarantine. Native CodeRabbit PR #52 P2 thread `PRRT_kwDOU4VJMM6zQFST`, comment `4458230282`, identifies capture at lines 819–821 outside the target cleanup try after target open at 817–818.
- Explicitly retain failure above the finite capture bound; document resource costs and the eventual growth ceiling.

## Non-goals

No implementation in this planning phase. No unlimited/dynamic capture, retries or fallback verifier, ledger migration, history deletion, gate suppression, replacement Rheos logic, or changes to the 24 Oikos cards. No models, services, deployments, credentials or personal data.

## Acceptance criteria

- [x] Both wrappers pass the same named finite maxBuffer of 16,777,216 bytes to Git spawnSync with unchanged encoding, `--no-replace-objects`, argv and `shell:false`; every transport error, missing status and nonzero exit still throws.
- [x] Real >1 MiB fixtures exercise binary immutable-ledger reads and text/metadata capture without ENOBUFS below the bound; returned bytes, multibyte text and SHA-256 are exact. Base-to-itself yields zero appended receipts, a valid append yields one, and secure append preserves every historical byte.
- [x] Exact-bound fixtures succeed and over-bound stdout/stderr fail closed for both wrappers, including multibyte byte-count cases. No partial result, PASS, receipt append or gate execution may follow capture failure; ledger and lock state remain unchanged. Include a real production-bound overflow test plus cheap reduced-bound edge tests.
- [x] A red failure-injection test throws from committed capture after target open and asserts target fd closed exactly once, no partial reservation, no append/gate execution, ledger bytes unchanged, lock cleanup and parent fd closed. Repeated failures in the same process produce no fd growth. A separate target-open failure closes no unowned target fd. Existing held-ledger validation failure, cleanup-error propagation and unverified-write quarantine behavior remain unchanged. Buffer growth alone must not stand in for this ownership repair.
- [x] Prefix rewrites/truncation, malformed/invalid receipt suffixes, missing suffix newlines, invalid UTF-8, wrong blob modes/paths, revision/catalog mismatches, lock/symlink/hardlink and race protections remain rejected by existing tests. Never weaken or delete tests to obtain a pass.
- [x] New large-success tests first fail with ENOBUFS on unchanged code; after an authorized implementation, all root NBB suites, lint, catalog/project validation and actual receipt-history checks pass for unchanged trusted base and trusted-base-to-committed-candidate. Record real results; no gate waiver.

## Verification

Run `nbb -cp src:scripts:test test/{workspace,project,lineage,evidence,evidence_cli}_test.cljs` separately for each suite; `clj-kondo --lint scripts test`; `nbb -cp src:scripts scripts/project.clj validate`; `nbb -cp src:scripts scripts/evidence.clj validate`; `git diff --check`. Run `nbb -cp src:scripts scripts/evidence.clj verify-receipts --base e31b43a57765a394521d6c49aafd068f5c4d5ad5 --at e31b43a57765a394521d6c49aafd068f5c4d5ad5`, then repeat with the committed implementation SHA as `--at`.

Planning baseline: workspace 24/122, project 12/171, lineage 11/58, evidence 17/89, evidence CLI 51/258; all zero failures/errors. Lint zero errors/warnings. Real immutable base-to-itself and starting-HEAD-to-itself checks each exit 2 with ENOBUFS. Expected unavailable-process fixture logs are not actual submodule lint results. See specification and appended local receipt for diagnosis and exact commands.

### Completed qualification: source promotion, not original-ledger admission

The coordinator reports independent exact-blob code approval with no findings,
123 tests/833 assertions and 52 adversarial checks, plus separate clean
integration approval at `88b2b184ac1f8a73350e087e3f8e5eb796c2dbf2`.
[Source-only promotion PR #53](https://github.com/riatzukiza/foresight/pull/53)
merged at 2026-11-03T18:38:08Z as
`542ec7b12bc275661ce1a6c2dda009d621e5706f` (M), with parents
`d25d4e7a926eddddafeaaf227ce1c592581110fe` and `88b2b184…`.
Local Git inspection confirms M's tree equals the integration tree and both
source blobs equal the frozen GREEN originals:
`cddfd35d9257d74a36bd632728a53605fda1ecc3` (adapter) and
`f83bc889260886120f4f82850ed0257b842e807f` (tests).
Actual strict `verify-receipts --base e31b43a… --at M` passes with 557 receipts
and 10 appended receipts. This clean promoted candidate satisfies the final AC.

The original repair history at `036cfd03a58314b2672632057820f3bd42214729`
still fails the same strict base-to-head check on three malformed receipt
envelopes. They remain byte-for-byte unchanged; no original-history pass,
validator exception, ledger rewrite or native review approval is claimed.
The coordinator reports the original archived remotely at
`evidence/receipt-git-buffer-20261103@036cfd03`, with its full 1,289,238-byte
ledger independently byte-matched. Completion here tracks the qualified runtime
source promotion only and completes no Oikos epic.

## Risks

Node 24 synchronous maxBuffer counts combined stdout plus stderr bytes against one 16 MiB ceiling, not two independent allowances. Retain a conservative 32 MiB per-invocation RSS planning allowance for capture/copies, plus chunk overshoot, decoding and other overhead; this is not an enforced whole-process memory cap. Two retained ledger Buffers plus parsed EDN add memory separately. Output above the bound remains an explicit operational blocker, never grounds to truncate history. Exact byte rather than character limits, combined-stream overflow and stderr failures require dedicated tests. Scope includes only the Git transport adapter and narrow pre-reservation target-fd ownership repair; existing trust, provenance, byte/hash, lock/parent cleanup and secure-append quarantine authority remains unchanged.

---
Planning-only response to native CodeRabbit PR52 P2 thread PRRT_kwDOU4VJMM6zQFST/comment4458230282: inspected open-append-reservation! lines801-844 and confirmed target opens817-818 before committed capture819-821, outside target-owned try822. Spec and incoming card now commit to a narrow future ownership repair placing capture inside that try (or equivalent): close owned target exactly once on every post-open/pre-reservation failure; preserve outer lock/parent cleanup, existing cleanup-error handling and unverified-write quarantine. Require independent red capture-throw test asserting no partial reservation/append/gate, unchanged ledger, target closed once, lock cleaned and parent closed; repeated same-process failures show no fd growth. Target-open failure must not close unowned fd; existing validation-failure and cleanup behavior retained. Corrected resource model: Node24 synchronous maxBuffer counts combined stdout+stderr against one16MiB cap, not two independent allowances; conservative32MiB RSS planning allowance plus overhead is not a hard limit. Combined-stream overflow tests required. No scripts/test edits, implementation, status transition or gate waiver; card remains incoming pending coordinator admission.

User now authorizes local implementation using completed independent planning reviews, including reviewed P2 ownership correction; no waiting for stale hosted planning cohorts and no fabricated native approvals. This is user direction/local review evidence, not hosted approval or merge permission. Direct incoming-to-todo attempt refused by installed Promethean FSM (exit3: No transition from incoming to todo). Inspected installed FSM; walking its lawful accepted -> breakdown -> ready -> todo -> in_progress path without force/status edits. Implementation remains restricted to approved capture/fd cleanup plus regressions; no commit/push/merge/PR publication authorized.

Local implementation ready for independent adversarial code review; remains in_progress and uncommitted. RED on unchanged adapter:57tests312assertions17failures4errors, including four ENOBUFS errors and eight leaked targetfds (22->30); test reclaimed only owned leakedfds after assertion. GREEN:59tests393assertions0failures0errors. Shared finite16MiB maxBuffer added to both wrappers; committed capture moved into existing target-owned try via held-ledger validation argument, preserving cleanup handlers. Coverage:large historical ledger both newline cases, secure append/exacthash/prefix, mutation/truncation/invalidsuffix rejection, real16MiB boundary/overflow, binaryoctets/largeUTF8metadata, stdout/stderr/combined/multibyte limits, missingstatus/nonzero/ENOENT, no effects on overflow, ownedtargetclosedonce/unownednever/eightfailuresnofdgrowth. Other suites workspace24/122 project12/171 lineage11/58 evidence17/89 pass;lint0/0;project/catalogvalidatePASS;diffcheckPASS. Actual e31b-to-itself and ownHEAD4deeab6-to-itself eachPASS. Additional e31b-to-ownHEAD fails closed on two prior committed planning receipts with string manifest/refs; rr-append helper also emitted the same malformed shape for this turn RED receipt. No historical/uncommitted ledger bytes rewritten and no validator weakened; coordinator must retain this separate provenance blocker. No commits, merge, push or sourcePR publication performed; no nativeapproval claimed.

Completion scope is qualified exact-source promotion, not original repair-history admission. Coordinator reports independent exact-blob code approval/no findings (123tests833assertions plus52adversarialchecks), separate clean integration approval88b2b184, and PR53 https://github.com/riatzukiza/foresight/pull/53 merged2026-11-03T18:38:08Z as542ec7b12bc275661ce1a6c2dda009d621e5706f. Locally verified M parents d25d4e7a926eddddafeaaf227ce1c592581110fe +88b2b184ac1f8a73350e087e3f8e5eb796c2dbf2, M tree exactly88 tree, frozen adapter/test blobs unchanged, actual e31b-to-M strictPASS557receipts10appended. Completion rerun full123tests833assertions0failures0errors;lint0/0;project/catalogvalidatePASS. Original e31b-to-036 still actually fails3malformedreceipts; preserved unchanged, no waiver/rewrite/claimed pass. Coordinator reports remote archive evidence/receipt-git-buffer-20261103@036cfd03 with byteequal1289238byte ledger. AC boxes checked only against actual tests and qualified clean promotedcandidate; prior metadata/comment history retained. Walking officialFSM testing->review->document->done under user completed-local-review admission; no fake nativeapprovals/hostedqueuewait. No sourcechanges, merge/rebase/branchswitch/push and no Oikos completion.

---