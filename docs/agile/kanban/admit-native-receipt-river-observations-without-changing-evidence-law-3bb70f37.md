---
category: "kanban"
labels: "receipt-river, compatibility, provenance, 5sp"
dependency: []
type: "task"
write-id: "1792085180798-0.xrfzkf8i4qi24ohdpiz"
points: "5"
title: "Admit native Receipt River observations without changing evidence law"
priority: "P1"
status: "breakdown"
uuid: "3bf328fd-2f14-4fca-9d6b-9b4e3bb70f37"
created_at: "2026-10-15T17:25:56.815Z"
---

# Admit native Receipt River observations without changing evidence law

## Context

Personal Foresight PR40 at `7f02c7f3b987741eefebe6d35fdf064e5310b927` has a real required-gate failure. Its two immutable new Receipt River version1 observation events, `c89e956a-e3c8-4bed-91db-9ea527ffe000` and `9affc210-96aa-4deb-9768-f36ac208e61f`, pass their canonical package schema but fail Foresight's strict top-level receipt envelope. Their fields are nested under `:event/payload`; manifest/refs remain scalar `"none"`. Unwrapping without coercion also fails the existing vector requirements. This is a consumer-format incompatibility, not a provider failure or missing credential.

Native evidence artifact11990816774 from run38769355693 is37728 bytes, SHA256 `96043f821aeb00a4fa3bd8bf04b928067cd9ef762de39b1490dcafee85275c3a`. Its verified deterministic log136 names both UUIDs; line137 records receipt_history2, the sole nonzero of12 recorded gates. Native review5536745597/root4285779905 and whole-body obligation review-body5536745597 remain genuine unresolved evidence. The accepted308287-byte/197-record prefix is intact in the312973-byte/199-record head. Eleven historical prefix records do not meet the current envelope law and retain deliberate historical compatibility; this story must not retrospectively validate or rewrite them.

This standalone story is five newly discovered points beyond F40's separate rejection-transport3. Broad native Rheos dedup of receipt/native/observation/envelope found only Alpha portable-envelope task3f2c935b, whose actual scope is issue51/Katamorph PortableInteger cross-host recovery. That is not this consumer feature or an evidenced parent. Fork-review-setup remains its separate open fork-protection3 owner. No parent estimate is enlarged or completed.

## Outcome

Foresight can independently recognize a narrow, canonically validated native Receipt River version1 observation as immutable provenance while preserving original bytes, the strict flat receipt contract and every evidence/promotion requirement. Unknown or malformed formats continue to fail. Native observation records provide zero test, review, approval, promotion or deployment evidence.

## Scope

- Preserve `receipt-envelope?`, `evidence-receipt?`, result/catalog/revision/gitlink matching and promotion decisions. Add an explicit pure consumer classification for this additional provenance record shape; do not convert it into a flat evidence receipt.
- Consume only the already-declared `open-hax/receipt-river` revision `7c7c62343fea0241ac545e37f14e57ab344bfa30`, whose canonical validator is `src/cljs/eta_mu/receipt_river/law/receipt.cljs`. Its Git blob `99f480e1c3fc5be282b3d5bb64310eb052ace2b8` matches the actual emitter validator. Delegate its schema validation instead of copying a validator/parser into Foresight. The NBB outer adapter invokes that actual source on original parsed input and composes pure Foresight consumer decisions; the `.cljc` pure layer must not import a Node/cljs runtime or accept caller admission booleans.
- Admit only exact registered event type `:receipt-river/receipt-recorded`, schema `:eta-mu.receipt-river/receipt-recorded` version1, and payload kind `:observation`. Require a complete canonical validation result, typed UUID identity and nonblank producer/subject/payload provenance fields. Retain every raw field and its ordinal/byte identity. Scalar `"none"` stays a string; no silent vector conversion or invented attribution is permitted.
- Reject any declared malformed/unknown event without a flat fallback. Reject evidence-looking native records, including `:evidence/*` at top level or payload, payload origin `revision-bound-evidence`, and test-run/build or other non-observation kinds. Reject ambiguous duplicate native event IDs, including a new ID colliding with prior retained history; existing historical compatibility is otherwise unchanged.
- Make the exact declared public canonical source and explicit isolated classpath available to affected CI consumers. Before qualifying that implementation, review the actual source/checkout/classpath commands, bind them to the reviewed root's unchanged gitlink and fail if missing/mismatched. No floating source, package pin advance, broad recursive checkout, new credential requirement or unavailable-as-pass behavior. Preserve all existing required gates and the receipt_history check.
- Future implementation paths are `src/foresight/evidence.cljc`, `scripts/evidence.clj`, `test/evidence_test.cljs`, `test/evidence_cli_test.cljs`, `.github/workflows/eta-mu-review.yml` and `README.md`. An instruction pointer is allowed only if needed to describe the existing writer/preflight; do not duplicate global process policy. Any broader dependency platform, library-schema change or runtime migration requires a separately reviewed scope/estimate.

## Non-goals

No F40 event/receipt movement, replacement, deletion, top-level lift, history normalization, UUID whitelist, ignore-error switch or altered integration base to grandfather failures. No invalid-record correction/supersession protocol. No native test/build evidence admission, schema or producer semantics change, alternate Receipt River validator, new Rheos parser, App/principal/pin/account/model change, CI-gate waiver, general event-format acceptance or CMS/deployment implementation. This planning card does not make F40 green or settle its review.

## Acceptance criteria

1. The exact two original native observation lines are retained unchanged as a privacy-safe causal fixture with their original UUIDs, source revision and raw byte hashes; a third distinct event is explicitly synthetic. The pre-feature consumer rejects the intended positive case for the actual format reason; the narrow feature then accepts it only after the real canonical validator runs. No mocked universal validation pass satisfies this criterion.
2. Explicit negative controls cover missing/invalid required values, non-UUID/duplicate/colliding IDs, bad timestamps, wrong event type/schema/version/producer, unknown or malformed declared events, ordinary invalid flat suffixes, evidence fields/origin and non-observation kinds. Direct or caller-supplied positive flags cannot bypass canonical validation; malformed declared events cannot fall back to a valid-looking flat wrapper.
3. Strict flat receipt/evidence and all result/catalog/revision/gitlink/promotion controls retain their behavior. Native observations remain a separate provenance classification and cannot satisfy a gate result or add review/approval/round/deployment credit. Original strings and fields remain unchanged; no conversion supplies missing evidence.
4. Whole committed base/head history, exact prefix bytes and all11 historical incompatible records retain current compatibility. Tests cover actual Git-ledger input, invalid new flat/event suffixes, missing/truncated/malformed UTF8 or EDN, changed source pin, unavailable canonical source, ambiguous identity, and the existing secure append/uncertain-write quarantine controls. Existing evidence and promotion failures remain failures.
5. The exact isolated CI source/classpath implementation consumes only declared7c, exposes no signing/deployment secrets, and preserves every existing required gate. Relevant law/adapter/lint suites pass with zero warnings, followed by actual hosted complete-source qualification and review. No local fixture, data-shaped canonical result or mere green check substitutes for the independently reviewed implementation boundary.
6. Separate qualified source adoption precedes any ordinary F40 integration. F40 keeps its entire original failed source/artifact/result and all historical bytes. Only fresh actual gate/full-review evidence can support current-head writer replies for both inline4285779905 and review-body5536745597. Neither a new good receipt nor this card supplies those results.

## Verification

Retain the actual original hosted receipt_history2 as RED; it is not relabeled. Register new positive/negative controls in the existing `test/evidence_test.cljs` and `test/evidence_cli_test.cljs` owners, including outer-adapter integration with the actual canonical package source. Execute existing evidence laws/adapter, workspace/project/lineage/catalog checks, receipt_history and owning lint after the implementation, using the exact classpath/source commands independently reviewed above. Preserve all original record bytes, capture source/pin/process/hash truth and make missing tools or failed gates visible. New required hosted qualification remains separate from this Markdown planning admission.

Planning verification here is limited to fresh native dedup/admission, exact card/event scope, actual status/estimate readback, diff hygiene and append-only custody. No product tests, model invocation, hosted dispatch or format implementation is claimed.

## Dependencies, cost and risks

Five provisional points: canonical dependency/source custody1; pure consumer/no-evidence constraints1; adapter composition1; causal fixture/CI qualification2. This is an additional five-point discovery, not hidden growth of F40's transport3 or fork-review-setup3. No evidenced parent or predecessor was found; omit invented native relationships. F40's separate story `a1cdea55-15fe-4b49-990a-46ed7f19a1a4` requires this accepted compatibility capability before its current historical suffix can qualify. Dependency direction is downstream F40 → this story; never this story → F40. F40 is not in this accepted-base source family, so its native dependency metadata must be linked in its own admitted lane, without importing or editing its unadopted records here.

PR35's separate segmented-ledger work is not format admission and is not silently made a dependency; coordinate exact source if it is accepted first. Its current new-record contract is still strict. Latest Receipt River main747d5030 exposes a portable law but is not this declared pin; no adoption or protected-branch claim follows from its existence.

Use already-supported valid top-level provenance for this story itself: actual nonblank required fields, vector manifest/refs, existing Foresight law preflight and existing secure writer. Do not emit another known-incompatible native-wrapped provenance event. Preserve the old native-SM null-cwd failure; portable candidate0/no-spore reflection is sufficient for this local planning step. Planning review/readiness precedes implementation; no current source readiness, merge or deployment is promised.

---
Local planning admission only: separate P1/5 newly discovered native-observation consumer story, on accepted personal main45e33f93a5680e4c75a3b683f9870ced82a9f18c. Broad native receipt/native/observation/envelope dedup found no equivalent owner or evidenced parent; existing Alpha and fork-review-setup metadata/history remain unchanged. F40 a1cdea55-15fe-4b49-990a-46ed7f19a1a4 is downstream of this capability, not a predecessor, and is absent in this accepted source family; no invalid F40 receipt suffix or unadopted family history is imported. Root-reviewed v2 proposal SHA25658e3784b8d35cc88255c4b6abc55be845c5a7bc117bd06d0e0f6f1c45dc557a3; exact body SHA256d9e629f19e41dcfdde027024ab1e3920303e1a14034c61293fd8cbde419b015d. Declared receipt-river7c canonical validator only, exact isolated CI source/classpath and real causal controls required before implementation. Strict flat/evidence/promotion law and all historical bytes preserved; native observations supply zero evidence credit. Valid flat provenance through existing law/secure writer; portable candidate0/no spore, no retry of old native-SM null-cwd failure. No implementation, readiness, public source/PR/review/model/account/runtime/deployment effect.

---