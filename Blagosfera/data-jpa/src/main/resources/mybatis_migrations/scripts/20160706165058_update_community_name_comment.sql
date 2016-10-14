-- // update_community_name_comment
-- Migration SQL that makes the change goes here.

update fields set comment = 'Введите название объединения людей или название юридического лица, если объединение действует в рамках этого юридического лица. Название объединения может быть не уникальным. Название юридического лица пишется с организационно-правовой формой (Например: Общество с ограниченной ответственностью "Ромашка").' where internal_name = 'COMMUNITY_NAME';
update fields set comment = 'Укажите короткое название на русском языке. Короткое название юридического лица пишется с сокрашённой организационно-правовой формой (Например: ООО "Ромашка").' where internal_name = 'COMMUNITY_SHORT_NAME';

-- //@UNDO
-- SQL to undo the change goes here.


