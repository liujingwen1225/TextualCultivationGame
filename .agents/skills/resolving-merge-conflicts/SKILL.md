---
name: resolving-merge-conflicts
description: Resolve an in-progress Git merge or rebase conflict hunk by hunk using intent from both sides, then verify and finish the operation.
---

# Resolving Merge Conflicts

Treat each conflict as an intent merge, not a line-selection exercise.

1. Confirm a merge/rebase is actually in progress and list conflicted files.
2. For each hunk, trace both sides to their primary sources: commits, issues/specs, tests, domain docs, and surrounding code.
3. State the intent each side is trying to preserve.
4. Write the smallest combined resolution that satisfies both compatible intents; when intents are incompatible, follow the newer authoritative product/domain decision and call out the trade-off.
5. Remove conflict markers, run focused tests, then the relevant broader verification.
6. Continue the merge/rebase to completion.

Do not use abort/reset as a shortcut. If a destructive recovery step becomes necessary, route through `git-safety-guardrails` and require explicit user intent.

Completion criterion: no conflict markers remain, the Git operation is finished, and verification covers behavior affected by both sides.