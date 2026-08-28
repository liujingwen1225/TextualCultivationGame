---
name: grill-with-docs
description: Relentlessly sharpen a fuzzy plan or design while writing resolved domain terms and durable decisions into project documentation. Use before specification when important ambiguity remains.
---

# Grill with Docs

Run the `grilling` discipline while applying `domain-modeling` continuously.

- Ask one decision question at a time.
- Answer facts by reading the repository; ask the user only for decisions or unavailable stakeholder facts.
- Offer a recommended answer and explain the trade-off briefly.
- Stress-test decisions with concrete edge cases before moving on.
- Update `CONTEXT.md` immediately when a canonical domain term is resolved.
- Create ADRs sparingly: only for hard-to-reverse, surprising decisions arising from a real trade-off.
- Keep implementation details out of `CONTEXT.md`.
- Do not implement code during this phase.

For this project, never restore a direction that `docs/00-docs-index.md` marks deprecated unless the user explicitly reverses it.

Completion criterion: remaining questions are implementation details that `to-spec` can synthesize without another product interview.