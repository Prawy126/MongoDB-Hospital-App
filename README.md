# Projekt zaliczeniowy z przedmiotu nierelacyjne bazy danych
## Aplikacja zarządzająca szpitalem z użyciem MongoDB(Obiektowo) oraz Java

Ten projekt stanowi zaliczenie z przedmiotu dotyczącego nierelacyjnych baz danych. Został napisany w języku Java i prezentuje implementację rozwiązań typowych dla systemów bazodanowych działających w modelu nierelacyjnym.

## Spis treści

- [Opis projektu](#opis-projektu)
- [Funkcjonalności](#funkcjonalności)
- [Wymagania](#wymagania)
- [Instalacja](#instalacja)
- [Sposób użycia](#sposób-użycia)
- [Struktura projektu](#struktura-projektu)
- [Autor](#autor)
- [Licencja](#licencja)

## Opis projektu

Projekt został stworzony w ramach zaliczenia przedmiotu dotyczącego nierelacyjnych baz danych. Głównym celem projektu jest implementacja rozwiązań, które demonstrują możliwości przechowywania i przetwarzania dużych zbiorów danych w modelu nierelacyjnym. Projekt jest przykładem na wykorzystanie technologii Java w połączeniu z nowoczesnymi podejściami do zarządzania danymi.

## Funkcjonalności

- Implementacja podstawowych operacji na danych (CRUD).
- Przykłady zarządzania obiektami w systemie nierelacyjnym.
- Integracja z wybranymi technologiami bazodanowymi.
- Wsparcie dla przetwarzania danych w czasie rzeczywistym.

## Wymagania

- Java 11 lub nowsza
- Maven lub Gradle jako system budowania projektu
- Inne wymagane biblioteki są określone w pliku konfiguracyjnym projektu (np. `pom.xml` lub `build.gradle`)

## Instalacja

1. Sklonuj repozytorium:
   ```bash
   git clone https://github.com/Prawy126/bazy-nierelacyjne.git
   ```
2. Przejdź do katalogu projektu:
   ```bash
   cd bazy-nierelacyjne
   ```
3. Zbuduj projekt przy użyciu wybranego narzędzia:
   - Za pomocą Mavena:
     ```bash
     mvn clean install
     ```
   - Za pomocą Gradle:
     ```bash
     gradle build
     ```

## Sposób użycia

Po zbudowaniu projektu uruchom aplikację. W zależności od implementacji, aplikacja może wymagać dodatkowej konfiguracji, która znajduje się w dokumentacji technicznej lub komentarzach wewnątrz kodu.

Przykładowe uruchomienie aplikacji:
```bash
java -jar target/nazwa-aplikacji.jar
```

## Struktura projektu

```
Prawy126/bazy-nierelacyjne
├── src
│   ├── main
│   │   ├── java
│   │   │   └── [pakiety z kodem źródłowym Java]
│   │   └── resources
│   └── test
│       └── java
├── pom.xml lub build.gradle
└── README.md
```

## Autor

Projekt został stworzony przez Michał Pilecki oraz Jakub Opar

## Licencja

Projekt jest dostępny na licencji MIT. Szczegóły znajdują się w pliku `LICENSE`.
