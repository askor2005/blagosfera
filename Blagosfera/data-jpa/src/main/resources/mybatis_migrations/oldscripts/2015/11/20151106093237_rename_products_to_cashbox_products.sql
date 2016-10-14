-- // rename products to cashbox_products
-- Migration SQL that makes the change goes here.

ALTER TABLE products RENAME TO cashbox_products;

-- //@UNDO
-- SQL to undo the change goes here.


