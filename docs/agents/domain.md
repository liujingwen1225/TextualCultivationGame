# Domain Documentation Convention

## Canonical domain source

The repository uses root `CONTEXT.md` as the single canonical glossary and domain-language source.

All new design work, specs, tickets, code, tests, and agent prompts must use the current orthodox cultivation life-sim RPG terminology defined there.

Do not reintroduce retired multi-life concepts such as Anchor, rewind, inheritance, realization, cross-life Trait, or cross-life Knowledge unless the user explicitly makes a new product decision to restore them.

## Active design documents

Only documents listed by `docs/00-docs-index.md` are active product/technical baselines.

Old files from previous branches, Git history, closed PRs, old issues, and superseded Grill decision logs are historical context only.

## Architecture decisions

Durable implementation-level decisions that are not simple restatements of active baselines may later be recorded under `docs/adr/`.

Do not create ADRs pre-emptively for decisions already explicit in canonical design documents.

## Precedence

When sources conflict, use this order:

1. User's latest explicitly confirmed decision.
2. Root `CONTEXT.md`.
3. Active documents indexed by `docs/00-docs-index.md`.
4. ADRs that do not conflict with newer product decisions.
5. Existing implementation.
6. Historical branches, commits, issues, specs, and design drafts.

## Current hard product direction

- Steam / Windows first.
- Orthodox cultivation life-sim RPG, not a Roguelite rewind game.
- Godot 4.7.x .NET + C# + .NET 8 + pure 2D.
- Pure C# Game Core owns authoritative rules.
- RTwP combat on the current map.
- Normal local SaveGame; death does not create a meta-progression loop.
