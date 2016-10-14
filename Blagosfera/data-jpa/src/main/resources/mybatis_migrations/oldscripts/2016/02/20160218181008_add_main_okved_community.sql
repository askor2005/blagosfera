-- // add_main_okved_community
-- Migration SQL that makes the change goes here.

do $$
declare
  fieldGroupId bigint;
begin

  ALTER TABLE communities ADD COLUMN main_okved_id bigint;

  ALTER TABLE communities
  ADD CONSTRAINT fk_main_okved_constraint FOREIGN KEY (main_okved_id)
      REFERENCES okveds (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

	select id into fieldGroupId from fields_groups where internal_name = 'COMMUNITY_WITH_ORGANIZATION_OKVEDS';

	insert into fields (id, hideable, internal_name, name, position, fields_group_id, type, comment, example, hidden_by_default, is_unique, points, required, verified_editable, use_case, attached_file)
	select nextval('seq_fields'), true, 'COMMUNITY_MAIN_OKVED_CODE', 'Код основного вида дейстельности', 2, fieldGroupId, 19, null, null, true, false, 0, false, false, null, null
	where not exists (select id from fields where internal_name = 'COMMUNITY_MAIN_OKVED_CODE');
end $$;

-- //@UNDO
-- SQL to undo the change goes here.


