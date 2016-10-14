-- // add test_sms section
-- Migration SQL that makes the change goes here.

insert into sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type, access_type, can_set_forward_url, forward_url)
    values (nextval('seq_sections'), '/admin/smstest', '', 12, 'Тест отправки СМС', 677, '', '', true, null, '', null, null, 2, null, null, null);

-- //@UNDO
-- SQL to undo the change goes here.


