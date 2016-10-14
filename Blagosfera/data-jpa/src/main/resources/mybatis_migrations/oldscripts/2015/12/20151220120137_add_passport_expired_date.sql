-- // add_passport_expired_date
-- Migration SQL that makes the change goes here.

insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
select nextval('seq_fields'), true, 'EXPIRED_PASSPORT_DATE', 'Срок действия', 8, 18, 1, null, null, true, true, 0, false, false, false, false
where not exists (select 1 from fields where internal_name = 'EXPIRED_PASSPORT_DATE')

-- //@UNDO
-- SQL to undo the change goes here.


