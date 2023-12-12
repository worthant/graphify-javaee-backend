-- Drop existing tables if they exist
DROP TABLE IF EXISTS point_model;
DROP TABLE IF EXISTS user_settings;
DROP TABLE IF EXISTS users;
DROP TYPE IF EXISTS role_enum;

-- Recreate the tables
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password TEXT                NOT NULL,
    role     VARCHAR(255)        NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE user_settings
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER      NOT NULL REFERENCES users (id),
    theme   VARCHAR(255) NOT NULL DEFAULT 'light'
);

CREATE TABLE point_model
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER          NOT NULL REFERENCES users (id),
    x       DOUBLE PRECISION NOT NULL,
    y       DOUBLE PRECISION NOT NULL,
    r       DOUBLE PRECISION NOT NULL,
    result  BOOLEAN          NOT NULL
);
