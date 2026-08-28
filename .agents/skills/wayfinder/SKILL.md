---
name: wayfinder
description: Map a large, uncertain multi-session engineering effort into decision tickets and resolve the fog before specification. Use only when the path cannot reasonably fit in one focused design session.
---

# Wayfinder

Wayfinder produces decisions, not implementation deliverables.

1. Define the destination in user/domain terms.
2. Read current domain docs, architecture, tracker state, and relevant research.
3. Identify the unknown decisions that prevent a buildable route.
4. Create a dependency map of decision tickets on the configured tracker; each ticket should resolve one uncertainty and state what it blocks.
5. Work the decision frontier blockers-first, using `grilling`, `research`, `prototype`, or `domain-modeling` only where each decision requires it.
6. Record resolved decisions in their durable source of truth.
7. When the route becomes clear, stop mapping and hand the resolved context to `to-spec`, then `to-tickets`.

Do not jump directly from a large decision map to implementation; the spec is the collapse point that makes the route buildable.

Completion criterion: remaining work can be represented as one buildable spec without unresolved decision tickets.