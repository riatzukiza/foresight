# Explicit signed field cell indices in the selected numeric contract

License: GPL-3.0-or-later.

## Signal

(己, p=1) This narrow planning amendment belongs to existing selection task
`b5f2ba9e-8d67-4c73-9919-e275983aca87` and physical task
`ff3e628b-cd5c-4a89-a397-42cc88dbde70`. It clarifies one numeric domain needed
by the already accepted finite B1/B2/B3 inventory. It creates no task, new
implementation inventory, estimate, board transition or dependency mutation.

The candidate selection admits floor-derived field cell indices as signed exact
safe integers within `[-(2^53-1), 2^53-1]`. Counts, wallets, elapsed time,
residuals, backlog and RNG counters remain exact nonnegative safe integers.
Positive cell size, finite binary64, 2D world units, the selected solver owner,
seed/time semantics and replay tolerance are retained. An invalid quotient or
index refuses the whole transition; no clamping, wrapping or shifted origin is
used to hide a negative coordinate.

## Evidence

(世, p=1) The original selected Git blob at
`6a4438982fdf1eca98f856748bb23b19ab017c0a`, path
`docs/notes/2026-10-07-cephalon-field-kernel-selection.md`, is SHA256
`4bd35dba59a5cc01020faed2ead59d885f46f889510a0414db4e4b9c6fde122b`.
Its adapter integer statement admits `[0,2^53-1]`, while its sparse keys use
`floor(x/cell-size)` and `floor(y/cell-size)`. Negative world coordinates can
therefore create a floor index the positive-only adapter rule does not admit.
Original source history and receipt bytes are retained; the candidate does not
relabel the earlier decision as already supporting signed indices.

(世, p=1) Actual native Codex finding
[4275090795](https://github.com/riatzukiza/eros-eris-field/pull/2#discussion_r4275090795)
identified that conflict in the owning Eros/Eris proposal. Its prospective plan
and ABI now name the signed exception and require this matching coordinator
amendment before implementation. Owning [PR2](https://github.com/riatzukiza/eros-eris-field/pull/2)
is still a source proposal; it has no implemented kernel, storage or live proof.
Its current source/checks/reviews remain an independent obligation.

(世, p=1) This PR starts from the actual personal character lineage
`04acc6dc9f3fa1fb5c071e37efcf3b616d06a736`, branch
`codex/cephalon-personal-review`, after the independently merged kernel and
receipt-anchor work. Unrelated personal main is not the selected character
base. No Root operational ledger or status is copied into this planning branch.

## Frames

(己, p=0.97) The floor-key rule naturally produces signed logical coordinates.
The explicit exception keeps that physical geometry and makes its adapter
representation lawful without broadening wallets, counters or time.

(己, p=1) This is a domain-selection refinement, not a new solver or proof that
the corresponding adapters accept it. Source qualification and native readiness
must precede RED/GREEN; old review approval does not transfer.

## Countermoves

(己, p=1) Required successor cases include negative coordinates on both axes,
cell-boundary behavior, both signed limits and overflow/nonfinite refusal,
while negative wallets/counts/time remain denied. The owning solver and
checkpoint/restore boundary must bind this exact amended decision revision.
Discrete cell identities still match exactly; continuous replay tolerance does
not excuse different cells or paths. No property/domain test is claimed here.

(己, p=1) After independently qualified planning MERGEs, consume the actual new
decision and matching ABI revision and revalidate the native predecessor
relationship. No larger story may become Done from this document. The accepted
field/mood/automatic-recall proofs remain outstanding; heartbeat stays paused.

## Next

(己, p=1) Obtain independent planning review of the explicit field-index
exception before consuming it in the owning kernel.
