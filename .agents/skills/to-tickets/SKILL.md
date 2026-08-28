---
name: to-tickets
description: Decompose an approved plan or spec into tracer-bullet implementation tickets with explicit blocking edges, then publish the approved tickets to the configured tracker.
---

# To Tickets

Work from the approved spec/issue and its comments.

Draft vertical slices:
- each ticket is a narrow but complete path through the layers it needs;
- each completed ticket is demoable or independently verifiable;
- each fits one fresh implementation context;
- prefer prefactoring that makes the next slice easy;
- use expand-contract only for truly wide mechanical refactors.

For each ticket present:
- Title
- Blocked by
- What it delivers

Before publishing, ask the user to approve granularity, blocking edges, and any merge/split changes.

After approval, create one GitHub Issue per ticket in dependency order, reference the parent spec, write acceptance criteria and `Blocked by` references, and apply `ready-for-agent` when genuinely startable once blockers clear.

The frontier is the set of tickets whose blockers are complete. Do not modify or close the parent spec.

Completion criterion: every ticket has explicit acceptance criteria and blocking edges, and at least one frontier ticket can be identified.