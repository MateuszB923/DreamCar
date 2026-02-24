# DreamCar

DreamCar to aplikacja webowa imitująca stronę salonu / wypożyczalni ekskluzywnych samochodów. Użytkownik może przeglądać ofertę pojazdów, przechodzić do szczegółów konkretnego samochodu, a po zalogowaniu wykonywać akcje takie jak rezerwacja terminu, pozostawienie opinii o samochodzie oraz wysłanie wiadomości przez formularz kontaktowy. Po stronie administracyjnej dostępny jest panel zarządzania ofertą, użytkownikami oraz aktywnością w systemie (wiadomości, rezerwacje, opinie).

Całość danych (samochody, rezerwacje, wiadomości, opinie, konta użytkowników) jest trwale przechowywana w bazie danych MySQL.

---

## Funkcjonalności

### Widok publiczny (niezalogowany gość)
- Przegląd oferty samochodów (lista samochodów pobierana jest z bazy danych).
- Szczegóły samochodu (opis, parametry, dostępność, opinie użytkowników).
- Podgląd strony kontaktowej (formularz jest widoczny, ale w celu wysłania wiadomości wymagane jest zalogowanie do serwisu).

### Konto użytkownika (po zalogowaniu)
- **Rezerwacje**
  - Użytkownik ma możliwość zarezerwowania samochodu na wybrany, dostępny termin.
  - Użytkownik ma podgląd historii swoich rezerwacji w panelu „Konto”.
  - Użytkownik ma możliwość anulowania swojej rezerwacji.
- **Opinie**
  - Użytkownik może dodać opinię o samochodzie.
- **Kontakt**
  - Użytkownik może wysłać wiadomość (formularz kontaktowy) - wiadomość jest zapisywana w bazie.
- **Bezpieczeństwo konta**
  - Użytkownik może zmienić hasło do konta.
  - Użytkownik może usunąć swoje konto.

### Panel administratora (tylko ADMIN)
- **Zarządzanie ofertą samochodów**
  - Dodawanie nowych samochodów do oferty.
  - Edycja samochodów znajdujących się w ofercie
  - Usuwanie samochodów z oferty.
- **Opinie**
  - Administrator ma podgląd wszystkich opinii wybranego samochodu.
  - Administrator ma możliwość usunięcia opinii o danym samochodzie.
- **Wiadomości (z formularza kontaktowego)**
  - Administrator ma podgląd wszystkich wiadomości wysłanych przez użytkowników.
  - Administrator może oznaczać wiadomości jako przeczytane oraz archiwizować je.
- **Rezerwacje**
  - Administrator widzi wszystkie rezerwacje samochodów w systemie.
  - Administrator może potwierdzić lub anulować rezerwację użytkownika.
- **Użytkownicy**
  - Administrator ma podgląd listy użytkowników.
  - Administrator może zablokować/odblokować/usunąć konto użytkownika.
  - Administrator może zresetować hasło użytkownika (generowane jest hasło tymczasowe).
  - Administrator nie może wykonać krytycznych operacji na własnym koncie (brak możliwości usunięcia własnego konta lub zresetowania własnego hasła).

---

## Bezpieczeństwo

Aplikacja jest zabezpieczona mechanizmem **JWT (JSON Web Token)**:

- Logowanie i rejestracja zwracają token, który jest używany do autoryzacji zapytań.
- Endpointy administracyjne są dostępne wyłącznie dla roli **ADMIN**.
- Zwykły użytkownik (rola **USER**) nie ma dostępu do panelu administracyjnego ani endpointów `/api/admin/**`.

---

## Technologie i architektura

Wymagania projektu zakładały backend w **Node.js/Express**, natomiast w ramach realizacji zastosowałem równoważne rozwiązanie w technologii **Java + Spring Boot**, 
zachowując podejście **REST API**, walidację danych, obsługę błędów oraz komunikację z bazą danych.

---

## Backend

- **Java 17 + Spring Boot** jako aplikacja serwerowa (REST API).
- **Spring Security + JWT**
  - logowanie zwraca token JWT,
  - endpointy administracyjne są dostępne tylko dla roli **ADMIN** (`/api/admin/**`),
  - zwykły użytkownik (**USER**) nie ma dostępu do panelu admina i operacji administracyjnych.
- **JPA/Hibernate** jako warstwa ORM do komunikacji z bazą danych (encje, relacje, repozytoria).
- **Maven** jako system budowania i zarządzania zależnościami:
  - konfiguracja w pliku `pom.xml`,
  - projekt zawiera typowe zależności m.in.:
    - `spring-boot-starter-web` (REST)
    - `spring-boot-starter-security` (autoryzacja + JWT)
    - `spring-boot-starter-data-jpa` (JPA/Hibernate)
    - baza danych (MySQL)
    - `lombok` (uzyty został w celu redukcji boilerplate: gettery/settery/builder)
- **Konfiguracja aplikacji**
  - `application.properties` zawiera konfigurację bazy danych (URL, user, hasło), ustawienia Hibernate oraz parametry JWT,
  - część konfiguracji (np. konto admina seedowane przy starcie) korzysta ze zmiennych środowiskowych (`ADMIN_EMAIL`, `ADMIN_PASSWORD`).

---

## Baza danych

- **MySQL** uruchamiany w kontenerze za pomocą **Docker Compose**:
  - łatwe uruchomienie i spójne środowisko (bez ręcznej instalacji MySQL),
  - mapowanie portów,
  - dane aplikacji są trwale zapisywane w bazie (samochody, rezerwacje, wiadomości, użytkownicy etc.).

---

## Frontend

- Strony wykonane zostały jako statyczne **HTML + CSS**.
- Komunikacja z backendem zachodzi przez **JavaScript**:
  - dane są pobierane i wysyłane przez `fetch()` (wrapper `Api.fetchJson`),
- Zasoby statyczne znajdują się w:
  - `src/main/resources/static/` (HTML/CSS/JS, grafiki)

---

## Architektura projektu (podział na warstwy i paczki)

Projekt posiada klasyczny podział na poszczególne odpowiedzialności:

- `controller` - endpointy REST (obsługa żądań HTTP)
- `service` - logika biznesowa (walidacje, reguły, operacje na danych)
- `repository` - umożliwia dostęp do bazy danych (Spring Data JPA)
- `model` - encje JPA i enumeratory (relacje, mapowanie tabel)
- `dto` - obiekty transferu danych (Request/Response)
- `mappers` - mapowanie encji <-> DTO
- `exception` - własne wyjątki aplikacyjne + obsługa błędów (np. NotFoundException, mapowanie na odpowiednie kody HTTP)
- `config` - konfiguracja (security/JWT oraz seedery danych startowych)

---

## System kontroli wersji

Projekt rozwijany był w systemie kontroli wersji **Git**:

- osobne branche na większe funkcjonalności (np. panel admina, wiadomości, rezerwacje),
- commity opisujące konkretne zmiany (feature/fix).


---


# Uruchomienie projektu

## Wymagania
- Java 17+
- Maven
- Docker + Docker Compose
- Przeglądarka (np. Firefox Google Chrome)

## Konfiguracja (.env) - konto admina

W katalogu głównym repozytorium znajduje się przykładowy plik `.env.example`.
Aplikacja seeduje konto admina przy starcie, jeśli ustawione są zmienne środowiskowe:
- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`

### Kroki

### 1. Skopiuj plik(pierwsze uruchomienie na danym komputerze):

`cp .env.example .env`

### 2. Załaduj zmienne środowiskowe do terminala (Linux/macOS)(Za każdym razem, gdy uruchamiasz projekt):

```bash
set -a
source .env
set +a
```

Logowanie do panelu admina: [/html/admin.html](http://localhost:8080/html/admin.html)
Dane do logowania: wartości ADMIN_EMAIL i ADMIN_PASSWORD z .env.

### 3. Uruchom bazę danych (MySQL w Docker Compose)(pierwsze uruchomienie na danym komputerze)

W katalogu głównym projektu:

`docker compose up -d`

Sprawdź czy kontener działa:

`docker compose ps`

### 4. Uruchom backend (Spring Boot)(Za każdym razem, gdy uruchamiasz projekt)

Uruchom aplikację:

`cd backend`
`mvn spring-boot:run`

Aplikacja działa pod adresem:
http://localhost:8080

---

## Linki do widoków

- Strona główna: `http://localhost:8080/html/index.html`
- Oferta: `http://localhost:8080/html/offer.html`
- Szczegóły auta: `http://localhost:8080/html/car.html?id=1`
- O nas: `http://localhost:8080/html/about_us.html`
- Kontakt: `http://localhost:8080/html/contact.html`
- Konto: `http://localhost:8080/html/account.html`
- Admin: `http://localhost:8080/html/admin.html`

---

## Dane demo (seedery)

Projekt może automatycznie dodać dane demonstracyjne przy starcie (np. auta, opinie).
W `application.properties` dostępne są flagi:
- `app.seed.cars=true`
- `app.seed.reviews=true`

Aby wyczyścić bazę i wczytać demo od nowa(Tylko gdy chcesz zresetować bazę i wczytać demo od nowa):

`docker compose down -v`
`docker compose up -d`

---

## Zatrzymanie projektu

Backend: `Ctrl + C`

Baza danych:

`docker compose down`

---

## Schemat Bazy Danych

<img width="1269" height="706" alt="obraz" src="https://github.com/user-attachments/assets/62801567-03f1-42bb-9b30-e2509a37b534" />

