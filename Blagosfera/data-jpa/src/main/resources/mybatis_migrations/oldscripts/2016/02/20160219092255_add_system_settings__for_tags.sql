-- // add_system_settings__for_tags
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'news.tags.max-count', '10', 'Максимальное число тегов, которое можно использовать в новости');
    VALUES (nextval('seq_system_settings'), 'tags.min-usages-to-autocomplete', '2', 'Минимальное число использований тега, после которого он становится доступным для автодополнения');
    VALUES (nextval('seq_system_settings'), 'tags.max-count-for-autocomplete', '10', 'Максимальное число тегов, предлагающихся для выбора в списке автодополнения');

-- //@UNDO
-- SQL to undo the change goes here.


