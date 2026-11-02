# Portable Python/JEP smoke test

The portable test bundle includes the compiled class. The repository keeps source only. To compile with JDK 11, run from this directory (replace the IPED path):

```powershell
javac --release 11 -encoding UTF-8 -cp 'D:\IPED\lib\jep-4.0.3.jar' JepPortableTest.java
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Test-Python-JEP.ps1 -IpedDir 'D:\IPED'
```

`IpedDir` is the directory containing `iped.exe`, `lib`, `jre` and `python`. No package installation is performed. The script uses bundled runtimes, changes environment variables only for this process and restores them afterward. The SQLite test uses memory only.

The JSON report is written beside the script and contains local runtime paths. Review it before sharing. Read its UTF-8 explicitly in Windows PowerShell 5.1:

```powershell
Get-Content -LiteralPath '.\wynik-jep-YYYYMMDD-HHMMSS.json' -Raw -Encoding UTF8 | ConvertFrom-Json
```

PASS covers runtime origin, Python 3.9, arithmetic, UTF-8 round trip, SQLite and a Python-to-Java call. It does not validate all IPED Python tasks, AI models or GPU features.
