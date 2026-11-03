# Portable AI - potwierdzone testy 2026-11-03

## Automatyczne kontrole w kopii testowej

- Python: import numpy 1.26.4, Pillow 10.4.0, dlib 19.23.1,
  face-recognition 1.3.0, modele 0.3.0, OpenCV 4.10.0.
- IPED przetworzyl dane w PowerShell 5.1. Oba wygenerowane obrazy: face_count=2,
  grafika bez twarzy: face_count=0. W indeksie zapisane wektory 128-wymiarowe.
- Odleglosci w indeksie: odpowiadajace twarze ok. 0.28 i 0.30; inne osoby
  ok. 0.95 i 0.96. Nie jest to ogolna walidacja skutecznosci.
- Podobienstwo obrazow: 4 wektory, mniejszy/jasniejszy dom blizszy bazie
  niz odmienny wzor. GUI uzytkownika: mniejszy dom 97%.
- Bazowy ZIP AI: 3898 plikow sprawdzonych SHA-256; po wypakowaniu runtime
  zaladowal biblioteki i modele oraz wykryl dwie twarze.

## Potwierdzenia GUI uzytkownika

- Drugie zdjecie tych samych fikcyjnych osob: 70%, wzorzec 100%.
- Nowa bazowa paczka AI przetworzyla fikcyjne zdjecia poprawnie.
- Sprawa TestFilmAI01: wzorzec 100%, film z twarzami 76%, kontrola bez twarzy
  pominieta. Na siatce klatek widoczne obramowania.
- Jedna zaznaczona twarz: film znaleziony, obramowania tylko wybranego mezczyzny.

## Zakres i ograniczenia

Obie osoby sa wygenerowane, dorosle i fikcyjne. Film MP4 ma 20 sekund,
25 fps i sklada sie z dwoch statycznych obrazow. Bez glosu. Nie testowano
ruchu, rozmycia, zasloniecia, malych twarzy ani wszystkich kodekow.
Procent to miara programu, nie prawdopodobienstwo tozsamosci.
Wczesniejsze testy OCR, eksportu, zapisu zakladek i raportow opisano osobno.
Nie wlaczono pozostalych modulow AI. Stare runtime nie byly aktualizowane.
W izolowanym srodowisku pozostaja niezalezne ostrzezenia rifiuti/RegRipper;
GUI moze wypisac ostrzezenia WhatsApp o wlasciwosciach HashTask mimo
wlaczonej konfiguracji przetwarzania. Nie zmieniano parsera w celu ich ukrycia.

Test2 ma nowe uruchomienie jednym kliknieciem oraz zebrane dane testowe.
Drugi komputer i ten nowy start wymagaja potwierdzenia. Sprawy prywatne,
logi uzytkownikow, modele i duze archiwa nie sa dodawane do Git.
Publiczny PR #3045 pozostaje bez zmian.
