-- // disallowed words
-- Migration SQL that makes the change goes here.

  -- создание таблицы
  CREATE TABLE disallowed_words
  (
    id bigint NOT NULL,
    type integer NOT NULL,
    word character varying(200) NOT NULL,
    CONSTRAINT disallowed_words_pkey PRIMARY KEY (id)
  )
  WITH (
    OIDS=FALSE
  );
  ALTER TABLE disallowed_words
    OWNER TO kabinet;

  -- создание сиквенса
  CREATE SEQUENCE seq_disallowed_words
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  ALTER TABLE seq_disallowed_words
    OWNER TO kabinet;

  -- Добавление запрещённого слова settings для имени ссылки пользователя
  insert into disallowed_words (id, type, word)
  select nextval('seq_disallowed_words'), 0, 'settings'
  where not exists(select 1 from disallowed_words where type = 0 and word = 'settings');

-- //@UNDO
-- SQL to undo the change goes here.


