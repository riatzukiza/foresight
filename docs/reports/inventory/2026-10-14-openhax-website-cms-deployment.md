# OpenHax website and CMS deployment observation — 2026-10-14

(世, p=1.0) The website is live. At 03:51:48 UTC, its production container was running and healthy on the approved configured coordinate `6fd48ac5df9a16e414efbbf2b6abe98ad331be78`, registry digest `sha256:ef3f2daa46b212cfcd82c2a72492946b0c44c23f1fab9b0fffde35d7199c836f`. This is the selected image coordinate; the earlier OCI revision label is recorded separately in deployment evidence.

(世, p=1.0) At 03:49:31 UTC, fresh HTTPS requests to the [website](https://open-hax.promethean.rest/), [English Garden](https://open-hax.promethean.rest/garden/), and [German Garden](https://open-hax.promethean.rest/de/garden/) each returned HTTP 200. These requests verify HTTP delivery. No fresh browser rendering or publication-manifest preservation check is claimed by this observation.

(世, p=1.0) The new CMS is not deployed. The production Knoxx backend, frontend and devtools remain on `841642adffa2656edd842db72843da0a0f4d8336`; all three containers were healthy. The native CMS create, provider translation, persisted split reviews, separate exact-revision whole approval, reconcile and public browser journey remains outstanding.

(世, p=1.0) [Services guide PR 97](https://github.com/open-hax/services/pull/97) was previously verified merged into protected main `a3034ccddbc43aecf34a70df444ebe0e5314f83c`. Guide admission is separate from the remaining runtime deployment-controller admission and owning application qualification.

(世, p=1.0) The active old backend uses `gte-large-en-v1.5`/1024 via Proxx. The reviewed target uses `qwen3-embedding:8b`/1024. The [immutable Services gate](https://github.com/open-hax/services/blob/a3034ccddbc43aecf34a70df444ebe0e5314f83c/digitalocean/services/knoxx/probe-embedding-migration.js) refuses an incompatible active or stopped contract, and has no authoritative migration-proof input. Equal vector dimensions do not establish model compatibility.

(世, p=1.0) The new production read-only inventory found 736 retained events and zero rows in compacted memories, hot vectors, compact vectors, graph embeddings, partition registry, graph views and graph labels. The graph embedding collection has a READY, queryable search index. The active writer was not stopped, so these counts are an operational observation, not a locked migration certificate or complete source-coverage proof.

(世, p=1.0) At 03:30:56 UTC, the expected reviewed Ollama route responded with version 0.33.2. `gemma4:e2b` was installed; `qwen3-embedding:8b` was absent. The reviewed host bootstrap covers the Qwen installation. Catalog availability does not prove effective actor settings, inference or a completed translation.

(世, p=1.0) At 03:49:50 UTC, [eta-mu run 38490599695, attempt 2](https://github.com/open-hax/eta-mu/actions/runs/38490599695/attempts/2) remained in progress on `2d24396ec89ab49df335633cc65314992f23cddd`. Muse and deterministic evidence jobs passed. The MiMo review job was still executing its bounded OpenCode review step. No final gate, review completion, protected source adoption or publication is inferred.

(己, p=1.0) The next owned work is the smallest Knoxx migration/coverage contract for the observed empty-projection, retained-source case, with an explicit source-preservation and writer-lock proof; Services must consume that reviewed proof. Existing qualification/review obligations, rollout health checks and the live Spanish publication journey remain required.

## Evidence custody and limits

- Production runtime and selected Mongo metadata: 2,037-byte captured stdout, SHA256 `591dd9a4f6a5ee3a172a972fec700948a3a7cf2844b5a5ede3bca3b764789831`; normal exit 0, cleanup complete, output untruncated.
- Actual production host: `open-hax-services-production`, SSH endpoint `157.245.125.134`, deploy UID 1000. Selected backend container: `92346c9e74b47fc816eefaae4294b0e9ed1a20545bf3e9a3f40263561095d7b5`.
- SDK source map: pinned `07085d6557b75834ce6f50e6c54b8ca47e1c7c08`, map SHA256 `413431c0fd5771930f1f88e6d2541ca4856d6a565e10b53359d589dec1a351e1`; no companion SDK change is needed to reuse existing materialization boundaries.
- Current review snapshot: 3,710 bytes, SHA256 `f13a51ea895aa55887cb5dbf5c1766974b1a3a01f9d9f4f8f6d3dfdeaff4f1c3`.
- The inventory used a direct Mongo driver read connection. It did not instantiate the SDK, create indexes, write the database, invoke a model, stop a writer or alter the deployment. Only counts, model/dimension groups and index status were returned; no source text or credential value was returned.
- The guessed root-level manifest file was absent and was explicitly recorded as unobserved. This does not show that the real mounted publication manifest is missing. Its previously verified preservation remains dated earlier evidence.
- This observation grants no review, approval, round, qualification, migration or deployment credit. The deployment goal remains active.

License: GPL-3.0-or-later.
