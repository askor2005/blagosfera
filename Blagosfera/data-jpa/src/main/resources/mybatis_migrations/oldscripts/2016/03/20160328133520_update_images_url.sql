-- // update images url
-- Migration SQL that makes the change goes here.

UPDATE field_values SET file_url = replace(file_url, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');

-- //@UNDO
-- SQL to undo the change goes here.


