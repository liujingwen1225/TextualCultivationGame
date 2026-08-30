# Specification Workflow

Buildable specifications are eventually published as GitHub Issues using the Matt Skills Curated `to-spec` structure.

A spec is ready for implementation decomposition when it contains:

- Problem Statement
- Solution
- User Stories
- Implementation Decisions
- Testing Decisions
- Out of Scope
- Further Notes

## Current design-stage rule

The project is currently still in product and architecture design consolidation.

Until `docs/10-project-status.md` explicitly moves the project into Spec stage:

- Do not create formal V0.1 implementation specs.
- Do not generate implementation tickets.
- Do not mark old multi-life / H5 / Spring Boot issues `ready-for-agent`.
- Do not start production implementation from historical specs.
- Small technical experiments are allowed only when they resolve a concrete architecture uncertainty.

## Current V0.1 test seams after design lock

The target desktop single-player architecture uses layered seams rather than a public HTTP API:

1. **Pure C# Game Core tests** — cultivation, world time, NPC/relationship, events, combat, economy, intel, injuries and deterministic rules.
2. **Continuous-Life Scenario Runner** — drive the Blackwater V0.1 flow without rendering using the same Application commands and content definitions as the game runtime.
3. **SaveGame round-trip** — serialize current GameState to a temporary local save, reload and prove authoritative state is preserved.
4. **Godot headless integration** — boot the real engine runtime without human input and execute representative scene/content/core flows.
5. **Visual / input smoke** — launch a real game window when available, simulate essential input and capture logs/state/screenshots.

Prefer a small number of high-value scenario tests that cross real domain boundaries. Add focused unit tests where they clarify rules and failure causes.

Godot Scene / Node / rendering types must not leak into pure Game Core tests.

## Historical concepts are invalid test seams

Do not create tests or architecture around:

- Anchor / rewind.
- inheritance / realization.
- cross-life Trait or Knowledge.
- Blackwater three-life scenario.
- Engine Spike comparison.
- REST endpoints as the main game acceptance seam.

## Ready-for-agent rule

A spec or implementation ticket may use `ready-for-agent` only when:

- Product behavior is locked.
- Architecture boundary is locked.
- Required content/data contract is clear.
- Test seam is explicit and executable.
- No active canonical document contradicts it.
