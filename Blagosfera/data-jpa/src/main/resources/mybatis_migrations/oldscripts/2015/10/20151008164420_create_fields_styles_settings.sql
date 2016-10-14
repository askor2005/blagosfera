-- // create_fields_styles_settings
-- Migration SQL that makes the change goes here.

insert into system_settings(id, key, val, description)
select nextval('seq_system_settings'), 'fieldsStyles',
'.mceNonEditable {
    background-color: #FFFFFF;
    color: #F51A0F;
}
.groupFieldStart, .groupFieldEnd {
    background-color: #FFFFFF !important;
    color: #0088cc !important;
}

table.documentTable {
    width: 100%;
    border: 1px solid #000;
    border-collapse: collapse;
}
table.documentTable th {
    text-align: left;
    background: #ccc;
    padding: 5px;
    border: 1px solid black;
}
table.documentTable td {
    padding: 5px;
    border: 1px solid black;
}', 'Стили для шаблонов документов'
where not exists (select id from system_settings where key = 'fieldsStyles');

-- //@UNDO
-- SQL to undo the change goes here.


