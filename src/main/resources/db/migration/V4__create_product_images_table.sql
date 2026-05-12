CREATE TABLE product_images (
                                product_id UUID NOT NULL,
                                image_url  TEXT NOT NULL,

                                CONSTRAINT fk_product_images_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products (id)
                                        ON DELETE CASCADE
);