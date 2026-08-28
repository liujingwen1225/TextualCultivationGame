---
name: setup-engineering-workflows
description: Configure or verify the repository conventions required by the Matt-style engineering workflows: tracker, triage labels, domain docs, ADRs, AGENTS.md, and project skills.
---

# Setup Engineering Workflows

Inspect the repository before changing conventions.

For this project, preserve the established defaults unless the repository proves they changed:

- GitHub Issues are the canonical implementation tracker.
- `ready-for-agent`, `needs-triage`, `needs-info`, `ready-for-human`, `wontfix` are the triage vocabulary.
- Root `CONTEXT.md` is the canonical domain glossary.
- `docs/00-docs-index.md` identifies the active design baseline and deprecated directions.
- Durable surprising implementation decisions belong in `docs/adr/` when they are genuinely hard to reverse.
- Project skills live under `.agents/skills/<name>/SKILL.md`.

Verify `docs/agents/issue-tracker.md`, `docs/agents/triage-labels.md`, `docs/agents/domain.md`, and exactly one `## Agent skills` section in `AGENTS.md`.

Completion criterion: every later workflow can locate tracker state, domain truth, triage vocabulary, ADRs, and project skills without guessing.