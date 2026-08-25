# Issue Tracker Convention

## Tracker

This repository uses **GitHub Issues** in `liujingwen1225/TextualCultivationGame` as the canonical implementation work tracker.

Design documents under `docs/` describe product and technical baselines. Once a feature is ready to build, its buildable specification and implementation tickets should be published as GitHub Issues rather than maintained as a second task list in Markdown.

## Workflow

1. Resolve product/domain decisions in the current design baseline.
2. Publish a buildable spec issue.
3. Decompose the approved spec into tracer-bullet implementation issues.
4. Record explicit `Blocked by` references between implementation issues.
5. Work only tickets whose blockers are complete.
6. Implementation PRs should reference the issue they deliver.

## Status source of truth

- Product/domain truth: root `CONTEXT.md` and current `docs/` baseline.
- Work status: GitHub Issues and Pull Requests.
- Code behavior: implementation and automated tests, subordinate to current approved design when migration is still pending.
