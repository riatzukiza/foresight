---
category: "content"
labels: "rheos, markdown, preservation, write-boundary, regression"
parent: "e7cdf5cf-423e-49fc-aca8-6ed6055f1cd5"
type: "story"
write-id: "1791047847932-0.6r60v85htl9cmizrph8"
points: "5"
title: "Preserve unrelated Rheos metadata and body through real writes"
priority: "P1"
status: "review"
epic: "e7cdf5cf-423e-49fc-aca8-6ed6055f1cd5"
uuid: "819717c4-735b-4dd5-b5b7-ba860d3942f9"
---

# Preserve unrelated Rheos metadata and body through real writes

## Context

(己, p=0.99) The source probe documented in the
[content-loop brief](../../notes/rheos-markdown-content-loop.md) at Rheos
`11811264a308d406cb612aefa1dad40818675e5e` loses nested/list/multiline
frontmatter and changes fenced-body spacing when adding a priority field.
That probe exercises a pure helper, not an installed CLI, browser or durable
write. Reviewed editing depends on source preservation before it can safely
build on existing writeback.

## Outcome

A maintainer can perform a targeted frontmatter edit through the existing
Rheos writeback function without losing unrelated metadata or body text, and
can independently reread the written fixture and its task metadata.

## Scope

- Implementation owner: `open-hax/rheos`; inspect its current main and applicable
  instructions before choosing the minimal repair. Donor eta-mu compatibility
  is consumed only where its own ownership/consumer contract requires it.
- Repair the shared existing writeback path for a permitted frontmatter edit,
  preserving unrelated source bytes, including nested/unknown YAML fields,
  multiline values, quoting, comments, newline style and fenced delimiters.
- Reuse the canonical parser for task-store metadata reads so preserved valid
  YAML remains readable after reload; keep engine-owned write metadata explicit.
- Repair only asynchronous handling needed to exercise the existing exported
  writeback function in compiled tests.
- Verify a real temporary-file write and independent reread through that
  function, plus invalid-input and no-op behavior at the changed boundaries.

## Non-goals

- No new general-document editor, proposal store or read/discuss/accept UI.
- No production API provider or document-admission runtime integration.
- No root parser/serializer, alternate board mutation protocol or changed FSM.
- No universal claim that CLI/API/MCP/UI, watchers or deployed services work
  because one write path passes.
- General comment serialization, installed transport qualification, durable
  ledger recording and write/event partial-failure recovery are separate work.

## Acceptance criteria

- A fixture with explicit UUID, nested/list/multiline and unknown frontmatter,
  comments, quoting and fenced `---` content demonstrates the current
  preservation failure before the repair; the recorded failure is attributable
  to this behavior rather than missing tools or setup.
- A permitted single-field edit changes only its intended source region and
  explicitly identified Rheos-owned write metadata. Unrelated metadata and body
  bytes remain unchanged, and task UUID remains canonical.
- The canonical metadata reader sees the edited status/fields, including inline
  comments, without a second YAML interpretation in the store.
- The exported writeback function writes an actual fixture file without a
  mocked persistence layer. An independent filesystem reread sees the intended
  content and preserved source; that evidence is not called an installed CLI,
  HTTP, MCP, browser or ledger test.
- Invalid YAML, duplicate keys or unsupported updates are refused before the
  changed write boundary; no-op helper calls preserve their input.
- Relevant compiled parser/writeback tests and lint pass at the reviewed
  revision. Full-suite dependency/tooling failures remain visible and block
  qualification of the broader repository gate.
- Foresight references the reviewed upstream result instead of duplicating the
  implementation; independent review disposition is linked before completion.

## Verification

Use the historical probe as reproduction context, then add a failing fixture
beside Rheos's actual source/write boundary. Record before/after source diffs,
the exact exported-function test/configuration, filesystem reread, semantic
metadata reread and negative/no-op results. Run the owning
repository's required checks and distinguish helper, entry-point, persistence
and transport evidence. Attach exact revisions and review results to the Rheos
PR and the Foresight coordination record.

## Risks

Parsing and serialization may silently normalize source beyond the selected
field. Shared helpers can affect status/comment behavior; general comment
serialization remains outside this repair. File writes and event recording
remain separate failure boundaries requiring later qualification. If the repair
requires a broader authoring model or exceeds five points, split that additional
work rather than expanding this card into the whole content loop.

---
Fresh canonical pr-flow first-five policy requires fixing the verified P3 duplicate YAML conversion rather than retaining the earlier performance deferral. Resume the already implemented source-preservation story for bounded canonical extern reuse of its checked value; preserve alias/cycle/tag refusals and source writes, qualify existing regressions and append evidence. The separate intake performance card remains incoming; no retroactive readiness or measured improvement is claimed. Root coordinates ordinary stack integration and current-head reviews.

REVIEW REPAIR: Rheos 93783d75b0e0752ffa7da6d4d76ca24c4f75047b reuses the once-validated native YAML value inside its private extern adapter, removing the second toJS conversion while preserving cycle/tag checks and Clojure semantic boundaries. Existing focused compiled suite passes 27 tests/140 assertions; full backend suite 166/858; lint zero errors/warnings, nine existing informational findings; compiler zero warnings. This uses the external backend/tool prefix and does not qualify the clean complete manifest/browser/all release targets. Prior P3 deferral is superseded by the actual fix under the current first-five-round policy. Loader visibility remains an open review finding; no broader story completion claimed.

REVIEW RESUMPTION: The remaining source-loader finding is confirmed: a malformed projected card is currently logged and omitted, admitting a successful partial board. Resume a bounded fail-closed repair in existing loader/composition paths: retain refusal path/cause, preserve valid-load shapes and intentional noncard filtering, let existing read surfaces report the failure, and verify real files plus repaired-source recovery. Availability consequence is explicit: one refused projected card rejects that load. No fabricated frontmatter, new diagnostics surface or broader document engine is planned.

QUALIFIED REVIEW REPAIR: red97e0aef5907749ad09456a6eac2b4c19dc2cef67 exposed29 failures/0errors in19tests75assertions; greenfc338625b204b5372e9a1cc3aa61b2ddc5e78315 retains source path/diagnostic/cause and propagates only refused errors through composition. Focused46/215 and full backend169/900 pass; lint zero errors/warnings and CLI release zero compiler warnings. Actual released CLI read-board, snapshot and read-task refuse bad source with exit3, empty stdout and path/reason; syntax/permission repairs restore successful reads. Original bytes unchanged, unrelated fallback/discovery filters retained. One refused candidate now rejects that load. Parent independently inspected code/tests; no material issue. External runtime/bootstrap qualification does not establish clean complete-manifest or browser/all-release behavior.

NEW BODY FINDING: CodeRabbit review5400526798 item907f4c9011758ebe5328c26e identifies numeric YAML label items passed to str/trim. Current fail-closed loader reports that normalization error, but valid numeric label metadata should normalize to strings. Resume bounded actual-file red/green with the exact suggested stringify-before-trim change, retaining nil/blank filtering, deduplication/order and valid-load shapes. Reviewer bootstrap setup is separately an operator prerequisite for completing these reviews, not standalone package CI implementation.

FINAL BOUNDED REPAIRS: numeric-label red e680dd9 reproduced six failures in37tests204assertions; exact suggested green74410c1 stringifies before trim, preserves blanks/dedup/order/labels-over-tags and passes full170/916. Renderer red23d9202 records two actual Marked heading failures while parser compatibility tests pass; greendc8189373a152edcd682779bffbbf02e1f3224de inserts one blank line before the existing closing delimiter. Full171tests921assertions passes, lint/compiler zero errors/warnings. Actual released CLI first/second comment appends and canonical rereads preserve section text/status/priority, render paragraphs and thematic breaks, and retain distinct write IDs plus prior ledger bytes. Parent independently reviewed both minimal code changes, regressions and actual evidence. General comment serialization remains lossy; no broad preservation or clean complete-manifest/browser/all-release qualification asserted. Head86cabb862bb460fc7d6d099dd231246d9299727c also installs the independently checked native reviewer caller and existing App configuration, an operational review prerequisite; this does not implement standalone package CI.

REVIEW RESUMPTION: native full review5400841626 on source86cabb8 identifies two bounded first-five source-load defects: unterminated frontmatter is misclassified as raw Markdown, and composition drops the configured card-projection when passing onlytasks-dir. Confirm both againstcurrentcode; red/green repairs stay within source parsing/loader criteria and do not expand into CMS/provider/broad diagnostic behavior. Existing receipt/root admission blockers and configured-review quota remain open.

QUALIFIED CURRENT REPAIRS: source head77510d8a14c89e53124e468a70b5be386c54a477 is published. Red f364ec25 and f4de6b3a reproduced unterminated-frontmatter/projection and complex-header/title-shape defects; green c69da36b and77510d8 repair existing parser/composition, preserve the original YAML header on comment append, and reject present nonstring titles through a portable .cljc law before projection/write. Fresh exact-head complete-manifest checkout with no preexisting dependencies or NODE_PATH passed177tests1029assertions, zero-error/warning lint and all four release targets with zero compiler warnings; tracked source/config/test hashes stayed unchanged. Actual released CLI and JVM law probes separately pass targeted refusal/preservation/recovery cases. General body serialization remains lossy; frozen install, hosted package CI and runtime transport integration remain unqualified. Native reviewed findings are replied/settled at actual commits; fresh native approvals, root receipt admission, Muse review publication and Codex quota remain outstanding. Move this bounded implementation back through testing to review, not done.

NATIVE REVIEW GATE RESUMPTION: CodeRabbit fullreview5401052281 at775 confirms the new caller overrides deterministic validation with diff_stat only. Local source green does not establish hosted gate qualification. Resume bounded correction of the existing native-review prerequisite: enable pinned upstream toolchain, record actual install/bootstrap/test/lint/all-release exits, preserve prerequisites/failure visibility and exact-head tracked-clean guard, and exclude only anchored untracked build outputs within the ephemeral checkout. No standalone frozen-lockfile workflow, readiness grant, hosting or board engine changes. Current source177/1029 qualification remains historical until changed-caller hosted checks actually run.

HOSTED NATIVE VALIDATION REPAIR QUALIFIED: caller3bc5df2d591a6976a627fe20c806310ebfaf92da is published and actualrun37128289422/artifact11276335238 reports semantic success, all9gatesattempted0, expected/executed/completion3bc and cleantrue. ActualNode22.23.3/pnpm10.14/TemurinJava21.0.12.1/kondo2025.10.23 installwithoutNODE_PATHorlockfile, pinned21protocols/10chat-sourcefiles, tests177/1029, lint0errors0warnings, all4release0compilerwarnings and nonemptyoutputs pass. Parent inspected actualsummary; archiveZIPmatchesGitHubdigest. Exactpinnedhelper localfault probes separately reject tool/install/bootstrap/test/build failures and preserve tracked/unexpectedfile clean refusal. SourceCodeRabbitP1 nowfixedwithactualhostedevidence. MiMo/finalreviewpending, prior775approvalhistorical; unlockedvalidationdoesnotqualifyfrozenstandaloneCI. Returnboundedstorythrough testingto review, notdone.

CURRENT REVIEW RESUMPTION: native MiMo review5401245276 on3bc confirms the new shape.content-parser dependency on extern.yaml violates the declared construction-order boundary; nine lint infos include this one new finding and eight unchanged infos. Hosted177tests1029assertions/all4releases remain valid evidence for3bc but do not settle architectural placement. Resume a bounded pure-decision/YAML-adapter composition repair without weakening the lint policy or duplicating board semantics. Separately verify the reviewer cyclic-set question before promoting it to a defect. New code will need fresh full-suite, lint, release and native review qualification; no done/acceptance claim.

Published source repair e37dd504da144c35148965e129438cb9c3c1b8e8 after cycle red27b39e9. Pure source framing/range patching and task-edit plans now use .cljc; YAML native conversion remains at extern/infra. A later independent peer found direct pure key admission bypass in the initial split; checked-updates now supplies the single admission law, with separate JVM/NBB red and green probes. Scalar-title edits verify decoded old value, raw requested new value, one event and written write-id. Final prepared local suite181tests1075assertions twice, four builds and lint0errors0warnings8unchangedinfos pass. Exact hosted run37134206738/artifact11277816398 independently reports clean head e37, all9 attempted gates0/errors empty, complete declared unlocked install and pinned source bootstrap. Native Map/Set cycle refusal includes valid ordered-map self-cycle and shared acyclic aliases. Fresh CodeRabbit request5970719523 and native MiMo execution remain separate review evidence; no completion, approval, frozen-CI or runtime transport claim.

Current native MiMo review5401618354 requests changes at sourcee37 after reviewing all27changed files. It raises create serializer quote/backslash poisoning of strict loads and unnamed collection priority/status refusal. Independent isolated reproductions are underway; reported deterministic gate names in its prose are inaccurate and do not replace the retained actual nine-gate hosted artifact. CodeRabbit current no-actionable coverage remains separate. Returning this story to implementation for verified first-five findings; no approval, configured-round completion or HTTP contract decision is asserted.

Published ordinary creation/core-field repair e634b40da8e5da7e1364138e703e052ca4f5b47b after tests-only red cde7e49, first green3b94d0e, supplemental persisted-surrogate red5eaeb4a and final green. Exact final local186tests1176assertions twice, JVM/NBB4/21, lint0errors0warnings8baselineinfos, all4zero-warning releases and eight compiled create/reopen/next-create title cases pass. Body byte losslessness and HTTP status changes are not claimed. Current source run37139221794 and CI-plan run37139274990 are pending; prior head reviews are historical. Native MiMo5401618354 confirmed two defects now repaired. Root review publication separately fails on Git-quoted Unicode paths; existing owning MusePR18 supplies an unmerged candidate under independent qualification. No historical receipt rewrite or review gate waiver; story remains in_progress pending hosted qualification.

Independent exact-clean hosted qualification now passes sourcee634b40 in run37139221794/artifact11279822952 and CI-plan4a38902 in run37139274990/artifact11279188999. All9gates attempted0/errors empty; complete221-package unlocked install, pinned21protocol/10chat source bootstrap,186tests1176assertions twice, lint0errors0warnings8baselineinfos, all4zero-warning releases/nonempty. Source attempt1 failed only the separate Muse context job on Maven403 before model execution; a single failed-jobs-only retry succeeded in context compilation and now awaits native review, reusing the original deterministic artifact. Ordinary source commits preserve actual red/green contexts; no body losslessness or HTTP status change is claimed. Both MiMo5401618354 confirmed findings have verified published repairs; new native approval and full configured review rounds remain pending. Foresight caller will select independently qualified unmerged Muse19456bb4 PR18 via supported muse_revision input, preserving strict publisher filename checks and all13original deterministic gate script bytes. No accepted-law promotion or owner-card readiness claim.

---