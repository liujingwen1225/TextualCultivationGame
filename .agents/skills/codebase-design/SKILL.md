---
name: codebase-design
description: Design or improve module boundaries using deep-module vocabulary: interface, depth, seam, adapter, leverage, and locality. Use when the shape of a module or test seam is the design problem.
---

# Codebase Design

Aim for deep modules: substantial behavior behind a small, stable interface.

Evaluate a design by asking:
- What is the smallest public interface callers actually need?
- Does the module hide complexity or merely move it?
- Is the seam located where behavior can be tested without internal knowledge?
- Do related rules and data live together (locality)?
- Is an adapter isolating an external concern, or is it an empty middle-man?
- Will one business change require edits scattered across unrelated modules?
- Can two materially different designs be sketched before choosing the seam?

For this project, protect the established boundary: Application Service owns transaction/command orchestration; LiteFlow owns high-level flow; Event Engine owns event-internal rules; domain services own policies such as Knowledge, Trait, Inheritance and deterministic random.

Completion criterion: the chosen design exposes a small interface, centralizes the behavior that changes together, and provides a clear public test seam.