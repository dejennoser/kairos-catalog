CREATE TABLE product_translations (
                                      id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      product_id  UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                      locale      VARCHAR(5) NOT NULL,
                                      name        VARCHAR(255) NOT NULL,
                                      description TEXT,
                                      UNIQUE(product_id, locale)
);