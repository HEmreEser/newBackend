-- schema.sql
CREATE TABLE app_user (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          full_name VARCHAR(255),
                          email VARCHAR(255) UNIQUE,
                          password VARCHAR(255),
                          role VARCHAR(50) DEFAULT 'USER'
);

CREATE TABLE app_item (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255),
                          size VARCHAR(50),
                          available BOOLEAN DEFAULT TRUE,
                          description VARCHAR(1000),
                          brand VARCHAR(255),
                          location VARCHAR(100),
                          gender VARCHAR(50),
                          category VARCHAR(50),
                          subcategory VARCHAR(50),
                          zustand VARCHAR(50)
);

CREATE TABLE app_rental (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            rental_date DATE,
                            end_date DATE,
                            return_date DATE,
                            extended BOOLEAN DEFAULT FALSE,
                            user_id BIGINT,
                            item_id BIGINT,
                            CONSTRAINT fk_rental_user FOREIGN KEY (user_id) REFERENCES app_user(id),
                            CONSTRAINT fk_rental_item FOREIGN KEY (item_id) REFERENCES app_item(id)
);

CREATE TABLE app_review (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            item_id BIGINT NOT NULL,
                            rental_id BIGINT NOT NULL UNIQUE,
                            rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
                            comment VARCHAR(1000),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP,
                            CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES app_user(id),
                            CONSTRAINT fk_review_item FOREIGN KEY (item_id) REFERENCES app_item(id),
                            CONSTRAINT fk_review_rental FOREIGN KEY (rental_id) REFERENCES app_rental(id)
);

-- data.sql
-- 👤 Beispiel-User (OHNE id - lass AUTO_INCREMENT arbeiten!)
INSERT INTO app_user (full_name, email, password, role) VALUES
                                                            ('Anna Admin', 'admin@hm.edu', 'admin123', 'ADMIN'),
                                                            ('Ben Benutzer', 'ben@hm.edu', 'benpass', 'USER'),
                                                            ('Clara Student', 'clara@hm.edu', 'clara456', 'USER'),
                                                            ('David Mueller', 'david@hm.edu', 'david789', 'USER');

-- 🎽 Beispiel-Items (OHNE id - lass AUTO_INCREMENT arbeiten!)
INSERT INTO app_item (name, size, available, description, brand, location, gender, category, subcategory, zustand)
VALUES
    ('Winterjacke', 'L', TRUE, 'Warme Winterjacke für Damen', 'North Face', 'LOTHSTRASSE', 'DAMEN', 'KLEIDUNG', 'JACKEN', 'NEU'),
    ('Skihose', 'M', TRUE, 'Wasserdicht und bequem', 'Burton', 'LOTHSTRASSE', 'HERREN', 'KLEIDUNG', 'HOSEN', 'GEBRAUCHT'),
    ('Snowboard', '120cm', FALSE, 'Perfekt für Anfänger', 'Nitro', 'KARLSTRASSE', 'HERREN', 'EQUIPMENT', 'SNOWBOARDS', 'GEBRAUCHT'),
    ('Flasche', '1.5L', TRUE, 'BPA-frei', 'Nalgene', 'PASING', 'UNISEX', 'EQUIPMENT', 'FLASCHEN', 'NEU'),
    ('Handschuhe', 'S', TRUE, 'Winddicht', 'Reusch', 'LOTHSTRASSE', 'DAMEN', 'ACCESSOIRES', 'HANDSCHUHE', 'NEU'),
    ('Wanderschuhe', '42', TRUE, 'Robust und wasserdicht', 'Salomon', 'PASING', 'HERREN', 'SCHUHE', 'WANDERSCHUHE', 'GEBRAUCHT'),
    ('Skibrille', 'One Size', TRUE, 'UV-Schutz und beschlagfrei', 'Oakley', 'KARLSTRASSE', 'UNISEX', 'ACCESSOIRES', 'BRILLEN', 'NEU'),
    ('Rucksack', '40L', TRUE, 'Perfekt für Wanderungen', 'Deuter', 'LOTHSTRASSE', 'UNISEX', 'TASCHEN', 'JACKEN', 'GEBRAUCHT');

-- 📦 Beispiel-Rentals (OHNE id - lass AUTO_INCREMENT arbeiten!)
INSERT INTO app_rental (rental_date, end_date, return_date, extended, user_id, item_id)
VALUES
    -- Aktuelle Ausleihe (noch nicht zurückgegeben)
    (CURRENT_DATE, DATEADD('DAY', 30, CURRENT_DATE), NULL, FALSE,
     (SELECT id FROM app_user WHERE email = 'ben@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Snowboard')),

    -- Abgeschlossene Ausleihen (bereits zurückgegeben)
    (DATEADD('DAY', -45, CURRENT_DATE), DATEADD('DAY', -15, CURRENT_DATE), DATEADD('DAY', -12, CURRENT_DATE), FALSE,
     (SELECT id FROM app_user WHERE email = 'clara@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Winterjacke')),

    (DATEADD('DAY', -60, CURRENT_DATE), DATEADD('DAY', -30, CURRENT_DATE), DATEADD('DAY', -28, CURRENT_DATE), FALSE,
     (SELECT id FROM app_user WHERE email = 'david@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Wanderschuhe')),

    (DATEADD('DAY', -20, CURRENT_DATE), DATEADD('DAY', 10, CURRENT_DATE), DATEADD('DAY', 8, CURRENT_DATE), FALSE,
     (SELECT id FROM app_user WHERE email = 'ben@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Skibrille')),

    -- Überfällige Ausleihe
    (DATEADD('DAY', -35, CURRENT_DATE), DATEADD('DAY', -5, CURRENT_DATE), NULL, FALSE,
     (SELECT id FROM app_user WHERE email = 'clara@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Handschuhe'));

-- ⭐ Beispiel-Reviews (nur für zurückgegebene Items)
INSERT INTO app_review (user_id, item_id, rental_id, rating, comment, created_at)
VALUES
    -- Review für Winterjacke
    ((SELECT id FROM app_user WHERE email = 'clara@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Winterjacke'),
     (SELECT r.id FROM app_rental r
                           JOIN app_user u ON r.user_id = u.id
                           JOIN app_item i ON r.item_id = i.id
      WHERE u.email = 'clara@hm.edu' AND i.name = 'Winterjacke'),
     5, 'Perfekte Jacke für den Winter! Sehr warm und bequem.', DATEADD('DAY', -10, CURRENT_DATE)),

    -- Review für Wanderschuhe
    ((SELECT id FROM app_user WHERE email = 'david@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Wanderschuhe'),
     (SELECT r.id FROM app_rental r
                           JOIN app_user u ON r.user_id = u.id
                           JOIN app_item i ON r.item_id = i.id
      WHERE u.email = 'david@hm.edu' AND i.name = 'Wanderschuhe'),
     4, 'Gute Schuhe, aber etwas abgenutzt. Trotzdem sehr funktional.', DATEADD('DAY', -25, CURRENT_DATE)),

    -- Review für Skibrille
    ((SELECT id FROM app_user WHERE email = 'ben@hm.edu'),
     (SELECT id FROM app_item WHERE name = 'Skibrille'),
     (SELECT r.id FROM app_rental r
                           JOIN app_user u ON r.user_id = u.id
                           JOIN app_item i ON r.item_id = i.id
      WHERE u.email = 'ben@hm.edu' AND i.name = 'Skibrille'),
     5, 'Exzellente Sicht und kein Beschlagen. Top Qualität!', DATEADD('DAY', -2, CURRENT_DATE));
