-- // add_news_max_count_of_attachments_to_system_settings
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.max-count-of-attachments', '10', 'Максимальное число вложений картинок и видео в новость');

-- //@UNDO
-- SQL to undo the change goes here.


