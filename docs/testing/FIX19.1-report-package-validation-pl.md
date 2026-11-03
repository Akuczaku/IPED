# Gotowa paczka REPORT-FIX i raport indeksowany — 2026-11-03

## Identyfikacja paczki

Archiwum: IPED-Polish-4.3.1-PORTABLE-REPORT-FIX-TEST.zip.

SHA-256:

```text
4cf71a591c3d2082ef4157a0826c894192812ba838506300e1f57b3fe9565f70
```

Wewnętrzny katalog to IPED-PL. Zweryfikowano 2438 plików względem manifestu; dodatkowym plikiem jest sam manifest. Nazwy sześciu fikcyjnych plików z polskimi znakami zachowano w UTF-8. Wykluczono sprawy, indeksy, raporty użytkownika, logi i kopie konfiguracji. Domyślne ustawienia: pl-PL, OCRLanguage=pol, enableOCR=false. Model i licencja są dołączone.

To lokalnie złożona testowa dystrybucja Windows oparta na wcześniejszej paczce OCR-PL i sprawdzonych nakładkach z PR #6. Nie jest pełnym nowym buildem Maven wszystkich runtime i narzędzi. Źródła PR #6 przeszły CI Java 11 i 14, a odczytany commit scalający to 8c4b4f53098f7195507dfbc36543f11f2e148cfa.

## Test po rozpakowaniu

Użytkownik uruchomił Otworz-sprawe.cmd z nowo rozpakowanej paczki. Konsola wskazywała bibliotekę java-dbx z nowej lokalizacji programu. GUI pokazało istniejącą fikcyjną sprawę OCR z 3 elementami i zachowaną zakładką TEST_ZAPISU_2026. Przekazany fragment konsoli nie zawierał błędu JEP; nie zastępuje to pełnego przeglądu wszystkich logów modułów Pythona.

Z tej paczki użytkownik utworzył nowy raport dla zakładki TEST_ZAPISU_2026, bez instalowania dodatkowych nakładek. Zrzut HTML potwierdził:

- jedną pozycję OCR-PL-obraz.png;
- zakładkę i komentarz „Zażółć gęślą jaźń — test zapisu”;
- „Strona 1 z 1”, jednostkę B, „Rozszerzenie” i polską kategorię;
- odnośnik do wyeksportowanego pliku i miniaturę.

## Raport indeksowany

Odczyt katalogu raportu potwierdził raport.htm, IPED-SearchApp.exe i iped/index. W katalogu eksportu znaleziono jeden PNG o rozmiarze 124475 bajtów i SHA-256:

```text
f743d855253bf56655053b4b68c495c649a492ef5589b2bfcecc35f88f5acb18
```

Suma zgadza się z dokładnym oryginalnym wejściem fikcyjnego testu. Nie wnioskowano o zgodności bajtów na podstawie samego wyglądu miniatury.

Użytkownik zamknął główne IPED i uruchomił IPED-SearchApp.exe znajdujący się w wygenerowanym raporcie. Zrzut pokazał otwartą sprawę raportu, jeden PNG, zakładkę TEST_ZAPISU_2026 (1) i komentarz z poprawnymi polskimi znakami. Kolejny zrzut potwierdził podgląd PNG wewnątrz raportu indeksowanego.

## Powiązany test zakładek

W źródłowej fikcyjnej sprawie utworzono zakładkę TEST_ZAPISU_2026, przypisano PNG i dodano komentarz. Po normalnym zamknięciu i ponownym otwarciu zrzuty potwierdziły zachowanie nazwy, licznika (1), komentarza oraz filtrowania: wybór tej zakładki pokazywał tylko OCR-PL-obraz.png. Kontrola ta jest odrębna od wcześniejszego testu trwałości indeksu OCR.

## Zakres i ograniczenia

Zaliczony zakres: otwarcie istniejącej sprawy z nowego ZIP; utworzenie raportu HTML i indeksowanego dla jednej zakładki z jednym syntetycznym PNG; zachowanie komentarza; zgodność bajtów eksportu; uruchomienie programu raportu i podgląd PNG. Sprawa źródłowa i program pozostawały na tym samym komputerze. Nie przenoszono wygenerowanego raportu na niezależny komputer i nie sprawdzano go po odłączeniu źródeł. Nie deklarujemy na tej podstawie pełnej niezależności od instalacji ani zgodności wszystkich formatów raportów.

Nie przebudowano ZIP po tym potwierdzeniu. Nie zmieniono parserów, danych prywatnych ani publicznego PR #3045. Test nie potwierdza wszystkich parserów, modeli AI/GPU, dowolnej jakości OCR ani wszystkich narzędzi zewnętrznych. Historyczne ostrzeżenia numpy/JPEG2000 i narzędzi RecycleBin/Regripper nie zostały naprawione w ramach tej kontroli.
