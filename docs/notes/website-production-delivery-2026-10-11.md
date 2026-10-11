# Website production delivery, 2026-10-11

SPDX-License-Identifier: GPL-3.0-or-later

The approved Website candidate is live at
[open-hax.promethean.rest](https://open-hax.promethean.rest/).
[Services PR96](https://github.com/open-hax/services/pull/96) was merged as
`d965cbfe5281b066877d37a4d642c9e47819ab01` at `22:46:36Z`.
[Deploy Stack run38181837478](https://github.com/open-hax/services/actions/runs/38181837478)
then completed successfully at `22:49:30Z`.

The reusable deployment procedure and candidate admission record belong to
Services. Read its [candidate promotion guide](../../services/docs/website-candidate-promotion.md)
and [exact owner decision](../../services/digitalocean/qualifications/website/6fd48ac5df9a16e414efbbf2b6abe98ad331be78.json).
This note records the completed execution; the guide's pre-deployment proposal
wording is the historical description of the reviewed source.

## What was deployed

| Coordinate | Observed value |
| --- | --- |
| Website source | `6fd48ac5df9a16e414efbbf2b6abe98ad331be78` |
| Immutable image | `ghcr.io/open-hax/website:6fd48ac5df9a16e414efbbf2b6abe98ad331be78@sha256:ef3f2daa46b212cfcd82c2a72492946b0c44c23f1fab9b0fffde35d7199c836f` |
| Services source | `d965cbfe5281b066877d37a4d642c9e47819ab01` |
| Serving configuration SHA-256 | `f603c3b0d3cf3f8534294b1b28c57de73f2d7dc4cf5160bd7a1c7de99b13b3e5` |
| Public deployment input manifest | 107 files, aggregate `93565f503c3553d3d1f8a15755f7e02f13e3a4f809dd699c84929e39a096969c` |
| Qualification | `owner-decision`; `mutationCampaignPassed: false` |

The main-branch workflow used isolated Website scope and the existing approved
image. Its Website build and other service deployment jobs were skipped. The
host lock covered fresh staging, staging proof, owner-decision admission,
production, and production proof. The three native proof artifacts bind the
same source, image, Services revision and configuration. The qualification's
embedded staging receipt equals the separately retained native staging proof.

## Live verification and preserved publications

A pinned SSH observation at `22:52:30.531221Z` found the production container
`77c285c0357f3f0e915f59afd7c26a5f4a18ec73ec47ec71a73a346f52ac319e`
running and healthy on the approved digest, with actual UID101, loopback port
8081, the Website network alias, and three read-only mounts for serving
configuration, published content and music. Staging remained healthy on the
same digest at loopback8082. The other nine production containers retained
their previous IDs and images and were healthy.

The existing English and German Garden publications were preserved. Their
public manifest remained 1004 bytes with SHA-256
`61bcd08ad5de09374f75775026cf0fa83d9fe521e450c624db1565c47cec933e`.
Thirty-two HTTPS checks passed: site and locale routes, the Garden manifest and
its two existing artifacts, all18 selected graphics, and four private-path404
checks. Private error response bodies were not read. Browser checks rendered
the gallery, six Suno song links, GitHub contact, and English and German Garden
content after asynchronous loading.

The local retained evidence directory is
`/home/err/.local/share/openhax-codex/20261010/review-state/production-path-current-recovery-v1`.
`approved-website-live-delivery-witness-v1.json` has SHA-256
`24ce180bc04191669a77f2eef8c0b1886e01a4bca6816c82313e3f4495674b3d`.
It binds the normal completed native captures, proof artifacts, public HTTP
results, and separately labeled root browser observations. The native run's
proof artifacts remain the public, hosted execution evidence.

## Remaining hosted CMS work

This run did not deploy Knoxx. Its production backend and frontend still use
`841642adffa2656edd842db72843da0a0f4d8336`, which lacks the native CMS creation
route. The successful local CMS/translation demonstration and staging
art/music publication bundle do not establish hosted CMS history, intent,
provider execution, split review, or whole-output approval. Those require the
separately reviewed and qualified Knoxx rollout, followed by native CMS source
creation and the Knoxx translation/publication path.

No staging publication state was copied into production and no private writer
state was read, copied or replaced. The owner exception applies to this exact
Website candidate only. It supplies no Knoxx qualification or completed
mutation-campaign credit.
