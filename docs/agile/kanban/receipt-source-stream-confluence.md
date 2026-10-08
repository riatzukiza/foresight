---
uuid: "d67f9876-6941-4bc9-b7cd-90e9b469aa95"
title: "Admit an immutable source receipt stream during branch integration"
status: "done"
priority: "P1"
points: "5"
labels: "evidence, receipts, compatibility, confluence, laws"
write-id: "1791432011087-0.p9k84mb9d6ojs20cncf"
---

## Context

The user requires appending a correction or leaving historical receipts intact.
Containing-repository context supplies obvious repository attribution; this
work does not add fields to historical maps. The merged documentary correction
reader [PR28](https://github.com/riatzukiza/foresight/pull/28) now admits its
intended interpretations, including when a correction becomes trusted history.

Its qualified merge is `b3fffe3bc29cca09b6ff70b41768160b288a12ba`, with parents
`1069d4c9bcba29ef55e6c72c7c597de101f0520b` and
`fb2a2a58176b29419810ee8e7d17c071e7c61d62`. The native merge gate passed with
current MiMo APPROVED5449927329, all13 executed hosted gates and one completed
available-agent code cohort. The old direct Rheos Review refusal is preserved;
the native instructed Testing→Review path succeeded separately.

The prerequisite here is that merged source artifact, verified at this branch's
base. The reader card's later consumer-handoff criterion remains outstanding;
this standalone card does not require its operational Done state. Declaring
that card as a completion predecessor would make its PR27 handoff depend on
this confluence work and create a cycle. Retain the source/card relationship
as a link, without asserting a native board dependency that is not this task's
actual prerequisite.

The actual integration attempt exposes a different missing law. The common
prefix has259 receipts. The merged parent contains five additional receipts;
[recall PR27](https://github.com/riatzukiza/foresight/pull/27) contains nineteen
different appends, including the eleven malformed originals and correction278.
Neither divergent ledger is a prefix of the other. Git merge-tree reports
receipt/reflection conflicts; current-base immutable verification exits2 with
`Receipt River head does not preserve the base bytes as a prefix`.

Flattening the parent first relocates PR27's targets267–277 to272–282 and its
correction278 to283. Flattening PR27 first violates the parent's exact prefix.
Both original journals and their source coordinates must survive. Merely
archiving ignored receipts, renumbering old corrections or selecting the older
base would not deliver admitted provenance.

## Outcome

The existing evidence reader admits one explicitly bound immutable source
stream during this integration. The current canonical ledger remains an exact
append of the actual current base. PR27's full historical blob, nineteen-row
delta and documentary correction remain in their original source coordinates.
All receipts contribute to the same validated derived evidence view, without
rewriting source bytes or silently converting source coordinates into combined
physical line numbers.

## Scope

- Extend `foresight.evidence`'s portable law and the existing NBB admission
  adapter. Reuse the strict envelope, semantic evidence checks, correction law,
  immutable Git reads and captured-HEAD rechecks; keep the three consumers on
  one admission path.
- Propose one strict native-envelope `:receipt-stream-import` record appended
  to the canonical ledger. A closed `:receipt/stream` descriptor identifies
  full source-base/source-head commits and fixed `.ημ/receipts.edn` path.
  Its exact keys are `:source/base`, `:source/head`, `:source/path`,
  `:source/ledger-sha256`, `:source/ledger-bytes`, `:source/ledger-records`,
  `:source/delta-sha256`, `:source/delta-bytes`, `:source/delta-records`.
  Commits are full40hex strings, hashes are64hex, path is that literal string,
  and counts are positive integers. Ledger fields describe the complete source
  head blob; delta fields describe exactly the bytes following the source-base
  prefix. Bytes include LF; records count parsed nonblank maps, not physical
  lines. Repository identity comes from the containing Git repository.
  Missing descriptor on this kind, or descriptor-key presence on another kind,
  is refused. This proposal still requires native review before RED.
- Authenticate both commits as ancestors of the exact immutable anchor (or
  captured committed HEAD for held-ledger admission). Also require source-base
  to be an ancestor of source-head. Verify source-base bytes are a prefix of
  source-head bytes and the actual trusted canonical base bytes, with
  original UTF-8/newline/Git-regular-blob requirements. The source must be an
  actual retained merge parent/ancestor, not an arbitrary external object.
- Validate the source delta and corrections in their own absolute source
  ordinals. Retain original maps/raw bytes separately from derived views,
  source revision/path/line/hash identity and combined order. A stream identity
  is the containing repository, fixed path, source-base/head and full ledger
  hash; occurrence identity adds original physical ordinal and including-LF
  line hash. The correction's original source-revision/ordinal binding remains
  distinct from this stream identity. Apply correction indexing/duplicate laws
  inside each stream before combining views; unrelated equal ordinals cannot
  collide. Count the proven common prefix once and every imported delta receipt.
- Initially admit only one additional direct source stream with no nested
  imports across the whole admitted head, including its trusted prefix. Refuse
  repeated imports, alias imports through another commit with the same ledger,
  ambiguous identities and unsupported descriptors. Outside the byte-proven
  common prefix, a delta row also present byte-identically in the canonical
  journal is ambiguous cross-stream overlap and is refused, never dropped.
  Equal raw rows at different ordinals within one source remain distinct
  occurrences. Do not deduplicate by parsed maps, titles, embeddings or models.
- An eventual ordinary merge into PR27 may retain the parent's canonical
  ledger and append the authenticated import record while retaining PR27's
  complete original immutable journal. Only the qualified reader may make that
  composition count as provenance; ignored archival is insufficient.

## Non-goals

Rewrite, reorder, renumber, regenerate or remove an original source journal;
invent repository fields in historical records; reinterpret observation as
provider approval; modify result/schema/outcome/owner/time/revision fields;
weaken exact current-base prefix validation; add a receipt CLI, alternate gate
or Rheos implementation; define general recursive/cross-repository imports;
repair Receipt River's separate missing-repo compatibility; implement mood,
physical fields, recall authorization or social behavior in this prerequisite.

## Acceptance criteria

1. Freeze the actual common259-row blob, parent264-row blob and PR27's original
   278-row blob plus its source91a6. Record full/delta and including-LF target
   hashes. The current reader refuses the actual divergent integration for the
   observed reason; no original production bytes are changed to make RED.
2. A qualified source import retains the actual current parent's entire byte
   prefix, all nineteen source-delta receipts and eleven corrected source
   views. Correction278 still names its original267–277 source coordinates;
   their source bytes remain exact. Derived ordering is labeled separately.
3. Missing, unavailable, foreign/nonancestor, changed held-HEAD, wrong blob,
   wrong delta/hash/count/path, omitted/reordered source rows, missing LF and
   invalid UTF-8 refuse admission before gate execution or append. An import
   of a descendant unavailable to the held HEAD remains refused.
4. Repeated imports, overlapping non-common source delta, ambiguous original
   identity, extra/malformed descriptor fields and nested imports are refused.
   Neither source precedence nor a last-wins interpretation can erase a row.
5. Existing narrow corrections retain exact interpretation/source checks.
   Invalid semantic evidence remains invalid after import or correction;
   imported review snapshots grant no current approval/round credit. Ordinary
   linear appends and unrelated historical-prefix compatibility remain intact.
6. Immutable verification, promotion and held-ledger admission agree on the
   same source/derived view and explicit physical-versus-imported counts. A
   refused held import executes no gate and leaves all ledger bytes unchanged.
   The minimal frozen one-import fixture has265 canonical receipts,19 imported
   receipts,284 combined originals/views and11 corrected views. Interpretations
   add no receipts; the259 shared records count once. Reauthentication at
   `base=head` and after one ordinary append retains stable imported views
   (then266 canonical/19 imported/285 combined/11 corrected). A later second
   import remains refused; blank-line fixtures preserve physical ordinals while
   reporting parsed receipt counts separately.
7. After independently qualified implementation, ordinary PR27 integration
   retains its full original immutable journal and passes the original hosted
   command against its actual current base/head. Its two P1 findings receive
   verified native settlement and fresh review; local fixtures and PR28's old
   approval cannot substitute for this consumer proof.
   Reflection conflicts are separate integration work. Preserve each reflection
   source and native card/event provenance; receipt import neither recreates
   those events nor claims their conflicts have been resolved.

## Verification

Planning review settles the strict descriptor, stream identity/counting,
compatibility, integration method and fair5point bound before native Rheos
Ready. Initial Incoming metadata is manually authored first-class input; it is
not native admission. Follow existing pure/adapter fixture seams for law RED
before domain/adapters GREEN, then all current required hosted gates/review.

The [retained confluence observation](../../../.ημ/review-evidence/receipt-source-streams/divergent-journals-observation.json)
is an observed Git/command-output transcription, not a formal behavioral RED,
provider approval or completed integration. All source revisions remain pinned.
The later output-channel correction retains the original observation and names
the exact command/cwd and separately captured stdout/stderr. The original tool
result had combined output; the later capture confirms64stdoutbytes and zero
stderr, rather than inferring the channel from source code alone.
Native board transitions use the instructed Testing→Review route after actual
tests; the old direct Node build refusal remains a separate observation.

## Risks

Import descriptors could hide omitted or altered receipts; source proof and
complete accounting must precede derivation. Relative stream order is preserved
without inventing a total historical clock order. Git object availability may
fail on CI and must remain visible. A bounded one-stream protocol must fail
closed for recursive/general graph cases instead of silently broadening scope.
If planning shows this cannot fit5points, split before readiness; do not expand
the implementation to solve adjacent workflow/board infrastructure.

## Relation to the character work

This repairs provenance composition blocking the reviewed recall investigation.
The full goal remains encounter→physical graph/field→independent persistent
mood/attention→associative recall→choice→observed outcome→memory, with outside
input, social engagement, relationships, character continuity and quality. A
receipt compatibility pass does not implement or deploy those behaviors.

---
Consumer handoff AC7 completed: personal PR27 exactd58ced88052470ef231988fea64db88c56023f6c/base6771e5fadbb5835c0c7f6a48e3b00645b1571be8 passed all13executed hosted gates in run37722282062, including original immutable receipt_history;274canonical +19original imported =293combined/11source-bound corrections. Original sourcebdac1ed6470c98128ff367e757132797a39ac436 full journal, correction coordinates and exact parent/source reflection prefixes remain intact. Both native P1 roots4212570105/4212700949 are fixed/resolved. Fresh CodeRabbit full completion6051522099/summary6047891540 and actual MiMo APPROVED5451097136/all103assessed pages/no declared omitted input qualified one available-agent planning cohort and canonical head-guarded gatePASS. Native PR27 merged03:56:09UTC asf525611dde98f63dca9594a741ecae4f96478df4, actualparents6771+d58 and exact reviewed tree. Reader/source PR28 andPR29 retained their separate earlier code qualification; these are actual consumer proof and receipt prerequisite completion, not character implementation/deployment. Native review observations6051825696 preserve prospective REDs, historical scope and UTF8/UTF16 unit correction. Larger encounter/mood/automatic recall slice remainsunfinished; native goalACTIVE/heartbeatPAUSED; no runtime/maker/PM2/cloud changes.
---