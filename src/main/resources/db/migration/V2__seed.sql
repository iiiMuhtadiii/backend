-- V2: seed categories and products

INSERT INTO categories (name) VALUES ('Electronics') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO categories (name) VALUES ('Books') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO categories (name) VALUES ('Clothing') ON DUPLICATE KEY UPDATE name=name;

INSERT INTO products (name, description, price, stock, image_url, category_id)
VALUES
('Wireless Mouse', 'A reliable wireless mouse', 19.99, 100, '', (SELECT id FROM categories WHERE name='Electronics'))
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO products (name, description, price, stock, image_url, category_id)
VALUES
('Clean Code', 'A handbook of agile software craftsmanship', 29.99, 50, '', (SELECT id FROM categories WHERE name='Books'))
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO products (name, description, price, stock, image_url, category_id)
VALUES
('Classic T-Shirt', 'Comfortable cotton t-shirt', 9.99, 200, '', (SELECT id FROM categories WHERE name='Clothing'))
ON DUPLICATE KEY UPDATE name=name;
