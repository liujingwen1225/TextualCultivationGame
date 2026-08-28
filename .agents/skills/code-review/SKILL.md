---
name: code-review
description: Review changes since a fixed point along two independent axes: repository/code quality standards and fidelity to the originating spec or ticket.
---

# Code Review

Pin a fixed point (branch, commit, tag, or merge-base) and review the diff to `HEAD`.

## Standards axis

Read `AGENTS.md`, project technical baselines, relevant ADRs, and existing conventions. Flag concrete problems such as duplicated logic, mysterious names, data clumps, primitive obsession, repeated switches, shotgun surgery, divergent responsibilities, speculative generality, message chains, middle-men, and weak seams. Treat smells as judgement calls; documented project rules win.

## Spec axis

Read the originating GitHub Issue/spec and comments. Report:
- missing or partial requirements;
- behavior added outside scope;
- apparently implemented requirements whose behavior is wrong;
- missing verification for acceptance criteria.

Keep the two axes separate so one cannot mask the other. Cite files/hunks and spec requirements precisely.

Do not silently rewrite the code during a review unless the user explicitly asks for fixes.

Completion criterion: every material changed area is accounted for on both axes, or the absence of a spec is explicitly stated.