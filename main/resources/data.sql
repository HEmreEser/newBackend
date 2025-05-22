-- Kategorien
INSERT INTO categories (id, name) VALUES ('11111111-1111-1111-1111-111111111111', 'Kleidung');
INSERT INTO categories (id, name) VALUES ('22222222-2222-2222-2222-222222222222', 'Elektronik');

-- Subkategorien
INSERT INTO subcategories (id, name, category_id) VALUES ('33333333-3333-3333-3333-333333333333', 'Jacken', '11111111-1111-1111-1111-111111111111');
INSERT INTO subcategories (id, name, category_id) VALUES ('44444444-4444-4444-4444-222222222222', 'Smartphones', '22222222-2222-2222-2222-222222222222');

-- Benutzer
INSERT INTO users (id, email, password, role) VALUES ('55555555-5555-5555-5555-555555555555', 'admin@example.com', '$2a$10$DOWSD6BP0FBkFnzHZF9Ke.ULWfztzY7zqNaf.LHwE6FR1EVyyR6G2', 'ADMIN');
INSERT INTO users (id, email, password, role) VALUES ('66666666-6666-6666-6666-666666666666', 'user@example.com', 'password123', 'USER');

-- Beispieldaten für Items
INSERT INTO items (id, name, description, category_id, subcategory_id)
VALUES
    ('77777777-7777-7777-7777-777777777777', 'Winterjacke', 'Eine warme Winterjacke', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333'),
    ('88888888-8888-8888-8888-888888888888', 'Smartphone XYZ', 'Ein modernes Smartphone', '22222222-2222-2222-2222-222222222222', '44444444-4444-4444-4444-222222222222');

-- Mietdaten
INSERT INTO rentals (id, user_id, item_id, rental_date, return_date)
VALUES ('99999999-9999-9999-9999-999999999999', '66666666-6666-6666-6666-666666666666', '88888888-8888-8888-8888-888888888888', '2025-05-01', NULL);