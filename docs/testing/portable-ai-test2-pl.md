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
Stan przed dalszymi testami: drugi komputer i nowy start wymagaly potwierdzenia. Sprawy prywatne,
logi uzytkownikow, modele i duze archiwa nie sa dodawane do Git.
Publiczny PR #3045 pozostaje bez zmian.

## Dalsze potwierdzenia uzytkownika - 2026-11-03

Uzytkownik potwierdzil dzialanie TEST2 na drugim komputerze. Nie otrzymano
niezaleznego dziennika srodowiska ani kompletnej listy wykonanych tam kontroli.
Nie utozsamiamy tego z automatycznym audytem wszystkich funkcji.

Zrzuty GUI z trudniejszych syntetycznych filmow (prog testu 50):
- ruch kadru, zoom/obrot i fragment rozmycia: 74%;
- cykliczne przyciemnienie/rozjasnienie: 73%;
- czasowe zasloniecie dolnej czesci twarzy: 73%;
- kontrola bez twarzy nie pojawila sie w wynikach.
Ramki widoczne na czesci klatek przyciemnionych i zaslonietych.
Sa to animacje statycznych obrazow, a nie naturalny ruch osob.
Wynik filmu nie oznacza dopasowania kazdej klatki ani samego trudnego fragmentu.

Krotkie pojawienie sie twarzy: film 30 s, fikcyjne twarze tylko w przedziale
[14,16) s. Film znaleziony z wynikiem 69%; kontrola bez twarzy pominieta.
Siatka pokazuje jedna pobrana klatke z twarzami. Potwierdza to trafienie
w probkowanie tego konkretnego fragmentu, nie gwarancje znalezienia kazdego
krotkiego wystapienia przy innych czasach i ustawieniach.

Zewnetrzny wzorzec z filmu dostarczonego przez uzytkownika: PNG 374x375 px,
bez powiekszania/wyostrzania. Detektor HOG upSampling=1 wykryl jedna twarz
oraz wyliczyl wektor. GUI uzytkownika pokazalo wzorzec 100%, film 57%
i ramki na czesci klatek. Wzorzec wycieto z tego samego filmu, wiec nie jest
to niezalezny test tozsamosci na innych nagraniach. Nie potwierdzano
prawdziwosci ani pochodzenia sceny przedstawionej w filmie.

Kontrola negatywna / ograniczenie rozdzielczosci: przy innym filmie 600x316 px
wycinki mialy ok. 41x41 px (sama twarz ok. 23x23). IPED nie wykryl twarzy
we wzorcu zewnetrznym; opcja biezacego elementu byla nieaktywna. Minimum
przetwarzania obrazu wynosi 48x48. Zmniejszenie progu podobienstwa nie naprawia
braku detekcji; skalowanie nie odtwarza brakujacych szczegolow.

Do repozytorium nie dolaczono zadnego filmu, wycinka, sciezek lokalnych,
metadanych zrodlowych ani zrzutow zawierajacych material uzytkownika.
Opis wynikow jest anonimowy. Publiczny PR #3045 i ZIP TEST2 bez zmian.
Procenty pozostaja miara programu, nie prawdopodobienstwem tozsamosci.
