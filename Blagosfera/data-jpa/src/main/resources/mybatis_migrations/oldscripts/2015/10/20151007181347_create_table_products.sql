-- // crete table products
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE products_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE products
(
  id                 BIGINT                 NOT NULL,
  name               CHARACTER VARYING(255) NOT NULL,
  code               CHARACTER VARYING(255) NOT NULL,
  count              NUMERIC(19, 2) DEFAULT 0.00,
  unit_of_measure    CHARACTER VARYING(255) NOT NULL,
  wholesale_price    NUMERIC(19, 2) DEFAULT 0.00,
  wholesale_currency CHARACTER VARYING(3)   NOT NULL,
  final_price        NUMERIC(19, 2) DEFAULT 0.00,
  final_currency     CHARACTER VARYING(3)   NOT NULL,
  shop               BIGINT                 NOT NULL,
  CONSTRAINT products_pkey PRIMARY KEY (id)
)
WITH (
OIDS = FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


