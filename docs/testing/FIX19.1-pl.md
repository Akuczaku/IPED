# FIX19.1 — raport stanu testowego portable

Data: 2026-11-02. IPED Polska Edycja by Czaku 4.3.1. Status: wydanie testowe, nie deklaracja pełnej walidacji wszystkich parserów.

## Zakres źródeł na tej gałęzi

Gałąź zbiera lokalne poprawki ICEpdf FIX16.4, raportów komunikatorów FIX17/FIX18, dalszej lokalizacji FIX19 i etykiety `messageStatus` z FIX19.1. Dodaje też raport testów. Bazuje na osobnej poprawce CI `74cac3389` i zapisie testów `96b7f51fa`. Publiczny PR #3045 nie jest modyfikowany.

Przeniesiono tylko ustalone zmiany lokalizacji. Pozostałe różnice lokalnego App.java względem tej gałęzi zachowano poza zmianą; dodany fragment dotyczy wyłącznie tekstów DockingFrames.

## Potwierdzone wyniki

| Zakres | Dowód i wynik |
| --- | --- |
| ICEpdf 7.0.0 | W pełnym IPED widoczne polskie etykiety, `1 z 3`, wyniki ALPHA na trzech stronach, działające Wyczyść i poprawiony komunikat postępu. |
| Rozmowy WhatsApp, Skype, Telegram | Zrzuty użytkownika potwierdzają poprawione wskazane etykiety, widoczne polskie znaki i daty syntetycznych wiadomości. |
| Załączniki | Sześć plików PNG/TXT: wyświetlenie lub otwarcie potwierdzono zrzutami. Nie porównywano ponownie bajtów eksportu. Szczegóły w raporcie FIX17/FIX18. |
| Portable FIX18 na drugim komputerze | Użytkownik potwierdził uruchomienie; nie jest to dowód pełnej niezależności od każdego składnika systemowego. |
| Portable FIX19 — indeksowanie | Log 2026-11-02: 53 elementy, 35 podrzędnych, 0 błędów parsowania, 0 błędów odczytu, 0 przekroczeń czasu. |
| Kategorie i panel | Zrzuty FIX19: „Zwykły tekst”, „Pozostałe teksty” i dymek „Przenieś ten panel na bok okna”. |
| Kontakty i metadane | Zrzuty: polskie etykiety kontaktu WhatsApp i nazwa do sortowania, nazwy wiadomości Skype z `_wiadomość_`, etykiety pozycji, typu konta i stanu wysyłania. |
| FIX19.1 | Zrzut działającej aplikacji potwierdza „Stan wiadomości” zamiast `messageStatus`. Identyfikator pola i wartość statusu nie ulegają zmianie. |
| DockingFrames — test programowy | 157 tekstów załadowano przez rzeczywisty CControl/TextManager; sprawdzono pokrycie kluczy. Nie oglądano ręcznie wszystkich 157 etykiet. |
| Python/JEP — test lokalny | Test uruchamia dołączoną Javę, Python 3.9 i JEP 4.0.3; sprawdza pochodzenie runtime, obliczenie, UTF-8, SQLite w pamięci i wywołanie Javy z Pythona. PASS. |
| Python/JEP — przekazany wynik z drugiej lokalizacji | Użytkownik przekazał JSON ze statusem PASS i `sys.prefix` wskazującym Python z portable na innym dysku; następnie jawny odczyt UTF-8 pokazał „Zażółć gęślą jaźń — Łódź”. Sam JSON nie identyfikuje komputera ani listy zainstalowanych programów. |

## Kodowanie wyniku JEP

Zniekształcone znaki przy domyślnym Get-Content w Windows PowerShell 5.1 były wynikiem odczytu pliku UTF-8 w innym kodowaniu. Odczyt `Get-Content -Raw -Encoding UTF8 | ConvertFrom-Json` potwierdził prawidłowy tekst. Nie wprowadzono na tej podstawie zmian parserów ani Pythona.

## Stan paczki

ZIP FIX19.1 zawiera aktualne portable, etykietę „Stan wiadomości”, fikcyjne dane FIX18, test Python/JEP i ten raport. Nie zawiera wcześniejszych spraw, indeksów, logów użytkownika ani jego wyników JSON. Sumy plików i ZIP są zapisywane przy pakowaniu.

To paczka z istniejącej dystrybucji Windows, przebudowanych modułów i zasobów. Część zmienionych klas FIX19 skompilowano JDK 11 i włączono do lokalnych JAR-ów. Nie opisujemy tego jako nowego, pełnego wydania Maven wszystkich narzędzi. Nowa gałąź źródłowa wymaga własnego CI.

## Nadal do sprawdzenia

- Konkretne funkcje Python/AI: ich modele, zależności, wyniki i obsługa błędów. PASS JEP tego nie potwierdza.
- GPU i OCR jako odrębne funkcje; nie wnioskować o nich z testu JEP.
- Cały interfejs, wszystkie typy raportów i pozostałe funkcje bibliotek. Naprawiono ustalony zakres mieszanej lokalizacji, nie przeprowadzono pełnego audytu wizualnego.
- Nowszy Skype, Telegram Android, grupy, połączenia i rekordy usunięte nie są objęte syntetycznym zestawem.
- Dołączony historyczny jep.bat ma ścieżki innego komputera; test używa własnego skryptu i nie deklaruje naprawy tego pliku.
- JRE zachowano z poprzedniej dystrybucji; nie wykonywano aktualizacji runtime ani pełnego audytu jego zależności.
- Poprzednie logi informowały o braku opcjonalnej wtyczki JPEG2000 dla PDF. Nie deklarujemy dodania tej wtyczki.

Parsera PDF, danych prywatnych i dotychczasowych spraw nie zmieniono. Surowe nazwy tabel i kolumn oraz techniczne identyfikatory pól pozostają oryginalne.

## Korekta po pierwszym CI tej gałęzi

Przebieg 41491250971 wykazał jedno niepowodzenie w `TelegramParserTest.testTelegramParser`: oczekiwano `phone:`, otrzymano `Phone:`. Przyczyną było użycie etykiety nagłówka kontaktu wewnątrz opisu uczestnika.

Rozdzielono zasoby: `TelegramContact.Phone` pozostaje nagłówkiem `Phone:` / `Telefon:`, a nowy `TelegramContact.PhoneInline` zachowuje dotychczasowe `phone:` po angielsku i `Telefon:` po polsku. Testów nie wyłączono ani nie zmieniono ich oczekiwań.

Lokalnie uruchomiono oryginalny `TelegramParserTest` z dekoderem Android 1.0.14: `OK (2 tests)`. Osobne procesy Java potwierdziły tekst uczestnika w języku polskim i angielskim. Przebieg CI [41493151064](https://github.com/Akuczaku/IPED/actions/runs/41493151064) dla commita `18bf54fcb` zakończył się powodzeniem dla Java 11 i Java 14. Użytkownik potwierdził również wynik obu kontroli w PR #1.

## Przegląd przed scaleniem

W czytniku ICEpdf zastąpiono wymuszony język polski językiem wybranym przez `LocaleResolver`, używanym również przez lokalizację pozostałych przeglądarek IPED. Polska konfiguracja nadal wybiera nowy pakiet polski; konfiguracje innych języków korzystają z pakietów ICEpdf. Ta korekta wymaga nowego przebiegu CI i sprawdzenia GUI; wcześniejsze zielone kontrole dotyczą commita `18bf54fcb`. Dotychczasowych paczek portable nie przebudowano w ramach tego przeglądu.

Weryfikacja lokalna korekty: kompilacja IcePDFViewer przez javac --release 11 przeciw bibliotekom portable zakończona powodzeniem. Test rzeczywistych pakietów zasobów: pl-PL → Szukaj, en-US → Search; domyślny język JVM nie został zmieniony. Wyłączono powrót do języka systemowego przy braku pakietu dla wybranego języka, zachowując dziedziczenie pakietu bazowego ICEpdf. Nie zastępuje to testu GUI.
