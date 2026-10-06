# Original PR116: personal-fork archaeology preservation

The ordinary candidate merge preserves original source `87281597088c7cfefda1135efa63b6abdece8fea` as an ancestor of the personal synchronization base `96a6dca24cb7a14b041bdd6e3e7922c568238da9`. All six original archaeology artifacts for `2fd1f2d9-97e0-44e8-9494-98e1c63c08f6` are byte-identical; `source-fidelity.json` records SHA-256 and source Git blobs. Historical receipt, reflection, and Rheos-event prefixes are retained.

## Observed admission

The historical private NBB1.3.204 profile admitted 29 declared resources and two schema revisions through Clio and Foresight's existing archaeology API. The target projection had the expected parent, `:continues` relation, three findings and 4 evidence items. That result is historical evidence only.

The current CI-pinned NBB1.4.207 profile rejects a numeric-leading keyword in this run's immutable `relations.edn`, through canonical `clio.shape.edn/read-one` / `clio.infra.ledger/read-ledger!`. The actual admission process exits 1. See the preserved failure log and target diagnostic. The diagnostic wrapper catches and prints the error; its exit status is not an admission pass. The complete projection stops at a legacy invalid relation keyword; the target read independently rejects this run.

Clio is pinned to `788cdd3434615a7932b924e68520dbf7f88408c2`, Katamorph to `3bd4cf26e68dc88fbe67f831baa4bc389e3363e7`, and Node to24.14.1. Dependencies and compiled artifacts are isolated in `/home/err/.codex/parallel-goal/personal-root-20261006/pr116-admission-runtime`; no shared runtime, dependency installation, repository settings or service was changed. The private runtime was upgraded after retaining the historical log; the current package version and failure are1.4.207.

## Blocker and boundary

[Foresight135](https://github.com/open-hax/foresight/issues/135) owns reconciliation of legacy EDN admission and prevention at the responsible writer boundary. This candidate does not rewrite event identity, normalize old records, weaken readers, pin an obsolete runtime, or implement a second ledger engine. The incoming planning card is authored in the PR115 replacement; it must receive planning review and Rheos-ready admission before implementation.

Current CI qualification and fresh native review remain blocked. Reviews, approvals and runtime passes on the original PR do not transfer to this personal-fork head. Final integration is a qualified release/deploy PR into origin.

Digest clarification: the retained initial `source-fidelity.json` field named `candidate_blob` contains a SHA-256 digest. `git-blob-fidelity.json` records the actual source and candidate Git blob IDs separately and confirms equality for all six artifacts. The original observation is retained; no archaeology artifact changed.
