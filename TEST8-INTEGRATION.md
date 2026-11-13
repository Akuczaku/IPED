# TEST8 — integration candidate, not a stable release

Upstream base: 31c59f41421b0b6cbdd668eb30c5c2d83d96ec6c (4.4.0-SNAPSHOT).
Polish integration base: 1da0d551c623abd525be5f8e8eede46144657f9c.

Java 11 Maven package: PASS. Selected TIFF/GIF/IPI regression tests: 12 PASS.
Synthetic portable smoke: indexed plain-text and Polish OCR markers; detected two synthetic faces.
Locale key audit: no missing PL keys among external base property bundles (quality review still needed).

Full upstream test run was not green on Windows/Polish locale: 235 parser tests, 6 assertion failures, 4 RegRipper execution errors, 3 skipped. Telegram/WhatsApp expectations use English titles while localized output is Polish. Other assertions still require baseline comparison. Later reactor tests were not executed by that failed run.

Portable assembly retains external patched Tika 2.4.0 Microsoft and misc-office binaries from TEST7, plus its Python/model runtime and DBX p7 plugin; their hashes must be listed in the package manifest. Maven-produced TSK 4.15/native tools and Telegram 1.0.16 remain paired. This repository alone does not build those external patched artifacts.

Known local environment restriction: bundled RegRipper/rifiuti tools fail Windows execution checks (system DLL relocation error). Do not suppress these errors or claim all registry/recycle-bin functions passed.

Local corrections include optional JPEG OCR resampling, narrow known-GIF recovery, SPX/IPI detection, TIFF supplied-stream parsing, map image menu/thumbnails, and localized processing-window title. The TIFF real-world cause remains unconfirmed upstream; the test establishes supplied-stream behavior only.

Before distribution beyond experimental testers: second-PC regression, image/video AI, Telegram attachment coverage, reports, GPS map UI and index compatibility. Existing TEST7 and cases were not modified.


2026-11-13 follow-up: startup title bundle key fixed; Polish timeline, boolean display, filter brackets and CSAM filter labels; Polish short help restored. Viewer-only WhatsApp dependency diagnostics verified separately from processing (explicit false still reports both dependencies). User confirmed text/OCR searches and viewer without errors. Packaging excludes generated cases.
