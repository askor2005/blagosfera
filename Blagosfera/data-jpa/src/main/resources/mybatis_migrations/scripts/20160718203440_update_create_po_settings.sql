-- // update_create_po_settings
-- Migration SQL that makes the change goes here.

update system_settings set val='var HumansStringUtils = Packages.ru.radom.kabinet.utils.HumansStringUtils;
var StringUtils = Packages.org.apache.commons.lang3.StringUtils;
var HashMap = Packages.java.util.HashMap;
var Padeg = Packages.padeg.lib.Padeg;
var DateUtils = Packages.ru.radom.kabinet.utils.DateUtils;
var ArrayList = Packages.java.util.ArrayList;
var ParticipantsTypes = Packages.ru.radom.kabinet.model.flowofdocuments.ParticipantsTypes;
var System = Packages.java.lang.System;

var founders = new ArrayList(resolvedObjects.founders);
var participantsInSoviet = new ArrayList(resolvedObjects.participantsInSoviet);
var participantsInAuditCommittee = new ArrayList(resolvedObjects.participantsInAuditCommittee);

var Images = {getResizeUrl : function(url, resize) {
	if (url) {
		var dotIndex = url.lastIndexOf(".");
		return url.substr(0, dotIndex) + "_" + resize + url.substr(dotIndex);
	} else {
		return "";
	}
}};

function createParticipantsStringList(participants) {
	var resultList = new ArrayList();
	for(var i = 0; i < participants.length; i++){
		var founder = participants[i];
		resultList.add("<span style=''padding-right: 10px;''><img src=''" + Images.getResizeUrl(founder.avatar, "c32") + "'' /></span><span>" + founder.fullName + "</span>");
	}
	return StringUtils.join(resultList, "<br/>");
}


var registerPODtoMap = serializeService.toMap(registerPODto);
var resolvedObjectsMap =  serializeService.toMap(resolvedObjects);
var payload = new HashMap();

var additionalOkvedsStr = "";
for (var i in resolvedObjectsMap.additionalOkveds) {
	var okved = resolvedObjectsMap.additionalOkveds[i];
	additionalOkvedsStr += okved.code + " " + okved.longName + "<br/>";
}

payload.put("additionalOkveds", additionalOkvedsStr);
payload.put("organizationNameParentPadeg", Padeg.getOfficePadeg(registerPODto.name, 2));
payload.put("registerData", registerPODtoMap);
payload.put("resolvedObjectsMap", resolvedObjectsMap);
payload.put("startDate", DateUtils.formatDate(registerPODto.startDateBatchVoting, DateUtils.Format.DATE_TIME_SHORT));
payload.put("endDate", DateUtils.formatDate(registerPODto.endDateBatchVoting, DateUtils.Format.DATE_TIME_SHORT));
payload.put("registrationEndDate", DateUtils.formatDate(registerPODto.registrationEndDateBatchVoting, DateUtils.Format.DATE_TIME_SHORT));

payload.put("foundersFullNameList", createParticipantsStringList(founders));
payload.put("participantsInSovietFullNameList", createParticipantsStringList(participantsInSoviet));
payload.put("participantsInAuditCommitteeFullNameList", createParticipantsStringList(participantsInAuditCommittee));

//payload.put("participantsInSovietIdsByComma", StringUtils.join(registerPODto.participantsInSovietIds, ","));
payload.put("participantsInSovietIdsBySemicolon", StringUtils.join(registerPODto.participantsInSovietIds, ";"));

payload.put("founderIdsByComma", StringUtils.join(registerPODto.founderIds, ","));
payload.put("founderIdsBySemicolon", StringUtils.join(registerPODto.founderIds, ";"));
payload.put("okvedIdsByComma", StringUtils.join(registerPODto.additionalOkvedIds, ","));
payload.put("activityScopeIdsByComma", StringUtils.join(registerPODto.activityScopeIds, ","));

payload.put("associationFormId", resolvedObjects.associationFormListItem.id);
payload.put("directorPositionId", registerPODto.directorPositionId);
payload.put("communityType", "COMMUNITY_WITH_ORGANIZATION");
payload.put("participantsInAuditIdsBySemicolon", StringUtils.join(registerPODto.participantsInAuditCommitteeIds, ";"));

var idToNameFounders = new HashMap();
for(var i = 0; i < founders.length; i++){
	var founder = founders[i];
	idToNameFounders.put(founder.id, founder.fullName);
}
payload.put("idToNameFounders", idToNameFounders);' where key='organization.register.po.script.variables';

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'register.po.soviet.min.count', '2', 'Минимальное количество членов Совета ПО';

insert into system_settings (id, key, val, description)
select nextval('system_settings_id'), 'register.po.audit.min.count', '2', 'Минимальное количество членов ревизионной комиссии ПО';


-- //@UNDO
-- SQL to undo the change goes here.


