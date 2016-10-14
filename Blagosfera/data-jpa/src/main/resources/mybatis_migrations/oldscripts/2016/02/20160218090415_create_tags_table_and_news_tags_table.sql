-- // create_tags_table_and_news_tags_table
-- Migration SQL that makes the change goes here.

CREATE TABLE tags
(
  id bigint NOT NULL,
  text character varying(32) NOT NULL,
  usage_count bigint NOT NULL DEFAULT 0,
  CONSTRAINT tags_pkey PRIMARY KEY (id),
  CONSTRAINT uk_j7dpk7fcxelkfvydpmxjcx51b UNIQUE (text)
)
WITH (
  OIDS=FALSE
);

CREATE INDEX tags_text_and_usage_count_index
  ON tags
  USING btree
  (text COLLATE pg_catalog."default", usage_count DESC);


  CREATE SEQUENCE seq_tags START 1;

CREATE TABLE news_tags
(
  news_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  CONSTRAINT fk_hy9fi0hxtjxa5ky694g9pkmv8 FOREIGN KEY (tag_id)
      REFERENCES tags (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_oyessyhlac2b62uffu4trufqx FOREIGN KEY (news_id)
      REFERENCES news (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE TABLE news_filters_tags
(
  news_filter_id bigint NOT NULL,
  tag_id bigint NOT NULL,
  CONSTRAINT fk_bnotbwcrdw91c0aclja12o7fw FOREIGN KEY (news_filter_id)
      REFERENCES news_filters (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_snp5b1w3nfa1uuqflriay6r5u FOREIGN KEY (tag_id)
      REFERENCES tags (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.


