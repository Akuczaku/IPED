# FIX17 / FIX18 — wyniki testów polskiej lokalizacji

Data: 2026-11-02. Projekt: IPED Polska Edycja by Czaku 4.3.1.

## Środowisko i materiał

Testy ręczne wykonano w Windows na lokalnej, zmodyfikowanej kompilacji 4.3.1. Użyto wyłącznie syntetycznych danych: WhatsApp Android (msgstore.db, wa.db i XML konta), Skype w starszym formacie main.db oraz Telegram iOS Postbox. Kontakty Łukasz Testowy i Żaneta Testowa są fikcyjne.

Ten dokument zapisuje wyniki lokalnych testów potwierdzone logami i zrzutami użytkownika. Nie oznacza, że poprawki FIX17 są już częścią kodu tej gałęzi lub publicznego PR #3045. Lokalny patch FIX17 ma SHA-256 `89C0015140557E6A83C662D28168BCD9E21E69EE573D5238B2F2B3101CC45ED5`, a testowany moduł iped-parsers-impl-4.3.1.jar — `3B1338E70C61F8A3B3297CDDF22380B72F02598E7C72346C1154C4E29D85FD21`.

## FIX17 — raporty rozmów

| Test | Potwierdzony wynik |
| --- | --- |
| WhatsApp | Nagłówek „WhatsApp Czat”, tytuł „Konto WhatsApp” i nazwy elementów zawierające „wiadomość” są po polsku. Widoczne kontakty, polskie znaki i godziny ALPHA/BETA są poprawne. |
| Skype | W raporcie rozmowy widoczne „Nazwa czatu”, „Utworzono”, „Ostatnia aktywność”. Portugalskie etykiety zastąpiono polskimi. Widoczne daty i tekst wiadomości są poprawne. |
| Telegram | Nazwy „Telegram_Czat” i „wiadomość”; przy nadawcach „Telefon:” zamiast „phone:”. Widoczne kontakty, polskie znaki i godziny są poprawne. |

Pierwotna sprawa FIX17: kod zakończenia 0, 0 błędów parsowania, 0 błędów odczytu, 27 elementów podrzędnych, 36 zindeksowanych elementów. Poprawione raporty sprawdzono następnie w osobnej sprawie FIX17-CASE2. Nie przypisujemy statystyk pierwszego przebiegu drugiej sprawie.

Kompilacja poprawki lokalnej zakończyła się BUILD SUCCESS. Testy Maven pominięto; osobno wykonano testy odczytu syntetycznych baz, generowania raportów oraz sprawdzenie polskich i angielskich zasobów. Surowych nazw tabel i kolumn SQLite nie tłumaczono.

## FIX18 — załączniki

Dla każdego komunikatora przygotowano PNG i dokument TXT z polskimi znakami w nazwach i treści. Nowe wiadomości pochodzą z 2026-01-15, 12:03 i 12:04 UTC (13:03 i 13:04 +01:00 w Europe/Warsaw).

| Komunikator | Obraz PNG | Dokument TXT |
| --- | --- | --- |
| WhatsApp Android | Zrzut pokazuje powiązany obraz w podglądzie IPED. | Zrzut pokazuje dokument otwarty w Notatniku, z poprawnymi znakami i UTF-8. |
| Skype main.db | Raport transferu zawiera dopasowanie, miniaturę i rozmiar 10 233 bajtów; kolejny zrzut pokazuje otwarty obraz w aplikacji Zdjęcia. | Zrzut pokazuje plik otwarty z raportu transferu w Notatniku, z poprawnymi polskimi znakami i UTF-8. |
| Telegram iOS | Zrzut pokazuje obraz otwarty z raportu rozmowy w aplikacji Zdjęcia. | Zrzut pokazuje dokument otwarty z raportu rozmowy w Notatniku, z poprawnymi znakami i UTF-8. |

Wynik zakresu FIX18: 6/6 testów wyświetlenia lub otwarcia plików potwierdzonych zrzutami. Nie wykonano ponownego haszowania plików otwartych przez aplikacje zewnętrzne; wynik nie jest deklaracją zgodności bajtowej eksportu.

Log indeksowania FIX18: 53 zindeksowane elementy, 35 elementów podrzędnych, 0 błędów parsowania, 0 błędów odczytu i 0 przekroczeń czasu. Występują informacje o nieusuniętych tymczasowych bibliotekach oraz ostrzeżenie o braku wtyczki JPEG2000 dla PDF; nie uznano ich za niepowodzenie tych testów PNG/TXT.

Skype prezentuje rekordy Transfers jako osobne elementy, nie jako załączniki osadzone w wiadomościach rozmowy. W teście zadziałało dopasowanie pliku po nazwie i rozmiarze.

## Pozostałe korekty i ograniczenia

- Skype: „Zaakceptuj datę” należy zmienić na „Data akceptacji”.
- Skype: „Prawdopodobnie Plik” wymaga korekty językowej, np. „Prawdopodobny plik”.
- Skype: brak separatora w tytule „Transfer przez SkypeFIX18…” — potrzebny odstęp lub dwukropek.
- Część dymków paneli aplikacji pozostaje po angielsku. To oddzielny zakres lokalizacji.
- Nie testowano rzeczywistych baz użytkowników, nowszego Skype, Telegram Android, grup, połączeń, usuniętych rekordów, innych formatów załączników ani wszystkich etykiet raportów.
- Nie zmieniano parsera PDF ani danych prywatnych. Wcześniejsze sprawy zachowano. Publiczny PR #3045 pozostaje odrębny i bez zmian.

Na tej gałęzi dodano wyłącznie niniejszy raport względem commita bazowego `74cac3389`. Jest to zapis wyników, nie wydanie nowej dystrybucji IPED.
