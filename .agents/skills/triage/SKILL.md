---
name: triage
description: Classify and verify incoming repository issues or contribution requests, resolve missing information, and turn valid raw work into an agent-ready implementation brief.
---

# Triage

Use only for raw incoming work. Do not re-triage tickets produced by `to-tickets`.

Follow the repository label vocabulary in `docs/agents/triage-labels.md`.

For each issue:
1. Read the full issue and comments.
2. Verify the reported behavior against current docs/code when possible.
3. Classify it as actionable, missing information, human-decision required, duplicate/not planned, or ready for an agent.
4. Ask for missing information only when repository evidence cannot resolve it.
5. For valid actionable work, rewrite/augment the issue into a durable brief with observable acceptance criteria, relevant domain vocabulary, and known constraints.
6. Apply the corresponding triage label without inventing parallel status vocabulary.

Completion criterion: the issue's next owner can act without rediscovering the triage reasoning, or the exact missing decision/information is explicit.