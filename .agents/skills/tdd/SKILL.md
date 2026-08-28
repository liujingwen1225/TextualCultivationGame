---
name: tdd
description: Test-driven development through public behavior. Use for concrete feature implementation or bug fixes that need durable red-green coverage.
---

# TDD

TDD is red -> green, one tracer bullet at a time.

## Tests

Test observable behavior through public interfaces, not internal implementation details. Expected results must come from the spec, a worked example, or another independent source of truth.

For this project, favor integration-style tests through the game API/application-command boundary with Testcontainers PostgreSQL. Add narrower domain tests when the highest seam makes a rule unnecessarily opaque or slow.

Mock system boundaries only when needed; prefer real components you control. Time and randomness must be controllable so tests are deterministic.

## Loop

1. Name the seam and behavior under test.
2. Write one failing test and run it to prove it is red for the intended reason.
3. Implement only enough to make that test green.
4. Run the focused test.
5. Repeat with the next behavior learned from the previous slice.

Avoid horizontal slicing, tautological assertions, private-method tests, and mocks of your own internal collaborators.

Completion criterion: each new behavior was observed red before green, and the retained tests survive plausible internal refactors.