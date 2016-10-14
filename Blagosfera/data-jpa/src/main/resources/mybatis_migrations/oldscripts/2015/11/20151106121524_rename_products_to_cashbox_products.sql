-- // rename products to cashbox_products
-- Migration SQL that makes the change goes here.

ALTER SEQUENCE products_id RENAME TO cashbox_products_id;

-- //@UNDO
-- SQL to undo the change goes here.


