# PR7 snippet completion reporter

This bounded source repair addresses native MiMo review5430144687, root comment
4196713200 and thread PRRT_kwDOU4VJMM6phIw3 at original PR7 head
`ae8acbf9f709871513c95368a7d0f776d5470cdb`. The source snapshot remains
unchanged in its original worktree; this successor uses an independent private
clone and isolated caches. No child is initialized or executed.

The explicit guard incorrectly treats `cljs.test/run-tests` as a returned
summary. Minimal actual reproductions return nil, so that guard evaluates false.
**The stronger native claim that the documented NBB command falsely exits zero
on failed assertions was not reproduced.** Both NBB1.3.204 and the actual
CI pin1.4.207 already exit1, through NBB's completion reporter. The replacement
uses the accepted `test/project_test.cljs` completion pattern: an
`:end-run-tests` report method sets `process.exitCode` from `test/successful?`.
It preserves successful and unsuccessful process outcomes while making this
suite's explicit guard obey the completion contract.

## Actual focused verification

- Original real suite, NBB1.4.207:4tests/37assertions,0failures/0errors,exit0.
- Original private hostile copy, NBB1.4.207:5tests/38assertions,1intentional
  failure/0errors,exit1. Existing assertions are retained unchanged.
- Corrected real suite, NBB1.4.207 and1.3.204:4tests/37assertions,
  0failures/0errors,exit0 on each.
- Corrected private hostile copy, NBB1.4.207 and1.3.204:5tests/38assertions,
  1intentional failure/0errors,exit1 on each.
- Targeted clj-kondo:0errors/0warnings. Diff hygiene:exit0.

The hostile fixtures append one `deftest` before the existing `run-tests` call;
they never edit any original assertion or repository document. Their exact bytes
are archived as strict base64, with hashes in the preservation proof. Commands
and stdout/stderr are losslessly archived in `capture-manifest.json`; intentional
exit1 is a verified rejection outcome, not a passing test run. PostgreSQL query
effects remain stubbed. No live database, provider or service is invoked.

Native full review, inline, comments, writer stage/head markers, canonical status
and thread identity are retained as observed evidence. MiMo COMMENTED is not an
approval. The review gate, missing configured cohort, unqualified sync base and
provider availability remain separate blockers. No native settlement, invitation,
merge, deployment, board transition or controller activation is claimed.

Receipt/reflection prefixes, all23Gitlinks, board/configuration/ledger bytes and
all unrelated existing files are preserved. The source delta is confined to the
snippet reporter; proof records are additive under this directory and `.ημ`.
The owning Foresight receipt consumer is run on the full committed candidate;
postcommit output lives outside the source head to avoid an incidental push.
