# Specification Workflow

Buildable specifications are published as GitHub Issues using Matt Skills Curated `to-spec` structure.

A spec is ready for implementation decomposition when it contains:

- Problem Statement
- Solution
- extensive User Stories
- Implementation Decisions
- Testing Decisions
- Out of Scope
- Further Notes

For V0.1, prefer the public game API/application command boundary as the primary test seam. Exercise real state transitions against PostgreSQL so that the same tests cover transactionality, persistence, idempotency, event resolution, death settlement, rewind, Knowledge, trait and inheritance behavior. Add narrower tests only for rules that cannot be made clear at that seam.

Specs and implementation tickets should use the `ready-for-agent` state only when no unresolved product decision blocks implementation.
