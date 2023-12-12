-- Create tables if they do not exist
CREATE TABLE IF NOT EXISTS users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password TEXT                NOT NULL,
    role     VARCHAR(255)        NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS user_settings
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER      NOT NULL REFERENCES users (id),
    theme   VARCHAR(255) NOT NULL DEFAULT 'light'
);

CREATE TABLE IF NOT EXISTS point_model
(
    id      SERIAL PRIMARY KEY,
    user_id INTEGER          NOT NULL REFERENCES users (id),
    x       DOUBLE PRECISION NOT NULL,
    y       DOUBLE PRECISION NOT NULL,
    r       DOUBLE PRECISION NOT NULL,
    result  BOOLEAN          NOT NULL
);
