CREATE TABLE IF NOT EXISTS categories (
                                          id UUID PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS subcategories (
                                             id UUID PRIMARY KEY,
                                             name VARCHAR(255) NOT NULL,
    category_id UUID REFERENCES categories(id)
    );

CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL
    );

CREATE TABLE IF NOT EXISTS items (
                                     id UUID PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category_id UUID REFERENCES categories(id),
    subcategory_id UUID REFERENCES subcategories(id)
    );

CREATE TABLE IF NOT EXISTS rentals (
                                       id UUID PRIMARY KEY,
                                       user_id UUID REFERENCES users(id),
    item_id UUID REFERENCES items(id),
    rental_date DATE NOT NULL,
    return_date DATE
    );