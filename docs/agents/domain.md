# Domain Documentation Convention

## Canonical domain source

The repository uses a single root `CONTEXT.md` as the canonical glossary and domain-language source.

All implementation specs, tickets, code, tests, and agent work must use the terminology defined there, including concepts such as 天衍锚点、回溯、承世、悟世、Knowledge、词条、锚点命运 and 一世变数.

## Design documents

The current numbered documents under `docs/` are the active product and technical baselines. `docs/00-docs-index.md` defines their precedence and identifies deprecated directions that must not be restored accidentally.

## Architecture decisions

Durable implementation-level architectural decisions that are not merely restatements of the active design baseline should be recorded under `docs/adr/` when they arise. Do not create ADRs pre-emptively for decisions already captured clearly in the active baseline.

## Precedence

When sources conflict, use this order:

1. User's latest explicitly confirmed decision.
2. Root `CONTEXT.md` domain definitions.
3. Current active documents indexed by `docs/00-docs-index.md`.
4. ADRs that do not conflict with newer product decisions.
5. Existing implementation.
