-- // crete table cashbox_exchange_totals
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE cashbox_exchange_totals_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE cashbox_exchange_totals
(
  id                     BIGINT NOT NULL,
  exchange_id            BIGINT NOT NULL,
  total_wholesale_amount NUMERIC(19, 2) DEFAULT 0.00,
  membership_fee         NUMERIC(19, 2) DEFAULT 0.00,
  total_final_amount     NUMERIC(19, 2) DEFAULT 0.00,
  payment_amount         NUMERIC(19, 2) DEFAULT 0.00,
  change_amount          NUMERIC(19, 2) DEFAULT 0.00,
  CONSTRAINT cashbox_exchange_totals_pkey PRIMARY KEY (id)
)
WITH (
OIDS =FALSE
);


-- //@UNDO
-- SQL to undo the change goes here.


