---
name: git-safety-guardrails
description: Safeguard destructive or irreversible Git operations such as force push, hard reset, clean, destructive restore, branch deletion, and history rewrite.
---

# Git Safety Guardrails

Prefer reversible Git operations and preserve recoverability.

Before a destructive operation:
1. Explain exactly what would be discarded or rewritten.
2. Inspect current branch, status, remotes, unpushed commits, and relevant refs.
3. Prefer a safe alternative: new branch, stash, revert, ordinary merge/rebase, non-destructive restore, or backup tag/ref.
4. If destruction is genuinely required, require explicit user approval for the concrete command/scope and create a recovery point when possible.
5. Never widen the destructive scope beyond what the user approved.

Force pushes must prefer `--force-with-lease` and only after remote divergence is understood.

Completion criterion: either a reversible path achieved the goal, or the approved destructive operation completed with a documented recovery path and exact scope.