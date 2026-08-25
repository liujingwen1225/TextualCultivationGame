# Triage Label Convention

The repository uses the Matt Skills Curated default triage vocabulary.

| Meaning | Label |
| --- | --- |
| Newly reported work requiring classification | `needs-triage` |
| Blocked on missing human information | `needs-info` |
| Fully specified and safe for an implementation agent to start | `ready-for-agent` |
| Requires a human decision or manual action before proceeding | `ready-for-human` |
| Intentionally not planned | `wontfix` |

A buildable spec produced by `to-spec` and approved implementation tickets produced by `to-tickets` should use `ready-for-agent` when no unresolved decision remains.
