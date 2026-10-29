# IPED Polish Edition by Czaku

> An unofficial Polish-language development fork of [IPED](https://github.com/sepinf-inc/IPED), based on **IPED 4.3.1**.

## Overview

The project aims to provide Polish interface resources and documentation, and to evaluate selected Windows startup and Outlook Express DBX processing fixes.

This is an **independent fork**, not an official IPED release or an endorsed distribution. The original IPED project and contributors retain their attribution and applicable licenses.

## Current development status

- **Source baseline:** IPED 4.3.1.
- **Polish edition development version:** v0.6 FIX4.2.
- **Localization:** Polish resource files added; coverage review is ongoing.
- **DBX Unicode:** locally tested changes to recover Unicode mail subjects; source review and additional regression tests remain necessary.
- **DBX parser loading:** a `ClassNotFoundException` involving `OutlookDBXParserImpl` was observed in the customized indexing environment and corrected locally. This is **not yet established as a clean upstream defect**.
- **Windows Portable:** BASE and GOLDEN configurations were tested locally; neither is a validated public distribution at present.

## Repository policy

Only relevant **source files and text resources** belong in the fork. Do not commit forensic evidence, case folders, generated indexes, local logs, personal configuration, bundled runtimes, or built Portable distributions. The two DBX changes should be reviewed and committed separately.

## Building and testing

Follow the upstream IPED project build and runtime instructions. The local development environment used Java 11 and Maven. Local functional test results do not guarantee compatibility with other Windows environments.

## Upstream and licenses

- Upstream: https://github.com/sepinf-inc/IPED
- Fork: https://github.com/Akuczaku/IPED

Preserve all applicable upstream copyright, licensing and third-party notices. Binary redistribution requires an additional dependency and runtime audit.

## Planned work

1. Complete the Polish localization review.
2. Submit a separate, reviewed commit for DBX Unicode subject handling.
3. Submit a separate, reviewed commit for DBX bootstrap/class-loading behavior.
4. Run UI, DBX and documentation regression tests.
5. Consider upstream contributions only after reproduction against a clean upstream baseline.
