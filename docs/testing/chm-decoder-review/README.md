# Apache Tika CHM decoder candidate (review only)

This patch targets the Apache Tika 2.4 CHM sources bundled with IPED 4.3.1. It is not wired into the IPED build and does not change a published portable package. Apache license headers in the source diff are retained.

## Problem and changes

Valid single-frame NirSoft help files were rejected because the reset table had one address. A current 7-Zip help file additionally has an explicit zero ITSF v3 data offset, a 128 KiB LZX window and back-references spanning multiple frames. The old decoder used an incomplete header, inferred the data offset, hardcoded its main-tree size/threshold, and retained only the previous frame.

The candidate accepts one reset-table address, rejects invalid indices, reads the full v3 header and explicit offset, derives Huffman symbols from the compression window and retains window-sized history. Overlapping back-references are copied across the retained history/current output boundary.

## Local validation (2026-11-06)

- Seven JUnit checks passed, including empty table, out-of-range index and truncated-header rejection.
- All 72 HTML pages from three samples matched independent 7-Zip extraction byte for byte.
- Full isolated IPED processing: parsing exceptions 3 -> 0; both runs indexed 4 items and processed 161862 bytes. Candidate had 0 timeouts and 0 I/O read errors.
- Original runtime libraries were restored and their hashes verified after testing.

Samples were public CProcess.chm (14348 bytes), current LastActivityView.chm (20066 bytes), and locally installed 7-Zip help (127448 bytes). Obtain the first two from the official NirSoft utility ZIPs and extract 7-Zip help from an installation; then independently extract each CHM using 7-Zip. The Java comparison expects INPUT/<name>.chm and EXTRACTED-<name>/ directories for names CProcess, LastActivityView and 7-zip-current. Set sample.dir to the INPUT directory when running JUnit. No third-party CHM binaries or user data are included here.

## Limits and next review

No full Maven build or broad malformed-input/window-size corpus was run. The tester's private files have not been verified byte-identical to these samples. Unrelated missing rifiuti/RegRipper initialization errors existed in the isolated test environment; the whole environment is not claimed error-free.

Review this against Apache Tika's current CHM implementation before upstreaming. Further review should cover memory limits, malformed streams, all supported window sizes and the legacy four-argument constructor. The PDF parser and upstream IPED PR #3045 are untouched.

The Java tests are review artifacts, not automatically executed by IPED CI. The diff paths refer to the CHM source directory in tika-parser-microsoft-module 2.4.0.
