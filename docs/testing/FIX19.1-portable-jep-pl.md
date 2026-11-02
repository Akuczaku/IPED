# Launchery Windows portable — FIX19.1 JEP-FIX2.1

## Problem i wynik

Po przeniesieniu paczki aplikacja otwierała sprawę, ale zgłaszała brak JEP i wyłączała moduły Pythona. Samodzielny test dołączonego Python/JEP przechodził. Launchery ustawiają teraz na czas procesu PATH i PYTHONHOME na runtime paczki, czyszczą PYTHONPATH i przekazują Java właściwe java.library.path oraz iped.root. Zmienne środowiskowe przywracane są w finally; systemowe ustawienia nie są zmieniane.

Otworz-sprawe.ps1 korzysta z BootstrapUI w iped.jar i tymczasowej listy spraw UTF-8. Nie zakłada obecności IPED-SearchApp.exe w katalogu programu. Test-portable.ps1 używa Bootstrap do przetworzenia dołączonych fikcyjnych danych w nowej sprawie, zapisuje log konsoli i raport JSON, a następnie otwiera sprawę tym samym launcherem. Odmawia użycia istniejącego folderu wynikowego.

## Odtworzenie launcherów w paczce

Zachowaj istniejącą strukturę Windows portable:

```text
PACKAGE/
  iped-4.3.1/       # iped.jar, lib/, jre/, python/, konfiguracja i narzędzia
  plugins/
  test-data/       # wyłącznie fikcyjne dane
  Otworz-sprawe.cmd
  Otworz-sprawe.ps1
  Test-portable.ps1
```

Skopiuj trzy pliki z tools/portable-smoke/windows do głównego folderu paczki. Wymagany jest dołączony runtime Windows z python39.dll i Lib/site-packages/jep/jep.dll. Nie instaluj pakietów do systemowego Pythona w ramach tego kroku.

W Windows PowerShell 5.1, z folderu paczki:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Test-portable.ps1
```

Można też podać -PackageDir z bezwzględną ścieżką paczki. Wartość domyślna jest ustalana wewnątrz skryptu przez PSScriptRoot, po deklaracji parametrów — poprawka błędu pustej ścieżki w Windows PowerShell 5.1.

Aby otworzyć już istniejącą sprawę, uruchom Otworz-sprawe.cmd i wskaż folder zawierający iped/index. Nie wskazuj folderu programu. W trybie wielosprawowym pytanie o utworzenie grafu można odrzucić, jeśli test dotyczy tylko PDF.

ZIP rozpakuj do krótkiej ścieżki, np. C:\IPED2. Windows Explorer przy głębokiej ścieżce zgłaszał 0x80010135. Nie pomijaj plików przy rozpakowywaniu.

## Dowody testów przekazane przez użytkownika

- Samodzielny test runtime w przeniesionej paczce: PASS, dołączony Python 3.9.12, poprawne UTF-8, SQLite 3.37.2, Python → Java. Raport wskazywał sys.prefix w paczce.
- Po poprawce launchera użytkownik potwierdził otwarcie istniejącej sprawy; kolejny fragment konsoli nie zawierał błędu JEP.
- Test przetwarzania z ustawieniami JEP: kod 0, indeks istnieje, w konsoli brak błędu ładowania JEP. Końcówka pełnego logu: 55 indeksowanych elementów, 35 podrzędnych, 0 Parsing Exceptions, 0 I/O read errors, 0 Timeouts, 36 sekund.
- Zintegrowany Test-portable.ps1 z jawnym PackageDir: PROCESSING_COMPLETED_NO_JEP_LOAD_ERROR; użytkownik potwierdził automatyczne otwarcie sprawy z 55 elementami.
- Poprawka domyślnej ścieżki w 2.1: osobny lokalny test w Windows PowerShell 5.1 potwierdził istniejący, niepusty katalog; nie powtarzano całego przetwarzania po tej korekcie.
- Polski PDF: wyszukiwanie na 3 stronach, po 1 wyniku, Wyczyść i dymek formularzy. Angielski PDF: Search, Clear, Text Results, of 3 i angielski dymek. Biblioteki nie zmieniły się podczas napraw launcherów.

Testowy zestaw FIX18 z dodanym folderem PDF daje 55 elementów. Liczba dotyczy tego konkretnego zestawu, a nie dowolnych danych. Same zerowe czasy zadań Python w statystykach nie dowodzą uruchomienia modeli.

## Integralność i granice

Archiwum testowe IPED-Polish-4.3.1-FIX19.1-PORTABLE-REVIEWED-JEP-FIX2.1.zip:

```text
db6f7370ab3258be6f5373a3a0f605e35557f506a8cfb9e7c1326bfcad459ad7
```

Zweryfikowano 2428 plików w archiwum względem jego manifestu SHA-256. Binarnej paczki, prywatnych logów, indeksów, spraw, lokalnych raportów JSON i klas .class nie dodajemy do repozytorium.

To odtwarza launchery, nie cały Windows portable z samego Maven. Paczka Windows powstała na wcześniejszej dystrybucji z lokalnymi modułami; artefakt CI Linux nie jest z nią identyczny. Pełny powtarzalny proces budowania Windows, licencje i pochodzenie binarnych runtime wymagają osobnej pracy.

Raport JSON skryptu bada kod zakończenia, istnienie indeksu i komunikaty konsoli. Nie zastępuje kontroli pełnego logu IPED. Delete failed dotyczyło sprzątania tymczasowych DLL. Testy nie potwierdzają wszystkich parserów Python, numpy, AI, OCR, GPU ani pełnej niezależności od składników systemowych. Nie zmieniono parsera PDF, runtime ani publicznego PR #3045.
