# Typed lineage derivation and owner-authorized admission — proposal

This design proposes the complete [Foresight issue #66](https://github.com/open-hax/foresight/issues/66)
outcome. It is not an accepted common law, implementation, promotion event, or
claim that a card is Ready. The eight-point epic proposes three/three/two-point
stories; reviewers must assess estimates and may require further lawful breakdown
without deleting the hard criteria. Personal synchronization PR #3 at
`96a6dca24cb7a14b041bdd6e3e7922c568238da9` is the unqualified parent; accepted upstream
`fcfc2d17f28640203066ddfe0a22f87db7372a32` is retained ancestry, not this plan's merge/admission.

## Current contract and boundaries

`src/foresight/lineage.cljc` separates six direct README-supported propositions from
six stronger recovered interpretations. `src/foresight/law/lineage.cljc` confines
historical sources, validates source/evidence identities, and requires all current
claims to be provisional interpretations. `docs/lineage/promethean.md` preserves
non-actionability and no workspace/execution authority. Issue #66 adds the future
relation model; it does not retroactively reinterpret the README as direct proof
of owner-sovereignty, intent-compiler, learn-once, context-field, modular-intent or
eidolon-physics. Existing continuity statuses remain distinct from epistemic tier.

The proposed model is a separate additive portable relation surface, provisionally
`foresight.lineage-admission` plus a corresponding law namespace. Existing inventory
records remain unchanged. Shaping, relation validation, ordering and projection
decisions belong in `.cljc`; Git/filesystem/clock/identity allocation/authentication
and actual ledger effects belong outside that pure boundary. Runtime choice cannot
alter the represented meaning. No child implementation is copied or normalized.

## Proposed records and total validation

Use distinct typed identities for observation, derivation, admission, rejection and
promotion; refer to the existing source/claim identities explicitly rather than
silently equating their domains. The review must decide canonical ID representation,
record versions, allowed relation vocabulary and exact payload equality before code.
Observations bind immutable repository/commit/path/blob/content hashes and explicit
source/descendant scope. A derivation resolves those references plus a version-bound
rule or an exact retained human decision. A supported derivation is still provisional.
A derivation cannot manufacture or relabel its input observations.

The pure validator receives Clojure-shaped data and a supplied verified evidence and
authorization context. It returns ordered structured errors for nil/scalars/incorrect
collections, missing fields, unknown/ref-kind-mismatched IDs, duplicate/conflicting
payloads, dangling references, self/multi-node cycles and mismatched evidence/rule
versions. Traversal must be cycle-safe and total, with deterministic bounded failure
rather than an exception/stack overflow or success over an empty fallback. Exact
historic input may remain lawful: stale means disagreement with the declared binding
or applicable decision scope, not merely an older timestamp. Acquisition adapters
report unavailable bytes explicitly; a cached projection or field saying verified
cannot grant trust. Diagnostic shape and order require positive and negative laws.

## Authority and append/replay seam

Admission evaluates whether an interpretation may be used in an explicit scope.
Promotion is a separate act changing epistemic status only after an owner-authorized
durable event over the exact claim, derivation/evidence, decision and contract version.
Neither support, model agreement, writer name, formal review approval, merge, board
state nor a green check supplies that authority. A reviewed trusted producer boundary
must furnish the owner authorization evidence; candidate fields cannot authenticate
themselves. Authentication remains an existing runtime/Axxium boundary when applicable,
not a new Foresight credential scheme. Missing or disputed issuer/scope authorization
blocks the effect and remains an unresolved planning decision, not self-approval.

Clio remains event admission/identity/causal replay authority. Foresight owns the
interpretation relation and maps its reviewed events to that existing contract.
Proposed append orchestration verifies immutable bindings, validates the proposed
relation and scoped authorization, and appends through the owning adapter. Projection
advances only after accepted durable append; no pre-append successful status. Equal
canonical payload under an identical request/event identity retries idempotently;
changed payload conflicts. Competing admissions must be refused or explicitly resolved
by a scoped authoritative supersession event; never silently choose the last writer.
Rejection/supersession append references to retained predecessors. Replay preserves
history and produces the same lawful view without rewriting an earlier observation.

No new event ledger/kernel, board parser, local FSM or alternate authority is proposed.
Durability/replay claims require the actual owning adapter; callbacks/mocks prove only
orchestration behavior. Current prototype no-authority remains legal with zero promotion
records. Even a future accepted interpretation cannot make Promethean executable.

## Full acceptance mapping

| Original criterion | Proposed responsibility | Required proof |
| --- | --- | --- |
| Typed observations/derivations/admissions/rejections/promotions | LA1.01 | Kind-specific IDs and wrong-kind/duplicate/conflict fixtures |
| Immutable source/descendant evidence and explicit rule/human decision | LA1.01, LA1.02 | Exact pinned positive; missing/mutated hash/version and missing decision refusals |
| Evidence support distinct from admission | LA1.01, LA1.03 | Supported candidate stays provisional without admission/promotion |
| Owner-authorized durable event before acceptance | LA1.02, LA1.03 | Correct producer/scope+real append/reopen; wrong/missing authority or failed append cannot promote |
| Rejection/supersession history preserved | LA1.02 | Prefix unchanged, predecessor references, repeat/reopen and competing admissions |
| Malformed/missing relations fail closed; validation total | LA1.01, LA1.03 | Arbitrary wrong shapes, dangling references and bounded cycle-safe diagnostics |
| Unknown IDs/cycles/stale/conflicts/unauthorized promotion adversarial laws | All three | Independent negative and guard-mutation controls, not empty/mirrored tests |
| Existing provisional Promethean claims lawful before relation exists | LA1.03 | Exact current inventory/doc compatibility and no-promotion positive control |

## Future red/green and gates — commands not executed by this planning diff

After native planning qualification and lawful readiness, write meaningful failing
laws/tests first. The proposed future new test entry is `test/lineage_admission_test.cljs`;
its implementation must make assertion failure exit nonzero and prove nonempty counts.
Then implement pure shape/law/domain decisions before effect adapters. Proposed commands:

```sh
nbb -cp src:test test/lineage_admission_test.cljs
nbb test/lineage_test.cljs
nbb test/project_test.cljs
nbb test/evidence_test.cljs
nbb -cp scripts:test test/evidence_cli_test.cljs
nbb -cp scripts:test test/workspace_test.cljs
nbb scripts/project.clj validate
clj-kondo --lint src scripts test
```

Use the actual reviewed host/tool versions required by the owning root gates. For
pure portability add a nonempty Babashka/JVM driver over the same `.cljc` fixture data
and contract; the exact runnable driver must be reviewed with implementation, not
claimed to exist now. The adapter/E2E fixture must acquire immutable fictional inputs
and append/reopen/replay in separate temporary source/cache/ledger roots through actual
pinned Clio APIs; no service or provider contact. Add same-payload retry, changed-ID
payload conflict, two competing admissions, failed-append state preservation, unknown
IDs, cycle, stale rule/evidence and wrong-authority cases. Mutating each guard must
produce a failed law. Preserve original required root checks and complete hosted input.

No full historical archaeology corpus is normalized to make these fixtures pass.
The separate legacy EDN/current-reader compatibility blocker (Foresight #135) remains
its owning scope if a later implementation actually consumes that affected corpus.
Cold dependency/build availability, producer authentication and native review are
reported separately from semantic hermetic proof; none can be replaced by a summary.

## Pinned child evidence and dedup — proposed reuse only

The fresh eight-repository read-only intake found no existing Epiphany/Katamorph
issue or PR implementing this complete Foresight-owned relation:

- Epiphany `643be698ea0d841dd19385506b272872306e456e` records deterministic local
  candidate lineage and append-only candidate review decisions (ENG-004C/005A/005G).
  `epiphany.domain.review` records candidate-scoped accepted/rejected/relabel/deferred
  decisions; that is reusable evidence, not an owner-authorized Foresight promotion.
  Its personal PRs #1/#2 concern document-provider evidence and registration/request
  identity repairs. Epiphany #15 still requires durable operator ownership evidence.
- Katamorph `fe6017b28baa2d561550dc03768b3dd0da3f1480` and personal PRs #1–#3 cover
  non-step grammar, lint semantics and offline dependency closure. Reusable shape
  validation cannot decide interpretation acceptance or authenticate its owner.
- Clio `788cdd3434615a7932b924e68520dbf7f88408c2` owns event/ledger admission/replay;
  actual consuming contract/runtime dependencies must be verified at implementation.
- The root project-law promotion-status triage keeps recovered claims, lift candidates
  and accepted lifts separate. No child pin/semantic role is promoted by this plan.

No hard dependency on an unrelated open child planning card is fabricated. Proposed
LA1.02 depends on LA1.01; LA1.03 depends on both. Parent sync qualification and reviewed
owner/issuer/identity/equality decisions remain prerequisite holds. No lifecycle event,
write ID, ready transition, bot request, source implementation or native finding
settlement is included. Estimates must be reviewed without narrowing issue #66.
