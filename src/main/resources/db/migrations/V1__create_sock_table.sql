CREATE TABLE sock
(
    id                BIGSERIAL PRIMARY KEY,
    color             VARCHAR(50)   NOT NULL,
    cotton_percentage FLOAT NOT NULL CHECK (cotton_percentage >= 0 AND cotton_percentage <= 100000),
    quantity          INT           NOT NULL CHECK (quantity >= 0)
);
