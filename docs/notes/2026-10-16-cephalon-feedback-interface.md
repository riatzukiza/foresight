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
Raw artifact bytes, unrelated restricted messages, credentials and private proof
paths do not become fields here. Required original query/memory/source values
remain private data under their owning access rules; retaining them by value
does not authorize public logging, a public event stream or wider disclosure.
Full matching artifact/destination proof stays behind the qualified protected
verification seam. The original normalized observation records its
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

PriorRecallReference uses the retained-owner layout selected below. It binds
the original recall, actual turn, selected item/source/content and original
graph/physical provenance without reconstructing a physical result from a
graph path or public run summary. Prompt inclusion alone does not establish
contribution. The selected outer layout is not a claim that its remaining owner
source leaves or their protected producers are already consumable.

## PriorRecallReference: retained owner values and turn custody

This is a proposed closed **seven-key outer layout**, not a new native Field
record, a current Knoxx export or a qualified replacement for the owner types.
Use the complete values of the selected Field ABI at7a3aab76; do not copy their
validators or flatten them into a second physical trace implementation.

| Key | Required original value |
| --- | --- |
| version | Literal `openplanner-prior-recall-reference/1`. |
| original-scope | Complete OriginalScope, equal to payload.scope and the original qualified principal/project source. |
| turn | Closed `run-id`, `session-id`, `conversation-id`: the exact three stored Knoxx coordinates for the actual call that disclosed this recall. Each must fit ID; no new UUID, trimming or fallback identity is introduced here. |
| field-admission | Complete original Field AdmissionRecord at the qualified read: all five owner keys, the complete original inputs/custody/emitted entity, and the current four-key result-disposition containing the complete eight-key RecallSuccess. |
| field-observation | Complete original qualified outer physical read/checkpoint source, including the actual owner, stored physical revision and snapshot binding, matching request/result, current-at-read authority and original source/history. This consumes the owner's source value; its complete leaf schema and protected producer remain pre-Ready selections, not an arbitrary map or an existing checkpoint-position export. |
| selected-item | Closed `inclusion-index`, `memory-binding`, `content-hash`: the actual zero-based index into both retained inclusion vectors, complete memory-variant NexusMemoryBinding, and SHA256 of the exact original owner memory content bytes under its qualified encoding. The encoding/source mapping remains an explicit owner selection; do not hash a prompt excerpt. |
| submission | Complete original qualified Knoxx submission observation binding these turn coordinates, the original recall/read and selected memory to the actual assembled provider input and the corresponding invocation occurrence. Its normalized leaf/schema and actual protected capture are selected in the existing composition plan before Ready; current prepared telemetry is not this value. |

The remaining source leaves above are named obligations, not `any`, an opaque
handle, a callback or permission to insert a verifier supplied by a caller.
They require complete source/interface/fixture plans before Ready and actual
producer/protection qualification before consumption. This layout does not
close the entire Field ABI or add an implementation-before-Ready prerequisite.

### Field record and selected-item agreements

Retain the entire five-key AdmissionRecord (`request-id`, `original-inputs`,
`original-custody`, `emitted-entity`, `result-disposition`). Its scoped request
and emitted entity must name that same actual request. The exact original
principal association is original-custody.principal-scope-source; preserve its
complete native scope, actor/contract and explicit character context. Its native
membership identity is the stored principal. Actor, contract and character
presence identities retain their separate roles; identical strings do not
establish an identity conversion. Project and graph authority retain their
qualified original source in custody, with no grant inferred from a run row.

Only the owner's `:current` disposition with a valid complete RecallSuccess
can supply this reference. An invalidated disposition has no result and cannot
be repaired by a prior result, current database refetch or public failure
facade. Preserve all five native success status values (`admitted`,
`no-contact`, `complete`, `incomplete`, `exhausted`) and their owner predicates.
The selected index must actually exist in both inclusion vectors; an empty
result cannot yield a selected item, regardless of its valid status. Do not
invent a complete-only filter or promote an error/partial failed operation to
success. Later current authorization is checked independently of the retained
current-at-read disposition; the word `current` is not a reusable grant.

At that same index, included-memory-ids equals memory-binding.memory-id, and
the full matching ten-key InclusionReason identifies its actual memory/content
revision, nexus, daimoi, step, pair, contact matrix revision, graph revision,
field history and fold revision. The binding is the complete six-key
`finite-nexus-memory-binding/2` memory variant, including its complete seven-key
`finite-nexus-memory-source/1` source-binding. Match its actual memory resource,
source event, owner/stored principals, original source revision and scope
against the qualified visible record and original Clio source envelope. No
non-memory variant, copied label or source-event-ID-only proof can substitute.

Keep the complete original source evidence/history that resolves these
bindings, including the exact authorized memory content and its owner-defined
byte representation. Content revision is not automatically its content hash.
The selected item's source memory need not have been one of the earlier native
cost walk's returned hits: the actual physical inclusion and its qualified
graph source establish selection. Do not silently restrict physical recall to
that earlier graph walk's top-k subset.

### Graph path, physical trace and revision roles

The physical result is the complete RecallSuccess: motion, bonds, contacts,
both inclusion vectors, kernel-horizon and budget-result as well as status.
Retain every actual request motion sample and its causal/deposit references,
every evaluated request bond entry and original contact evidence, without
truncating them to a rendered prompt or the global trail window. Preserve the
original physical budget, actual executed prefix, history/fold and all
accumulated work/trace/byte counts. A read, retry or checkpoint adds no new
motion, time, inclusion or budget allowance.

Original native graph request/projection/authority/result values remain in the
Field's qualified original source evidence/history where its contracts require
them. When that source contains the native graph hit path, retain its real node
path (at most64) and corresponding edge IDs (at most63), actual seed/neighbor
reason, endpoint and full source mapping. Those are graph-source facts. They
are not a replacement for physical motion/bonds/contacts, and physical contact
does not mint a graph edge or a traversed graph path. A seed-only useful item
does not need a fictitious traversed edge.

Original request.graph-revision and projection-revision remain distinct. Match
the inclusion reason and NexusMemoryBinding to their actual graph source; do
not relabel a projection or native query digest as a graph or physical revision.
Match field-observation to the retained horizon's selected-end-checkpoint and
qualified physical snapshot. The owner's three-key CheckpointPosition
(`kernel-time-us`, `event-cursor`, `admitted-event-cursor`) is a position; it is
neither a stored physical revision nor an authenticated snapshot/hash. Its
outer source must establish those facts. The generic native graph v1
`field-status: not-loaded`/`field-revision: not-loaded` remains legitimate for
that earlier read interface and supplies no physical proof here.

The original scoped request's actual Field scope-revision is the value used by
the feedback envelope. The complete native policy fingerprint remains original
principal provenance; no automatic alias, retagging or fresh grant is inferred.
The existing qualified authority factory must select the physical scope mapping
before Ready. Current authorities and protection through commit/exposure remain
separate obligations even when every original revision still matches.

### Actual turn and submission boundary

At selected Knoxxb6, initial run admission persists run_id, session_id and
conversation_id, and a stored authorization snapshot. Its run-store identity
comparison covers only those coordinates plus org_id/user_id. Membership,
actor and policy are outside that comparison; actor-binding is not retained by
auth-snapshot. A run document therefore supplies coordinates but cannot replace
the original qualified character/principal association or a current grant.
Keep native spellings in their original source values; this normalized turn
map is an explicit checked projection, not a change to the native run schema.

Knoxx's `passive-graph:` recall-id hashes conversation plus message and may
repeat across turns. Preserve it as the actual native graph query identity.
Field request ID, run ID, session/conversation coordinates, native provider
currentTurn and invocation occurrence are separate values. Do not invent a
turn ID from that hash, infer run=session, or copy Knoxx's runtime event_id into
Clio's supplied UUID namespace. Where a qualified source maps these values,
retain the complete actual source and verify its agreement; spelling alone is
not proof. The submission source must distinguish two real invocations even
when the conversation/query and therefore passive recall-id repeat.

The inspected hydration projection discards original authority/candidate
bindings and most original recall data. Encounter resources retain IDs,
inclusion evidence and counts, while omitting the actual encounter/prompt
contents. The hydration/inclusion event is emitted with status `prepared` at
stage `request-context`. Subsequent disclosure checks, prompt assembly and
the call to send-user-message! occur later. Neither those projections, the
persisted pre-hydration request messages nor the redacted console prompt log
can reconstruct or prove the complete reference or actual submission.

The proposed submission capture belongs at the existing Knoxx caller that
actually assembles and dispatches the provider input, with its original
turn/principal/recall binding and unchanged current disclosure checks. Its
normalized record and protected evidence must distinguish preparation,
dispatch, accepted/definitely rejected/unknown delivery and any later execution
observation. No prepared-only, rejected or unknown dispatch may impersonate a
qualified actual submission. Full prompt/private message bodies remain behind
their existing protected owner seam; the normalized observation preserves the
complete authorized item/representation/call matching facts and original proof
custody. It must bind the representation actually sent, including any excerpts
or truncation; the original memory hash is not a claim that every original byte
was sent. This does not require a second provider call or replay publication.
The original read/submission sources exist before this feedback reference is
formed. They bind the earlier request/result/item/call facts; neither their
payloads nor their custody contain this enclosing PriorRecallReference,
FeedbackProposal, feedback intent or a later outcome/acknowledgement. A native
source event retains its actual accepted ID outside its own semantic payload.
Do not close a missing binding by creating a self-containing event or hash.

Submission and actual inclusion still do not establish causal contribution.
The independently qualified outcome/witness channel must prove that the
selected consumed input affected the actual permitted artifact/action. Keep
that obligation in VerifiedOutcomeObservation; do not move it into a telemetry
flag or award feedback when the reference is merely valid.

### SubmissionObservation: exact original input and delivery state

The selected submission value is the proposed closed SubmissionObservation
below. Knoxx owns capture and source protection; OpenPlanner validates the
qualified normalized observation. This is the existing packet's source/interface
plan, not an exported function or installed observer. At selectedb6,
IAgentSession promises only a Promise from send-user-message! and an opaque
current-turn; its wrapper forwards to sendUserMessage. Promise resolution and
persisted run status do not define provider acceptance or successful work.

SubmissionObservation has exactly eleven keys:

| Key | Required value |
| --- | --- |
| version | Literal `knoxx-recall-submission-observation/1`. |
| observation-id | Actual accepted Clio event ID for this observation under the selected supplied-UUID admission/readback contract. |
| runtime-occurrence-id | Original occurrence from the existing Knoxx event producer for this particular planned call; distinct from the canonical observation ID, session/currentTurn and query hash. |
| turn | Exact three-key original turn projection selected above. |
| original-scope | Complete OriginalScope, matching actual original principal/actor/project and disclosure sources. |
| recall-binding | Complete seven-key RecallSubmissionBinding below. |
| caller-input | Complete InputObservation for the captured assembled caller content. Prepared records establish only that candidate; dispatched records must establish these exact bytes were actually passed. Boundary is `caller-content`. |
| provider-input | Exactly `{:status :not-observed}` or `{:status :observed :input InputObservation}`. Observed input has boundary `provider-request`. |
| delivery | Exactly one DeliveryObservation variant below. |
| execution | Exactly `{:status :not-established}` or `{:status :observed :source-events [...]}` with complete independently qualified native execution observations for this occurrence/input. |
| original-sources | Complete typed NativeEvents in actual qualified causal order: original read/disclosure, caller/input/delivery sources and this observation event. No IDs-only substitute or caller-supplied trusted flag. |

The observation event's semantic payload is its version plus the eight matching
runtime-occurrence/turn/scope/recall/caller/provider/delivery/execution facts.
It excludes observation-id and original-sources. Its accepted envelope supplies
observation-id and can appear in original-sources without containing this
record. Earlier read/input/delivery events do not contain this later observation,
reference or outcome. Nested source events must precede their consumer; no
self-reference, forward-reference or hash cycle repairs missing provenance.
The canonical typed payload/producer mapping and actual source protection stay
in the already required native catalog selection before Ready. This introduces
no new ID generator or event-admission implementation.

RecallSubmissionBinding has exactly `field-request-id`, `native-graph-recall-id`,
`graph-revision`, `projection-revision`, `field-scope-revision`, `field-revision`
and `field-snapshot-hash`. These retain their actual distinct qualified native
roles and agree with the original Field read, admission and outer snapshot.
Complete original read/custody remains in the source events and retained
reference, not replaced by this projection. Every item below must resolve to
its actual included memory and source at that same read.

InputObservation has exactly `boundary`, `encoding-revision`, `byte-count`,
`sha256` and `recalled-items`. Boundary is `caller-content` or `provider-request`;
byte-count is the actual finite Count and sha256 hashes those exact original
input bytes. The source owner supplies the qualified encoding and original bytes
behind its protected seam. Plain text caller input uses scalar UTF-8 without
trimming or normalization. Multipart caller encoding must be explicitly
selected from its owner representation. Provider input binds the actual
outbound body under its transport owner's byte representation, never a later
reserialization. There is no JSON/pr-str, media-URL fetch or binary64 fallback.
Owner encoding, decoding/path semantics and native transport source mapping are
part of this same packet's codec/catalog selections before Ready.

Recalled-items is the complete ordered vector of actual recalled text
occurrences in that input. Retain repeats when a memory was actually rendered
twice; the occurrence identity includes its actual input path and byte range.
Each entry has exactly `memory-binding`, `content-hash`, `content-path`,
`start-byte`, `end-byte`, `representation-hash` and `derivation`:

- Memory-binding is the complete original memory variant; content-hash is its
  independently bound original memory byte hash, not the prompt excerpt hash.
- Content-path is a finite vector of exact string keys/nonnegative Count
  indices locating an actual text value under the qualified owner decoding.
  It is data, never an executable accessor. The half-open byte range must be
  nonempty, in bounds and aligned to scalar UTF-8 boundaries in that value.
  Representation-hash hashes exactly that range.
- Check the path/range against protected original input bytes, never a console
  log, regenerated prompt or current memory lookup. Preserve the actual owner's
  content order then offsets, without inventing a map-key traversal order.
  Reject ambiguous overlaps or unsupported native representations; these are
  text coordinates, not graph paths or physical motion coordinates.
- Derivation is exactly `{:kind :verbatim}`, requiring equality to the entire
  original memory bytes; or `{:kind :excerpt :source-start-byte N
  :source-end-byte M}`, requiring equality to that exact original scalar UTF-8
  slice; or `{:kind :transformed :source-event NativeEvent}`. The transformed
  branch retains the full original qualified renderer observation binding
  actual original memory, complete source/configuration/inputs and output
  bytes. Its exact source-specific payload belongs to the native catalog
  selection and independent qualification. A producer assertion or matching
  hash alone is insufficient; a transformation is not relabeled an excerpt.

The protected checker follows the transport owner's actual adaptation between
caller and provider inputs, retaining splitting, transformation, truncation or
omission. Whole-input hashes may legitimately differ. A caller item dropped
from the provider input proves no provider inclusion; an unrelated equal string
or copied item label supplies no source binding. Before a reference can supply
feedback evidence, its selected item must have at least one independently
verified occurrence in the actual observed provider input. An excerpt retains
its complete original memory binding without claiming all original bytes were
sent. No original/private prompt body is copied into this normalized record.

DeliveryObservation is a closed tagged union:

| status | Exact additional keys and obligation |
| --- | --- |
| prepared | None. Assembly/capture only; actual send is not established. |
| host-dispatched | `source-events`: complete actual caller-dispatch NativeEvents for this occurrence and caller input. This establishes no provider acceptance. |
| accepted | `source-events`: complete actual qualified transport/provider-owner acceptance NativeEvents bound to this occurrence, original request and exact observed provider input, with independently matching caller-to-provider custody. |
| rejected | `source-events`: complete actual owner refusal observations proving this request was definitely rejected before acceptance/execution. Generic exceptions, timeouts or missing output do not suffice. |
| unknown | `reason`, exactly `transport`, `timeout`, `crash-gap`, `missing-proof` or `unsupported-proof`; and `source-events`, the complete available matching NativeEvents, possibly empty. Neither acceptance, rejection nor termination is inferred. |

Prepared requires provider-input not-observed and execution not-established.
Execution observed is permitted only with accepted delivery and the matching
actual provider input; every nonaccepted delivery keeps execution
not-established. Host-dispatched, rejected and unknown may retain an actually
observed outbound body without implying acceptance. Reject contradictory
combinations rather than silently upgrading a weaker record.

Accepted requires provider-input observed. A fulfilled SDK Promise, stream-open,
HTTP200, echoed request or success label alone cannot supply acceptance. Qualify
actual owner request/response or accepted-execution hooks and their input
correspondence; no custom vendor API, model route or credential is implied.
Execution observed retains source-specific owner evidence and exact matching;
it asserts no successful artifact/destination outcome by itself. Otherwise
execution stays not-established, including when delivery is accepted. The
independent contribution/outcome witness still proves actual consumed input
and successful permitted artifact/destination matching. Status alone earns no
feedback.

Each observation is immutable. A later delivery/execution observation has a
new actual observation event for the same original runtime occurrence, turn,
read and caller input, retaining the earlier complete sources. Input changes or
another real call require a distinct occurrence even when query/session repeat.
Never patch an earlier event, upgrade a prepared record by projection, or
rewrite a retained proposal/reference with a later observation. Before using
an accepted observation, independently examine complete current owner history
for conflicting/revoked/mismatched proof, and require current authority for all
retained items and the whole input/evidence audience. Chronology alone cannot
resolve contradictory source facts.

Use the existing runtime occurrence producer once for an actual planned call;
retain its original returned map on durable-write retries. Qualify its linkage
to actual canonical Clio admission without equating the IDs. Intent persistence
does not prove dispatch. A crash between actual send and durable observation
leaves uncertain delivery; never replay the provider call, publication or maker
turn to manufacture evidence. Capture preserves existing disclosure checks and
active-turn/terminal ownership, adds no second send and implements no scheduler
or provider policy. Missing qualified observation refuses feedback evidence;
it does not silently change ordinary maker lifecycle/failure behavior.

All original input/proof bytes remain behind their qualified owner seam. Facts,
identifiers, digests and every NativeEvent retain their actual audience rules.
No full prompt, credential, raw auth-context, SDK session/currentTurn object,
callback or private proof path enters this Field-retained normalized record.
Every copy counts under existing input/state/history/work limits; this plan
proves no positive fit or cap increase.

The same packet's after-Ready implementation/fixture plan exercises the actual
caller and isolated deterministic transport owner: exact captured bytes passed
once; repeated equal queries in distinct occurrences; prepared-only/dispatch;
actual acceptance; definite refusal versus transport/timeout/crash uncertainty;
Promise completion without acceptance; transformed or dropped provider context;
verbatim/excerpt/qualified transformed mapping; stale/denied/mixed scope and
whole-evidence audience; conflicting later history; durable observation retry
without a second send; exact/one-over bounds; and contribution proof unchanged.
It requires neither another natural maker turn nor custom live vendor calls.
Full physical composition and served B3 remain their existing later proof.

### Retention, source qualification and negative fixtures

The reference and all nested original values remain unchanged through intent
admission, pending/confirmed feedback retry, checkpoint, restore and readback.
No current lookup regenerates the original read, query, scope, content or prompt
representation; fresh current authorization is independently required for use
and exposure. All copies count under the existing finite input/state/checkpoint/
history/trace/work caps. No JSON/pr-str fallback is a qualified binary64 codec,
and this source selection proves no positive fit or enlarged cap.

Within this same packet, fixture planning must cover valid selected inclusion
under every applicable owner status, empty/invalidated/error results, mismatched
index/reason/binding/content, seed-only and physical non-top-k inclusion, graph
path versus physical trace, distinct revisions, stale/denied scope, repeated
query across distinct invocations, prepared-only/rejected/unknown submission,
excerpt-to-original matching, original custody through retry/restore, privacy,
and exact/one-over owner budgets. Use isolated qualified source fixtures for
writer implementation after Ready. Full physical producer composition and
actual served proof retain their existing later qualification; neither a
fixture nor this contract supplies that proof or becomes a new pre-Ready
implementation dependency for the independent writer packet.

## VerifiedOutcomeObservation and FeedbackProposal

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
   PriorRecallReference owner leaves beneath the selected seven-key layout and
   VerifiedOutcomeObservation/witness variants, their actual normalized producer contracts and by-value original
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
