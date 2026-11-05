# Embedded binary Office container lifetime regression

Synthetic reproducer for `ClosedChannelException` on Word DOC entries in TAR/ZIP.
No private user documents or logs are included.

The ZIP contains three generated DOC files, the same files in TAR/ZIP, a public
IPED test template, its licensing information and the generator source. The DOC
body and summary author/title are replaced with synthetic content. See the ZIP's
README and SHA256SUMS for provenance and exact inputs.

## Local observations (2026-11-05)

Runtime: portable IPED 4.3.1 TEST3, Java 11.0.13, POI 5.2.2, Tika 2.4.0-p1.
Process the unpacked INPUT directory into a new case using profile `forensic`.

| Check | Original engine | Candidate engine |
| --- | ---: | ---: |
| Total items | 17 | 17 |
| DOC parsing exceptions | 6 (3 TAR + 3 ZIP) | 0 |
| Timeouts / input I/O errors | 0 / 0 | 0 / 0 |
| DOC items with synthetic author metadata | 3 of 9 | 9 of 9 |
| DOC items matching the indexed 20261105 marker | 9 | 9 |

The marker was searchable even before the fix, so a successful keyword search
alone does not establish complete Office extraction. The author/title metadata
comparison verifies that the candidate restores data instead of hiding warnings.
The same original failure was independently reproduced by the user on TEST3
without the forensic profile (6 exceptions, 17 items).

Additional candidate control run: synthetic XLS, PPT and DOCX, both standalone
and inside TAR/ZIP, produced 14 items, 0 parsing exceptions, 0 timeouts and
0 input I/O errors. The unique content marker was indexed in all 9 documents.

Three unrelated external-tool setup errors (rifiuti/RegRipper) occurred under the
isolated test home in each run. They are not DOC failures; the entire environment
is not described as error-free.

`SyntheticDocProbe` also exercises direct Tika OfficeParser parsing of each DOC
via stream, file-backed TikaInputStream and memory input: 9 passes with end marker.
`CheckSyntheticDocIndex` inspects the resulting Lucene index and stored author/title.
`GenerateOfficeControls` creates the additional XLS/PPT/DOCX controls.

## Candidate change

`ParsingTask.parseEmbedded` previously transferred a detected POIFSFileSystem
from the extraction stream to an independently queued child. The extraction
stream can close that filesystem before the child's parser reads it. Skip only
that POIFS object when handing off the child, allowing OfficeParser to reopen
its own filesystem from the extracted bytes. Other custom openContainer objects
retain their existing behavior. No parser exceptions are suppressed.

The changed ParsingTask was compiled with JDK 11 against the shipped libraries;
only its classes were replaced in the test engine JAR. All other JAR entries
were compared byte-for-byte. The full Maven reactor was not run. No claim is made
that every private document is repaired, or that current upstream has been tested.

This is an experimental candidate. Reprocess affected files into a NEW case;
opening or resuming an existing index does not retroactively restore metadata.
