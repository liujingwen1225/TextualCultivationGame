# Project Matt Skills

This directory contains **project-local Agent Skills** for the engineering workflow used by 《诸世问道》 / `TextualCultivationGame`.

They are project-adapted from Matt Pocock's MIT-licensed `mattpocock/skills` methodology and the Matt Skills Curated workflow used during project planning. The adaptations intentionally point at this repository's canonical `AGENTS.md`, `CONTEXT.md`, active docs, GitHub Issues workflow, and Java/V0.1 test seam.

Codex discovers repository skills from `.agents/skills/<skill-name>/SKILL.md`.

## Main route

`ask-matt` is the router when the next workflow is unclear.

Primary build chain:

`grill-with-docs -> to-spec -> to-tickets -> implement -> code-review`

Supporting skills:

- `grilling`
- `domain-modeling`
- `setup-engineering-workflows`
- `tdd`
- `diagnosing-bugs`
- `prototype`
- `research`
- `triage`
- `wayfinder`
- `handoff`
- `codebase-design`
- `improve-codebase-architecture`
- `resolving-merge-conflicts`
- `git-safety-guardrails`
- `wizard`

## Project rules

- GitHub Issues are the canonical implementation tracker.
- Root `CONTEXT.md` is the domain glossary.
- `docs/00-docs-index.md` is the active design index and deprecation guard.
- `ready-for-agent` implementation tickets are worked blockers-first from the frontier.
- V0.1 implementation favors public API/application behavior against real PostgreSQL as the highest practical test seam.

See `LICENSE-MATT-POCOCK` for upstream license/attribution.