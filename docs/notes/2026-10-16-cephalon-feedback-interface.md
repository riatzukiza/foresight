# Proposed outcome-feedback envelopes and writer observations

License: GPL-3.0-or-later.

## Status, ownership and scope

This is the interface portion of existing Incoming packet
`deliver-outcome-attributed-once-only-graph-feedback` (5 points: contribution
witness 2, once-only writer/readback 3). It proposes OpenPlanner-owned portable
feedback contracts and outer persistence/verification adapters. It is not an
existing SDK API, an implemented schema, a new task, or native Ready admission.
Foresight owns this cross-repository delivery proposal; OpenPlanner retains the
implementation and graph policy. The Eros/Eris kernel consumes qualified data
and never implements a second graph writer or reinforcement policy.

The accepted B1/B2/B3 finish condition and the complete current authority,
contribution, artifact/destination, audience, once-only, recovery and finite
requirements in the owning card remain mandatory. This note specifies envelope
and observation boundaries; the final reinforcement policy/read projection,
complete normalized outcome/witness leaf schemas remain explicit pre-Ready
planning exits below. Actual new producer/protection implementation follows
lawful Ready and its executed qualification precedes consumption/Done. These are unfinished
parts of this same packet, not new predecessor identities or point increases.

## Observed selected source

These are local Git-blob observations at OpenPlanner
`3ab5ffbb6e34a2ee3607a74bf421785d47d5391d`, independently inspected on October16.
They do not establish executed feedback behavior:

- `packages/graph/graph-claim-core/src/openplanner/graph/recall/mongo.cljs`
  lines97–112 and169–178 loads `mongo.graphEdges`, validates
  `data.scoped_recall` and takes its `cost`. The scope, source nodes and
  provenance are admitted before the retained-row limit.
- `mongo_contract.cljc` lines25–32 closes that nested projection over version,
  org_id, project, character_id, provenance_event_ids and cost. It is a read
  carrier, not a write proposal. Request feedback remains `:none`; returned
  feedback remains `:not-requested` with zero attempted/completed.
- SDK `mongodb.ts` lines1196–1266 and1355 onward exposes structural/semantic
  bulk upserts. Their result is input row count, and they accept neither a
  session nor expected revision. They provide no persisted feedback receipt.
- Semantic `conductance`/`reinforcement_count`/`last_reinforced_at` are in a
  different collection from the selected reader's scoped edge cost. Updating
  them alone cannot qualify causal feedback for this read path.
- SDK event upsert and protocol append replace event content or echo the
  supplied event. Existing transaction examples own their sessions for other
  operations. Neither surface supplies this packet's immutable conflict,
  once-only outcome, whole-feedback transaction or authority-through-exposure
  contract. Shared URI, readback or majority/snapshot settings do not supply it.

The retained historical582 `/graph/memory` handler reinforced traversed semantic
edges during retrieval. Its incidental write and caught/logged errors are donor
history. Retrieval alone remains insufficient evidence of usefulness, and that
handler is absent from selected3ab. Do not port its strength/decay rule by name.
Exact selected source paths and hashes accompany this proposal's receipt.

## Prospective owner ports

The names below designate proposed interfaces, not exports found at3ab:

1. **Prepare feedback:** the selected OpenPlanner/Knoxx host composition consumes
   the actual artifact, destination and witness owners through qualified
   protected ports. Knoxx retains its application/source policy and actual
   file/social adapters; OpenPlanner validates matching/provenance and owns
   graph feedback, without acquiring another set of destination credentials or
   policy evaluators. The host admits one immutable feedback intent through
   the actual canonical Clio interface,
   and returns an OutcomeHandoff. Preparation/admission records intent; it is
   not proof of graph persistence, success, permission to publish or delivery.
2. **Write feedback:** accepts the retained FeedbackProposal and independently
   reloads the actual owners, native versions, current target and expected
   storage revisions through protected host ports. It owns the complete atomic
   reinforcement plus immutable effect receipt, bounded attempt/recovery and
   independently authorized complete readback. Caller data supplies no SDK,
   principal resolver, policy evaluator, verification callback or credentials.
3. **Observe the writer:** only actual matching persisted/definitely failed
   observations can produce FeedbackAcknowledgement. Partial/unknown commit,
   missing readback, unsupported protection or unestablished exposure returns
   a separate refusal/quarantine outcome without a fabricated acknowledgement.
4. **Read projected feedback:** a separately versioned read profile consumes the
   actual stored qualified outcome contributions. Retrieval stays read-only.
   The existing v1 profile and its contracts do not silently acquire writes or
   new fields. This packet owns the consuming profile and its causal fixture;
   the earlier scoped-read packet does not have to implement this successor.

Implementation is proposed under the existing graph component with portable
`openplanner.graph.feedback.contract`/pure law/domain before its outer Mongo and
proof adapters. Exact files, build/exports and actual transaction participation
must be qualified in the owning source; a proposed name is not a working port.
Source/interface/full-fixture planning and actual existing upstream inputs
precede Ready; this packet's own implementation follows lawful Ready. Executed
qualification precedes consumption or Done, without an implementation-before-
Ready cycle. The unrelated OpenPlanner consumer's approved four pin/routing/Node
changes remain its separate scope.

## Elementary data and native provenance

All objects below are Clojure maps/vectors/scalars. Every listed map has exactly
its declared keys; extra, missing, duplicate, malformed or unsupported variants
refuse without coercion, truncation, fallback identity or silent migration.

| Type | Proposed finite contract |
| --- | --- |
| ID | The existing Field ABI ID: nonempty exact UTF-8 scalar string, at most128 bytes; canonical native values stay unchanged. A wider SDK string that does not fit refuses. No local event ID factory or hash-to-ID repair. |
| Revision | Existing ABI nonempty exact qualified UTF-8 scalar string, at most512 bytes. An original revision is provenance, never a current grant. |
| Digest | Existing ABI64 lowercase hexadecimal SHA256. Hash agreement is not authority or proof that an external read occurred. |
| NativeEvent | The complete event value under the already selected canonical Clio schema/admission/history, with the exact typed payload designated here. Consume the owner contract and actual admission result; do not duplicate its validator, mint an event ID or accept a supplied `trusted` boolean. |
| OriginalScope | Closed actor-id, org-id, membership-id, user-id, project (ID), policy-revision (Revision). Bound to the actual stored principal and original selected scope. Current owner decisions and protected native versions are independently supplied by host authority. |

Full original normalized observations and their native source events are kept
by value, not regenerated from current database lookups or replaced by ID/hash
summaries. The native envelope is retained and validated by its actual owner.
Native payloads must match the exact corresponding carrier, not arbitrary data.
Raw artifact bytes, restricted message content, credentials and private proof
paths do not become fields here. Full matching proof stays behind the qualified
protected verification seam. The original normalized observation records its
complete authorized matching facts and bound proof custody; it cannot be a
wrapper substituted for a different producer's original payload.

## FeedbackPayload and FeedbackProposal

**FeedbackPayload** has exactly:

- `version`: literal `openplanner-feedback-payload/1`;
- `operation`: literal `reinforce-qualified-recalled-item`;
- `scope`: complete OriginalScope;
- `recall`: complete original PriorRecallReference;
- `outcome`: complete original VerifiedOutcomeObservation;
- `original-sources`: complete original recall and outcome NativeEvent values,
  plus the matching witness/artifact/destination observation NativeEvents
  required by that selected outcome channel, in original qualified order.

PriorRecallReference identifies the original recall, actual turn, selected
item/source-event/content hash, original graph and physical-field revisions,
actual authorized node and edge path, exact original scope and the independently
qualified original Field scope revision. The latter is not the source policy
revision/fingerprint: the consumed Field contract leaves that mapping to its
qualified authority factory and prohibits automatic aliasing. Keep both values
with their actual different provenance; select the mapping before Ready, never
copy or retag one into the other. Node path is
bounded by the existing64 and edge path by63, with the selected item at its
actual position and source mapping. Prompt inclusion alone does not establish
contribution. Its complete normalized record/source schema remains an explicit
owner-leaf selection below; these required facts are not an arbitrary map.

VerifiedOutcomeObservation identifies the actual outcome, turn, action/effect,
item/content/recall binding, independently observed persistent artifact
identity/hash/type and actual configured destination/account/record matching,
the selected contribution witness and original observation/producer provenance.
Consume the owning card's two witness channels in full: qualified actual
consumed-input/choice-to-artifact execution with effective parameter mapping,
or its at-most-two isolated executions within10000ms with frozen remaining
inputs and publication disabled. A producer assertion, copied item label,
ignored input, changed hash alone or unrelated successful artifact is invalid.
Complete normalized leaf fields/variants and independent source verification
must be selected before Ready; this paragraph does not qualify a placeholder.

**FeedbackProposal** has exactly these seven fields:

| Field | Value and agreement |
| --- | --- |
| version | Literal `openplanner-feedback-proposal/1` |
| effect-id | Actual accepted NativeEvent :event/id for this exact intent |
| outcome-id | Original admitted outcome identity, matching payload.outcome and its native source |
| payload-hash | Digest of complete FeedbackPayload under the graph owner codec/hash contract still requiring selection before Ready |
| scope-revision | Actual original recalled Field scope revision, independently bound through payload.recall; distinct from OriginalScope.policy-revision |
| prior-recall-reference | Exact complete payload.recall value |
| payload | Complete FeedbackPayload |

The semantic payload is formed before the feedback-intent event is admitted.
It contains the prior recall/outcome source events, not its own feedback event,
proposal envelope, later acknowledgement, enclosing kernel state or snapshot
hash. Clio owns its native event envelope, ID validity, admission/conflict rules and
causal-history validation. At pinned788, append-event! returns :appended or
:already-present for a supplied event; it does not generate an ID or return a
payload hash. The law validates the supplied :event/id as a UUID. Mirror that
actual accepted field and verify the native readback/history, without inventing
an API-generated identity. domain.canonicalize/canonicalize validates/unions
history; it is not a payload byte encoder or SHA256 API. The graph owner must
select the exact payload encoding/hash source and native schema-root/leaf plus
producer mapping before Ready, then qualify its implementation before use. Do
not infer a codec from a method name, redefine Clio identity/admission/history,
introduce a Field/Rheos shadow codec or create a self-referential hash recipe.
The resulting intent NativeEvent binds this exact payload and its actual
accepted :event/id; payload fields do not authenticate their own admission.

## OutcomeHandoff and pure-kernel bridge

**OutcomeHandoff** is closed over version
`openplanner-feedback-handoff/1`, `outcome` (the complete original normalized
outcome), `proposal` (complete FeedbackProposal), `intent-event` (the actual
admitted feedback-intent NativeEvent), and `original-custody` (the exact
complete original qualified recall/outcome/witness/read source bundle).
Payload.outcome equals handoff.outcome; proposal, outcome, recall, effect,
source order, payload bytes/hash and original scope agree. Proposal.scope-revision
agrees with the original Field scope binding, not a copied native policy stamp.
Native admission
and current permission are independently established before causal use.

This handoff is the proposed concrete owner value for Field apply-outcome's
existing observed-choice-outcome argument. Its prior-recall-reference argument
must exactly match proposal.prior-recall-reference. Existing current-authority
and producing-identity arguments remain separate. The pure kernel validates
qualified matching data, retains the full original handoff/proposal/custody and
returns that same designated proposal. It does not fetch a missing body,
prepare a different proposal, call Clio or storage, grant access, choose graph
strength, change elapsed time or create external effects. No new argument or
state root is proposed. Existing finite-state/input/history/snapshot/work caps
count every duplicate source, wrapper, retained body and validation step.

The existing pending-feedback record retains the five required bindings and
these full original values. Pending and confirmed retries return the exact
original proposal with unchanged state and no second write/reinforcement or
pending reactivation. There is no new expiry, removal or eviction policy.
Current authority is required for every later use/exposure; original custody
is not a permission snapshot that can be reused as a current grant.

## FeedbackAcknowledgement

A qualified writer observation produces a closed carrier with exactly:

| Field | Value |
| --- | --- |
| version | Literal `openplanner-feedback-ack/1` |
| observation-id | Actual accepted writer-observation NativeEvent :event/id |
| writer-principal | Actual observed writer ID, independently authenticated/authorized |
| effect-id | Original proposal.effect-id |
| outcome-id | Original proposal.outcome-id |
| payload-hash | Original proposal.payload-hash |
| scope-revision | Original proposal.scope-revision, never retagged |
| storage-result | Literal persisted or failed, matched to write-observation |
| write-observation | Complete matching PersistedWriteObservation or FailedWriteObservation |
| source-event | Complete actual writer-observation NativeEvent |

The source event's semantic payload contains its version plus the seven matching
writer/effect/outcome/hash/scope/storage-result/write-observation facts, excluding its own
observation-id and enclosing source-event. Its actual accepted :event/id supplies
observation-id. Version is bound by that typed native payload. This avoids a
self-containing event. No copied event identifier or client-generated flag can
replace its actual native producer, payload and history verification.

PersistedWriteObservation must retain the actual atomic immutable effect
receipt, complete matching reinforcement/projection readback, expected/prior
and resulting owner revisions, writer/authority/source custody and actual
commit outcome. A repeated already-persisted match returns an actual current
readback of that same committed effect, without another reinforcement write.
FailedWriteObservation records the actual definite failed/no-commit outcome
and independently established unchanged target/effect baseline as required by
its branch, with exact reason and original identities. Complete storage/native
readback leaf contracts remain explicit owner-source exits below; a generic
row count, empty result, echoed request or `acknowledged` flag is insufficient.

An uncertain, partial, unsupported or unobservable outcome is not `failed`
merely because a deadline elapsed. It remains quarantined/refused outside the
kernel confirmation path until actual qualified recovery establishes it.
Preserve an acknowledged committed effect even when later authority prevents
exposure. Do not claim rollback, remint identity or replay publication/write to
manufacture matching proof. Current authorization/audience applies to the
acknowledgement and any audit exposure as well as the graph mutation.

Field confirm-feedback consumes the actual qualified carrier and retains its
existing seven required acknowledgement bindings, six-key confirmation summary,
matching/authority precedence, failed-storage versus invalid-ack reasons and
unchanged refusal state. Complete source/acknowledgement stays in its qualified,
counted provenance/history. A matching acknowledgement is not permission to
publish or evidence of a new natural creative outcome.

## Before-Ready choices and executed qualification

This envelope proposal alone does not close every Field owner leaf or admit
feedback5. In this same packet's remaining plan, select:

1. The original/current Field scope bindings and their qualified mapping, kept
   distinct from policy/source revision provenance. Exact graph-owner payload
   encoding/hash contract and canonical Clio
   event-root/leaf, producer and supplied-native-UUID admission/readback mapping;
   no existing payload-hash or ID-generation API is asserted. Complete closed
   PriorRecallReference and VerifiedOutcomeObservation/witness variants, their actual normalized producer contracts and by-value original
   source mappings, including protected private-artifact/destination proof.
2. The exact reinforcement policy, owned stored target/projection and versioned
   read mapping. Updating an unconsumed counter is not acceptable. Cover both
   a useful seed item with no traversed edge and a useful graph-neighbor item;
   do not narrow the selected useful-item contract to non-seeds. No strength,
   decay, new memory horizon or competing force law is selected by this note.
3. Complete persisted/failed observation and immutable effect-receipt shapes,
   real uniqueness/conflict rules, expected storage revisions, common client/
   session participation, finite attempt/work/deadline bounds and actual owner
   protection through computation/commit/exposure. Canonical identity and
   authorization remain with their existing owners. The packet implements its
   own missing writer/cooperation after Ready; it does not require its own
   completed implementation as an external pre-Ready prerequisite.
4. Exact source/build/export and fixture plan. The existing read interface is a
   completed input; this successor owns its writer plus consuming projection.
   Actual existing upstream prerequisites and planning review precede Ready;
   relevant causal RED precedes pure domain and adapters GREEN.

Executed qualification must independently read the actual stored target and
immutable effect receipt, reject mismatches/partial states/unknown commit, and
show repeat admission/write/restore/submission/confirmation cannot multiply
reinforcement. With frozen permitted query/corpus/embedding/seed/time/authority,
a valid admitted useful outcome must change the actual new reader's consumed
feedback input and at least one bounded selection/path result. Preserve the
canonical content embedding. A record count or diagnostic revision alone is
insufficient. Qualify seed-only and graph-neighbor positives plus unused-item,
forged-label, ignored-input, missing/failed destination, stale/mixed scope,
denied/changed current authority, private-audience, malformed, conflict, exact/
one-over budget and recovery/quarantine negatives. Retrieval with no outcome
continues to write nothing. Full physical/source composition and actual served
proof remain their existing later packets; a cost-walk fixture cannot replace
the selected physical computation or count as live B1/B2/B3 proof.
