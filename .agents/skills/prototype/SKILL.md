---
name: prototype
description: Build throwaway code to answer one design question. Use when logic/state behavior needs to be felt or a UI direction needs to be compared before production implementation.
---

# Prototype

A prototype answers a question; it is not a head start on production code.

Choose the branch:
- Logic/state question -> build the smallest interactive harness that exposes relevant state transitions.
- UI question -> build clearly different alternatives that can be compared quickly.

Rules:
- mark it clearly as prototype/throwaway;
- make it trivial to run;
- keep state in memory unless persistence is the question;
- skip production abstractions, broad error handling, and premature tests;
- expose state/results after every meaningful action;
- capture the question, verdict, and evidence when finished;
- fold only the validated decision into the real implementation.

For game-system prototypes, use canonical terms from `CONTEXT.md` and do not let prototype behavior silently redefine domain rules.

Completion criterion: the prototype produces evidence sufficient to answer the original design question.