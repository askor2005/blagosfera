-- // add_news_attachments_table
-- Migration SQL that makes the change goes here.

CREATE TABLE news_attachments
(
  id bigint NOT NULL,
  src character varying(255) NOT NULL,
  type character varying(255) NOT NULL,
  news_id bigint NOT NULL,
  height integer,
  width integer,
  CONSTRAINT news_attachments_pkey PRIMARY KEY (id),
  CONSTRAINT fk_a6ia298dd26n6gjjfr00vr1qg FOREIGN KEY (news_id)
      REFERENCES news (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE SEQUENCE SEQ_news_attachments START 1;

-- //@UNDO
-- SQL to undo the change goes here.


