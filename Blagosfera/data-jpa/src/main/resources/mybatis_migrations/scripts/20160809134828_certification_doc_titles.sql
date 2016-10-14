-- // certification doc titles
-- Migration SQL that makes the change goes here.

update user_certification_doc_types set title = 'Фотография лица пользователя в фас 3х4 (без документов)' where title = 'Фото пользователя';
update user_certification_doc_types set title = 'Фотография пользователя фас вместе с паспортом, раскрытым на странице с персональными данными' where title = 'Фото пользователя вместе с документами';
update user_certification_doc_types set title = 'Фотографии или сканы документов (страница паспорта с персональными данными, страница паспорта с адресом регистрации и другие документы)' where title = 'Документы';

-- //@UNDO
-- SQL to undo the change goes here.


