# newBackend
Datenmodell - Entities & Enums
User
•	id: UUID
•	email: string (HM-Mailadresse)
•	role: string (user oder admin)
•	Methode: canRent()
Rental (Leihe)
•	id: UUID
•	userId: UUID (FK zu User)
•	itemId: UUID (FK zu Item)
•	startDate: Date
•	endDate: Date (max. 4 Monate nach Start)
•	returned: boolean
•	returnedAt: Date (optional)
•	Methode: isActive()
Item (Artikel)
•	id: UUID
•	name: string
•	description: string
•	brand: string
•	availableFrom: Date (Datum, ab wann verfügbar)
•	imageUrl: string (optional)
•	Methoden: isAvailable()
•	Verknüpfungen zu:
o	Size (Enum: XS, S, M, L, XL)
o	Gender (Enum: Damen, Herren)
o	Condition (Enum: Neu, Gebraucht)
o	Status (Enum: Verfügbar, NichtVerfügbar)
o	Category (z.B. Schuhe, Kleidung, etc.)
o	Subcategory (z.B. Wanderschuhe, Jacken, etc.)
o	Location (Enum: Lothstraße, Pasing, Karlstraße)
Category
•	id: UUID
•	name: string
Subcategory
•	id: UUID
•	name: string
Enums
•	Size: XS, S, M, L, XL
•	Gender: Damen, Herren
•	Condition: Neu, Gebraucht
•	Status: Verfügbar, NichtVerfügbar
•	Location: Lothstraße, Pasing, Karlstraße
________________________________________
Funktionale Besonderheiten
•	Nutzer können mehrere Artikel gleichzeitig ausleihen, maximal 5 pro Nutzer.
•	Rückgaben sind jederzeit möglich (flexibel).
•	Es gibt keine Verlängerung der Leihzeit (erstmal).
•	Filter sind hierarchisch aufgebaut (erst Gender, dann Kategorie, dann Unterkategorie, dann Größe, wo anwendbar).
•	Suche: Zusätzlich wird es eine Suchleiste geben, um Artikel schnell zu finden.
•	Es gibt keine Benachrichtigungen (Push, Mail) zum Rückgabedatum.
________________________________________
Authentifizierung & Sicherheit
•	Registrierung / Login nur mit Hochschul-Mailadressen möglich
•	Admins sind anhand der Email-Adresse (adminX@hm.edu) definiert
•	Rollenbasiertes Zugriffssystem (User vs Admin)
________________________________________
ToDos & Erweiterungen (Zukunft)
•	Bilder-Upload bei Artikeln implementieren
•	Push- oder E-Mail-Benachrichtigungen für Rückgaben
•	Erweiterbare Kategorien/Subkategorien (derzeit statisch)
•	Eventuell Verlängerungen der Leihzeit ermöglichen
