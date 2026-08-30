# Specification Workflow

Buildable specifications are published as GitHub Issues using Matt Skills Curated `to-spec` structure.

A spec is ready for implementation decomposition when it contains:

- Problem Statement
- Solution
- extensive User Stories
- Implementation Decisions
- Testing Decisions
- Out of Scope
- Further Notes

## Redesign / Engine Spike rule

`redesign/steam-pixel-rpg` is currently resolving a hard-to-reverse engine choice. Until `docs/10-engine-selection.md` is finalized:

- Engine Spike code is an evaluation prototype, not production implementation.
- Do not mark V0.1 implementation tickets `ready-for-agent` based on the old H5 / Spring Boot architecture.
- Do not use REST, PostgreSQL, MyBatis-Flex or LiteFlow as the default test seam.
- A prototype may exist between `grill-with-docs` and `to-spec` only to settle the engine decision.

## V0.1 primary test seams after engine lock

The desktop single-player architecture uses layered seams rather than a public HTTP API:

1. **Game Core / Application command boundary** — pure rule tests for Anchor, Knowledge, inheritance, traits, time, events and deterministic random.
2. **Scenario Runner** — drive the complete Blackwater multi-life scenario without rendering, using the same commands and content definitions as the game runtime.
3. **SaveGame round-trip** — serialize to a temporary local save, reload, and prove authoritative state is preserved.
4. **Runtime / headless integration** — boot the real engine runtime without human input and execute representative scene/event flows.
5. **Visual / input smoke** — launch a real game window when the environment supports it, simulate essential input, and capture logs/state/screenshots for verification.

Prefer a small number of high-value scenario tests that cross real domain boundaries. Add narrower unit tests where they make rules easier to understand or failures easier to diagnose.

Engine-specific Scene/Node/rendering types must not leak into Game Core tests.

Specs and implementation tickets should use the `ready-for-agent` state only when no unresolved product or engine decision blocks implementation.
