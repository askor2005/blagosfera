-- // change_left_menu
-- Migration SQL that makes the change goes here.
BEGIN;

update sections set position = (select max(position)+1 from sections where parent_id = (select id from sections where title = 'Деловой Портал')) where title = 'Документы';
update sections set position = (select max(position)+1 from sections where parent_id = (select id from sections where title = 'Деловой Портал')) where title = 'Доверенности';
update sections set position = (select max(position)+1 from sections where parent_id = (select id from sections where title = 'Деловой Портал')) where title = 'Волеизъявления';

update sections set parent_id = (select id from sections where title = 'Деловой Портал') where title = 'Документы';
update sections set parent_id = (select id from sections where title = 'Деловой Портал') where title = 'Доверенности';
update sections set parent_id = (select id from sections where title = 'Деловой Портал') where title = 'Волеизъявления';




insert into sections select nextval('seq_sections'),null,null,(select max(position)+1 from sections where parent_id = (select id from sections where title = 'Деловой Портал')),'Документы',(select id from sections where title = 'Деловой Портал'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='Документы');	




insert into sections select nextval('seq_sections'),'/document/service/documentListPage',null,0,'Список',(select id from sections where title = 'Документы'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='Список' and parent_id = (select id from sections where title='Документы'));	

insert into sections select nextval('seq_sections'),null,null,1,'Создать',(select id from sections where title = 'Документы'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='Создать' and parent_id = (select id from sections where title='Документы'));	

insert into sections select nextval('seq_sections'),'/document/service/signDocumentsListPage',null,2,'На подпись',(select id from sections where title = 'Документы'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='На подпись' and parent_id = (select id from sections where title='Документы'));	





insert into sections select nextval('seq_sections'),null,'uservotings',(select max(position)+1 from sections where parent_id = (select id from sections where title = 'Деловой Портал')),'Волеизъявления',(select id from sections where title = 'Деловой Портал'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where name='uservotings');		

insert into sections select nextval('seq_sections'),'/uservotings/batchvotings','userbatchvotings',0,'Собрания с моим участием',(select id from sections where title = 'Волеизъявления'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='Собрания с моим участием' and parent_id = (select id from sections where title='Волеизъявления'));	


	





insert into sections select nextval('seq_sections'),null,'letterOfAuthority',(select max(position)+1 from sections where parent_id = (select id from sections where title = 'Деловой Портал')),'Доверенности',(select id from sections where title = 'Деловой Портал'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where name='letterOfAuthority');	

insert into sections select nextval('seq_sections'),'/letterofauthority/myLetterOfAuthority.html','myLetterOfAuthority',0,'Доверенности выданные мне',(select id from sections where title = 'Доверенности'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='Доверенности выданные мне' and parent_id = (select id from sections where title='Доверенности'));	

insert into sections select nextval('seq_sections'),'/letterofauthority/ownerLetterOfAuthority.html','ownerLetterOfAuthority',1,'Доверенности выданные мной',(select id from sections where title = 'Доверенности'),
null,null,'t',null,null,null,null,0,null,null,null,'f','f','f','f',null,'f' where not exists (select * from sections where title='Доверенности выданные мной' and parent_id = (select id from sections where title='Доверенности'));	

COMMIT;


-- //@UNDO
-- SQL to undo the change goes here.


