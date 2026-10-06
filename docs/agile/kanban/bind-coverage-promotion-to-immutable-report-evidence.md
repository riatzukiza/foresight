---
uuid: "93572f91-a9fc-4555-8dd6-469b180f81d6"
title: "Bind coverage promotion to immutable report evidence"
status: "todo"
priority: "P1"
labels: [""]
created_at: "2026-08-29T18:22:39.638Z"
parent: "4f263499-a765-4b0f-bac0-7f8175672d60"
write-id: "1788036237839-0.o17qadwn6xptjiv6vu"
---

---
GitHub issue https://github.com/open-hax/foresight/issues/59 owns the versioned coverage-attestation contract: immutable report digest and format, measured metrics, repository-owned thresholds and baseline identity, exact gate/child/catalog binding, producer trust, negative tamper tests, and hosted E2E proof. PR #49 must fail closed for automatic :coverage promotion until that contract lands.

Administrative lifecycle repair: the card file was already todo but had no status-change event. The enforced CLI round-trip recorded the intended todo state; this does not claim issue #59 implementation.
---

# Coverage promotion bound to retained evidence — planning refinement

This BODY refines existing task `93572f91-a9fc-4555-8dd6-469b180f81d6`
and [Foresight issue #59](https://github.com/open-hax/foresight/issues/59).
Preserve the original historical comment and every issue acceptance criterion.
The card remains `todo`. This is a planning proposal, not implementation,
assignment, admitted new cards, points/dependency changes, readiness or native
review qualification. Fresh full open-PR inventories and the adjacent PR19/20
bodies/files expose no concrete conflicting coverage-attestation lane; that
limited evidence leaves external ownership unresolved. Recheck overlap and
owner decisions before publication or implementation.

## Context and outcome

The accepted adapter already refuses automatic coverage promotion:
`require-attestable-result` converts an attempted `:coverage` pass to unavailable
while retaining the attempted outcome/exit, and `automatic-promotion-supported?`
rejects coverage at the pure promotion boundary. Preserve both guards until
all issue59 acceptance is satisfied with revision-bound evidence.

A zero command exit and shape-valid result do not retain a coverage report,
prove its measured values, authenticate its producer, or admit a threshold or
baseline. The outcome is a portable coverage-evidence contract plus adapters
that consume independently verified, retained evidence and reject invalid or
unavailable inputs before promotion. Do not erase an attempted pass or turn an
unavailable action into success.

## Original issue acceptance, retained in full

- Define a versioned portable `.cljc` coverage-evidence shape for report identity, raw digest, format, measured metrics, repository-owned thresholds, baseline identity, and target revision.
- Bind the coverage evidence to the exact gate, child commit, catalog identity, and immutable Receipt River record.
- Make the effectful adapter validate the retained artifact or a trusted hosted attestation without trusting caller-supplied paths or digests.
- Specify artifact retention and producer authentication across local and hosted execution; integrate with #57 where the same trusted identity is required.
- Reject missing, malformed, stale, threshold-lowering, regressed, or mismatched reports.
- Add pure decision-table tests, adapter integration tests, tamper/negative tests, and one hosted end-to-end coverage promotion proof.
- Remove PR #49's temporary fail-closed coverage-promotion guard only when all acceptance evidence is revision-bound.

## Evidence meanings and admission boundary

Keep three evidence channels explicit. Machine-readable test-coverage metrics
come from the retained report and its defined format. Native review-provider
coverage identifies observed review scope/commit binding, not code-coverage
metrics or a promotion verdict. The actual admitted review/execution input must
be independently checked against the required source and artifact bytes;
a selected-file count, provider marker, context manifest, matching head or green
job alone does not prove the whole required input was available. An explicit
incomplete-input admission stays incomplete; a later positive claim must
retrieve and review the omitted input rather than relabel it complete.

Keep observed bytes, derived metrics, repository policy and the final admitted
verdict separately inspectable. No provider review coverage marker or claimed
percentage substitutes for missing report bytes, policy, producer authentication
or hosted integration proof. Report retention and reviewer-input completeness
also remain separate obligations; a retained test report does not by itself
qualify a native review or supply completed review rounds.

## Portable shape and pure decision laws

Propose a versioned Clojure-shaped `.cljc` coverage record containing report
identity, raw-byte digest, declared report format/version, measured metric names
and values/units, repository-owned thresholds, baseline identity/revision,
target revision, exact gate ID, child commit, catalog raw-byte identity, exact
command/source, producer attestation reference and immutable Receipt River
record binding. Resolve metric/threshold/baseline semantics through reviewed
repository policy; this plan selects no threshold, allowed regression, format,
signer or trust mechanism.

Pure laws validate the normalized shape, supported format/version, complete
required metrics, valid values/units and defined denominators, exact binding,
threshold and regression decisions. Review explicit equality/boundary cases
for the eventual metric policy. Reject absent/unknown policy, undefined or
incompatible metric/baseline semantics and caller attempts to lower thresholds
or select a different baseline to manufacture a pass. Passing numerical
comparison is insufficient if provenance, retained bytes or producer admission
is absent. Pure code cannot certify network identity, artifact retention,
signature/check verification or a successful physical read.

## Effectful verification, retention and producer authentication

The adapter obtains the retained artifact or trusted hosted attestation through
an independently authorized source. A caller-supplied path, URI, digest,
threshold, baseline or issuer is untrusted input. Resolve the authorized
artifact locator/retention proof, read exact raw bytes when that is the selected
verification route, compute and compare the raw digest independently, parse
only the reviewed format, and validate normalized output at the pure boundary.
A trusted hosted attestation must authenticate the same required tuple and
retention guarantees; a mere hosting URL or successful workflow is insufficient.
An unavailable artifact, verification source or required trust policy fails
closed with a sanitized reason.

Define a reviewed retention policy for both local and hosted reports: immutable
identity/digest, producer and exact run/revision, storage authority, retrievability
for admission and audit, expiry/removal behavior and the reference recorded in
Receipt River. A report that has disappeared, expired or resolves to different
bytes must not continue authorizing promotion. Do not claim a concrete storage
service or retention duration is accepted by this proposal.

Where authenticated producer identity is required, consume the qualified
[issue57](https://github.com/open-hax/foresight/issues/57) contract under task
`f66c6539-d719-48ae-8284-18fd989b31ef`; a planning PR or Git integrity alone
cannot supply that prerequisite. Verify local and hosted producer authority,
repository/PR/head/child/gate/catalog/command/outcome/artifact binding and the
reviewed freshness/revocation policy. If that authenticated boundary is not
available, keep coverage promotion unavailable/blocked. No new trust-root
choice or independent signing/publication implementation belongs to issue59.

Use the qualified [issue58](https://github.com/open-hax/foresight/issues/58)
exact-commit execution boundary where hostile execution isolation is needed,
under task `c9cfd94c-4c18-4469-acf4-d79fbe44f659`. This proposal does not
implement its sandbox or adopt its owner. Keep candidate execution unprivileged,
without signing/deployment credentials; verification policy and privileged
publication remain outside candidate control under the accepted root policy.

## Proposed units for planning review

The following breakdown is provisional under the existing UUID. It creates no
new cards and changes no frontmatter or board state. Confirm ownership,
interfaces, estimates and lawful Rheos readiness before implementation. Split
again if reviewed format diversity, retention or cross-repository integration
makes an adapter exceed its bounded estimate.

| Proposed unit | Points for review | Completion proof |
| --- | --- | --- |
| Portable coverage shape and decision laws | 3 | Versioned fields/bindings and reviewed metric/policy semantics; positive/boundary and missing/malformed/stale/regression/threshold-lowering decision tables, initially red. |
| Retained-artifact/attestation adapter | 5 | Independently verified bytes/digest and format, local/hosted retention, authenticated issue57 boundary where required, immutable receipt and policy binding; adapter integration/tamper tests. |
| Hosted promotion integration and guarded replacement | 5 | Real retained hosted report and authenticated producer admitted through the reader into coverage promotion, adversarial hosted failures, all original criteria revision-bound before replacing either temporary guard. |

The proposed total is 13 points across 3/5/5 units, subject to native planning
review. Portable laws/tests precede green domain code and then effectful
adapters. Issue57 qualification and any required issue58 execution guarantees
are semantic prerequisites, not changed dependency metadata or acceptance
claims. None of these planning units is admitted implementation-ready here.

## Positive and hostile verification matrix

| Case | Required proof/result |
| --- | --- |
| Local positive retained report | Known raw bytes/digest and supported format yield the complete metrics; independently admitted repository threshold/baseline and exact gate/child/catalog/command/receipt tuple match; required producer authority is verified. This adapter proof alone is not the hosted end-to-end criterion. |
| Hosted positive passing promotion | Authentic authorized producer, retained report/attestation, exact reviewed revisions and full required input, matching immutable receipt/policy, passing threshold/regression result admitted through the real promotion reader. Archive native job/check/artifact identities and revision-bound verdict. |
| Threshold boundary and baseline comparison | Reviewed decisions for equality/just-below/just-above and compatible baseline metrics; missing baseline or unsupported units/denominators cannot silently pass. |
| Missing/malformed/unsupported/stale report or missing required metric | Fail closed; empty output, unknown schema, invalid values or unavailable retention never become coverage success. |
| Lower threshold, substitute/regress baseline, or change repository policy without its independently authorized binding | Reject policy substitution/threshold lowering or regression; record the exact mismatch. |
| Mutate retained bytes, swap digest/format/metrics, spoof locator, or resolve expired/missing/different artifact | Reject or remain unavailable; independently verified bytes and trusted retention must control, never caller digest/path alone. |
| Replay across repository, PR, expected/observed head, child commit, gate, catalog raw digest, command/source or immutable receipt | Reject each independently mutated binding, including an otherwise well-formed internally consistent foreign report. |
| Wrong/untrusted producer, missing/revoked/expired attestation or candidate-controlled verifier policy/keys | Fail closed through qualified issue57 verifier; familiar names and a green workflow do not authenticate the producer. |
| Provider claims scope/coverage while required actual review input is missing or truncated | Record incomplete evidence; verify and review recovered input before claiming complete scope. Neither that claim nor a file count substitutes for the test report or native approval. |
| Coverage command exits zero with no qualified report or hosted proof | Retain the existing unavailable attempted-pass result and pure coverage-promotion rejection; no guard bypass. |

Implement pure decision tables, actual adapter integration and tamper/negative
fixtures, plus one real hosted end-to-end promotion proof. Exercise artifact
and authentication failures through the effectful boundary, not only synthetic
maps. A local mock, planned workflow, manifest or successful preparation build
cannot supply the hosted proof. Receipt evidence records exact source/revisions,
format/raw digest, policy/baseline identity, retained artifact reference,
producer/run/check identity, outcome and sanitized failures without credentials.

## Exit, non-goals and qualification holds

Remove or replace both temporary coverage guards only after every original
criterion, local/hosted retention contract, authenticated producer boundary
where required, and full hosted positive/negative integration evidence are
revision-bound and reviewed. Until then retain fail-closed behavior. The
replacement must continue failing closed when any required evidence becomes
unknown/unavailable; landing a portable shape alone does not admit promotion.

This document activates no storage, signer, workflow, publisher, controller,
reviewer, deployment, protection or settings change. It repins no child and
implements no parser, verifier, coverage tool or board operation. Issues57/58
keep their own full scope and qualification; PR20/19 are plans and do not
satisfy those contracts or issue59. Trust selection, repository policy,
retention details and external ownership remain unresolved. Keep the existing
TODO state until qualified planning and lawful Rheos admission permit work.
