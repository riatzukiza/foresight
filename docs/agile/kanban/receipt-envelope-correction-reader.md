---
uuid: "707901cb-e498-4ce3-aa1c-245202ced648"
title: "Support bound envelope corrections in immutable receipt verification"
status: "in_progress"
priority: "P1"
points: "3"
labels: "evidence, receipts, compatibility, laws"
created_at: "2026-10-07"
write-id: "1791413899606-0.sayxzmk0izr8z47rum"
---

# Support bound envelope corrections in immutable receipt verification

## Context

The user requires preserving old receipt bytes and appending a correction or
leaving the historical receipt. The containing repository supplies obvious
repository attribution. The actual Foresight envelope is stricter than the
canonical skill's minimum required-key list: it requires string metadata and
vectors of strings for manifest/refs, but does not require a repo field.

[Recall refinement PR27](https://github.com/riatzukiza/foresight/pull/27) preserves
all historical bytes and appends a valid documentary correction at line278.
Its eleven original maps267-277 still fail the existing required gate, exit2.
[Native P1 root4212570105](https://github.com/riatzukiza/foresight/pull/27#discussion_r4212570105)
requires a qualified append-only reader repair before that PR can merge.

The existing strict `receipt-envelope?` belongs to `foresight.evidence`.
`scripts/evidence.clj` calls `require-valid-receipt-records!` from immutable
verification, promotion readiness and held-ledger validation. The current
validator interprets each map independently and has no correction admission.
Shared Receipt River has a different legacy contract; its reader neither folds
this correction nor substitutes for Foresight's evidence contract.

This standalone prerequisite starts from exact qualified planning head
`1069d4c9bcba29ef55e6c72c7c597de101f0520b`. Its active receipt ledger includes
only that preserved prefix and properly shaped new provenance. PR27's malformed
records enter future tests as fixtures, without importing them into this plan's
active ledger or replacing PR27 history. Initial Incoming metadata and proposed
3 points are manually authored first-class inputs, not native Rheos admission.

## Outcome

The existing three Foresight receipt-validation consumers can admit an explicitly
bound, permitted documentary envelope correction while preserving every original
receipt byte, strict ordinary envelopes, semantic evidence validation and the
original epistemic tier. Uncorrected or ambiguously corrected malformed suffixes
remain refused with explicit errors.

The first accepted real case is PR27's frozen 19-record suffix: precisely eleven
bound envelope interpretations become valid; original records, counts, hashes,
facts and failed historical observations remain inspectable.

## Scope

- Keep `receipt-envelope?` strict. Add one portable `.cljc` correction admission
  decision in `foresight.evidence`, consuming verified target facts supplied by
  the existing adapter. Preserve original maps separately from derived envelope
  views and disclose the correction provenance and count.
- Initially permit only the four corrected envelope fields here: `manifest`,
  `refs`, `dod` and `pi`. Exact string-to-singleton-vector wrapping preserves
  manifest/refs contents, including commas; namespace-preserving keyword text
  supplies Pi (namespace/name without the leading colon). For DoD, permit only
  a nonempty vector of nonblank strings joined in original order with the exact
  separator `; `, preserving each element's contents. The supplied replacement
  must equal that result; other element types or transformations are refused.
  No automatic coercion of arbitrary malformed records or semantic fields.
- Extend the existing NBB adapter's immutable blob/line machinery to verify full
  source commit, same-repository ancestry, original line ordinal/origin and
  SHA256 over exact original UTF-8 line bytes including the final LF. Retain all
  existing UTF-8, newline, immutable-source and base-prefix refusals. The source
  must be an ancestor of the exact `--at` commit for immutable verification and
  promotion. For a held working-ledger extension, the anchor is its captured
  committed HEAD; a source descending from that HEAD is refused until committed
  ancestry actually includes it. Recheck that anchor before any gate execution.
- Apply that single admission decision in `verify-receipts!`,
  `promotion-ready-at!` and held-ledger validation. Keep current CLI/workflow
  command surfaces and separate raw receipt totals from corrected views.
- Qualify the correction syntax and compatibility explicitly, including the
  existing documentary `:correction/entries` / `:envelope/corrected-fields` data
  in PR27; a valid outer map alone is not correction admission.

## Non-goals

Rewrite, reorder, remove or replace original receipts; infer context from a model
or guessed role; manufacture repo fields in historical maps; broaden the strict
ordinary envelope; change test outcomes, evidence schema/payload, provider
approval, reviewed revision, owner, timestamps or publication authority; implement
a second receipt CLI, workflow gate or Rheos parser/validator/board writer; define
an accepted common correction protocol for Receipt River or other repositories;
change shared eta-mu's separate missing-repo legacy finding; implement or deploy
the character loop as part of this prerequisite.

## Acceptance criteria

1. A frozen real PR27 base/source/head fixture keeps the exact original prefix
   and all19 suffix records. Existing source refuses the eleven malformed maps
   for the expected reason; the repaired decision admits only the eleven exact
   bound envelope views, preserves their original raw bytes, and reports both.
2. Each correction verifies a full same-repository source commit and preserved
   original line/origin/hash, with the existing including-LF hash convention.
   Missing/unavailable/foreign/non-ancestor sources and wrong line/origin/hash
   fail closed. Source verification stays at the outer Git adapter boundary.
   Fixtures cover a source descending from the base but ancestral to `--at`,
   the same source refused by an older held HEAD, and a changed captured HEAD.
3. Malformed correction entries, forward/self targets, duplicate or conflicting
   target interpretations, unsupported fields and arbitrary replacement values
   are refused explicitly; no order-dependent last-wins behavior. The permitted
   field interpretations preserve the original exact contents as described above.
4. A correction cannot change timestamp, owner, origin, kind, command, outcome,
   revision, schema or evidence payload. An invalid `revision-bound-evidence`
   record remains refused after any permitted envelope correction; no evidence
   or approval is promoted by documentary compatibility.
5. Ordinary valid suffixes still pass. Uncorrected malformed maps, scalar/extra
   forms, unreadable EDN, invalid UTF-8, missing LF, and rewritten/truncated base
   prefixes retain existing refusals. A target hash excluding LF is rejected.
6. All three existing validation consumers agree on the same qualified extension.
   A refused held extension prevents gate execution and leaves ledger bytes
   unchanged. Re-reading a qualified immutable extension is deterministic.
7. The actual real PR27 suffix passes its original required command only after
   the independently qualified repair is integrated by ordinary commits. Native
   current-head checks and complete review inputs remain required there; this
   card, a local pass or parent PR22 approval cannot waive PR27's P1 or CI.

## Verification

Planning review must settle correction grammar, permitted field interpretations,
source binding, the estimate and ownership before native Rheos Ready. After
actual readiness, use the existing pure `test/evidence_test.cljs` and adapter
`test/evidence_cli_test.cljs` fixture seams (`with-historical-ledger-fixture`,
immutable objects, prefix/newline refusal, malformed suffix and held-ledger
no-execution negatives) for RED before domain and adapter GREEN.

Retain frozen base1069, source91a6fc6b27264da4b8c9b8cd7919f153adea0ff2 and
PR27headbdac1ed6470c98128ff367e757132797a39ac436; record original full bytes,
line/hash identities and expected exclusions. Isolate fixture I/O and disposable
Git objects; no historical production ledger is edited to prepare tests.
Run the relevant root law/adapter suites, native `verify-receipts --base ...
--at ...`, diff hygiene and current exact-head hosted evidence/review gates.
The initial planning artifact contained prospective scenarios. Implementation
evidence now retains pure RED49tests/313assertions/224failures/0errors in
`04549da`, and expanded adapter RED59tests/285assertions/17failures/3errors in
`f2e570a`. The three adapter errors were legitimate positive admission calls
throwing the missing correction behavior; an earlier edit parse error was
repaired before that captured RED execution. All original laws remain covered.

Local GREEN passes49puretests/313assertions and64adaptertests/335assertions,
zero failures/errors. Scoped source and complete scripts/test lint pass with
zero warnings/errors using unchanged repository configuration. The GREEN test
calls the implemented law directly, removing only the RED absent-API resolver.
Disposable Git cases verify ancestry, source availability, absolute ordinals,
UTF-8 including-LF hashes, unchanged raw target bytes, semantic evidence refusal,
both older and changed held HEADs, and no execution on refused admission.

The frozen nineteen-record PR27 suffix is retained under
`test/fixtures/receipt-correction/`:60487bytes/SHA256
`52e3dc0e2fa4103c03a688d5c747780ae8d1d0ecd58af3c854e4455e094e6eba`.
Its fixture facts exercise the pure boundary, separately from actual Git proof.
The original `verify-receipts` command against the actual immutable base1069,
source91a6 and headbdac returns exit0 locally, retaining278raw receipts,
19appends and11qualified views. This is preparation, not integration, provider
approval or a replacement for PR27's required hosted gate and P1 settlement.

Native Rheos admitted this card through Ready and In_progress before RED. Its
later In_progress→Review attempt failed: the installed engine invokes
`pnpm build`, but this root has no Node package manifest. The card and event
ledger remained byte-identical; status is still In_progress. This is the existing
upstream Clojure build-gate gap documented in root AGENTS.md. No alternate
board writer, parser, fabricated Node manifest or bypass is introduced here.

## Risks

Malformed corrections could disguise new evidence or inconsistent admission;
permitted fields and original-byte/source binding must stay narrow. Raw lines,
parsed maps and derived views must not be silently substituted. A referenced Git
object may be unavailable on CI; failure must remain visible. Shared Receipt
River's different compatibility/schema semantics need their own qualification
if generalization is proposed. Native Rheos owns status/WIP/build admission;
source inspection or a parent planning PASS does not demonstrate installed Ready.

The subsequent prefix-promotion regression was captured in `4ce3650`:
65adaptertests/339assertions/2failures/0errors. A valid corrected attestation was
lost when its correction became the trusted base, including after an ordinary
append. The repaired adapter reconstructs every declared correction at the
current exact anchor, revalidates source binding and semantics, and leaves
untargeted ordinary prefix records under historical compatibility. Final local
GREEN is66tests/342assertions/0failures/0errors; explicit prefix bad-hash and
invalid-result cases remain refused without changing original bytes.
Native CodeRabbit review5449712860/comment4213133901 independently confirmed
the same defect as Major. This verification is local preparation; settlement
and fresh exact-head hosted review still follow the pushed repair.

## Relation to the character work

This prerequisite unblocks retained recall evidence for the full encounter ->
physical field -> independent persistent mood/attention -> associative graph
recall -> choice -> observed outcome -> memory goal. That goal also retains
social engagement, relationships, continuity, privacy and quality obligations.
All existing character card metadata/status, kernel/runtime/artifact state and
old root/source receipt/reflection appends remain untouched.
