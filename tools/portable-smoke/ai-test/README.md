# Portable AI + OCR PL - test2

Ta zmiana dodaje pliki startowe i przepis skladania paczki testowej. Nie zmienia
parserow, kodu rozpoznawania ani domyslnych ustawien glownej dystrybucji Maven.
Wlaczona konfiguracja znajduje sie w przypietej bazowej paczce AI.

Paczka Windows x64 zawiera Python 3.9.12, dlib 19.23.1 oraz biblioteki i modele
opisane w dependency-sources.json. Dotychczasowe poprawki lokalizacji, JEP,
raportow i polskiego OCR zachowano. Aktywne: OCR PL, miniatury wideo,
podobienstwo obrazow, twarze CPU/HOG, haszowanie MD5/SHA-1/SHA-256.
Inne moduly AI pozostaja wylaczone. Nie dodano GPU, transkrypcji ani zdalnego AI.

## Skladanie

Wymagany Python 3.12+, przypiety bazowy ZIP AI i katalog dziesieciu fikcyjnych
plikow INPUT opisanych w build_test2.py. Baza i jej hash sa jawne w skrypcie.
Nie uruchamiaj skryptu w paczce zawierajacej prywatne dane; podaj tylko fixtures.

```powershell
python build_test2.py --base IPED-Polish-4.3.1-PORTABLE-AI-OCR-PL-TEST.zip --output IPED-Polish-4.3.1-PORTABLE-AI-OCR-PL-TEST2.zip --fixtures DANE-TESTOWE\INPUT
```

Skrypt odmawia nadpisania ZIP, kontroluje zestaw danych, sprawdza aktywne flagi,
tworzy manifest i weryfikuje wszystkie pliki oraz zachowane hashe JAR.
Duzych bibliotek/modeli/ZIP nie dodajemy do historii Git. Licencje sa zachowane
wewnatrz archiwum, zrodlowe pakiety PyPI maja przypiete wersje i SHA-256.

## Sprawdzenie przez testera

Rozpakuj calosc do krotkiej sciezki. Uruchom Uruchom-test.cmd. Powstaje nowa,
unikalna sprawa obok programu, po przetworzeniu otwierana w przegladarce.
Instrukcja oczekiwanych wynikow jest w TESTY-KROK-PO-KROKU.txt.
Drugiego komputera oraz nowego przycisku jednym kliknieciem jeszcze nie uznajemy
za przetestowane. Test na podstawowej paczce AI wykonal uzytkownik recznie.
