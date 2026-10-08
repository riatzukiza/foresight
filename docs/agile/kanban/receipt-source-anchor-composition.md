---
uuid: "b44514f0-7ed6-44ae-91bc-ab3a052871f1"
title: "Authenticate receipt stream common prefixes independently of an older diff base"
status: "incoming"
priority: "P1"
points: "3"
labels: "evidence, receipts, confluence, regression, laws"
created_at: "2026-10-08"
---

## Context

The user requires preserving historical receipt bytes and appending corrections
or leaving originals intact. The containing repository supplies attribution.
Qualified [source-stream PR29](https://github.com/riatzukiza/foresight/pull/29)
and [consumer PR27](https://github.com/riatzukiza/foresight/pull/27) delivered
one authenticated imported journal with its original coordinates. Their actual
current-base verification and review remain historical facts; both native
receipt prerequisite cards are Done for that delivered scope.

The next cumulative parent check exposes an uncovered composition boundary.
On exact parent head `e7ce01804633e0c3ddbb82cd444ac9e796b8cf65`, the original
command with actual parent base `96a6dca24cb7a14b041bdd6e3e7922c568238da9`
exits2: `source base is not the proven common byte prefix`. The adapter introduced
by `3d0efb2092e6f8a54964594c16dbc367dfb97470` authenticates the stream's
common prefix against the command's comparison-base bytes. The stream's actual
common commit `1069d4c9bcba29ef55e6c72c7c597de101f0520b` is later than that
comparison base. Both exact prefixes survive in the canonical head: 1069 starts
with96, and e7 starts with1069. Neither journal was damaged.

The [separately captured refusal](../../../.ημ/review-evidence/receipt-source-streams/older-parent-base-refusal-20261008.json)
records stdout90bytes, stderr0bytes, immutable blob hashes and the three prefix
relations. It is an actual command observation, not a new failing feature test,
hosted CI result or provider approval. The first capture's incorrect stderr
assumption failed before a proof write; the later capture preserves that error.

This is one bounded receipt-adapter prerequisite for the existing finite
character loop milestone. Initial Incoming metadata and proposed3points are
manually authored Rheos inputs, without a creation event or readiness claim.
The existing merged sources are prerequisites by artifact identity; do not
introduce a board relationship that the installed Rheos cannot enforce.

## Outcome

The existing admission path verifies a lawful imported stream when its shared
prefix is later than the caller's comparison base, while independently enforcing
the actual comparison-base prefix and the stream's immutable canonical anchor.
Every original, source occurrence, correction and count remains inspectable.

## Scope

- Keep the three existing consumers on `admit-receipt-extension!`: immutable
  verification, promotion readiness and held-ledger admission. Keep the CLI,
  workflow, strict envelope and one direct stream descriptor unchanged.
- Propose authenticating the stream common-prefix bytes against the regular,
  UTF-8/LF-valid canonical ledger blob at the exact immutable anchor. Continue
  proving the same prefix against the canonical bytes being admitted. The
  anchor is actual `--at` for immutable verification/promotion and captured
  committed HEAD for held admission; an uncommitted suffix cannot supply the
  trusted common prefix. This proposal requires reviewed planning before RED.
- Retain the independent existing comparison-base-to-canonical-head exact
  prefix check. Do not select a newer base, reinterpret a failed gate as a pass,
  override `PR_BASE_SHA`, or trust the imported head as canonical authority.
- Reuse immutable Git reads, same-repository ancestry, current-HEAD guards,
  source/delta hash and byte/record validation, original physical coordinates,
  per-stream correction admission and common-view agreement. Preserve duplicate,
  nested-import, ambiguous overlap, invalid UTF-8 and incomplete-line refusals.
- Add narrow positive and negative fixture coverage at the existing adapter
  seams. Keep portable decisions in `foresight.evidence` when a new semantic
  decision is needed; Git and Buffer reads remain outer adapter effects.

## Non-goals

Rewrite, reorder, drop or regenerate old receipts/reflections; add historical
repository fields; change stream identity or correction ordinals; deduplicate
rows; relax ordinary envelope or semantic evidence checks; infer ancestry from
titles or models; add a receipt command, alternate gate, workflow or Rheos
implementation; generalize recursive/cross-repository imports; change actor,
memory, mood, social, runtime or deployment behavior; reopen completed cards by
hand-editing their status; transfer source approvals to the parent.

## Acceptance criteria

1. Retain the actual96/e7 refusal and all three immutable blob hashes. After
   qualified planning and native Ready, a regression fixture with comparison
   base96, common1069 and the delivered one-import ancestry fails before the
   repair for the common-prefix reason. No production bytes change to make RED.
2. After repair, that fixture admits the unchanged e7 canonical275 receipts
   plus imported19, combined294 and11 source-bound corrected views. Source
   original267–277 and correction278 retain exact bytes, line hashes and
   coordinates. The common prefix counts once. Later owned appends increase
   canonical counts only; derived receipt identities stay explicit.
3. The same semantic admission and refusal rules apply to all three consumers.
   Existing immediate-base, `base=head` and ordinary append cases stay valid.
   A held import whose common prefix exists only in staged/uncommitted bytes
   is refused before any gate; no captured HEAD or ledger bytes are changed.
4. A comparison base whose bytes are not a prefix of the candidate remains
   refused. A source common blob not byte-proven against the exact canonical
   anchor remains refused even if it matches the supplied candidate. Missing
   anchor blobs, nonancestor sources and changed held HEAD remain refusals.
   Keep existing malformed descriptors, overlapping deltas, conflicting common
   views, duplicate imports and source framing tests passing.
5. Preserve the original parent command surface. The repaired immutable
   candidate passes this command with the actual comparison-base commit and
   captured full candidate SHA:

   ```bash
   nbb -cp src:scripts scripts/evidence.clj verify-receipts \
     --base 96a6dca24cb7a14b041bdd6e3e7922c568238da9 \
     --at "$(git rev-parse HEAD)"
   ```

   Its actual implementation-PR base/head gate also passes, with separately
   reported counts. All required hosted gates, complete changed-input review,
   native findings settlement and exact-head convergence pass before parent
   integration.

## Verification

Review the anchor-versus-comparison-base distinction, refusal boundaries and
fair3point estimate as planning; use native Rheos for Ready. Then add the
regression/refusal tests RED, repair the owning admission adapter GREEN, and
execute the existing evidence adapter/pure suites and original immutable
receipt command against both actual bindings. Run the existing13 hosted gates
on the implementation head. Archive named failures and actual output channels;
an observation, local fixture or old source approval is not qualification.

## Risks

An anchor read must not accidentally borrow trust from a held suffix or source
branch. Preserving the exact comparison-base check and separate canonical
anchor proof is essential. Wider parent comparisons validate more canonical
appends; their ordinary envelope and evidence failures must remain visible.
The extra immutable read may fail on missing history; refuse explicitly rather
than using the working tree. This repair closes a receipt integration boundary
and does not establish the character's encounter, consumed mood or live recall.
