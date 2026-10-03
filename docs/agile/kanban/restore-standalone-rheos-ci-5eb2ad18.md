---
uuid: "5eb2ad18-53d7-4412-818d-dfd6a9b42258"
title: "Restore standalone Rheos CI and qualify its release targets"
type: "story"
category: "kanban"
status: "incoming"
priority: "P1"
points: 5
dependency: "819717c4-735b-4dd5-b5b7-ba860d3942f9"
labels: "rheos, ci, extraction, release-qualification, follow-up"
---

# Restore standalone Rheos CI and qualify its release targets

## Context

The [initial Rheos extraction](https://github.com/riatzukiza/rheos/commit/fbf6aa0c7745765528a0a6cc7bd3fdbd344032d7)
did not carry eta-mu's repository-level workflows into the standalone package.
The [pinned donor workflow](https://github.com/open-hax/eta-mu/blob/0ed56aa74a53a1d1e9c2e55ce95451817a7f3a90/.github/workflows/rheos.yml)
qualified tests, lint and release targets, but its workspace installation and
package paths do not work unchanged in the extracted repository.

Source-preservation story `819717c4-735b-4dd5-b5b7-ba860d3942f9` is implemented
in [Rheos PR #1](https://github.com/riatzukiza/rheos/pull/1) at `dda73f1` and has
a completed full review. Its 166-test/858-assertion test run used a declared
external tooling prefix; it did not qualify a clean package installation or the
browser release. A separate CI PR starts from that exact reviewed head and is
stacked on its branch. Its dependency records integration order, not a claim
that the source-preservation PR is merged or this intake card is ready.

## Outcome

A fresh GitHub runner visibly installs Rheos's declared dependencies, bootstraps
its pinned source inputs, tests and lints the package, and builds every declared
release target. Operators can distinguish those checks from an absent workflow
or a successful local backend test run.

## Scope

- Implementation owner: `open-hax/rheos`; publish on the `riatzukiza/rheos`
  fork, with no deployment or repository-setting changes.
- Add a standalone workflow for pull requests, pushes to `main`, and manual
  dispatch, using repository-root commands and explicit tool versions.
- Establish a standalone pnpm lockfile from a successful actual package
  installation, then use frozen installation in CI. Review this extraction
  packaging change before claiming reproducible dependency selection.
- Run the existing source bootstrap with its declared Protocols and Chat UI
  commit pins, followed by `pnpm test`, `pnpm lint:kondo`, and `pnpm build`.
- Check release artifacts for `server`, `cli`, `github-sync`, and `app`, and
  load the built CLI through its non-mutating `--help` path.
- Correct the README's surviving monorepo paths and inaccurate claim that the
  browser release is excluded from the pnpm build.

## Non-goals

- No Cloudflare hosting, deployment credentials, package publication, or change
  to shared account resources; the user has closed that hosting experiment.
- No new board parser, workflow engine, or command implementation in Foresight.
- No rewrite of dependency policy in other independently owned repositories.
- No claim that CLI help qualifies HTTP, MCP, GitHub writes, browser interaction,
  or the proposed content-management/review loop.
- No broad runtime repair hidden inside CI restoration. Split any substantial
  failure found by release qualification into an owning repair card.
- No repair of the PR-flow zero-finding-review recognizer in this story.

## Acceptance criteria

- The workflow has pull-request, `main` push and manual triggers without inherited
  monorepo path filters; failures and unavailable tools remain visible.
- A fresh checkout with no ambient sibling repositories or external `NODE_PATH`
  installs the actual declared package dependencies. The checked-in lockfile and
  CI package-manager version agree, and frozen installation succeeds.
- Pinned source bootstrap, the full test script, clj-kondo lint and all four
  release targets complete successfully, with counts and compiler diagnostics
  retained in the qualification record.
- The server module, CLI executable, GitHub projector executable and browser
  main module exist after release. `node dist/cli.cjs --help` exits successfully
  without loading or mutating a board.
- CI uses read-only repository permissions, pinned action revisions and a bounded
  timeout. It does not require hosting or GitHub mutation credentials.
- The actual hosted workflow runs on the CI PR head and exposes the checks;
  local qualification alone does not count as hosted CI evidence.
- Current-head full review completes, and every finding receives an explicit
  verified disposition before the PR is described as reviewed.
  P0/P1 findings require fixes or remain open for user adjudication; only P2/P3
  may be handled, deferred to an existing card, or rejected with evidence.

## Verification

First record the missing standalone workflow and a clean installation/release
baseline, including any failures, without implying earlier test coverage proved
these boundaries. Review this plan before implementation and transition the card
through Rheos. Establish dependencies in an isolated checkout, run the declared
commands, inspect the four artifacts and safe CLI help, then inspect the hosted
run and its full review at the exact head. Record revisions, commands, findings,
warnings and limitations in Receipt River. Foresight supplies coordination;
Rheos remains the sole implementation authority for board operations.

## Risks

Git package installation can expose donor preparation hooks or missing published
packages. Browser dependencies and release compilation have not been qualified
by the existing backend test run. Lockfile generation can reveal dependency
conflicts that need their own bounded repair. A stacked PR needs explicit review
and cannot merge before its base. If this work exceeds five points, split it
before expanding implementation scope or claiming release qualification.
