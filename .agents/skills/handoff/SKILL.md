---
name: handoff
description: Compact current work, decisions, evidence, blockers, and next actions into a durable handoff for another agent, person, harness, or later session.
---

# Handoff

Create a portable Markdown handoff only when work genuinely needs to cross a context boundary.

Include:
- goal and current status;
- authoritative references (issues, specs, `CONTEXT.md`, docs, ADRs);
- decisions already made and why they matter;
- concrete evidence/verification already obtained;
- changed files/branches/commits when applicable;
- unresolved blockers or risks;
- exact next frontier action;
- commands/tests needed to re-establish the feedback loop.

Prefer links/pointers over duplicating long source material. Do not include secrets or private scratch reasoning.

Completion criterion: a fresh agent can resume from the handoff without asking the previous agent to reconstruct hidden context.