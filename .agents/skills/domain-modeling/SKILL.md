---
name: domain-modeling
description: Build and sharpen the project's ubiquitous language. Use when terminology, CONTEXT.md, domain boundaries, or durable architectural decisions are being changed.
---

# Domain Modeling

Root `CONTEXT.md` is the single canonical glossary for this repository.

- Challenge fuzzy or overloaded terms immediately.
- Reuse existing project terms such as 天衍锚点、回溯、承世、悟世、Knowledge、词条、锚点命运、一世变数 rather than inventing synonyms.
- Check claims against current design docs and code.
- Use concrete edge cases to expose boundary mistakes.
- When a term resolves, update `CONTEXT.md` inline.
- Keep `CONTEXT.md` pure domain language: no table names, framework choices, endpoint paths, implementation plans, or transient tasks.
- Record an ADR only when a choice is hard to reverse, surprising without context, and the result of a real trade-off.

Precedence follows `docs/agents/domain.md`.

Completion criterion: each changed concept has one unambiguous canonical name and meaning, and any qualifying ADR is recorded.