
CREATE SCHEMA IF NOT EXISTS bank_app;


CREATE TABLE IF NOT EXISTS bank_app.users (
                                              id BIGSERIAL PRIMARY KEY,
                                              login VARCHAR(255) NOT NULL UNIQUE
    );


CREATE TABLE IF NOT EXISTS bank_app.accounts (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 user_id BIGINT NOT NULL,
                                                 money_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES bank_app.users(id) ON DELETE CASCADE
    );