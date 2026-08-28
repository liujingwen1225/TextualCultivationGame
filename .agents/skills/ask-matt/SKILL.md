---
name: ask-matt
description: Route engineering work to the smallest useful Matt-style workflow. Use when the next step or best skill is unclear, or when a task spans planning, implementation, debugging, review, architecture, research, or handoff.
---

# Ask Matt

Route first, then execute the first applicable phase.

Read `AGENTS.md`, `CONTEXT.md`, `docs/00-docs-index.md`, and `docs/agents/` before routing repository work.

## Main flow

For a new feature or design that is still fuzzy:

`grill-with-docs -> to-spec -> to-tickets -> implement -> code-review`

Use a prototype between grilling and spec only when a runnable artifact is needed to settle a design question.

## On-ramps

- Ready spec or `ready-for-agent` frontier ticket -> `implement`.
- Hard bug or performance regression -> `diagnosing-bugs`, then `tdd` for the durable regression test.
- Incoming raw issue/request -> `triage`. Tickets produced by `to-tickets` are already agent-ready and should not be triaged again.
- Large effort whose route cannot fit in one session -> `wayfinder`, then return to `to-spec`.
- Architecture health/design -> `improve-codebase-architecture` or `codebase-design`.
- Primary-source investigation -> `research`.
- Work that must move to another session/person/harness -> `handoff`.
- In-progress merge/rebase conflict -> `resolving-merge-conflicts`.
- Destructive Git operation -> `git-safety-guardrails`.
- Human-only setup/dashboard/credential steps -> `wizard`.

Prefer one primary skill. Add a second only when it owns a distinct phase or quality gate.

Completion criterion: name the smallest route that covers the task, then start the first phase when the user asked for action.