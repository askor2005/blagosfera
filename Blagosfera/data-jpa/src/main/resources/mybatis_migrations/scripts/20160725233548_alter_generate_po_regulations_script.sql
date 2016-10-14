-- // alter_generate_po_regulations_script
-- Migration SQL that makes the change goes here.

do $$
declare
	sectionId bigint;
begin
	select id into sectionId from community_sections where name = 'SETTINGS';

	insert into community_sections(id, link, name, permission, position, title, parent_id, is_guest_access)
	select nextval('seq_community_sections'), '/input_member_settings', 'INPUT_MEMBER_SETTINGS', 'SETTINGS_COMMON', 6, 'Приём и вывод участников', sectionId, false
	where not exists(select id from community_sections where name = 'INPUT_MEMBER_SETTINGS');

	update fields set max_size = null where internal_name = 'COMMUNITY_BRIEF_DESCRIPTION';


	update system_settings set val =
	'var createDocumentParameters = new ArrayList();

var communityParameters = new ParticipantCreateDocumentParameter();

communityParameters.documentParticipants = new ArrayList();

communityParameters.type = "COMMUNITY_WITH_ORGANIZATION";
communityParameters.documentParticipants.add(community);
communityParameters.name = "Потребительское Общество";

var activityTypesText = userFieldsMap.get("activityTypesText");
if (activityTypesText != null) {
	activityTypesText = activityTypesText.replace(/(?:\r\n|\r|\n)/g, "<br />");
}


var userFields = new ArrayList();
userFields.add(UserFieldValueBuilder.createStringValue("hasStamp", userFieldsMap.get("hasStamp")));
userFields.add(UserFieldValueBuilder.createStringValue("whoApprovePosition", userFieldsMap.get("whoApprovePosition")));
userFields.add(UserFieldValueBuilder.createStringValue("mainOkved", userFieldsMap.get("mainOkved")));
userFields.add(UserFieldValueBuilder.createStringValue("additionalOkveds", userFieldsMap.get("additionalOkveds")));
userFields.add(UserFieldValueBuilder.createStringValue("countDaysToQuiteFromPo", userFieldsMap.get("countDaysToQuiteFromPo")));
userFields.add(UserFieldValueBuilder.createStringValue("whoApproveDatePay", userFieldsMap.get("whoApproveDatePay")));
userFields.add(UserFieldValueBuilder.createIntegerValue("countMonthToSharerPay", userFieldsMap.get("countMonthToSharerPay")));
userFields.add(UserFieldValueBuilder.createStringValue("monthToSharerPayMorph", userFieldsMap.get("monthToSharerPayMorph")));
userFields.add(UserFieldValueBuilder.createStringValue("startPeriodPay", userFieldsMap.get("startPeriodPay")));
userFields.add(UserFieldValueBuilder.createStringValue("startPeriodPayAlter", userFieldsMap.get("startPeriodPayAlter")));
userFields.add(UserFieldValueBuilder.createStringValue("onShareDeath", userFieldsMap.get("onShareDeath")));
userFields.add(UserFieldValueBuilder.createLongValue("minCreditApproveSovietPO", userFieldsMap.get("minCreditApproveSovietPO")));
userFields.add(UserFieldValueBuilder.createStringValue("minCreditApproveSovietPOMorph", userFieldsMap.get("minCreditApproveSovietPOMorph")));
userFields.add(UserFieldValueBuilder.createLongValue("minContractSumApproveSovietPO", userFieldsMap.get("minContractSumApproveSovietPO")));
userFields.add(UserFieldValueBuilder.createStringValue("sovietOfficePeriod", userFieldsMap.get("sovietOfficePeriod")));
userFields.add(UserFieldValueBuilder.createStringValue("presidentOfSovietKindWorking", userFieldsMap.get("presidentOfSovietKindWorking")));
userFields.add(UserFieldValueBuilder.createStringValue("presidentOfSovietKindWorkingAlter", userFieldsMap.get("presidentOfSovietKindWorkingAlter")));
userFields.add(UserFieldValueBuilder.createStringValue("participantsOfBoardOfficePeriod", userFieldsMap.get("participantsOfBoardOfficePeriod")));
userFields.add(UserFieldValueBuilder.createStringValue("countDaysPerMeetingOfBoard", userFieldsMap.get("countDaysPerMeetingOfBoard")));
userFields.add(UserFieldValueBuilder.createStringValue("quorumMeetingOfBoard", userFieldsMap.get("quorumMeetingOfBoard")));
userFields.add(UserFieldValueBuilder.createStringValue("boardReportFrequency", userFieldsMap.get("boardReportFrequency")));
userFields.add(UserFieldValueBuilder.createStringValue("participantsAuditCommitteeOfficePeriod", userFieldsMap.get("participantsAuditCommitteeOfficePeriod")));
userFields.add(UserFieldValueBuilder.createStringValue("branchesPart", userFieldsMap.get("branchesPart")));
userFields.add(UserFieldValueBuilder.createStringValue("activityTypesText", activityTypesText));

createDocumentParameters.add(new CreateDocumentParameter(communityParameters, userFields));
var flowOfDocumentDTO = flowOfDocumentService.generateDocumentDTO("po_regulations", createDocumentParameters);'
	where key = 'generate.po.regulations.script';

	update system_settings set val =
	'var HumansStringUtils = Packages.ru.radom.kabinet.utils.HumansStringUtils;
var StringUtils = Packages.org.apache.commons.lang3.StringUtils;
var Padeg = Packages.padeg.lib.Padeg;
var HashMap = Packages.java.util.HashMap;

function stringForms(intValue, value1, value2, value3) {
    var result = "";
    if (intValue > 10 && intValue < 20) {
        result = value3;
    } else {
        if (intValue % 10 == 1) {
            result = value1;
        } else if (intValue % 10 > 1 && intValue % 10 < 5) {
            result = value2;
        } else {
            result = value3;
        }
    }
    return result;
}

var hasStampStr = "";
if (poHasStamp) {
  hasStampStr = "Потребительское общество имеет печать с полным наименованием Потребительского общества на русском языке, штампы и бланки со своим наименованием.";
} else {
  hasStampStr = "Потребительское общество не имеет печать с полным наименованием Потребительского общества на русском языке, штампы и бланки со своим наименованием.";
}

var whoApprovePositionStr = "";
switch(whoApprovePosition) {
  case "commonBatchVotingPO":
    whoApprovePositionStr = "Общим собранием ПО";
    break;
  case "sovietPO":
    whoApprovePositionStr = "Советом ПО";
    break;
  case "presidentOfSovietPO":
    whoApprovePositionStr = "Председателем Совета ПО";
    break;
  case "boardPO":
    whoApprovePositionStr = "Правлением ПО";
    break;
  case "presidentOfBoardPO":
    whoApprovePositionStr = "Председателем Правления ПО";
    break;
}

var mainOkvedStr = "";
if (mainOkved != null){
   mainOkvedStr = "- " + mainOkved.code + " " + mainOkved.longName;
}

var additionalOkvedsStr = "";
var additionalOkvedsStrArr = [];
if (additionalOkveds != null && additionalOkveds.length > 0){
   for (var i=0; i<additionalOkveds.length; i++) {
       additionalOkvedsStrArr.push("- " + additionalOkveds[i].code + " " + additionalOkveds[i].longName);
   }
   additionalOkvedsStr = additionalOkvedsStrArr.join("<br/>");
}

var countDaysToQuiteFromPoStr = countDaysToQuiteFromPo + " " + stringForms(countDaysToQuiteFromPo, "дня", "дней", "дней");

var whoApproveDatePayStr = "";
switch(whoApproveDatePay) {
  case "whoApproveDatePayCommonBatchVotingPO":
    whoApproveDatePayStr = "Общим собранием пайщиков ПО";
    break;
  case "whoApproveDatePaySovietPO":
    whoApproveDatePayStr = "Советом ПО";
    break;
}

//var countMonthToSharerPayStrForm = StringUtils.trim(HumansStringUtils.number2string(countMonthToSharerPay));
//var countMonthToSharerPayStr = countMonthToSharerPay + " (" + Padeg.getOfficePadeg(countMonthToSharerPayStrForm + " месяца", 2) + ") " +  stringForms(countMonthToSharerPay, "месяца", "месяцев", "месяцев");
var monthToSharerPayMorph = stringForms(countMonthToSharerPay, "месяца", "месяцев", "месяцев");

var startPeriodPayStr = "";
var startPeriodPayAlter = "";
switch(startPeriodPay) {
  case "quarter":
    startPeriodPayStr = "квартала";
	startPeriodPayAlter = "квартал";
    break;
  case "year":
    startPeriodPayStr = "финансового года";
	startPeriodPayAlter = "год";
    break;
}

var onShareDeathStr = "";
switch(onShareDeath) {
	case "childMayBeSharer":
		onShareDeathStr =
		"В случае смерти пайщика его наследники могут быть приняты в "+
		"Потребительское общество решением Совета Общества на основании заявления "+
		"(пункт 3.1 настоящего Устава) с приложением свидетельства о наследстве "+
		"(иное может быть предусмотрено Уставом)";
		break;
	case "payToChild":
		onShareDeathStr =
		"В случае смерти пайщика Потребительское общество передает наследникам его "+
		"паевой взнос и кооперативные выплаты в порядке, предусмотренном статьями "+
		"5.3 - 5.5 настоящего Устава. Право участия в общих собраниях Потребительского "+
		"общества и другие права пайщиков указанным наследникам не передаются.)";
		break;
}

var minCreditApproveSovietPOMorph = stringForms(minCreditApproveSovietPO, "рубль", "рубля", "рублей");

var sovietOfficePeriodStr = sovietOfficePeriod + " " + stringForms(sovietOfficePeriod, "год", "года", "лет");

var presidentOfSovietKindWorkingStr = "";
var presidentOfSovietKindWorkingAlterStr = "";
switch(presidentOfSovietKindWorking) {
	case "free":
		presidentOfSovietKindWorkingStr = "общественных началах";
		presidentOfSovietKindWorkingAlterStr =
		"Председатель и члены Совета Общества, исполняющие свои полномочия на общественных началах, " +
		"могут быть освобождены от исполнения полномочий в любое время на основании решения Общего собрания Общества.";
		break;
	case "charge":
		presidentOfSovietKindWorkingStr = "платной основе";
		presidentOfSovietKindWorkingAlterStr =
		"Председатель Совета Общества, исполняющий свои обязанности на платной основе, может быть уволен досрочно " +
		"на основании решения Общего собрания в соответствие с законодательством Российской Федерации.";
		break;
}

var participantsOfBoardOfficePeriodStr = participantsOfBoardOfficePeriod + " " + stringForms(participantsOfBoardOfficePeriod, "год", "года", "лет");

var countDaysPerMeetingOfBoardStr = countDaysPerMeetingOfBoard + " " + stringForms(countDaysPerMeetingOfBoard, "день", "дня", "дней");

var quorumMeetingOfBoardStr = quorumMeetingOfBoard + " " + stringForms(quorumMeetingOfBoard, "процента", "процентов", "процентов");

var boardReportFrequencyStr = "";
switch(boardReportFrequency) {
	case "onePerMonth":
		boardReportFrequencyStr = "не реже, чем один раз в месяц";
		break;
	case "onePerQuarter":
		boardReportFrequencyStr = "не реже, чем раз в квартал";
		break;
	case "onePerYear":
		boardReportFrequencyStr = "не реже, чем раз в год";
		break;
}

var participantsAuditCommitteeOfficePeriodStr = participantsAuditCommitteeOfficePeriod + " " + stringForms(participantsAuditCommitteeOfficePeriod, "год", "года", "лет");

// Раздел устава про филиалы
function getBranchesString(branches, currentBranchIndexWrapper, type) {
	var result = "";
	for (var i=0; i<branches.length; i++) {
		var branch = branches[i];

		var simpleBranchTypeName = "";
		var capitalizeBranchTypeName = "";
		if (type) {
			simpleBranchTypeName = "создан филиал ";
			capitalizeBranchTypeName = "Филиал ";
		} else {
			simpleBranchTypeName = "создано представительство ";
			capitalizeBranchTypeName = "Представительство ";
		}

		result = result +
		"<p style=\"text-align: justify;\"><span>14." + currentBranchIndexWrapper.index + ". В Потребительском обществе " + simpleBranchTypeName + branch.branchName + " по адресу: " + branch.address.geoLocation + ".</span></p>";
		currentBranchIndexWrapper.index++;

		result = result +
		"<p style=\"text-align: justify;\"><span>14." + currentBranchIndexWrapper.index + ". " + capitalizeBranchTypeName + branch.branchName + " Потребительского общества выполняет следующие функции:</span></p>";
		currentBranchIndexWrapper.index++;

		if (branch.branchFunctions != null && branch.branchFunctions.length > 0) {
			for (var j=0; j<branch.branchFunctions.length; j++) {
				var endSymbol = ";";
				if (j == branch.branchFunctions.length - 1) {
					endSymbol = ".";
				}
				var branchFunction = branch.branchFunctions[j];
				result = result +
				"<p style=\"text-align: justify;\"><span>" + branchFunction.text + endSymbol + "</span></p>";
			}
		}
	}
	return result;
}


var branchesPart = "";
if ((branches != null && branches.length > 0) || (representations != null && representations.length > 0)) {
	var branchesPart =
	"<ol style=\"text-align: justify;\" start=\"14\">" +
	"<li>СВЕДЕНИЯ О ФИЛИАЛАХ И ПРЕДСТАВИТЕЛЬСТВАХ</li>" +
	"</ol>" +
	"<p style=\"text-align: justify;\">&nbsp;</p>";
	var currentBranchIndexWrapper = {index : 1};
	if (branches != null) {
		branchesPart = branchesPart + getBranchesString(branches, currentBranchIndexWrapper, true);
	}
	if (representations != null) {
		branchesPart = branchesPart + getBranchesString(representations, currentBranchIndexWrapper, false);
	}
}


var userFieldsMap = new HashMap();

userFieldsMap.put("hasStamp", hasStampStr);
userFieldsMap.put("whoApprovePosition", whoApprovePositionStr);
userFieldsMap.put("mainOkved", mainOkvedStr);
userFieldsMap.put("additionalOkveds", additionalOkvedsStr);
userFieldsMap.put("countDaysToQuiteFromPo", countDaysToQuiteFromPoStr);
userFieldsMap.put("whoApproveDatePay", whoApproveDatePayStr);
userFieldsMap.put("countMonthToSharerPay", countMonthToSharerPay);
userFieldsMap.put("monthToSharerPayMorph", monthToSharerPayMorph);
userFieldsMap.put("startPeriodPay", startPeriodPayStr);
userFieldsMap.put("startPeriodPayAlter", startPeriodPayAlter);
userFieldsMap.put("onShareDeath", onShareDeathStr);
userFieldsMap.put("minCreditApproveSovietPO", minCreditApproveSovietPO);
userFieldsMap.put("minCreditApproveSovietPOMorph", minCreditApproveSovietPOMorph);
userFieldsMap.put("minContractSumApproveSovietPO", minContractSumApproveSovietPO);
userFieldsMap.put("sovietOfficePeriod", sovietOfficePeriodStr);
userFieldsMap.put("presidentOfSovietKindWorking", presidentOfSovietKindWorkingStr);
userFieldsMap.put("presidentOfSovietKindWorkingAlter", presidentOfSovietKindWorkingAlterStr);
userFieldsMap.put("participantsOfBoardOfficePeriod", participantsOfBoardOfficePeriodStr);
userFieldsMap.put("countDaysPerMeetingOfBoard", countDaysPerMeetingOfBoardStr);
userFieldsMap.put("quorumMeetingOfBoard", quorumMeetingOfBoardStr);
userFieldsMap.put("boardReportFrequency", boardReportFrequencyStr);
userFieldsMap.put("participantsAuditCommitteeOfficePeriod", participantsAuditCommitteeOfficePeriodStr);
userFieldsMap.put("branchesPart", branchesPart);
userFieldsMap.put("activityTypesText", activityTypesText);'
	where key = 'generate.po.regulations.script.data';

end $$;

-- //@UNDO
-- SQL to undo the change goes here.


