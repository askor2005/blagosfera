-- // add_content_params_of_news_to_system_settings
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.items.default-count', '10', 'Число новостей, подгружающихся при загрузке страницы');

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.items.count-of-not-scrolled-items-before-downloading', '3', 'Число непромотанных новостей, при котором происходит загрузка новой порции котента');

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.items.count-of-lazy-downloadable', '5', 'Число новостей для динамической подгрузки во время прокрутки страницы.');

-- //@UNDO
-- SQL to undo the change goes here.


