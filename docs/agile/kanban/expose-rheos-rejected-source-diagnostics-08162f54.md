---
uuid: "08162f54-2f5c-4616-b62b-5bee7c507e1b"
title: "Report Rheos rejected sources without fabricating board tasks"
type: "story"
category: "kanban"
status: "incoming"
priority: "P2"
points: 5
labels: "rheos, diagnostics, source-admission, follow-up"
---

# Report Rheos rejected sources without fabricating board tasks

## Context

Review of [Rheos PR #1](https://github.com/riatzukiza/rheos/pull/1#discussion_r4172161297)
found that the existing task loader logs a parse failure and filters that source
out of the task collection. Strict source admission exposes this visibility gap.
The proposed fallback to empty frontmatter would assign filename identity and
default board state to invalid input. That is unsuitable for canonical UUID
identity and does not preserve the distinction between a rejected source and a
valid task.

This is a separately planned follow-up to the three preparation stories, not an
additional child in their eleven-point epic. It is initial intake, not ready or
accepted implementation work.

## Outcome

A board operator can discover which source was rejected and why, while valid
tasks retain their canonical identity and invalid sources cannot acquire normal
board status or become mutation targets.

## Scope

- Implementation owner: `open-hax/rheos`; inspect the actual loader and consumer
  contract before choosing the smallest diagnostic surface.
- Define a reusable refusal shape before its loader/consumer adapter, keeping
  portable decisions in `.cljc` where practical.
- Expose source location and a bounded failure reason alongside a supported
  existing board read, without representing refusal as an empty successful load.
- Qualify the real filesystem load boundary and one consumer-visible read path.
- Keep valid reads and mutation refusal behavior explicit and compatible.

## Non-goals

- No fallback that fabricates UUID, title, priority, or status for invalid input.
- No new parser, board engine, or command implementation in Foresight.
- No automatic source repair, migration, quarantine writes, or recovery service.
- No broad loader/transport rewrite, browser redesign, or generic provider runtime.
- No expansion of the current source-preservation PR's claimed qualification.

## Acceptance criteria

- A real directory fixture contains a valid explicitly identified card plus
  malformed YAML, duplicate keys and a cyclic-alias source. The valid task is
  returned unchanged; each refused source has inspectable diagnostic evidence.
- Refused sources are distinguishable from absent sources and valid empty
  results. Diagnostics identify the inspected source context and failure class.
- No refused source receives invented board identity/state or becomes eligible
  for transition, comment or frontmatter mutation.
- Diagnostic reads may return valid cards alongside refusal evidence. Every
  mutating Rheos command requires a complete, trusted board load before any
  board file or event write. If any projected source is refused, or the snapshot
  is unavailable or untrusted, refuse mutations to every card on that board,
  including otherwise valid targets, until a complete load succeeds.
- One supported read consumer exposes the refusals, with failure outcomes
  visible rather than only hidden in server logs.
- Read/write support for valid flow-root mappings is decided explicitly;
  unsupported shapes are reported honestly rather than silently coerced.
- The owning repository's relevant tests and required checks pass, and review
  distinguishes loader, consumer and broader transport evidence.

## Verification

Use the historical filtered-source behavior as reproduction context and pin
the current complete-load refusal baseline before adding diagnostic reads.
Exercise real files and the selected existing read consumer. Verify
diagnostic shape, valid-task parity and unchanged source bytes. Attempt every
mutating Rheos command on the partial/refused board, including a valid target,
and verify refusal before any board file or event write; repeat with an
unavailable or untrusted snapshot and after a complete-load recovery. Attach
exact revisions, commands and independent review dispositions. Use the owning
Rheos operations and fixtures, without an alternate board parser or validator;
Foresight records coordination only.

## Risks

Adding diagnostics can alter an existing consumer contract. Filename fallbacks
can blur source identity and task identity. If qualifying one visible read seam
requires more than five points, split the law, loader and consumer work before
readiness rather than expanding this card into the whole content system.
