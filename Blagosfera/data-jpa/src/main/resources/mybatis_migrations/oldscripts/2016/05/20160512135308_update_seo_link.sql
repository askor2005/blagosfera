-- // update_seo_link
-- Migration SQL that makes the change goes here.

do $$
begin

	delete from field_values where field_id = (select id from fields where internal_name = 'COMMUNITY_SHORT_LINK_NAME') and (string_value = '' or string_value is null);

	update field_values fv1 set string_value =
	(select a[array_length(a, 1)] as seolink
	from (
	select regexp_split_to_array(fv.string_value, '/') from field_values fv where fv.field_id = (select id from fields where internal_name = 'COMMUNITY_SHORT_LINK_NAME') and fv1.id = fv.id
	) as dt(a))
	where fv1.field_id = (select id from fields where internal_name = 'COMMUNITY_SHORT_LINK_NAME');

	update field_values set string_value = string_value || overlay(random()::text placing '' from 2 for 1)
	where string_value in (select string_value from (
	select count(*) as cnt, string_value from field_values where field_id = (select id from fields where internal_name = 'COMMUNITY_SHORT_LINK_NAME')
	group by string_value) as qwe where cnt > 1) and field_id = (select id from fields where internal_name = 'COMMUNITY_SHORT_LINK_NAME');

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


