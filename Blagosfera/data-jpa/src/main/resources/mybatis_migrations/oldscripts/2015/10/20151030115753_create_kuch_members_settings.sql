-- // create_kuch_members_settings
-- Migration SQL that makes the change goes here.

-- Настройки участников КУч ПО
insert into system_settings(id, key, val, description)
select nextval('seq_system_settings'), 'KuchSharerCommunityMemberSettings',
'{
	// Настройки для шаблона заявления пайщика о вступлении в КУч ПО
	entranceSharerToCommunityDocumentTemplateCode: "kuch_statement_to_shareholders_of_the_physical_person", // Код шаблона
	entranceSharerToCommunitySharerParticipantName: "Пайщик физ. лицо", // Наименование участника - кандидата в пайщики физ лица
	entranceSharerToCommunityCommunityParticipantName: "Кооперативный Участок", // Наименование участника - КУч
	entranceSharerToCommunityParentCommunityParticipantName: "Потребительское Общество", // Наименование участника - ПО

	// Настройки протокола собрания о вступлении пайщиков в КУч ПО
	joinSharerToCommunityDocumentTemplateCode: "kuch_protocol_join_new_members_to_cooperative", // Код шаблона
	joinSharerToCommunityLoaDocumentTemplateCode: "kuch_protocol_join_new_members_to_cooperative_loa", // Код шаблона по доверенности
	documentProtocolJoinSharersListParticipantName: "Участники (физ. лица)", // Наименование участника - Список физ лиц для вступления
	documentProtocolDelegateParticipantName: "Представитель КУч ПО", // Наименование участника - представитель от КУч ПО
	documentProtocolJoinSharersListCooperativeParticipantName: "Кооперативный Участок",  // Наименование участника - КУч
	documentProtocolJoinSharersListParentCooperativeParticipantName: "Потребительское Общество", // Наименование участника - ПО
	sharersStatementDocumentListUserFieldName: "Заявления кандидатов в пайщики", // Пользовательское поле - строка с заявлениями от физ лиц

	// Настройки заявления на выход из КУч ПО
	requestLeaveSharerFromCommunityDocumentTemplateCode: "kuch_statement_to_leave_physical_persons_from_community", // Код шаблона
	leaveStatementDocumentSharerParticipantName: "Пайщик физ лицо", // Наименование участника - пайщик физ лицо
	leaveStatementDocumentCommunityParticipantName: "Кооперативный Участок", // Наименование участника - КУч
	leaveStatementDocumentParentCommunityParticipantName: "Потребительское Общество", // Наименование участника - ПО

	// Настройки протокола совета на выход пайщиков из КУч ПО
	leaveSharersFromCommunityDocumentTemplateCode: "kuch_protocol_leave_members_from_cooperative", // Код шаблона
	documentProtocolLeaveSharersListParticipantName: "Пайщики физ. лица", // Наименование участника - Список физ лиц для выхода
	documentProtocolLeaveSharersListCooperativeParticipantName: "Кооперативный Участок", // Наименование участника - КУч
	documentProtocolLeaveSharersListParentCooperativeParticipantName: "Потребительское Общество", // Наименование участника - ПО
	sharersStatementToLeaveDocumentListUserFieldName: "Заявления пайщиков на выход из КУч ПО" // Пользовательское поле - строка с заявлениями от физ лиц
}', 'Настройки шаблонов документов для вступления и выхода физ лица из КУч'
where not exists (select id from system_settings where key = 'KuchSharerCommunityMemberSettings');

-- //@UNDO
-- SQL to undo the change goes here.


