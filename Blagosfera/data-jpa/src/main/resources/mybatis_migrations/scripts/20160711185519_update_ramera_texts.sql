-- // update_ramera_texts
-- Migration SQL that makes the change goes here.

update ramera_texts set description='Текст соглашения при идентификации физического лица', text='<p>Текст соглашения при идентификации физического лица</p>' where code = 'CERTIFICATION_AGREEMENT';

-- //@UNDO
-- SQL to undo the change goes here.


