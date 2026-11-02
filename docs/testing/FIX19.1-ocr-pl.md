# Polski OCR w testowym Windows portable

## Audyt i potwierdzone wyniki

Audyt użytkownika: Tesseract 5.3.2-24-g3922, enableOCR=false, OCRLanguage=por, brak pol.traineddata. Z oficjalnego tesseract-ocr/tessdata_fast pobrano polski model przypięty do rewizji i SHA-256 w tools/portable-smoke/ocr-pl/model/SOURCE.json. Dołączona licencja to Apache-2.0. Modelu binarnego nie przechowujemy w tym PR.

Fikcyjny PNG 300 DPI i jednostronicowy PDF bez warstwy tekstowej przetworzono przez IPED z pol, pageSegMode=6, skipKnownFiles=false. Test przywrócił oryginalne ustawienia i usunął tymczasowo dodany model. OCR_TEXT_CHECK_PASS: dwa wpisy bazy ocr-results.db zawierały IPEDOCRALPHA2026 oraz „Zażółć gęślą jaźń.”. Zrzut GUI pokazał wyszukanie obu plików; użytkownik potwierdził polskie znaki w zakładce Tekst PNG i PDF. Nie przejrzano jeszcze pełnych statystyk błędów tego przebiegu OCR.

## Odtworzenie testu

W tools/portable-smoke/ocr-pl, na Windows z Pythonem wyposażonym w Pillow, reportlab i pypdf:

```powershell
python .\generate_fixture.py
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Get-PolishModel.ps1 -ModelDir .\model
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Audit-OCR.ps1 -PackageDir 'C:\IPED'
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Test-OCR-PL.ps1 -PackageDir 'C:\IPED'
```

Zamknij IPED przed testem. Generator używa Arial z C:/Windows/Fonts. PDF zawiera wyłącznie raster; test generatora sprawdza pusty tekst i identyczność pikseli z PNG. Do indeksowania trafia wyłącznie INPUT, nigdy EXPECTED.txt. Podczas testu konfiguracje są zapisywane w kopiach i przywracane bajt w bajt w finally. Test nie jest odporny na wymuszone zabicie procesu; kopie pozwalają wtedy przywrócić ustawienia ręcznie.

Sprawdź raport JSON i pełny log IPED. check_ocr.py sprawdza dwa wpisy z frazą w bazie, nie przyporządkowuje ich do GUI; stąd osobny test wyszukiwania obu plików i kontroli zakładki Tekst. Przetwarzanie może zapisywać logi i wyniki lokalnie; nie dodawaj ich do Git.

## Przygotowanie paczki z polskim modelem

Pobierz model powyższym skryptem, sprawdź hash i skopiuj do iped-4.3.1/tools/tesseract/tessdata/pol.traineddata. Obok umieść LICENSE jako pol-LICENSE.txt i SOURCE.json jako pol-SOURCE.json. W kopii konfiguracyjnej paczki ustaw OCRLanguage=pol w conf/OCRConfig.txt. Zachowaj enableOCR=false w IPEDConfig.txt. Nie zmieniaj pozostałych parametrów ani parserów. Test PSM=6 nie jest pełnym potwierdzeniem jakości domyślnego PSM=1.

Skopiuj Ustaw-OCR.ps1 z tools/portable-smoke/windows do głównego folderu paczki. Zamknij IPED; potem:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Ustaw-OCR.ps1 -State On
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\Ustaw-OCR.ps1 -State Off
```

Skrypt sprawdza model i OCRLanguage=pol przed włączeniem, tworzy kopię IPEDConfig.txt i zmienia tylko enableOCR. Test lokalny Windows PowerShell 5.1: On/Off PASS, pozostała treść konfiguracji identyczna. Zmiana dotyczy przyszłego przetwarzania, nie istniejących indeksów.

W lokalnej paczce OCR-PL-TEST dołączono model/licencję/proweniencję, OCRLanguage=pol i skrypt przełączania. OCR pozostaje domyślnie wyłączony. Biblioteki, runtime i parser PDF są bez zmian. Nowy ZIP wymaga sprawdzenia po rozpakowaniu; wcześniejszy test OCR dotyczył tymczasowej konfiguracji opisanej powyżej. Nie jest to walidacja dowolnych skanów, AI ani GPU. Publiczny PR #3045 pozostaje bez zmian.
