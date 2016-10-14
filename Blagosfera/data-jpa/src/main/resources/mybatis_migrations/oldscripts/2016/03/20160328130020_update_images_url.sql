-- // update images url
-- Migration SQL that makes the change goes here.

UPDATE sharers SET avatar_photo_src = replace(avatar_photo_src, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');

-- //@UNDO
-- SQL to undo the change goes here.


