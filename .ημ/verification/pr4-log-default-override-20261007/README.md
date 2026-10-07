# PR4 exact log-default override

This local correction is an ordinary successor of published PR4 head
`cc5dd3550b3bac2ef98bd576fadc5fe6f7f330e2`, based on personal sync
`96a6dca24cb7a14b041bdd6e3e7922c568238da9`. There was no checked-in
CodeRabbit configuration at the source head. The only product setting added is
`reviews.path_filters: ["**/*.log"]` in `.coderabbit.yaml`.

The current official [path-filter documentation](https://docs.coderabbit.ai/configuration/path-instructions)
states that an exact default ignored pattern without its leading exclusion
marker disables that default, without restricting the review to an include
allowlist. The default log rule is `!**/*.log`; broader includes do not override
it. This correction uses only that exact override. No local matcher duplicates
vendor semantics. YAML parsing and the fetched official schema validate the
configuration shape; they do not prove hosted input coverage.

Root's native reviewer acknowledgement records five log files omitted from the
previous whole23-path PR scope. All23 original paths, all gitlink identities,
all six CC1.01 criteria, source law/test/workflow bytes, board events and prior
receipt/reflection prefixes are preserved. A future native review must actually
consume the complete successor, including these log artifacts. The previous
head's approvals and scoped repair acknowledgements do not qualify the new
head. The separate `.agents` issue24 defense against admitting explicit omitted
input remains unresolved by this vendor configuration change.

No billing, paid mode, review-gate, auto-review, workflow, trust identity,
board state, reviewer request or deployment setting is changed. The observed
PR was Ready, blocked and automatic-merge off at the native preflight. This
local source preparation changes no native state. Root alone may publish after
independent peer and fresh source/native guards, and must serialize any automatic
review triggered by a push. Historical GitHub500 observations do not become
passing qualification; a local successful read is not a guarantee of later
publication availability.

`command-collection.json` contains the scoped preparation observations and
validation commands. Query-bearing connector URLs are withheld before display
persistence; raw byte counts/hashes distinguish those sanitized views from raw
native data. Official documentation is represented by source/hash provenance and
a short derived description, not a claim that sanitized HTML is lossless.
The initial local donor lacked the requested source commit; its failed checkout
and import are retained. The exact personal commit was then fetched into the
new independently owned complete store. No previous source or audit was edited.
No bootstrap/runtime tests are rerun for this configuration-only product edit.
Actual immutable root receipt admission and current Receipt River suffix checks
are recorded outside the source after commit with their separate scopes.
