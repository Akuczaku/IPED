# IPED Polish Edition by Czaku — developer status

**Baseline:** upstream IPED 4.3.1 (`4.3.1` tag).  
**Local development designation:** v0.6 FIX4.2.  
**Release status:** working local Portable variants, not a published binary release.

## Changes observed in the local development build

| Area | Local observation | Validation limit |
|---|---|---|
| Polish UI | Locale resources and a Polish timeline viewer were tested | Some technical metadata labels remain untranslated |
| DBX Unicode | 29 messages from one fixture, 30 items indexed, Unicode subject displayed | More fixtures and regression tests required |
| DBX parser bootstrap | Encountered `ClassNotFoundException: dpf.sp.gpinf.indexer.parsers.OutlookDBXParserImpl` with 0 extracted subitems; a subsequent local FIX4.2 build worked | Not yet demonstrated against unmodified upstream |
| Portable | BASE and GOLDEN functioned in the developer's Windows environment | Isolated second-machine/JRE audit pending |

## Commit boundaries

- Commit A: Polish documentation (this package).
- Commit B: DBX Unicode parser change **only after reviewing exact source diff**.
- Commit C: DBX indexer/bootstrap classpath change **only after reviewing exact source diff**.

Never copy the entire existing source tree over the Git fork, which could add build outputs, development backups or unrelated changes.
