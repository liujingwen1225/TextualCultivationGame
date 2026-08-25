# Agent Instructions

This repository is currently building the V0.1 playable prototype of **诸世问道**.

Before making implementation decisions, read root `CONTEXT.md` and `docs/00-docs-index.md`. Treat the active numbered design documents as the current baseline and do not restore directions explicitly marked as deprecated.

Prefer small, verifiable vertical slices. V0.1 exists to validate the multi-life gameplay loop, not to pre-build production-scale infrastructure.

## Agent skills

Repository workflow conventions used by engineering agents are defined here:

- `docs/agents/issue-tracker.md` — canonical tracker and implementation-workflow rules.
- `docs/agents/triage-labels.md` — triage/status vocabulary.
- `docs/agents/domain.md` — domain-document source of truth and precedence.

When durable implementation architecture decisions arise, record them under `docs/adr/` if they are not already covered by the active design baseline.
