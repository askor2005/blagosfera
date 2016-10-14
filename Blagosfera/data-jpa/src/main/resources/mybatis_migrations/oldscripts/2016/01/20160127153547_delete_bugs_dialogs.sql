-- // delete_bugs_dialogs
-- Migration SQL that makes the change goes here.

-- Скрипт для удаления лишних диалогов между 2мя участниками
DO $$
BEGIN
	CREATE TEMPORARY TABLE deleted_dialogs (
		id    bigint
	);

	insert into deleted_dialogs

	select distinct r.dialog_id from (select ds.*,
	(select sharer_id from dialogs_sharers where dialog_id = ds.dialog_id and sharer_id != ds.sharer_id) as alter_sharer_id
	from dialogs_sharers ds
	where
	(select count(*) from dialogs_sharers where dialog_id = ds.dialog_id) = 2) as r
	where
	exists (
		select s.dialog_id
		from dialogs_sharers s
		where
		s.dialog_id != r.dialog_id and
		s.sharer_id = r.sharer_id and
		(select count(*) from dialogs_sharers where dialog_id = s.dialog_id) = 2 and
		exists (select dialog_id from dialogs_sharers where dialog_id = s.dialog_id and sharer_id = r.alter_sharer_id)
		) and r.dialog_id not in (
	select dt.min_dialog_id from (select min(r.dialog_id) as min_dialog_id, (r.sharer_id + r.alter_sharer_id) as sharer_id_sum from (select ds.*,
	(select sharer_id from dialogs_sharers where dialog_id = ds.dialog_id and sharer_id != ds.sharer_id) as alter_sharer_id
	from dialogs_sharers ds
	where
	(select count(*) from dialogs_sharers where dialog_id = ds.dialog_id) = 2) as r
	where
	exists (
		select s.dialog_id
		from dialogs_sharers s
		where
		s.dialog_id != r.dialog_id and
		s.sharer_id = r.sharer_id and
		(select count(*) from dialogs_sharers where dialog_id = s.dialog_id) = 2 and
		exists (select dialog_id from dialogs_sharers where dialog_id = s.dialog_id and sharer_id = r.alter_sharer_id)
		)
	group by sharer_id_sum) as dt
	);

	delete from chat_message_receivers
	where message_id in (select id from chat_messages where dialog_id in (select id from deleted_dialogs));

	delete from chat_messages where dialog_id in (select id from deleted_dialogs);

	delete from dialogs_sharers where dialog_id in (select id from deleted_dialogs);

	delete from dialogs where id in (select id from deleted_dialogs);

	drop table deleted_dialogs;

END $$;

-- //@UNDO
-- SQL to undo the change goes here.


