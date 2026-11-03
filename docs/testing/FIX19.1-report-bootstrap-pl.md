# Poprawka startu raportu z Windows portable

Objaw z GUI: ReportDialog uruchamiał Bootstrap z lib/iped-search-app.jar i iped.ui.report. Main.setConfigPath uznawał katalog tego JAR-a (lib) za katalog konfiguracji, szukając lib/LocalConfig.txt i lib/conf/Log4j2ConfigurationConsoleOnly.xml. Raport nie powstawał.

Poprawka zachowuje dotychczasową obsługę katalogu sprawy iped/lib. Dodatkowo rozpoznaje dystrybucję portable: gdy JAR pochodzi z katalogu lib, a jego katalog nadrzędny zawiera iped.jar i LocalConfig.txt, konfiguracja jest pobierana z tego katalogu nadrzędnego. Bez tych dwóch plików zachowanie pozostaje dotychczasowe. Nie kopiuje się konfiguracji do lib.

Walidacja lokalna:
- Main.java skompilowany przez JDK 11 --release 11 przeciw bibliotekom badanej paczki.
- Rzeczywisty Main.setConfigPath przetestowany w osobnych procesach Java dla trzech układów: portable lib JAR, główny iped.jar i dotychczasowy układ sprawy iped/lib. Wszystkie PASS.
- Próba utworzenia raportu na odizolowanej kopii aplikacji, z zapisanym wyborem fikcyjnej zakładki użytkownika: kod 0, 1 wyeksportowany plik, 2 indeksowane elementy, 0 wyjątków parsowania, 0 błędów odczytu, 0 timeoutów. Powstały katalogi raportu HTML i indeksowanej sprawy. Nie oceniono jeszcze wizualnie całego raportu.
- W środowisku lokalnego testu zgłoszono brak narzędzi RecycleBin/Regripper; ten test ich działania nie potwierdza. Wczesna próba bez izolowanego user.home została zablokowana przy zapisie ipedRoot.txt; właściwy test użył osobnego katalogu domowego i tymczasowego wewnątrz workspace.
- Lokalna nakładka do badanej paczki zmienia tylko iped/app/processing/Main.class w iped.jar i lib/iped-search-app.jar; wszystkie inne wpisy obu JAR-ów porównano według SHA-256 i są identyczne. Nie jest to pełne nowe wydanie Maven.
- Instalator PS5.1: kontrola wersji SHA-256, kopie oryginałów, atomowa podmiana, próba na kopii i ponowne uruchomienie instalatora PASS.

Status: poprawka źródłowa i lokalna nakładka przygotowane; test przez GUI użytkownika oraz nowe CI pozostają do wykonania. Nie zmieniono źródłowej sprawy, parsera PDF ani publicznego PR #3045. Nie należy uznawać nieudanego katalogu wyjściowego za kompletny raport; kolejna próba powinna używać nowego folderu.
