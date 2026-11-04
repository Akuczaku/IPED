# Tester diagnostics fixes

Closing the processing preview before viewer panels exist used to throw at
ArrayList.addAll(viewerDocks). Null collections, empty dock contents and absent
case/control objects are now handled during cleanup.

Graph loading is deferred while the same application has an active processing
Manager. GraphTask creates the database during finalization; the UI explicitly
asks to reopen the case after processing. Missing graph data after processing
still logs an error. No finished-case error is suppressed.

The forensic profile makes external hash database lookup opt-in. Configure
hashesDB in LocalConfig.txt before setting enableHashDBLookup=true. HashTask and
SHA-256 calculation are unchanged. No hash database is bundled.

Two targeted retry profiles are included: portable-large-images uses 600 + 8
seconds per MB as the parsing budget; portable-text-recovery uses TXTParser in
place of TextAndCSVParser for an exported damaged TXT input. Other parser entries,
including PDF, are unchanged. Use only affected files and a NEW output case.

Validation on Java 11: standalone PreviewCloseRegression failed with the original
JAR and passed with the patch (including empty panels and repeated cleanup).
GraphPreviewRegression verified deferral before graph configuration/service access.
The harness uses Unsafe only to simulate partially initialized UI objects without
opening a real window; it is not production code. Compile against the app JAR and
bundled lib/*, place patched classes first. Run headless.

PowerShell 5.1 installer validates payload and original target hashes before writes,
backs up files, rolls back on installation error and updates the file manifest.
Fresh installation/repeat passed. Incompatible edited config was rejected without
any changes. The installer in this directory requires the released PATCH payload;
the binary JAR payload is not stored in Git.

Synthetic TXT retry: end marker searchable, 0 timeouts/parser exceptions/read errors.
Synthetic PNG retry: configured budget observed, OCR marker recovered, 0 timeouts
and parser exceptions. The tester's private JPG/TXT were not available and their
failures are NOT claimed fixed. A small synthetic CSV example did not reproduce
the exact original exception; recovery is an opt-in workaround, not a parser fix.

JPEG2000/JPX dependency is still absent and not fixed here. Temporary file deletion
behavior is unchanged. GUI regression verification on tester hardware and full
Maven CI are still pending. No private logs, paths, filenames or samples are
included. Public upstream PR #3045 is unchanged.

Compatibility follow-up: the installer also accepts the original Portable AI OCR PL
TEST archive manifest (SHA256 3077ffc1aff82dacd7597bda31b058e2fb89f8573aa3c75126b13ceaf123b314).
The original archive SHA256 and each replaced file were verified; all replaced
originals match TEST2. No arbitrary manifest bypass was added. PowerShell 5.1
installation and repeat on an isolated AI TEST target passed.
