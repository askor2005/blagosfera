-- // create_table_news_categories
-- Migration SQL that makes the change goes here.

CREATE TABLE news_categories
(
  id bigint NOT NULL,
  description character varying(1000),
  key character varying(200) NOT NULL,
  "position" integer DEFAULT 0,
  title character varying(200) NOT NULL,
  parent_id bigint,
  CONSTRAINT news_categories_pkey PRIMARY KEY (id),
  CONSTRAINT fk_nyb1g0tr0vn965hl2a225f7s1 FOREIGN KEY (parent_id)
      REFERENCES news_categories (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_rwv2v1lbc8mqh2f4p5e7fcexw UNIQUE (key)
)
WITH (
  OIDS=FALSE
);

CREATE SEQUENCE SEQ_news_categories START 1;

-- //@UNDO
-- SQL to undo the change goes here.


