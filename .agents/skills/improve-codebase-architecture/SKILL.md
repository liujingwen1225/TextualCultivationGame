---
name: improve-codebase-architecture
description: Survey the codebase for deepening opportunities, weak seams, low locality, and architectural drift, then discuss the highest-value candidate before any refactor.
---

# Improve Codebase Architecture

This is a survey first, not an automatic refactor.

1. Read `AGENTS.md`, `CONTEXT.md`, technical baselines, ADRs, and the code structure.
2. Look for shallow modules, duplicated policy, leaky abstractions, weak test seams, excessive cross-module knowledge, shotgun surgery, mixed transaction ownership, and domain rules living in controllers/UI/configuration.
3. Describe each candidate in terms of current pain, proposed deeper boundary, likely leverage, blast radius, and evidence.
4. Rank a small set by value and risk.
5. Present the candidates and use focused grilling to select/shape one.
6. Feed the selected idea into the normal `grill-with-docs -> to-spec -> to-tickets -> implement` flow rather than refactoring opportunistically.

Completion criterion: the user has a concrete, evidence-backed architecture candidate and a proposed seam, without unapproved production changes.