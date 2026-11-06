# Receipt legacy-source binding

## Why the binding exists

The personal synchronization parent contains a receipt import and documentary
corrections authenticated against original legacy streams. Their evidence uses
immutable Git revisions, exact bytes including line terminators, and physical
line numbers. The accepted segmented store freezes `.ημ/receipts.edn` and moves
divergent rows into immutable one-record files. Sorting those records cannot
recreate their original physical coordinates.

The optional `config/receipt-legacy-source.edn` binds the preserved canonical
source. It lets the segmented reader reuse the existing source-import and
documentary-correction laws without rewriting source receipts or treating new
invalid records as trusted history.

## Input contract

The configuration contains exactly one EDN map with these two keys:

```edn
{:receipt-legacy-source/revision "<full lowercase Git commit ID>"
 :receipt-legacy-source/sha256 "<SHA-256 of that commit's .ημ/receipts.edn>"}
```

The file must be a non-executable regular Git blob and at most 4,096 bytes. The
source must be an ancestor of the reviewed commit, and its ledger digest must
match. Once the trusted comparison base contains the configuration, the next
revision must retain its exact bytes. Removing or replacing it is refused.

The first binding is subject to the same existing source authentication and
documentary-correction rules as the legacy reader. It is not a new author,
reviewer approval, signature, or permission to modify semantic evidence.

## Admission order

1. Check the ordinary segmented extension: committed segments remain unchanged,
   frozen legacy rows only migrate whole, base records remain present, and
   canonical records do not repeat.
2. Authenticate the configured original source and prove that every physical
   receipt row survives byte-identically in the current canonical store.
3. Reauthenticate the original import and corrections at the original immutable
   source revision. Preserve their original ordinals and occurrence identities.
4. Project authenticated documentary views onto the corresponding canonical
   records. Validate every newly introduced canonical record against the actual
   trusted base after this projection. The source replay alone is insufficient
   to admit a new record.
5. Apply current admission rules to genuinely new imported occurrences as well.
   A source replay does not establish that an import was already admitted by the
   actual comparison base. Reauthenticate its declaration and source proof.
6. Retain the admitted imported occurrences. Refuse raw overlap between imported
   occurrences and current canonical rows, including already committed segments.

Verification and promotion consume this same admitted ledger. Its storage
identity and canonical originals remain those of the current segmented store;
its documentary provenance points to the unchanged original source.

## Historical reads and timestamps

An unchanged historical ledger can be verified without a configuration that did
not yet exist. This compatibility path is limited to committed, unsegmented
snapshots with identical legacy bytes and no source binding. It reauthenticates
the original source imports and documentary corrections; missing or forged
source proofs still fail. It does not permit new legacy rows, new unbound
declarations, or working writes through the historical path.

Historical records retain their original timestamp values, including nonblank
values accepted before strict instant validation was introduced. Authentication
of their documentary views uses that historical envelope rule only for records
already present in the actual trusted comparison base. An imported stream is
historical only when its authenticated declaration already belongs to that base.
All genuinely new canonical and imported records must satisfy the current
timestamp, envelope and evidence rules after documentary projection. Neither an
anchor replay nor a correction can turn a newly introduced invalid timestamp
into trusted history.

## Boundaries and refusal behavior

New import or correction declarations outside the bound canonical source are
refused. This contract preserves a frozen legacy stream; it does not define a
general import format for newly authored segment streams. Changing the frozen
binding or adding a second source requires a separate versioned contract.

Canonical source records with duplicate content identities are refused because
the segmented store cannot represent their mapping unambiguously. Distinct
imported occurrences retain their original identities and multiplicity. Missing
rows, rewritten bytes, lost line terminators, forged source hashes, unavailable
objects, invalid ancestry, semantic evidence errors, and conflicting corrections
remain failures.

Working writers capture one committed HEAD. They check the working configuration
against that commit, authenticate the whole working ledger, and recheck the same
HEAD before executing a gate and writing its result. Existing exclusive segment
reservations, no-follow file checks, byte limits, locks, and quarantine behavior
remain in force. A mutation while a gate runs must not become ordinary evidence.

## Executable checks

`test/evidence_integration_test.cljs` exercises real temporary Git histories for
byte-preserving migration, documentary projection, rejected invalid ordinary
records, immutable and working configuration changes, ancestry, and imported
overlap. The existing pure and adapter suites retain the source-import,
correction, storage, process-custody and writer-boundary regressions. The hosted
review workflow runs the integration suite alongside those suites.
