# Bound Git capture for receipt-history verification

Status: planning only; implementation requires planning review and a lawful
Rheos ready transition. This specification grants no gate waiver.

## Diagnosis (2026-11-03)

`scripts/evidence.clj` defines `git-capture!` (UTF-8 text) and `git-buffer!`
(raw Buffer) using Node `spawnSync` without `maxBuffer`. The implicit 1 MiB
capture ceiling is smaller than the committed receipt ledger. Both wrappers
throw on the resulting `result.error`; this is correct fail-closed behavior,
not malformed ledger data or an append-only validation failure.

Observed on Node v24.14.1 / NBB v1.3.204:

- Unchanged trusted base `e31b43a57765a394521d6c49aafd068f5c4d5ad5`
  contains a 1,275,072-byte ledger. `verify-receipts --base BASE --at BASE`
  exits 2 with `Git invocation failed: spawnSync git ENOBUFS`.
- Starting worktree HEAD `b9ac3141a9799a06abb2bd0d2c8320e8b8b9ec02`
  contains a 1,280,738-byte ledger. HEAD-to-itself fails identically.
- Direct calls to **both** wrappers with `show BASE:.ημ/receipts.edn` fail
  identically. The binary ledger read is the actual verifier failure path;
  text capture has the same latent transport limit. Current `ls-tree -rz HEAD`
  metadata is only 56,206 bytes; no current metadata overflow is claimed.
- A read-only Node transport probe with explicit 16 MiB `maxBuffer` succeeds
  for HEAD in text and Buffer modes, returning all 1,280,738 bytes. Both match
  the unchanged working file with SHA-256
  `b0cbce2ed8c3184e7ac8131f66f821d03d6bc9ccbb2af5cc2f535aeabb83ce1f`.
  This proves transport feasibility, not a passing receipt-verification gate.

## Proposed minimal change

Define one named finite capture constant in `scripts/evidence.clj`:
`max-git-output-bytes = 16 * 1024 * 1024` (16,777,216 bytes).
Pass it as `:maxBuffer` to **both** Git `spawnSync` option maps. Preserve their
different encodings and existing errors/status handling. Do not change generic
gate-process execution or add a CLI/environment override, retry, alternate
verifier, shell pipeline, streaming reader, or unlimited capture.

This cap gives approximately 13 times the current ledger size without a
size-derived, attacker-controlled allocation. It is an explicit operational
ceiling, not permanent accommodation of arbitrary history growth. Above it,
verification remains blocked for a separately reviewed decision; never migrate
or truncate history to obtain a pass.

Resource boundary: `maxBuffer` bounds captured stdout/stderr in bytes, not total
process RSS, Git object expansion cost, or parsed EDN heap. Conservatively budget
two 16 MiB captured streams per invocation, plus Node chunk overshoot and
decoding/copies. Verification retains two ledger Buffers (up to 32 MiB) and
their parsed records. Text-mode UTF-16 strings and parsing add further bounded
input-derived allocations. This is not a whole-process memory hard limit or a
new bound on reading the working ledger. No unbounded input-derived cap is
permitted. Existing receipt-line limits remain separate and unchanged.

## Regression design (write only after ready)

Use `test/evidence_cli_test.cljs`'s `with-receipt-fixture` and
`with-historical-ledger-fixture`; rebind `cli/root` / `cli/receipt-file` to the
temporary local Git repo as existing tests do. Use local commits with fixture
identity and signing disabled. Do not depend on the real repository ledger's
changing contents or create giant individual receipt lines.

1. Commit a synthetic ledger over 1 MiB but below 16 MiB, comprising many small
   historical EDN maps and including multibyte Unicode. Test both terminal-
   newline cases. Real `git-buffer!`/`read-immutable-receipt-ledger!` must return
   exact Buffer bytes and SHA-256. Base-to-itself must pass with zero appended
   receipts; append one valid current receipt and require count 1 and exact
   historical prefix. A changed prefix, truncation, invalid suffix or missing
   suffix newline must still fail. Exercise secure evidence append on the large
   committed base and assert no historical bytes change.
2. Use `git-capture! ["show" "REV:PATH"]` on the >1 MiB UTF-8 fixture, asserting
   complete text and encoded-byte length. Also capture a real text metadata
   command over 1 MiB (a fixture commit message via `git log -1 --format=%B`,
   supplied through a file rather than argv). This covers the text wrapper
   without misrepresenting today's small tree metadata as the failure source.
3. In a separate non-EDN blob fixture, test raw binary bytes (including NUL and
   non-UTF-8 octets) through `git-buffer!`; compare Buffer equality and hash.
   Never route ledger hashing or immutable-prefix checks through decoded text.
4. Capture a real Git blob exactly at the explicit byte bound and one above it
   (use bound + 64 KiB for robust overflow). Both wrappers must throw on the
   above-bound output, never return partial data or print PASS. Assert that
   transport error, missing exit status and nonzero exit remain failures.
   Exercise large stderr separately using an isolated Git fixture/config or a
   narrow spawn seam; assert both option maps carry the same finite cap and
   every error precedes success handling. No shell execution in production.
5. Use a small temporary cap via `with-redefs` for additional exact-bound,
   bound-plus-one, multibyte-byte-count and stderr cases to keep tests cheap;
   retain a real production-bound overflow integration case. Instrument
   append/gate entry points to prove capture failure causes no receipt write
   or gate execution, and leaves ledger/lock state unchanged.

New large-success tests must fail red with ENOBUFS on the unmodified adapter.
Overflow tests must pass by rejecting output, not by marking unavailable as
success. Never weaken/remove existing secure-append, UTF-8, blob-mode, identity,
catalog, revision, provenance, lock, symlink/hardlink, race or historical-prefix
tests.

## Verification

Run each of these before and after implementation:

```sh
for f in workspace project lineage evidence evidence_cli; do
  nbb -cp src:scripts:test "test/${f}_test.cljs" || exit $?
done
clj-kondo --lint scripts test
nbb -cp src:scripts scripts/project.clj validate
nbb -cp src:scripts scripts/evidence.clj validate
git diff --check
BASE=e31b43a57765a394521d6c49aafd068f5c4d5ad5
nbb -cp src:scripts scripts/evidence.clj verify-receipts --base "$BASE" --at "$BASE"
# After committing an authorized implementation and append-only receipts:
nbb -cp src:scripts scripts/evidence.clj verify-receipts --base "$BASE" --at "$(git rev-parse HEAD)"
```

Planning baseline: workspace 24 tests/122 assertions; project 12/171; lineage
11/58; evidence laws 17/89; evidence CLI 51/258. All pass with zero failures or
errors. Lint: zero errors/warnings. Workspace tests intentionally log missing
command/unsupported fixtures; these are not claims of submodule lint execution.
Both real receipt-history reproductions above exit 2; the gate remains blocked.
No separate root typecheck command is defined in the root NBB configuration.

## Security scope and non-goals

Only the bounded Git capture adapter and its regression tests are proposed.
Keep `--no-replace-objects`, argv invocation with `:shell false`, immutable full
commit IDs, exact regular-blob mode/path checks, strict UTF-8, byte hashes,
append-only history and all receipt validation/secure-append checks unchanged.
No ledger migration, history deletion, gate suppression, second Rheos engine,
Oikos card edits, model/service/deployment work, or credentials/personal data.

## Risks and handoff

The cap still eventually blocks growth and admits larger synchronous captures
than before. Tests must constrain stdout and stderr and distinguish byte counts
from character counts. Review the resource tradeoff explicitly; do not silently
raise the cap later. Planning review, lawful readiness, red/green implementation
and native PR operations remain coordinator-owned.
