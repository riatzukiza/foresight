---
category: "kanban"
labels: "evidence, attestation, security"
parent: "760f7f1e-a086-4e0a-82a5-71d2a761073d"
type: "task"
write-id: "1788047365221-0.ya4sutlmxn5uax6u2f"
title: "Authenticate promotion evidence"
priority: "P1"
status: "todo"
uuid: "f66c6539-d719-48ae-8284-18fd989b31ef"
created_at: "2026-08-29T23:48:37.864Z"
---

# Authenticate promotion evidence

The trusted-producer attestation contract lives in
[Foresight issue #57](https://github.com/open-hax/foresight/issues/57).
Git content integrity and review authorization remain distinct from authenticated
CI producer identity.

## Exit

Merge a portable attestation contract plus an effectful verifier with replay,
mutation, identity-mismatch, missing-attestation, and green-path integration
proof bound to exact repositories, revisions, gates, catalogs, and artifacts.

---
Projected from Foresight #57 as planned strengthening. Git integrity remains valid but must not be described as authenticated producer identity.
---

## Planning refinement — producer attestation boundary

This is an isolated planning refinement of the existing task and issue #57.
The original exit and every upstream acceptance criterion remain requirements.
The card remains `todo`; this text does not admit implementation, assign an
owner, or establish planning qualification. External worker ownership remains
unresolved: a fresh open-PR body search found no exact task UUID, issue URL, or
named outcome match, which cannot establish the absence of external work.
Recheck source and owner overlap before publication or implementation.

### Context and outcome

The accepted revision-bound reader proves reviewed Git content integrity and
consistency; `promotion-evidence-consistent?` explicitly receives records
already authenticated by an effectful boundary. Extend the evidence boundary
so an authorized producer can attest the exact result tuple. A reviewed commit,
a green check, a familiar workflow name, candidate-controlled configuration or
keys, and this planning document alone must never become producer authority.

### Original issue acceptance, retained in full

- Define the trust root for an authorized GitHub workflow/check identity or DSSE signer.
- Bind the attestation to repository, pull request, expected and observed head SHA, child gitlink SHA, gate ID, catalog raw-byte digest, exact command/source, outcome, and evidence artifact digest.
- Require the exact expected check context to be emitted on the exact reviewed SHA; a similarly named or merge-ref-only status is insufficient.
- Reject replay across repository, PR, head, child revision, gate, catalog, or command.
- Reject absent, malformed, expired/revoked, or untrusted attestations fail-closed.
- Keep portable consistency laws pure; signature/check verification remains in an effectful adapter.
- Add green-path integration coverage and negative tests for identity mismatch, payload mutation, replay, and missing attestation.
- Record sanitized Receipt River evidence without credentials or private key material.

### Scope and unresolved trust decision

Review and document the applicable authorized workflow/check identity or DSSE
signer trust root before implementing its verifier. Record the decision's
independent authority, permitted issuer/principal, exact context and workflow
or signer binding, verification-policy version, freshness, expiry and
revocation rules, and the provenance of that policy. The choice between the
issue's supported alternatives is unresolved here; no preferred mechanism,
new signer, App allowlist, publisher, or protection change is authorized by
this plan. If trustworthy policy or an authentic producer cannot be verified,
return unavailable/blocked evidence and refuse authenticated promotion.

The accepted promotion policy requires privileged admission/publication to
use a trusted external App or independently pinned base workflow that never
executes candidate code. Candidate workflows, builds and dependency hooks
remain read-only and receive no signing/deployment secrets. Candidate-controlled
code, keys, gate implementation/configuration, or claimed issuer metadata
cannot define the policy that authenticates its own result. A verifier must
resolve that authority independently, not trust fields merely because the
payload declares them. Preserve these restrictions whichever trust mechanism
is eventually reviewed.

Keep an immutable, versioned Clojure-shaped payload and portable `.cljc` laws
for required fields, revision binding, payload consistency and replay rejection.
Keep signature/check retrieval, issuer verification, time/revocation lookup,
and artifact-byte reads in the effectful adapter. Validate untrusted inputs at
that boundary before passing authenticated records into the pure consistency
law; a pure predicate cannot certify that a signature, remote identity, or
artifact fetch was actually verified. Retain structured sanitized failure
reasons so unknown trust, unavailable evidence and failed gates remain visible.

### Proposed reviewed breakdown

These are provisional planning units under the existing UUID, not new admitted
cards or changed frontmatter/points/dependencies. Review the estimates and
ownership before any Rheos authoring or transition; split further if the
selected mechanism materially expands the verifier boundary.

| Proposed unit | Points for review | Completion proof |
| --- | --- | --- |
| Portable attestation shape and consistency laws | 3 | Versioned exact binding tuple; red laws for absent/malformed fields, independently mutated bindings and replay; pure layer has no signature/network/filesystem authority. |
| Effectful producer verifier and trust-policy boundary | 5 | Reviewed trust-root decision and independently obtained policy; issuer/context/revision, freshness/revocation and artifact-byte verification; unknown or unavailable trust fails closed; no candidate signing secrets. |
| Integrated producer proof and adversarial fixtures | 5 | A real authorized producer's positive result admitted through the verifier into the accepted reader, plus the rejection matrix and sanitized evidence; local synthetic passes never substitute for this producer proof. |

The provisional total is 13 points across 3/5/5 units. No unit is implementation
ready until planning qualification, owner/overlap resolution, the reviewed trust
decision needed by that unit, and lawful Rheos readiness. Portable red fixtures
precede green domain code, then effectful adapters; no implementation is supplied
by this change.

### Verification and adversarial acceptance

Use a positive fixture from the selected authorized producer, obtained through
its verified identity surface, with a complete payload bound to repository,
PR, expected **and** observed reviewed head, child gitlink SHA, gate ID,
catalog raw-byte SHA-256, exact command and source, outcome, and retained
artifact-byte digest. Require its exact expected check context on the exact
reviewed SHA. Preserve independently observed provenance and test that the
existing consistency reader consumes the verified record. Record which proof
is local preparation and which is authentic hosted/signed producer evidence.

| Proof case | Required result |
| --- | --- |
| One valid authorized producer, complete unchanged tuple, valid freshness/revocation and matching retained bytes | Verifier authentication and pure consistency both pass; authenticated acceptance stays distinct from general promotion/review qualification. |
| Change each repository, PR, expected head, observed head, child SHA, gate, catalog raw-byte digest, command or source independently | Reject mutation/replay, including an internally consistent payload copied from another revision or repository. |
| Change outcome or artifact bytes/digest after attestation | Reject; no trusted pass may be manufactured from a failed result or different artifact. |
| Similar check name, wrong issuer/App/workflow/signer, untrusted key, or expected name emitted only on a merge ref | Reject; names and merge-ref success cannot substitute for the reviewed-head identity binding. |
| Missing, malformed, expired, revoked or untrusted attestation; unknown policy or failed remote identity/revocation/artifact retrieval | Fail closed with a sanitized reason; never fall back to unauthenticated success. |
| Candidate changes its trust keys, allowlist, gate code/configuration, producer claims or proposed signing/publishing path | Reject self-authorization; demonstrate the verifier still uses independently authorized policy without candidate secrets. |

Retain immutable payload/artifact digests, exact producer/check/run identifiers,
reviewed revisions, sanitized verdict and reason, and test commands in Receipt
River. No credentials, signing keys or authorization headers may enter evidence.
The negative suite must exercise the effectful boundary as well as pure laws;
synthetic identity claims or local mock verification alone cannot establish the
positive trusted-producer acceptance criterion.

### Non-goals and adjacent scope

This refinement activates no workflow, signer, publisher, deployment, protection
rule, trust identity or lifecycle transition. It adds no implementation and
makes no authenticated-producer, native approval, review-round or readiness
claim. Preserve the narrower accepted Git-integrity contract while the stronger
producer boundary remains unqualified.

[Issue #59](https://github.com/open-hax/foresight/issues/59) remains separate and
unsatisfied: coverage report retention, format/metrics, repository threshold and
baseline policy, report-byte binding/tamper rejection and its hosted end-to-end
integration are not completed by an attestation plan or its future verifier.
The current fail-closed coverage guard stays intact. Similarly,
[issue #58](https://github.com/open-hax/foresight/issues/58)'s hostile exact-command
execution scope is independently owned; this plan neither adopts nor satisfies
it. Consumption of either adjacent outcome requires its own reviewed evidence.

### Risks and qualification holds

Trust-root choice and policy authority remain unresolved. Runtime/provider
availability, freshness/revocation semantics, and proof retention may affect the
verifier estimate. External ownership cannot be inferred from absent assignees
or a limited PR search. Before any implementation, reconcile overlaps, qualify
the planning scope through native review, and use Rheos for readiness. These
holds do not justify weakening any retained issue criterion.
