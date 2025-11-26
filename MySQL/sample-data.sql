-- Create database (if not already created)
CREATE DATABASE IF NOT EXISTS inventorydb;
USE inventorydb;

-- Tables will be created by Hibernate (ddl-auto=update),
-- so usually you only need INSERTS.
-- Clear existing data for a clean demo:

DELETE FROM inventory_reservations;
DELETE FROM products;

-- Sample products
INSERT INTO products (id, sku, name, available_stock, version)
VALUES
    (1, 'SKU-001', 'Demo Product 1', 100, 0),
    (2, 'SKU-002', 'Demo Product 2', 50, 0),
    (3, 'SKU-003', 'Demo Product 3', 10, 0);

-- If you let Hibernate auto-generate the schema, ensure column
-- names match your actual DB (e.g. availableStock -> available_stock).
