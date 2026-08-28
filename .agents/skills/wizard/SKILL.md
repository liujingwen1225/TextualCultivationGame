---
name: wizard
description: Create an interactive human-facing setup wizard for steps the agent genuinely cannot perform, such as credentials, third-party dashboards, approvals, or one-off cutovers.
---

# Wizard

Use only when a human must cross a boundary the agent cannot.

Generate a simple, readable script/procedure that:
- explains the goal and prerequisites;
- walks one human action at a time;
- opens or names the exact dashboard/page when applicable;
- validates each value or checkpoint before continuing;
- stores secrets only in appropriate secret/env destinations, never in source control or printed logs;
- is safe to restart or clearly states where it is not idempotent;
- includes verification and rollback/cancel guidance for risky cutovers.

Do not make the human perform steps that available repository/tool access could execute directly.

Completion criterion: the human can complete the blocked setup without needing ad-hoc instructions from the agent between steps.