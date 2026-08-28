---
name: implement
description: Implement a concrete approved spec or ready ticket as a scoped, verified vertical slice. Use when the requested work is already sufficiently specified.
---

# Implement

Implement one frontier ticket at a time.

1. Read the full ticket/spec, comments, `AGENTS.md`, `CONTEXT.md`, relevant design docs and ADRs.
2. Verify every `Blocked by` dependency is complete. If not, stop and report the blocker.
3. Identify the pre-agreed public test seam; for V0.1 prefer API/application behavior with real PostgreSQL where practical.
4. Use `tdd`: one red test, minimum green implementation, repeat vertically.
5. Keep the change within ticket scope; do not pre-build later tickets or speculative infrastructure.
6. Run focused tests frequently, static/build checks regularly, and the full relevant suite at the end.
7. Run `code-review` against the ticket/spec before considering the work complete.
8. Remove temporary instrumentation and leave a concise verification record.

Follow the current Java modular-monolith baseline and server-authoritative game rules.

Completion criterion: all ticket acceptance criteria pass through observable behavior, verification is green, and review has no unresolved blocking finding.