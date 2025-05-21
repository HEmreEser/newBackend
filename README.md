🏀 HM Kreisel Backend – Sportgeräte-Ausleihsystem
Dies ist das Spring Boot Backend für die App "HM Sportsgear / Kreisel", ein Sportgeräte-Ausleihsystem für die Hochschule München. Es stellt eine RESTful API zur Verfügung, mit der Benutzer Sportgeräte suchen, filtern und ausleihen können.

🔧 Tech Stack
Java 17+

Spring Boot

Gradle

H2 In-Memory-Datenbank

Spring Security (mit Rollen: ADMIN, USER)

REST API

OpenAPI (Swagger)

JUnit & MockMvc für Tests

📁 Projektstruktur
bash
Kopieren
Bearbeiten
src/
├── controller/        # REST-Controller
├── service/           # Business-Logik
├── model/             # Entitäten & Enums
├── repository/        # Spring Data JPA Repositories
├── dto/               # Data Transfer Objects (DTOs)
├── config/            # Security- & Web-Konfiguration
🚀 Endpunkte (Auszug)
🔐 Rollen
ADMIN = Verleiher: kann alle Items & Rentals verwalten

USER = Studierende: kann Items sehen & selbst ausleihen

📦 Items
Methode	Pfad	Beschreibung	Rolle
GET	/api/items	Alle Items (optional gefiltert)	Alle
GET	/api/items/{id}	Einzelnes Item	Alle
POST	/api/items	Neues Item anlegen	Admin
PUT	/api/items/{id}	Item bearbeiten	Admin
DELETE	/api/items/{id}	Item löschen	Admin

Optional Query-Parameter für /api/items:

search (Name, Beschreibung, Marke)

gender

categoryId

subcategoryId

size

status

location

📄 Rentals
Methode	Pfad	Beschreibung	Rolle
GET	/api/rentals	Eigene Ausleihen (User) oder alle (Admin)	Auth
GET	/api/rentals/active	Noch nicht zurückgegebene Ausleihen	Auth
POST	/api/rentals	Neues Gerät ausleihen	Auth
POST	/api/rentals/{id}/return	Rückgabe eines Geräts	Auth

🧠 Besondere Logik
Automatischer Status-Wechsel: Items werden beim Ausleihen als VERLIEHEN markiert, nach Ablauf oder Rückgabe auf VERFÜGBAR.

Filter- und Suchfunktion: kombinierte Filterung über viele Parameter möglich.

Sicherheitsregeln: Admins sehen alle Rentals, User nur ihre eigenen.

🛠️ Build & Start
bash
Kopieren
Bearbeiten
./gradlew bootRun
Swagger UI: http://localhost:8080/swagger-ui/index.html

H2-Konsole (Testdaten): http://localhost:8080/h2-console

🧪 Tests
bash
Kopieren
Bearbeiten
./gradlew test
100% Testabdeckung für Service-Schicht (z. B. ItemServiceTests)

MockMvc-Tests für die REST-Endpunkte

🔐 Beispielnutzer
Rolle	Benutzername	Passwort
Admin	admin	admin
User	user1	user

🧾 DTO-Beispiel: CreateItemDto
json
Kopieren
Bearbeiten
{
  "name": "Basketball",
  "description": "Offizieller Ball Größe 7",
  "brand": "Spalding",
  "availableFrom": "2025-06-01",
  "imageUrl": "https://example.com/basketball.jpg",
  "size": "L",
  "gender": "UNISEX",
  "condition": "GUT",
  "status": "VERFÜGBAR",
  "location": "Lothstraße",
  "categoryId": "uuid-1",
  "subcategoryId": "uuid-2"
}
👨‍💻 Entwickler
📚 Hochschule München, Wirtschaftsinformatik Digitales Management

🎯 Ziel: Praktische Lösung für reale Ausleihe an der HM
