-- // certification doc types
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE user_certification_doc_types_id
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

CREATE TABLE user_certification_doc_types
(
  id        BIGINT NOT NULL,
  name      TEXT   NOT NULL,
  title     TEXT   NOT NULL,
  min_files INT    NOT NULL,
  CONSTRAINT user_certification_doc_types_pkey PRIMARY KEY (id)
) WITH (
OIDS = FALSE
);

insert into user_certification_doc_types (select nextval('user_certification_doc_types_id'), 'user', 'Фото пользователя', 1);
insert into user_certification_doc_types (select nextval('user_certification_doc_types_id'), 'userdoc', 'Фото пользователя вместе с документами', 1);
insert into user_certification_doc_types (select nextval('user_certification_doc_types_id'), 'doc', 'Документы', 1);

-- //@UNDO
-- SQL to undo the change goes here.


