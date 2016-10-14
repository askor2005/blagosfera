-- // change_img_repository_to_https_and_clean_trash
-- Migration SQL that makes the change goes here.

UPDATE system_settings SET val = 'https://blagosfera.su' where key = 'application.url';

INSERT INTO system_settings(id, key, val, description)
    VALUES (nextval('seq_system_settings'), 'img.repository', 'https://images.blagosfera.su/', 'URL хранилища картинок');

ALTER TABLE sharers DROP COLUMN avatar_url_bak;

UPDATE applications SET logo_url = replace(logo_url, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');
UPDATE communities SET avatar_url = replace(avatar_url, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');
UPDATE community_inventory_units SET photo = replace(photo, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');
UPDATE sharers SET avatar_src = replace(avatar_src, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');

-- //@UNDO
-- SQL to undo the change goes here.


