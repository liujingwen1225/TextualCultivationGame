# Agent Instructions

This repository is currently building the V0.1 playable prototype of **诸世问道**.

Before making implementation decisions, read root `CONTEXT.md` and `docs/00-docs-index.md`. Treat the active numbered design documents as the current baseline and do not restore directions explicitly marked as deprecated.

Prefer small, verifiable vertical slices. V0.1 exists to validate the multi-life gameplay loop, not to pre-build production-scale infrastructure.

## Agent skills

Project-local engineering skills live under `.agents/skills/` and are versioned with this repository.

- When the correct workflow or next step is unclear, start with `.agents/skills/ask-matt/SKILL.md`.
- The normal build chain is `grill-with-docs -> to-spec -> to-tickets -> implement -> code-review`.
- Work implementation tickets blockers-first from the GitHub Issues frontier; do not implement a ticket while any `Blocked by` issue remains incomplete.
- Use `tdd` for implementation feedback loops and `diagnosing-bugs` for hard bugs before guessing at causes.
- Use `domain-modeling` whenever canonical domain vocabulary changes; use `codebase-design` / `improve-codebase-architecture` when module seams or locality are the problem.

Repository workflow conventions used by the skills are defined here:

- `docs/agents/issue-tracker.md` — canonical tracker and implementation-workflow rules.
- `docs/agents/triage-labels.md` — triage/status vocabulary.
- `docs/agents/domain.md` — domain-document source of truth and precedence.
- `docs/agents/spec-workflow.md` — buildable spec structure and V0.1 primary test seam.

When durable implementation architecture decisions arise, record them under `docs/adr/` if they are not already covered by the active design baseline.
