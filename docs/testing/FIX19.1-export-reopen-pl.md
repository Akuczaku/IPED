# Eksport i ponowne otwarcie fikcyjnej sprawy OCR

Data testu: 2026-11-03. Zakres: paczka IPED Polish Edition 4.3.1 OCR-PL-TEST i sprawa utworzona z fikcyjnego PNG oraz PDF bez warstwy tekstowej, opisana w [teście OCR](FIX19.1-ocr-pl.md).

## Wyniki

1. Użytkownik otworzył sprawę i wyszukał IPEDOCRALPHA2026. Zrzut pokazał dwa wyniki: OCR-PL-obraz.png i OCR-PL-skan.pdf.
2. Oba wyróżnione pliki wyeksportował przez interfejs IPED do osobnego folderu. Zrzut potwierdził obecność PNG i PDF.
3. Uruchomił Check-Export.ps1. Przekazany wynik: oba pliki True, status EXPORT_BYTES_PASS.
4. Po zamknięciu i ponownym uruchomieniu aplikacji otworzył tę samą sprawę. Kolejny zrzut ponownie pokazał oba wyniki wyszukiwania IPEDOCRALPHA2026.

To potwierdza zgodność bajtów eksportowanych oryginałów z dwoma plikami wejściowymi oraz dostępność wyników indeksu OCR po ponownym otwarciu tej sprawy. Nie potwierdza trwałości zakładek, eksportu raportu HTML/ZIP ani dowolnych innych formatów eksportu.

## Odtworzenie testu

Po wykonaniu testu OCR i otwarciu jego sprawy:

- Wyszukaj IPEDOCRALPHA2026 i zatwierdź Enterem.
- Wyróżnij oba wyniki z Ctrl, wybierz „Eksportuj wyróżnione elementy” i nowy pusty folder.
- Uruchom skrypt z repozytorium:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\tools\portable-smoke\export\Check-Export.ps1 -ExportDir 'C:\IPED-EXPORT-TEST'
```

Skrypt przeszukuje wskazany katalog rekurencyjnie i porównuje zawartość plików według SHA-256, niezależnie od nazw nadanych podczas eksportu. Nie zapisuje do plików eksportu. Raport JSON powstaje obok skryptu i zawiera lokalne ścieżki; przejrzyj go przed udostępnieniem i nie dodawaj do Git.

Oczekiwane sumy oryginałów:

| Plik fikcyjny | SHA-256 |
| --- | --- |
| OCR-PL-obraz.png | f743d855253bf56655053b4b68c495c649a492ef5589b2bfcecc35f88f5acb18 |
| OCR-PL-skan.pdf | 7568157ad2f090b3d6e64f542edc46a4197494536563f016f73f902f785d216f |

Te sumy dotyczą dokładnych plików użytych w teście. Ponowne wygenerowanie PDF może zmienić metadane i hash mimo identycznego obrazu. W takim przypadku sprawdź nowe wejście i ustal jego sumy przed testem eksportu; nie dostosowuj sum do już wyeksportowanych wyników tylko po to, aby uzyskać PASS.

Zamknij IPED, otwórz ponownie tę samą sprawę i powtórz wyszukiwanie. Oczekiwane są oba pliki. Nie wykonuj ponownego indeksowania w ramach kontroli trwałości indeksu.

## Walidacja i granice

Lokalna kontrola weryfikatora na oryginalnych fikcyjnych wejściach przeszła w Windows PowerShell 5.1. Następnie użytkownik wykonał właściwy eksport w GUI i przekazał EXPORT_BYTES_PASS. Kontrola weryfikatora na wejściach sama nie byłaby dowodem działania eksportu; dowód ten pochodzi z późniejszego testu użytkownika.

Archiwum OCR-PL-TEST nie zostało przebudowane. Nie dodano indeksów, spraw, raportów użytkownika, surowych logów ani binarnych plików testowych. Nie zmieniono parserów, danych prywatnych ani publicznego PR #3045. Test pozostaje ograniczony do dwóch syntetycznych plików.
