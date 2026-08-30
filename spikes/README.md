# Engine Selection Spike

This directory contains disposable prototypes used only to choose the desktop engine for 《诸世问道》.

Authoritative scope and scoring: `docs/12-engine-spike-spec.md`.

## Candidates

- `libgdx/` — Java 21 + libGDX 1.14.2
- `godot/` — Godot 4.7.2 .NET + C# / .NET 8

Both prototypes implement the same minimum rule:

```text
first life: drink poisoned wine -> die -> gain K_BLACKWATER_POISON
next life: prior knowledge exposes a remember/refuse choice -> survive
```

The domain rule must remain outside rendering/scene code.

## libGDX CLI

From `spikes/libgdx` with Gradle available:

```text
gradle clean build
gradle :core:scenarioTest
gradle :headless:run
gradle :lwjgl3:run
```

## Godot CLI

With .NET 8 and Godot 4.7.2 .NET available:

```text
dotnet build spikes/godot/core/SpikeCore.csproj
dotnet run --project spikes/godot/scenario/Scenario.csproj
dotnet build spikes/godot/ZhushiEngineSpike.csproj
godot --headless --path spikes/godot -- --headless-smoke
godot --path spikes/godot
```

The spike is not production code and may be deleted after the engine decision.
