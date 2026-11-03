---
uuid: "bound-git-capture-for-receipt-history-verification"
title: "Bound Git capture for receipt-history verification"
status: "incoming"
type: "task"
priority: "P1"
points: "3"
labels: "evidence, regression, security"
dependency: []
category: "kanban"
write-id: "1793726268098-0.jwyfhddiphfbk4zkk27"
created_at: "2026-11-03T17:17:48.098Z"
---

## Context

The immutable receipt-history gate fails with `spawnSync git ENOBUFS` even when unchanged trusted base `e31b43a57765a394521d6c49aafd068f5c4d5ad5` is verified against itself. Its 1,275,072-byte ledger exceeds Node spawnSync's implicit 1 MiB capture limit. Starting HEAD b9ac3141 has 1,280,738 bytes and fails identically. Both text `git-capture!` and binary `git-buffer!` omit maxBuffer; binary immutable ledger loading is the observed gate failure path.

## Outcome

Restore bounded, exact receipt-history verification for supported ledger sizes without weakening immutable history or security validation. Implementation is blocked until planning review and a lawful Rheos ready transition; this incoming card is not readiness or a gate waiver.

## Scope

- Follow `docs/specs/receipt-git-buffer.md`: one shared finite 16 MiB maxBuffer in both Git capture option maps, keeping text and Buffer behavior distinct.
- Add red/green regressions using existing evidence CLI temporary Git fixtures, including >1 MiB ledgers, text metadata and bounded overflow.
- Explicitly retain failure above the finite capture bound; document resource costs and the eventual growth ceiling.

## Non-goals

No implementation in this planning phase. No unlimited/dynamic capture, retries or fallback verifier, ledger migration, history deletion, gate suppression, replacement Rheos logic, or changes to the 24 Oikos cards. No models, services, deployments, credentials or personal data.

## Acceptance criteria

- [ ] Both wrappers pass the same named finite maxBuffer of 16,777,216 bytes to Git spawnSync with unchanged encoding, `--no-replace-objects`, argv and `shell:false`; every transport error, missing status and nonzero exit still throws.
- [ ] Real >1 MiB fixtures exercise binary immutable-ledger reads and text/metadata capture without ENOBUFS below the bound; returned bytes, multibyte text and SHA-256 are exact. Base-to-itself yields zero appended receipts, a valid append yields one, and secure append preserves every historical byte.
- [ ] Exact-bound fixtures succeed and over-bound stdout/stderr fail closed for both wrappers, including multibyte byte-count cases. No partial result, PASS, receipt append or gate execution may follow capture failure; ledger and lock state remain unchanged. Include a real production-bound overflow test plus cheap reduced-bound edge tests.
- [ ] Prefix rewrites/truncation, malformed/invalid receipt suffixes, missing suffix newlines, invalid UTF-8, wrong blob modes/paths, revision/catalog mismatches, lock/symlink/hardlink and race protections remain rejected by existing tests. Never weaken or delete tests to obtain a pass.
- [ ] New large-success tests first fail with ENOBUFS on unchanged code; after an authorized implementation, all root NBB suites, lint, catalog/project validation and actual receipt-history checks pass for unchanged trusted base and trusted-base-to-committed-candidate. Record real results; no gate waiver.

## Verification

Run `nbb -cp src:scripts:test test/{workspace,project,lineage,evidence,evidence_cli}_test.cljs` separately for each suite; `clj-kondo --lint scripts test`; `nbb -cp src:scripts scripts/project.clj validate`; `nbb -cp src:scripts scripts/evidence.clj validate`; `git diff --check`. Run `nbb -cp src:scripts scripts/evidence.clj verify-receipts --base e31b43a57765a394521d6c49aafd068f5c4d5ad5 --at e31b43a57765a394521d6c49aafd068f5c4d5ad5`, then repeat with the committed implementation SHA as `--at`.

Planning baseline: workspace 24/122, project 12/171, lineage 11/58, evidence 17/89, evidence CLI 51/258; all zero failures/errors. Lint zero errors/warnings. Real immutable base-to-itself and starting-HEAD-to-itself checks each exit 2 with ENOBUFS. Expected unavailable-process fixture logs are not actual submodule lint results. See specification and appended local receipt for diagnosis and exact commands.

## Risks

16 MiB capture admits larger synchronous memory use; budget both captured streams, chunk overshoot, decoding/copies and two retained ledger Buffers plus parsed EDN. This is not a whole-process memory cap. Output above the bound remains an explicit operational blocker, never grounds to truncate history. Exact byte rather than character limits and stderr failures require dedicated tests. Scope is the Git transport adapter only; existing trust, provenance, byte/hash and secure-append authority remains unchanged.
