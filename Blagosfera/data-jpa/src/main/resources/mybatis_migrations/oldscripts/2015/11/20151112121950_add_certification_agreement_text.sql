-- // add certification agreement text
-- Migration SQL that makes the change goes here.

insert into ramera_texts (id, code, description, text)
select nextval('seq_ramera_texts'),
'CERTIFICATION_AGREEMENT',
'Текст соглашения при идентификации физического лица',
'<p>Текст соглашения при идентификации физического лица</p>'
where not exists(select id from ramera_texts where code = 'CERTIFICATION_AGREEMENT');

-- //@UNDO
-- SQL to undo the change goes here.


