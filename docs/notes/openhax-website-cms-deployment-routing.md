# OpenHax Website and Knoxx CMS deployment routing

(己, p=1) This is recovery memory for the deployment requested in October 2026.
It records source-backed routes and dated observations; it is not a live health
guarantee, a new controller, a board-state implementation or a release waiver.
Read the [append-only deployment report](../reports/inventory/2026-10-14-openhax-website-cms-deployment.md)
for the observation history. Use the global `pr-flow` pointer in AGENTS.md for
review policy; this note does not duplicate that policy.

## Keep planning closure, application work and rollout distinct

(世, p=1) The [October 15 source audit](/home/err/.local/share/openhax-codex/20261010/review-state/deployment-critical-path-audit-20261015T040839Z-v1/AUDIT.md)
traced the actual production sources and caller-pinned staging recipe. The published PR140 chain is
Rheos custody/recovery → metadata owner → ADA → **cutover planning closure**.
ADA's six effects remain exactly four initial-status writes and two empty
`dependency` clears. AFAC dependency registration and epic/capacity reconciliation
are separate obligations. Those obligations remain binding; the audit found no
ADA/Rheos invocation in the deployment admission/build/controller sources it
inspected. A subsequent adoption check below distinguishes the unmerged staging
recipe from adopted Services main. A dependency for completing planning does not establish an
executable deployment input or require every existing application owner to halt.
See the [epic's explicit planning-closure link](https://github.com/open-hax/foresight/blob/af07f19628482cac8e25ccebaf39b00d9ad9738a/docs/agile/kanban/epic-source-preserving-knoxx-embedding-cutover-82c55f16.md#L153)
and [ADA's ownership direction](https://github.com/open-hax/foresight/blob/af07f19628482cac8e25ccebaf39b00d9ad9738a/docs/agile/kanban/apply-adopted-rheos-metadata-repair-to-foresight-cutover-cards-217142ea.md#L31).

(世, p=1) Existing scoped application work and expressly permitted private source
composition/QA can continue. The [AFAC scope](https://github.com/open-hax/foresight/blob/af07f19628482cac8e25ccebaf39b00d9ad9738a/docs/agile/kanban/knoxx-durable-scoped-publication-receipts-b450407b.md#L31)
allows private adapter/source qualification in parallel with missing registration;
its actual dependency edges and accepted capabilities still precede readiness and
handoff. An existing UI or test owner does not admit a new crash-recovery,
migration, database-fence or application-decomposition scope. Source preservation,
a passing private test, native review approval and deployment admission each
supply different evidence.

## Services and Knoxx routes: main adoption versus caller pins

(世, p=1) Services owns topology, environment schemas and deployment orchestration.
Knoxx owns application behavior. The SDK is a separately pinned dependency.
Merged Services documentation at `a3034ccddbc43aecf34a70df444ebe0e5314f83c`
explains the [deployment model](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/docs/deployment-model.md),
[Website staging and promotion](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/docs/website-staging-deployment.md),
[CMS publication](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/docs/knoxx-cms-publication.md)
and [Ollama/embedding operations](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/docs/knoxx-ollama-embeddings.md).
Guide admission does not itself admit every later runtime/controller change.
The Website's exact-candidate exception applies only to its approved coordinate;
it supplies no Knoxx qualification credit.

| Route | Frozen source and actual behavior |
| --- | --- |
| Existing main caller; unmerged staging controller | Knoxx main's [environment promotion](https://github.com/open-hax/knoxx/blob/3409977bca4ba35e09967f9a99d50867a679a73c/.github/workflows/environment-promotion.yml) pins Services `bbbf0c2ab25d2a2cbadfbb69f7d18c984b3a285b`. That controller is source from still **OPEN/unmerged [Services PR83](https://github.com/open-hax/services/pull/83)**, whose observed current head is `1e7ad3dda3bb2a8c996425b3de6c802fffb020d1`. Services main `a303…` has no `scripts/build-service-environment.sh`. The pinned [workflow code](https://github.com/open-hax/services/blob/bbbf0c2ab25d2a2cbadfbb69f7d18c984b3a285b/.github/workflows/deploy-service-environment.yml) separates admission, read-only-credential build, serialized current-source/lease recheck and restricted receiver; its code requires a current testing lease or current merged-main staging SHA. **A caller pin does not adopt that controller into Services main or authorize rollout.** Local execution is private QA only. |
| Production | Services `a3034ccddbc43aecf34a70df444ebe0e5314f83c` [stack chain](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/.github/workflows/deploy-stack-chain.yml#L107) invokes Knoxx qualifier `662a1f8f9f522c7fc601aea6f857e36378bdecc2`, then uses immutable backend, frontend **and devtools** image digests. The later full cutover also needs its reviewed trusted admission/controller and locked staging proof. |

(世, p=1) The caller-pinned, unmerged staging [workflow](https://github.com/open-hax/services/blob/bbbf0c2ab25d2a2cbadfbb69f7d18c984b3a285b/.github/workflows/deploy-service-environment.yml#L70)
pins sibling `openplanner` SDK source
`07085d6557b75834ce6f50e6c54b8ca47e1c7c08`. Its [build script](https://github.com/open-hax/services/blob/bbbf0c2ab25d2a2cbadfbb69f7d18c984b3a285b/scripts/build-service-environment.sh)
uses the sibling already present; it installs/builds the SDK, runs
backend typecheck and normal tests, runs frontend typecheck/build, then saves
backend and frontend images with an archive/hash. Its backend base image is an
intermediate. **This two-image staging artifact is not the three-image production
release.** It also does not replace the application's genuine frontend coverage,
AVA, full lint or later production qualifier.

(世, p=1) That script's native SHA guard checks only source `HEAD`. Preparation
must separately bind clean source/controller revisions, the SDK revision and
actual SDK tree, and the build environment; explicitly record any intentional
overlay rather than treating HEAD equality as full-tree equality. It derives the SDK location from `dirname(source_root)` and
writes generated frontend files, including `Environment.Caddyfile`. Keep the
frozen composition pristine and build in a fresh isolated sibling layout:
`controller/`, `source/`, `openplanner/`, `artifact/`, with owned empty HOME and
Docker configuration, an explicitly reviewed environment and only the allowed
read-only repository credentials. Do not inherit production, deployment,
signing, provider or host credential state. The [isolated context proposal](/home/err/.local/share/openhax-codex/20261010/review-state/Knoxx-b637-KW-KD-KE-private-source-composition-v1/PRIVATE-UNEXECUTED-isolated-build-context-proposal.json)
records a proposed layout, not controller adoption or authorization to deploy.
Its earlier "accepted controller" label is superseded by this PR83/main check.

(世, p=1) The original audit and earlier report observations used "accepted" too
broadly for `bbbf…`; keep those historical records, but use this correction when
resuming. The [complete frozen Services main tree](/home/err/.local/share/openhax-codex/20261010/review-state/deployment-critical-path-audit-20261015T040839Z-v1/services-main-tree.json)
contains 570 entries, is untruncated and lacks that build-script path. Source
presence in a caller or an open PR is distinct from protected-main adoption.
Root's canonical readbacks found existing `services-knoxx-staging-migration` and
`services-staging-slot-pattern` owners each ready at three points; that readback
starts no work or metadata transition. Their source/PR83 review remains separate.

## Qualification and cutover requirements that remain

(世, p=1) Production [qualifier `662a`](https://github.com/open-hax/knoxx/blob/662a1f8f9f522c7fc601aea6f857e36378bdecc2/.github/workflows/deploy-production.yml)
requires the actual same-source application gates, coverage/integration/e2e and
browser translation evidence, builds, and **four distinct batches of 250
mutation tests with genuine kills**. The [mutation verifier](https://github.com/open-hax/knoxx/blob/3409977bca4ba35e09967f9a99d50867a679a73c/scripts/verify-production-mutation-evidence.py)
checks fingerprints and assertions. Old isolated tests or repeated mutants do not
qualify a new combined source. Failed and partial evidence must remain visible.

(世, p=1) The source-preserving change from the old `gte-large-en-v1.5`/1024
contract to `qwen3-embedding:8b`/1024 requires genuine SDK/Ollama/Mongo integration,
complete retained-source and graph coverage, accepted migration/verifier source,
and a database-enforced writer fence through inventory, apply, verify and
activation, including safe recovery/release. Equal dimensions or empty vector
stores do not establish compatibility or complete source coverage. The accepted
[a303 pre-copy probe](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/digitalocean/services/knoxx/probe-embedding-migration.js)
rejects incompatible running or stopped contracts and a populated fresh-store
inventory; it has **no authoritative migration-proof consumption branch**.
Stopping the old writer or hand-writing a receipt cannot supply that missing
capability. A reviewed Services consumer must follow the accepted application
migration/verifier.

(世, p=1) Crash-surviving pre-effect publication intent, unknown append/observation
recovery and Services persistence through publisher/container replacement remain
separately required. Restart/readback or an in-process retry is not the full
promise. After an admitted rollout, the user goal still needs actual Spanish
translation through Knoxx, persisted independent judgments, separate approval of
the exact immutable revision as a whole, reconciliation, an independent durable
public receipt and browser rendering, while preserving existing publications.
A healthy container or a German page returning 200 does not prove that journey.

## Dated progress and serving observations

(世, p=1) These are snapshots, not current guarantees:

- At the PR352 publication checkpoint, `c0fe3dceb64ffeb0ea1fa8e67388a6bde2a93155`
  was actually pushed. Four verified planning `Fixed` settlements addressed roots
  `4278471912`, `4278471918`, `4278471926` and `4278471931`; seven older threads
  remained open. New canonical [CodeRabbit](https://github.com/open-hax/eta-mu/pull/352#issuecomment-6184104212)
  and [Codex](https://github.com/open-hax/eta-mu/pull/352#issuecomment-6184111397)
  requests were posted. These planning/metadata corrections do not implement the
  future custody/recovery runtime or close cutover planning.
- Knoxx `b6377686f4899734d8dbbbe2094ca2d6ccdf1e73` contains the actual one-file,
  six-warning test cleanup. Owning lint and compiler warnings are zero; 75 Node
  tests and 1,918 CLJS tests / 9,921 assertions pass. Full primary lint still has
  seven errors and 259 warnings. The real Mongo DNS failure remains a live
  availability limitation. [Exact source/gate custody](/home/err/.local/share/openhax-codex/20261010/review-state/Knoxx-K1c70-run-steps-actual-GREEN-v1/ROOT-READY-actual-one-test-path-run-steps-GREEN-and-full-lint-limits-v1.json).
- The new private combined candidate is
  `19f8f2d7e6e7684c93df8b620ad83ffc0ec30b1c`, tree
  `608785ef213300366dfe59063e5366ea653bd71f`. Ordinary KW → KD → KE merges preserve
  all owned blobs/deletions and exact ledger suffixes. At its frozen pre-build
  handoff, no build had run. The [composition manifest](/home/err/.local/share/openhax-codex/20261010/review-state/Knoxx-b637-KW-KD-KE-private-source-composition-v1/ROOT-READY-frozen-private-source-composition-and-isolated-build-context-v1.json)
  supplies preservation evidence, not qualification, readiness or rollout. Build
  and adoption results require their own later records.
- Private Bluesky plan `b699ba14e192939435bd5cdf0147f6472c53e426` is held. The
  [PR390 snapshot](/home/err/.local/share/openhax-codex/20261010/review-state/Knoxx-PR390-fb7-seventeen-thread-current-native-and-b699-triage-v1/root-ready-complete-seventeen-thread-native-and-source-backed-b699-triage-v1.json)
  records 17 native roots at public `fb7a603abc725333ff994b61c02d6e3591c58640`;
  private planning corrections are not admitted application source.
- PR354 rerun job `116238282805` completed **SUCCESS** at 04:16:18 UTC, and actual
  native [APPROVED review `5527965693`](https://github.com/open-hax/eta-mu/pull/354#pullrequestreview-5527965693)
  was published at 04:16:14 UTC on `74d761c05f1a7ea60a8d9f9b75a7ac8933d2b3da`.
  Accepted full-input review artifacts were then independently verified. At
  **04:28:47 UTC**, one ordinary protected exact-head merge produced main
  `fefa6dd52135e1045b1ad56802a346dc0779970c`, parents `0a4b8a8c…` and `74d761c0…`,
  with the reviewed tree preserved, 16 passing checks, zero threads and unanimous
  CodeRabbit/Codex/MiMo approval. [Final merge proof](/home/err/.local/share/openhax-codex/20261010/review-state/PR354-74d-rerun2-terminal-accepted-native-recovery-v1/ROOT-READY-PR354-rerun2-accepted-native-review-and-actual-protected-merge-v1.json)
  and root's 91-descriptor verification establish this merge without auto-merge
  or administrator bypass. This adopts the CI cwd repair; it does not qualify
  native rejection assessment, the general metadata engine or CMS deployment.
- Private local execution of the caller-pinned, unmerged Services recipe ran from
  **04:30:10.976 to 04:31:45.568 UTC** on exact `19f8f2d…`, SDK `07085d65…` and
  controller `bbbf0c2…`. [Retained v2 result](/home/err/.local/share/openhax-codex/20261010/knoxx-local-staging-build-context-19f8-SDK07085-bbbf-v1/evidence/actual-local-build-v2.result.json)
  records **exit 1**. SDK cache/doc-hydration builds and SDK TypeScript build
  passed; backend typecheck passed with 548 files, 514 compiled, zero warnings.
  The combined candidate's Node run had **84 tests: 82 passed, two failed** on
  the missing absolute auxiliary `KNOXX_NATIVE_ERROR_KONDO` executable. Compiled
  CLJS tests, frontend checks and Docker were not reached. The first wrapper
  had stopped before intent/child launch on an overly strict Docker-directory
  guard; v2 preserved that refusal and accepted the inspected empty-auth client
  configuration. The disposable SDK overlay deliberately omits its tracked root
  `.env`; code/package/lock bytes remain unchanged, but its full tree is dirty.
  This is no exact-full-SDK-tree or hosted qualification. Root is preparing a
  separate explicit-tool-path rerun; its outcome requires a later record. The
  earlier 75/1,918 test counts belong to K's isolated slice, not this composition.
- The subsequent **v3 private local run, 04:33:59.874–04:37:17.517 UTC**,
  [retained exit 1](/home/err/.local/share/openhax-codex/20261010/knoxx-local-staging-build-context-19f8-SDK07085-bbbf-v1/evidence/actual-local-build-v3.result.json).
  Node passed **84/84** after the verified auxiliary path was supplied. CLJS ran
  **1,943 tests / 10,740 assertions, one failure and zero errors**, compiling
  892 files / 891 compiled / zero warnings. The sole failed genuine SVG Chromium
  render encountered an overlong Unix socket path under the deep private TMPDIR.
  That trace identifies an environment limitation, not an established product
  regression; the suite nevertheless failed. Frontend and Docker remain
  unreached. Root is changing only TMPDIR to the owned short
  `/home/err/.cache/ohx19f8-v4` for an unchanged-script v4 rerun; no v4 terminal
  result is recorded here. Production qualifier `662a…` already uses the actual
  target's [local Knoxx setup action](/home/err/.local/share/openhax-codex/20261010/knoxx-private-deployment-composition-b637-KW-KD-KE-v1/.github/actions/knoxx-setup/action.yml),
  which prepares the native-error auxiliary. The missing-tool gap in v2 belongs
  to the legacy/caller-pinned staging recipe; it is not a demonstrated production
  qualifier omission.
- At **2026-10-15 04:25:12 UTC** (October 14, 23:25:12 CDT), Website and the three
  selected Knoxx containers were running/healthy, and public home, English
  Garden and German Garden all returned HTTP 200. Their selected container/image
  fields were unchanged from 03:46:51 UTC: Website coordinate
  `6fd48ac5df9a16e414efbbf2b6abe98ad331be78`, digest
  `sha256:ef3f2daa46b212cfcd82c2a72492946b0c44c23f1fab9b0fffde35d7199c836f`;
  old Knoxx source tag `841642adffa2656edd842db72843da0a0f4d8336`, with devtools SDK
  `07085d6557b75834ce6f50e6c54b8ca47e1c7c08`. [One-shot serving proof](/home/err/.local/share/openhax-codex/20261010/review-state/child-user-status-live-website-cms-20261015T042508Z-v1/ROOT-READY-one-fresh-serving-snapshot-and-prior-comparison.json).
  No fresh browser, publication-manifest, authenticated CMS, database or provider
  translation proof was collected; no new selected CMS image set was observed.

(己, p=1) Resume from the actual combined-source build and its recorded outcome,
then pursue each remaining owning capability and the admitted locked rollout.
Append later results to the report rather than rewriting these dated observations.
The existing [OpenCode free MiMo routing memory](opencode-free-mimo-routing.md)
remains separate; this deployment-routing update does not change it.

License: GPL-3.0-or-later.


## Recovered Services development delivery and held source custody

(汝, p=1) The user's ordinary human message in “Triage Foresight PRs and cards” at2026-10-06T13:53:53.357Z requires development PRs on personal forks, with final integration through a separately qualified release/deploy PR into origin. Root verified the actual native user row, role, message ID and SHA256 directly in the [retained session](/home/err/.codex/sessions/2026/10/06/rollout-2026-10-06T08-05-36-01a11151-cd7d-7ec0-8dfe-84229d767a2e.jsonl:1090); source-row SHA256cfb9aecd90793e2b08160578e94042720ec8a32d3cff3d845f9f4bc3680ab37c. The [recovered proof](/home/err/.local/share/openhax-codex/20261010/review-state/Services-staging-native-error-aux-prepare-readonly-admission-v1/recovered-direct-user-personal-fork-policy-v2.json) distinguishes that direct instruction from PR comments or generated summaries. Resume Services development using that delivery route while preserving existing work and each PR's actual scope.

(世, p=0.99) The Services [owner and recovery handoff](/home/err/.local/share/openhax-codex/20261010/review-state/Services-staging-native-error-aux-prepare-readonly-admission-v1/ROOT-READY-complete-Services-owner-current-PR83-aux-proposal-and-held-path-recovery-v2.json), SHA2569b4d4598f52124e5ac7e6b0016cda84fc42ba50be798a40cdeeb29cb4876c825, records existing ready3 staging ownership, PR83's current sixteen-root review corpus, and separate open personal-fork synchronization/bootstrap-planning PRs. Their current scope does not incorporate the controller feature. Earlier unpublished empty-runtime REDbdc4eff and GREEN35ef7d2 are confirmed by actual retained tool output, but the original disposable checkout and Git store are gone. Full forty-character commit and tree identities were not recovered. Deterministic test/source changes can be reconstructed with explicit provenance, new actual commits and fresh RED/GREEN; do not fabricate the lost Git history or transfer old gate credit to a new tree.

(世, p=0.99) The native-error helper is application-owned in private candidate19f8f2d, introduced by d06cfc0e; public Knoxx main3409977 does not contain it at this checkpoint. Its use in private tests does not establish public adoption. The helper's official archive/binary verification stays with the app; Services' proposed compatibility repair calls that helper in the unprivileged build only and keeps deployment credentials in a separate job.


## Later private build result — October 15, 04:50 UTC

(世, p=0.99) Buildv4 completed successfully at04:50:12.296UTC:84/84Node,1943CLJS tests/10741assertions/0failures/0errors, backend/frontend typechecks and frontend build, with zero compiler warnings. Its2,117,118,464-byte two-image archive matches SHA2561337a148e5409004c4a3b116355f3a364ab1c110b18dbe48e26088dc51939086. Backend/frontend final users1000/1000:1000 and original frozen composition/SDK preservation are independently verified in the [actual build proof](/home/err/.local/share/openhax-codex/20261010/knoxx-local-staging-build-context-19f8-SDK07085-bbbf-v1/evidence/ROOT-VERIFIED-v4-actual-two-image-build-archive-and-nonroot-users.json), SHA2569e6e9fe2f37e94849754900abc22ecf850e7d115b440c618909a517718fbcd9a.

(己, p=0.99) Keep the explicit SDK.env absence, required auxiliary and shortTMPDIR overlays, previous failures and pnpm/Docker packaging warnings. No source/test suppression, application runtime, registry push, staging or production deployment occurred. The controller remains unmergedPR83 source and this artifact contains only two images. Full release qualification, migration/fence/recovery/storage, reviewed controller adoption and live Spanish publishing remain required. Resume from this actual result and the appended report; do not repeat the already successful unchanged build without a new failure or changed source.



## October 15: frontend test coverage boundaries

(己, p=0.99) The additional registered frontend CLJS suite ran once against the built19f source and unchanged reviewed environment:464tests/2114assertions, all67 frontend namespaces, zero failures/errors/compilerwarnings. All source/index/Caddy/SDK/original bytes and the existing image archive were preserved. The emitted missing-revision Git error is the intentional thrown-error negative control, not a hidden suite failure. This is distinct from the already recorded267passing frontend TypeScript tests; their41test.todo matrix declarations were not executed. Source audit found the matrix is metadata-generated and does not establish41missing runtime features; separate interaction tests cover translation behavior. Eight Router warnings arise from four test wrappers; productionroot already supplies both future flags. Raw9.52%V8 coverage and its generated-output denominator remain as observed. None of these local results supplies genuine provider/database integration, hosted release qualification, controller adoption or a live CMS publication. [Actual frontend CLJS proof](/home/err/.local/share/openhax-codex/20261010/review-state/Knoxx-19f8-built-context-one-registered-frontend-CLJS-v1/ROOT-READY-one-registered-frontend-CLJS-actual-result-v1.json).


(世, p=0.99) The new user-status snapshot at2026-10-15 05:09:39UTC (October15,00:09:39CDT) finds Website and existingKnoxx backend/frontend/devtools running and healthy; publichome, EnglishGarden and GermanGarden returnHTTP200. All selected container/image/digest/user/OCI/health fields are unchanged from04:25:12. Website remains6fd48ac5@ef3f2daa; Knoxx remains841642ad. This is the older servingCMS, not the new19f candidate or a verified Spanish publication. No browser/authCMS/provider/database/preservation-content check was included. [Fresh serving proof](/home/err/.local/share/openhax-codex/20261010/review-state/child-user-status-live-website-cms-20261015T050936Z-v1/ROOT-READY-one-fresh-serving-snapshot-and-prior-comparison.json), SHAefa33af4ec789a24c8935d6cb2654d6e5ed715f17c796ba9b3e3ea34dfc381e3. The current deployment goal remains active.


(世, p=0.99) Fresh canonical predecessor reads at05:07:53–05:09:18UTC identify the existing ready2 services-caddy-hostname-scale-decision before ready3 services-staging-slot-pattern, then ready3 services-knoxx-staging-migration. These scoped owners govern the controller work; no transition occurred. The smallest next admission is the existing bounded hostname/certificate decision through canonical Rheos, followed by the dependent slot/controller source work. Personal-fork ServicesPR1 remains a separate four-path review-workflow synchronization, blocked with4pass/1fail/no trusted approval/no completed planning round; it includes noPR83 controller. Its hosted failure concerns missing GitHubApp publication credentials (fresh secret-name inventory total0), which is separate from anonymous credential-free MiMo inference and outside that narrow sync card. No secret values or credentials were read/changed. Origin Services remainsa303 and personalmain214882. [Complete Services next-admission evidence](/home/err/.local/share/openhax-codex/20261010/review-state/Services-personal-sync-PR1-predecessor-readonly-admission-v1/ROOT-READY-current-personal-PR1-and-canonical-predecessor-next-admission-v1.json), SHAfa1ed93b81313a26f4add8812b0e1dc7ecf407ce753f39ade9bda269e089bd20. No new board/source/PR/model/deployment effect occurred in this recovery.


## October 15: Services decision published; historical approval test repaired locally

(世, p=0.99) Existing two-point services-caddy-hostname-scale-decision was admitted through canonical Rheos ready→todo→in_progress, with its intended labels preserved by the canonical scalar operation. The source decision inventories18names as7current-main+8unmergedPR83+1planned+2reserved, retains default exact-name HTTP/TLS-ALPN automation, records local20-name/20planned-issuance review ceilings and state migration mitigation, and preserves every runtime assignment. [Personal Services PR3](https://github.com/riatzukiza/services/pull/3) was actually created atb3af8062 over a new integration base pinned to accepted originmaina303; personalmain and separate PR1 are unchanged. Neither the decision nor a caller pin adopts PR83 or deploys CMS.

(己, p=0.99) Native Code Quality failed its existing real Website-approval test because current public input bytes differ from the historical approved record. The existing owner canonically admitted a one-test repair. Actual registered RED14tests/1failure preceded GREEN14tests/0failures/0errors; independent review found no findings and verified all107input modes/members with106other byte hashes unchanged. Source3fa2759d8e81d523246b9659402c7fbf5e2ae2b4 changes only scripts/test-website-owner-decision.py (+9/-4); Services provenance38aac19c74490e8cedac4bfbe4fbead6c35c67d8 and Knoxx scope/provenance32192a7d4757a9bc1d4956677ae18a4540cb227c preserve ledger prefixes. The approval record and controller remain unchanged. Approved historical inputs must refuse fresh promotion after input drift; do not rebind an old exception just to make a test pass. Added real Caddy comment-drift controls exercise select and qualify without mocking admission. [Exact source and qualification proof](/home/err/.local/share/openhax-codex/20261010/review-state/Services-PR3-b3af-historical-Website-binding-test-repair-private-proposal-v1/approved-execution/actual-root-qualified-one-test-source-commit-proof.json).

(世, p=0.99) At05:54UTC, actual Codex review5528640534 on publishedb3af added three valid P2 roots:4279457615 stale central ingress guidance,4279457625 undocumented default ZeroSSL fallback,4279457630 omitted mounted managed route fragments during migration. The held38aa test repair addresses none of those documentation roots. Root held the push and body update before any effect, then recorded their documentation repair scope through canonical Rheos. The two original author-only conversations and native publication-authentication failure remain separate obligations; no self-settlement, fake native approval or extra review request was made. [Complete immutable native intake and publication hold](/home/err/.local/share/openhax-codex/20261010/review-state/Services-PR3-three-native-Codex-P2-findings-before-38aa-publication-v1/ROOT-READY-three-native-Codex-P2-findings-complete-source-and-38aa-publication-hold-v1.json).


(己, p=0.99) Existing runtime ownership was recovered without creating duplicate cards or changing their readiness. SDK boundary6cc1e1ca and database fencec002072c are incoming5; migration11bc1411 depends on actual runtime/SDK capabilities; pre-effect attempt custodie945de08 and storagea3ca141e require durable/replacement proof. The private publication code still has key-only inflight state and in-memory prepared/retry state; a mounted /app/state alone does not select a durable receipt store. Private interface776f6a50 is not ancestor of accepted Knoxx main3409977, so PR140's actual AFAC source/edge-admission findings cannot be closed by prose clarification alone. Private source QA may continue within admitted scope; it supplies no readiness, accepted interface, writer fence or complete migration. [Actual owners and concrete source gaps](/home/err/.local/share/openhax-codex/20261010/review-state/Knoxx-19f8-runtime-migration-fence-crash-owner-readonly-v1/ROOT-READY-existing-runtime-owners-concrete-gaps-and-smallest-parallel-scope-v1.json).

(己, p=0.99) No later production check or rollout occurred in this update. The last serving snapshot remains05:09:39UTC: approved Website6fd48ac5@ef3f2daa and older Knoxx841642ad healthy; replacement19f images remain local. Complete application qualification, accepted controller/source, retained-source migration and database fence, crash-safe publication/storage, locked staging/all-three-images and the actual Spanish whole-approval/public-receipt/browser journey remain outstanding. The complete deployment goal stays active.
