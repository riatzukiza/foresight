---
category: "content"
labels: "rheos, markdown, preservation, write-boundary, regression"
parent: "e7cdf5cf-423e-49fc-aca8-6ed6055f1cd5"
type: "story"
write-id: "1791027720794-0.m1g152irubirrhhw63a"
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
---