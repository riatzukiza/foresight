# Frozen PR27 receipt suffix

This fixture is copied byte-for-byte from the immutable `.ημ/receipts.edn`
append between base `1069d4c9bcba29ef55e6c72c7c597de101f0520b` and head
`bdac1ed6470c98128ff367e757132797a39ac436` in `riatzukiza/foresight#27`.
The intermediate source is `91a6fc6b27264da4b8c9b8cd7919f153adea0ff2`.
`manifest.json` records the measured full-ledger and suffix hashes and byte counts.
The suffix is 60487 bytes, nineteen physical lines, originally numbered260–278.
Eleven original maps267–277 have malformed envelope metadata; line278 contains
the documentary correction. No original byte, timestamp or failed observation
is normalized in this fixture.

The adapter suite supplies259 blank lines solely to retain absolute physical
ordinals, recomputes each original including-LF hash and exercises the pure
decision using explicit fixture facts. Those blank lines are not the historical
base; these supplied facts are not a Git ancestry proof. Disposable real-Git
fixtures independently exercise source objects, ancestry and exact-byte matching.
The local real-case command uses the actual immutable base/source/head objects:

```sh
nbb -cp src:scripts scripts/evidence.clj verify-receipts \
  --base 1069d4c9bcba29ef55e6c72c7c597de101f0520b \
  --at bdac1ed6470c98128ff367e757132797a39ac436
```

That local check is preparation. PR27 still requires ordinary integration of
the independently reviewed reader, fresh current-head hosted gates and native
settlement; a fixture or local pass grants no merge or provider approval.
