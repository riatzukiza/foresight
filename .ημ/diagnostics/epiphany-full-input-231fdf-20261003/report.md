# Local exact-head qualification of Epiphany supplementary full-diff gate

Signal — Actual committed caller gate execution succeeded in a fresh isolated clone of `231fdf18cd5c34ae0bb1efc1d808d6c7f3edfd0d`, against PR base `643be698ea0d841dd19385506b272872306e456e`, under Node `v22.20.0`. The runner executed the exact freshly fetched eta-mu `b18764d47c0b8da0b2d31f02d8bdd889323ee65c` checkout guard, primary staging, custom-gate harness and summary scripts, with the actual committed caller's `evidence_gates_script`. This is local execution with an explicitly simulated local PR event, not a hosted GitHub check or native model review.

Evidence — Positive summary has `exact_head=true`, `clean_checkout=true`, `diff_stat=0`, `full_review_input=0`, result `success`, zero summary errors. The complete diff is **550,095 bytes**, SHA256 **ff38f8d1ccbc55cc9b8b9ea5531fb8eceee2cda75c17a17017f6aa1947f0ee3a**, byte-identical to independently regenerated raw `git diff --find-renames base...head`. Its metadata binds exact base/head/command/bytes/hash; the checksum and appended context pointer agree. No `.partial` remains after success.

The primary remains **300,062 bytes**: the original 300,000-byte prefix plus the pinned 62-byte notice. It is unchanged before/after the supplementary gate, SHA256 **3629b6a9eb592da36c321b8b49e26282d165f71446407731028b2d8790c57a57**, and matches the complete diff's intended prefix and actual-size notice exactly. This is the measured final committed diff size; the prior working proposal's 546,161 bytes was a different input.

| External case | Guard | Recorded statuses | Summary |
| --- | --- | --- | --- |
| Positive exact clean clone | exit 0 | diff_stat=0; full_review_input=0 | success |
| Tampered primary after valid staging | exit 0 | diff_stat=0; full_review_input=1 | failure |
| Wrong head injected after successful exact-head guard | exit 0 | diff_stat=0; full_review_input=1 | failure |
| Nonexistent 40-hex base injected for diff generation | exit 0 | diff_stat=128; full_review_input=1 | failure |
| Dirty tracked file in separate clone | exit 1 | checkout_guard=1; custom gates not executed | failure |

All three supplementary-gate failures leave no final `pr-full.diff` and no complete-input metadata. Partial intermediate evidence may remain for diagnosis. The wrong-head-after-guard case verifies the supplementary gate's own revision binding; it does not pretend the outer guard admitted an initially wrong head. The dirty case verifies the existing clean guard fails before custom gate execution.

Canonical retention was measured before and after this qualification and remained byte-identical for local `docs/kanban/board.json` and all three ledgers. Independent source inspection verifies the 478,348-byte local ignored/untracked board equals parent and PR-base blob `486f06ea342b645d3609c084e76e39a71c91c011`, while all three ledgers are unchanged across parent/head/working copies. Canonical tracked status stayed clean. No canonical file, index, Git config, review, workflow request, receipt, push or board state was mutated by the qualification. Git global configuration was isolated to an external scratch path.

Frames — This verifies complete input preservation, source/hash binding, primary-bound preservation and failure observability. The existing workflow transports the whole evidence directory, but no GitHub artifact upload was performed locally. The exact unmodified scripts and gate source are retained under `scripts/`; each fresh case retains emitted evidence and logs under `verified-cases/<case>/`.

Countermoves — This is not complete indexed review qualification: pinned Muse still indexes only primary `pr.diff`, supports added RIGHT-side lines, and retains the known quoted Unicode path defect. Supplemental availability does not prove reviewer consumption or solve omitted/deletion-only inline locations. No model call, `review_submit`, submission artifact, native review, approval, round, policy acceptance, board transition or PR merge was executed or inferred.

The first local runner stopped after the correctly failing tampered-primary case because its diagnostic assertion looked at the step console log instead of the pinned internal `deterministic.log`. That runner error is preserved in `run-qualification-initial.py` and its initial case logs. The assertion was corrected to inspect the actual deterministic evidence log, then all five cases were executed in new fresh clones and completed successfully as qualification cases. This was an external harness correction, not a caller/workflow fix.

Next — Use this exact-231 local qualification as input-preservation evidence, then obtain actual hosted and native full-scope results without counting an incomplete approval.

## Retained evidence

- `qualification-results.json`: exact bindings, full/primary hashes, emitted summaries, canonical before/after hashes.
- `qualification-console.log`: compact verified case results.
- `qualification-runner-stderr.log`: empty on the completed qualification.
- `verified-cases/positive/evidence/summary.json`, `pr-full.json`, `pr-full.sha256`, `pr-context.md`, `pr-full.diff`, `pr.diff`, `deterministic.log`: actual successful gate output.
- `verified-cases/{tampered-primary,wrong-head-after-guard,diff-generation-failure,dirty-checkout-guard}/evidence/`: actual negative evidence and explicit statuses.
- `pinned-b187-workflow.yml`, `scripts/`: freshly fetched exact owning workflow and extracted scripts plus committed caller gate.
- `canonical-retention-proof.json`: independent source preservation proof, SHA256 `40976c0c37f38993c1f155eb77c2f14285a015f482536c8e11a93564d72a85ea`.
