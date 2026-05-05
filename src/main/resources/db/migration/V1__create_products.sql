CREATE TABLE products (
                          id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name        VARCHAR(255)   NOT NULL,
                          description TEXT,
                          price       NUMERIC(10, 2) NOT NULL,
                          category    VARCHAR(100)   NOT NULL,
                          stock       INTEGER        NOT NULL DEFAULT 0,
                          created_at  TIMESTAMP      NOT NULL DEFAULT now(),
                          updated_at  TIMESTAMP      NOT NULL DEFAULT now()
);