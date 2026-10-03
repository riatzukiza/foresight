# Rheos e37 native-findings repair — isolated qualification

The final candidate is ready for parent inspection and transplant. It is uncommitted in `checkout`, whose HEAD remains `e37dd504da144c35148965e129438cb9c3c1b8e8`. No canonical edits, commits, pushes, board actions or review requests were made by this agent.

## Reviewed defect and repair

Native MiMo review `5401618354` on e37 requested changes. Its HIGH creation finding and MEDIUM core-field diagnostics finding were independently reproduced with the compiled CLI. The native body admitted static analysis and misstated deterministic gate names; actual execution evidence is preserved separately.

The shared portable serializer now escapes double quotes, backslashes, C0 controls, YAML line separators and UTF-16 surrogate code units in both scalar strings and existing stringified vector items. The portable frontmatter law admits present decoded title/priority/status strings before normalization or edit planning; absent fields and scalar lexical conversion retain their existing behavior, while structured extension data remains open. Load/edit errors name the refused core field and source. Creation validates its rendered frontmatter through the existing decoder and law before mkdir, watcher registration, file write or creation event.

An independent review found a residual UTF-8 seam after the first repair: literal lone surrogates passed in-memory parsing but became U+FFFD on disk. Separate supplemental regression evidence preceded the surrogate escape fix. The final independent source/fixture/log review found no material residual issue in this scoped repair.

## Ordinary patch sequence and exact contexts

1. Original tests-only RED, parent commit `cde7e49567957672b26a1a81606e70b77348ad0a`, parent e37: `tests-red.patch`, SHA256 `3573f33fbabacc1f24ed8c928bfa1e86edfab2513c2eb7862bb2948c219db5c8`. Actual full suite 186 tests / 1158 assertions / 36 failures / 0 errors; JVM 4 tests / 21 assertions / 10 failures / 0 errors. `red-bindings.json` binds those test bytes.
2. First GREEN, parent commit `3b94d0e7d38e7558532f23207b41299ca754f1ac`: `green-before-surrogate.patch`, SHA256 `811649029e9f2536d5cfd1be11652af0b268f061f24d87aa57b1ffcbe8014d0e`. Actual full suite 186/1158 twice, zero failures/errors; JVM/NBB4/21 pass; lint0errors0warnings8baselineinfos; four releases0warnings. After two indentation-only lines were normalized, an exact intermediate patch full-suite run was captured in `first-green-full-test-exact-patch.log`; `first-green-bindings.json` binds that production and original RED test state.
3. Supplemental tests-only RED, parent commit `5eaeb4ac869db7e5da9a952862761473d0c4d67f`: `tests-surrogate-red.patch`, SHA256 `3703c41d22c40f09e44b0b66e2802d2a386295c0aff07adae272ef441f48a0ee`. Actual full suite186/1176, four failures, zero errors. Two assertions detect lone-surrogate persisted-title changes; two require portable quoted scalar/vector escapes. This is first GREEN production plus new tests, not pristine e37. See `surrogate-red-bindings.json` and `red-surrogate-full-test.log`.
4. Final GREEN production delta from the actual canonical supplemental RED: `final-green-after-supplemental-red.patch`, SHA256 `46065de37599d3a81fcc3a7540cc59cd600baa17b817a1dce90f46bebc5e6610`. Exactly two paths: the pure encoder surrogate range/docstring and README's precise body-persistence limit. All other tracked canonical bytes match supplemental RED. The source-only encoder delta is also retained as `final-green-encoder.patch`.

`final-candidate-validation.json` is authoritative for final hashes: all 12 changed paths, all 44 source files, intermediate patch hashes, log hashes and compiled output sizes/hashes. Earlier `candidate-validation.json` is historical first-GREEN evidence and must not be used as final qualification.

## Actual final commands and outcomes

Commands ran in the isolated checkout using the existing prepared complete npm-manifest overlay and copied pinned bootstrap source dependencies. Public pnpm/NBB commands set `NPM_TOKEN=''`, `PATH=/tmp/rheos-boundary-repair-20261003.uWN4Wm/runtime/node_modules/.bin:$PATH`, and `NODE_PATH=/tmp/rheos-boundary-repair-20261003.uWN4Wm/runtime/node_modules`. Node24.14.1, YAML2.9.1, shadow-cljs3.4.10; no credentials or package/workflow policy changes.

- `pnpm test`: shadow autorun and the package's explicit node run each report186 tests /1176 assertions,0 failures/errors. Compiler143files/30compiled/0warnings. `final-green-full-test.log`.
- JVM direct pure tests:4tests/21assertions,0failures/errors. `final-green-jvm.log`.
- NBB same portable tests:4tests/21assertions,0failures/errors. `final-green-nbb-portable.log`.
- `pnpm lint`:0errors/0warnings and precisely the same eight construction-hook infos as the previous qualified e37 source candidate. `final-green-lint.log`.
- `pnpm build`: server106files/14compiled, CLI110/15, github-sync67/4, app95/0; all0compilerwarnings. Every output is nonempty. `final-green-build.log`.
- Compiled CLI: eight actual create→load→next-create cases preserve input title, including lone high/low surrogates and a valid supplementary character. Four collection priority/status sources refuse with source path and named field; missing and scalar priority7/statusfalse/nested-extension controls load. `final-green-cli/results.json`. Because OS argv cannot carry lone UTF-16 units, those two cases use Node `-e` to initialize actual CLI `process.argv` and require the compiled CLI; this is disclosed in the recorded execution argv. Ordinary cases invoke the compiled executable normally. Source/config fixture bytes remain unchanged; successful reads may initialize Rheos's own drift ledger.
- `git diff --check`: passes.

The earlier standalone YAML oracle demonstrates exact decoded scalar and legacy stringified vector values for quotes/backslashes/allC0/NEL/LS/PS. Final compiled creation fixtures and exact portable serialization tests add actual persisted surrogate coverage.

## Limits

This is local candidate qualification, not a hosted exact-commit CI pass or native approval. The general serializer still stringifies vector items and gains no arbitrary nested-map serialization promise. Generated/authored Markdown body bytes have no lossless guarantee: lone-surrogate template body units may be replaced at the UTF-8 boundary, while the escaped frontmatter title remains intact. HTTP response status behavior is unchanged; the native needs-human500/400 note did not establish a required repository contract. Missing optional source-map-support emits its notice twice in the final test log; compiler warnings remain zero. Eight existing construction-layer infos remain visible.

Parent owns final commit/push, hosted qualification, native review settlement and the later engine transition.
