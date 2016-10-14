-- // create_sharer_po_member_settings
-- Migration SQL that makes the change goes here.

insert into system_settings(id, key, val, description)
select nextval('seq_system_settings'), 'POSharerCommunityMemberSettings',
'{
	// Настройки для шаблона заявления пайщика о вступлении в ПО
	entranceSharerToCommunityDocumentTemplateCode: "statement_to_shareholders_of_the_physical_person", // Шаблон документа заявления пайщика о вступлении в ПО
	entranceSharerToCommunitySharerParticipantName: "Кандидат в пайщики физ. лицо",  // Наименование участника документа - физ лицо
	entranceSharerToCommunityCommunityParticipantName: "Потребительское Общество", // Наименование участника документа - ПО

	// Насткройки протокола собрания о вступлении пайщиков в ПО
	joinSharerToCommunityDocumentTemplateCode: "protocol_join_new_members_to_cooperative", // Код шаблона документа протокола собрания о вступлении пайщиков в ПО
	joinSharerToCommunityLoaDocumentTemplateCode: "protocol_join_new_members_to_cooperative_loa", // Код шаблона документа протокола собрания о вступлении пайщиков в ПО для подписания по доверенности
	documentProtocolJoinSharersListParticipantName: "Участники (физ. лица)", // Участник документа - список кандидатов в пайшики
	documentProtocolDelegateParticipantName: "Представитель ПО", // Участник документа - представитель ПО для шаблона документа по доверенности
	documentProtocolJoinSharersListCooperativeParticipantName: "Потребительское Общество", // Участник документа - ПО
	sharersStatementDocumentListUserFieldName: "Заявления кандидатов в пайщики", // Пользовательское поле - список заявлений от кандидатов в пайшики

	// Настройки заявления на выход из ПО
	requestLeaveSharerFromCommunityDocumentTemplateCode: "statement_to_leave_physical_persons_from_community", // Код шаблона заявления пайщика о выходе из ПО
	leaveStatementDocumentSharerParticipantName: "Пайщик физ. лицо", // Наименование участника документа - физ лицо
	leaveStatementDocumentCommunityParticipantName: "Потребительское Общество", // Наименование участника документа - ПО

	// Настройки протокола совета на выход пайщиков из ПО
	leaveSharersFromCommunityDocumentTemplateCode: "protocol_leave_members_from_cooperative", // Код шаблона протокола собрания совета ПО по выходу пайщиков
	documentProtocolLeaveSharersListParticipantName: "Пайщики физ. лица", // Наименование участников документа протокола выхода пайщиков из ПО - физ лица
	documentProtocolLeaveSharersListCooperativeParticipantName: "Потребительское общество", // Наименование участника документа протокола выхода пайщиков из ПО - ПО
	sharersStatementToLeaveDocumentListUserFieldName: "Заявления пайщиков на выход из ПО" // Пользовательское поле - список заявлений выхода пайщиков из ПО
}', 'Настройки шаблонов документов для вступления и выхода пайщиков в ПО'
where not exists (select id from system_settings where key = 'POSharerCommunityMemberSettings');

-- //@UNDO
-- SQL to undo the change goes here.


