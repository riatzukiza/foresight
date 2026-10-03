---
category: "content"
labels: "alpha, content, portability, contracts"
parent: "e7cdf5cf-423e-49fc-aca8-6ed6055f1cd5"
type: "story"
write-id: "1791024407627-0.5zppu69kx4xs1x2338z"
points: "3"
title: "Admit portable source-bearing document observations in Alpha"
priority: "P1"
status: "review"
epic: "e7cdf5cf-423e-49fc-aca8-6ed6055f1cd5"
uuid: "e6aa52eb-681d-4d79-8605-87aa1fc8b4ca"
---

# Admit portable source-bearing document observations in Alpha

## Context

(己, p=0.99) Existing Alpha provides Artifact/Ref and Markdown shapes, but those
base shapes do not by themselves admit a source observation and its assembled
document as a matched pair. The prepared candidate at
`0602064f9dafdd5708b790cd986de32b5e3e50fb` adds this law. It predates these
planning cards and remains subject to planning and independent code review;
no prior planning/red sequence is claimed.

## Outcome

A consumer can validate supplied Markdown or non-file document maps under one
small portable contract while retaining their distinct source and observation
identities and explicit limitations.

## Scope

- Implementation owner: Foresight's native `alpha/` component, using existing
  Ref, PortableValue and Katamorph/Malli mechanics.
- Versioned observation/document shapes and an assembly signature containing
  identity/version and declared input/output schema kinds.
- Pure admission checks for both shapes, the selected observation/source
  context and both schema boundaries, with diagnosable failure stages.
- Bounded Markdown and ephemeral API-shaped fixtures, portable tests and
  README/API limits; review and revise the prepared candidate as needed.

## Non-goals

- No acquisition, parsing, assembly execution, provider registry or persistence.
- No real-time freshness, digest computation, edit acceptance, source write or
  authorization guarantee.
- No universal file, Git, frontmatter, task identity or board workflow fields.
- No replacement of Katamorph portable data laws or duplication of Rheos rules.

## Acceptance criteria

- A source-owned Markdown observation and assembled map pass admission while
  preserving the supplied raw/parsed metadata and body values unchanged.
- A partial, ephemeral API-shaped observation and readable map pass without
  filesystem path, Git fields, immutable source revision or source-write
  capability.
- Observation identity, source entity/revision, optional digest, coverage,
  retention and capabilities occupy explicit separate roles.
- Missing required fields, malformed/version-incompatible envelopes and nested
  runtime/executable values are refused with a useful failure stage.
- Unknown or violated input/output schemas fail closed; valid input does not
  imply valid output.
- A document naming another supplied observation, source entity or source
  revision is refused. The API and documentation explicitly distinguish this
  referential check from real source freshness.
- JVM and compiled ClojureScript/Node tests exercise the contract with zero
  failures/errors, and relevant lint reports zero warnings/errors.
- Independent review dispositions and exact-head verification are recorded;
  earlier candidate checks do not stand in for review of a revised head.

## Verification

Run `clojure -M:test` from `alpha/`, relevant `clj-kondo` lint, and compiled
ClojureScript/Node tests for the portable contract. Record exact commands,
counts, tool versions where material, source head and limitations in the PR
and Receipt River. If planning changes the candidate, preserve the actual new
test/code sequence rather than inventing an earlier failing commit.

## Risks

An admitted representation can still be stale, incorrectly assembled or based
on an unavailable source. Capability/retention declarations are not verified
runtime effects. Registry versioning remains caller-owned. Expansion into a
generic CMS DSL would exceed this three-point contract slice.

---
Full current-head CodeRabbit review c8340b2 found P1 source-context equality compares entire open Ref maps. Verify extensions on either side remain unchanged while declared ref/type, ref/id, ref/revision identity must agree. Review finding: https://github.com/riatzukiza/foresight/pull/2#discussion_r4172818377. Begin bounded prospective red/green repair; earlier 26/89 and5/31 results do not qualify this new regression case.

P1 repair qualified: red a3bf533 reproduced12 expected JVM failures; green a68012d60ba3d7815eaff9f4b18dfd34c51d0ead passes JVM27tests107assertions, compiled CLJS6tests49assertions, lintzero errors/warnings. Four unilateral extension cases preserve full maps; type/id/revisionvalue/presence differences still reject. Card resumption ready to todo to in_progress was recorded after red; no earlier in-progress claim. Proceed prospectively to review, not done or accepted runtime integration.
---