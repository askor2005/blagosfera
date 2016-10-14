-- // add_extra_params_of_news_to_system_settings
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.collage.images-per-row', '3', 'Число картинок в одной строчке коллажа новости');

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.max-height', '800', 'Максимальная высота в пикселях, при которой новость не скрывается');
-- //@UNDO
-- SQL to undo the change goes here.


