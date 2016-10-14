-- // update images url
-- Migration SQL that makes the change goes here.

UPDATE fields SET example = replace(example, 'http://img.ra-dom.ru/', 'https://images.blagosfera.su/');

-- //@UNDO
-- SQL to undo the change goes here.


