-- // update transactions
-- Migration SQL that makes the change goes here.

DROP TABLE community_payment_types;
DROP SEQUENCE seq_community_payment_types;

CREATE SEQUENCE transaction_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE transaction_detail_id INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE transaction
(
  id bigint NOT NULL,
  amount numeric(19,2) NOT NULL DEFAULT 0.00,
  description text,
  post_date timestamp without time zone,
  state character varying(255) NOT NULL,
  submit_date timestamp without time zone NOT NULL,
  type character varying(255) NOT NULL,
  document_folder_id bigint,
  CONSTRAINT transaction_pkey PRIMARY KEY (id),
  CONSTRAINT fk_transaction_document_folder FOREIGN KEY (document_folder_id)
  REFERENCES document_folder (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE transaction_detail
(
  id bigint NOT NULL,
  amount numeric(19,2) NOT NULL DEFAULT 0.00,
  type character varying(255) NOT NULL,
  account_id bigint NOT NULL,
  transaction_id bigint NOT NULL,
  CONSTRAINT transaction_detail_pkey PRIMARY KEY (id),
  CONSTRAINT fk_transaction_detail_accounts FOREIGN KEY (account_id)
  REFERENCES accounts (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_transaction_detail_transaction FOREIGN KEY (transaction_id)
  REFERENCES transaction (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE transaction_parameters
(
  transaction_id bigint NOT NULL,
  value text,
  key text NOT NULL,
  CONSTRAINT transaction_parameters_pkey PRIMARY KEY (transaction_id, key),
  CONSTRAINT fk_transaction_parameters_transaction FOREIGN KEY (transaction_id)
  REFERENCES transaction (id) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- //@UNDO
-- SQL to undo the change goes here.


