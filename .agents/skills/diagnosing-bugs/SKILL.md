---
name: diagnosing-bugs
description: Diagnose hard bugs and performance regressions with a disciplined feedback-loop-first method. Use when something is broken, flaky, failing, or unexpectedly slow and the cause is not obvious.
---

# Diagnosing Bugs

Redact secrets from every shown log, request, trace, or artifact.

## 1. Build a tight red loop

Before theorizing, create one agent-runnable command that exercises the user's exact symptom and can turn green after the fix. Prefer, in order: failing test, HTTP/CLI repro, browser script, captured-trace replay, minimal harness, fuzz/property loop, bisection/differential loop. Make it fast and deterministic; for flakes, raise the reproduction rate.

## 2. Reproduce and minimize

Run the loop red repeatedly. Remove inputs, steps, config, and callers one at a time until every remaining element is load-bearing.

## 3. Hypothesize

Produce 3-5 ranked, falsifiable hypotheses. Each must predict what observation would support or refute it.

## 4. Instrument

Change one variable per probe. Prefer debugger/inspection, then targeted tagged logs. For performance, measure and profile before fixing.

## 5. Fix and lock down

Write the regression test at the correct public seam before the fix when possible. Apply the smallest root-cause fix, rerun the red loop to green, run surrounding tests, and remove temporary instrumentation.

If no correct test seam exists, record that architecture finding and consider `improve-codebase-architecture`.

Completion criterion: the original exact symptom is green on the same loop that was previously red, with a durable regression test where a valid seam exists.