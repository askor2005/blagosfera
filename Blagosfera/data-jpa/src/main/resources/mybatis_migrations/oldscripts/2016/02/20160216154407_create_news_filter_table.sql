-- // create_news_filter_table
-- Migration SQL that makes the change goes here.


CREATE TABLE news_filters
(
  id bigint NOT NULL,
  author_id bigint,
  category_id bigint,
  date_from date,
  date_to date,
  community_id bigint,
  sharer_id bigint NOT NULL,
  CONSTRAINT news_filters_pkey PRIMARY KEY (id),
  CONSTRAINT fk_1v6flxjg9q2qj5hcnc1o021mr FOREIGN KEY (sharer_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_4iorruynnsxlr7i6ltdodr5dt FOREIGN KEY (community_id)
      REFERENCES communities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_hioitft4cpr791dt1jy6ktw62 UNIQUE (sharer_id, community_id)
)
WITH (
  OIDS=FALSE
);

CREATE SEQUENCE seq_news_filters START 1;


-- //@UNDO
-- SQL to undo the change goes here.


