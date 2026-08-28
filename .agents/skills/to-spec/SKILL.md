---
name: to-spec
description: Synthesize the current conversation and repository context into a buildable specification and publish it to the configured tracker. Use after design decisions are sufficiently settled; do not re-interview the user.
---

# To Spec

Use GitHub Issues as configured in `docs/agents/issue-tracker.md`.

1. Read `CONTEXT.md`, the active docs baseline, relevant ADRs, and current code state.
2. Identify the highest practical public test seam. Prefer existing seams and as few seams as possible.
3. Synthesize, without another product interview, a spec with:
   - Problem Statement
   - Solution
   - extensive numbered User Stories (`As an <actor>, I want ..., so that ...`)
   - Implementation Decisions
   - Testing Decisions
   - Out of Scope
   - Further Notes
4. Avoid brittle file paths and code snippets unless a prototype snippet encodes a decision more precisely than prose.
5. Publish the spec to GitHub Issues and apply `ready-for-agent` only when no unresolved decision blocks decomposition.

For V0.1 game work, prefer the public game API/application-command boundary against real PostgreSQL as the primary integration seam, consistent with `docs/agents/spec-workflow.md`.

Completion criterion: another fresh agent can derive implementation tickets from the issue without asking a new requirements interview.