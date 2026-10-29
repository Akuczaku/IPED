# IPED Polish Edition by Czaku

> Nieoficjalna polska edycja projektu [IPED](https://github.com/sepinf-inc/IPED), rozwijana na bazie wersji **4.3.1**.

## O projekcie

Celem projektu jest przygotowanie polskojęzycznego interfejsu i dokumentacji IPED oraz sprawdzenie wybranych poprawek dotyczących przetwarzania skrzynek pocztowych Outlook Express DBX i uruchamiania programu w Windows.

Projekt jest **niezależnym forkiem**, a nie oficjalnym wydaniem IPED ani projektem zatwierdzonym przez jego autorów. Oryginalny IPED i jego twórcy zachowują odpowiednie informacje o autorstwie i licencjach.

## Stan prac

- **Baza źródłowa:** IPED 4.3.1.
- **Wersja rozwojowa polskiej edycji:** v0.6 FIX4.2.
- **Lokalizacja:** polskie zasoby interfejsu; weryfikacja kompletności trwa.
- **DBX Unicode:** lokalna poprawka dekodowania tematów wiadomości, w tym znaków Unicode; wymaga osobnego przeglądu kodu i testów regresji.
- **DBX class loading:** lokalnie zaobserwowana i poprawiona niedostępność klasy `OutlookDBXParserImpl` w procesie indeksowania; nie potwierdzono jeszcze, że jest to błąd czystego upstream.
- **Windows Portable:** lokalnie przetestowane warianty BASE i GOLDEN. Nie stanowią one na tym etapie publicznego wydania.

## Uwaga o zawartości repozytorium

To repozytorium powinno zawierać **kod źródłowy i niezbędne zasoby**, nie zaś dane dowodowe, utworzone sprawy, indeksy, logi, konfiguracje osobiste lub skopiowaną instalację Portable. Zmiany w parserze DBX i bootstrapie powinny trafiać do odrębnych commitów po przeglądzie diffów.

## Kompilacja i testy

Kompilacja i uruchamianie opierają się na instrukcjach oraz wymaganiach oryginalnego projektu IPED. W lokalnym środowisku program zbudowano przy użyciu Java 11 i Maven. Wynik testu lokalnego nie gwarantuje działania na każdej instalacji Windows.

## Upstream i licencja

- Projekt bazowy: https://github.com/sepinf-inc/IPED
- Fork: https://github.com/Akuczaku/IPED

Obowiązują licencje, powiadomienia o autorstwie i warunki dystrybucji poszczególnych składników oryginalnego IPED. Przed dystrybucją binarną konieczny jest przegląd zależności i dołączonych komponentów.

## Dalsze prace

1. Audyt kompletności lokalizacji.
2. Oddzielny commit poprawki tematów DBX Unicode.
3. Oddzielny commit poprawki classpath/bootstrapa DBX wraz z testami.
4. Regresja GUI, DBX i dokumentacji.
5. Ewentualne zgłoszenia upstream dopiero po weryfikacji na czystej wersji IPED.
