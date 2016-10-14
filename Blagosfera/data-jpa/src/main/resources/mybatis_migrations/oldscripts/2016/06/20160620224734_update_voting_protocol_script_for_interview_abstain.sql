-- // update_voting_protocol_script_for_interview_abstain
-- Migration SQL that makes the change goes here.
update system_settings set val='var System = Packages.java.lang.System;
var Padeg = Packages.padeg.lib.Padeg;
var PadegConstants = Packages.ru.radom.kabinet.utils.PadegConstants;

function getUser(userId) {
	var foundUser = null;
	for (var index in users) {
		var user = users[index];
		if (user.id == userId) {
			foundUser = user;
			break;
		}
	}
	return foundUser;
}

function getUsersFioByComma(userIds) {
	var userFullNames = [];
	for (var index in userIds) {
		var userId = userIds[index];
		var user = getUser(userId);
		if (user != null) {
			userFullNames.push(user.fullName);
		}
	}
	return userFullNames.join(", ");
}

function getVoterIds(votingItem) {
	var result = [];
	for (var index in votingItem.votes) {
		var vote = votingItem.votes[index];
		result.push(vote.ownerId);
	}
	return result;
}

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

function getSimpleResultVoting(voting, isSecretVoting, isValid, isCandidate, successDecree, failDecree) {
	var sentenceResult = "";
	
	successDecree = successDecree == null ? voting.additionalData.successDecree : successDecree;
	failDecree = failDecree == null ? voting.additionalData.failDecree : failDecree;
	
	var i = 1;
	if (isCandidate) {
		sentenceResult += "<b>Предложены следующие кандидатуры:</b><br/>";
		for (var index in voting.votingItems) {
			var votingItem = voting.votingItems[index];
			if (votingItem.value != "ABSTAIN") {
				var candidateId = parseInt(votingItem.value);
				var candidate = getUser(candidateId);
				sentenceResult += i + ") " + candidate.fullName + "<br/>";
				i++;
			}
		}
	} else {
		sentenceResult += "<b>Предложены следующие варианты:</b><br/>";
		for (var index in voting.votingItems) {
			var votingItem = voting.votingItems[index];
			if (votingItem.value != "ABSTAIN") {
				sentenceResult += i + ") " + votingItem.value + "<br/>";
				i++;
			}
		}
	}
	
	var abstainVotingItem = null;
	var abstainUserIds = [];
	
	
	var votingItemsResult = "<b>Проголосовали:</b><br/>";
	var winners = [];
	for (var index in voting.votingItems) {
		var votingItem = voting.votingItems[index];
		var voterIds = getVoterIds(votingItem);
		if (votingItem.value == "ABSTAIN") {
			abstainVotingItem = votingItem;
			abstainUserIds = voterIds;
		} else {
			var isFullVictory = false;
			if (votingItem.votesPercent == 100) {
				isFullVictory = true;
			}
			var percentForWin = 51;
			if (voting.additionalData.percentForWin != null) {
				percentForWin = parseInt(voting.additionalData.percentForWin);
			}
			if (isCandidate){
				var candidateId = parseInt(votingItem.value);
				var candidate = getUser(candidateId);
				var candidateName = Padeg.getFIOPadeg(candidate.lastName, candidate.firstName, candidate.secondName, candidate.sex, PadegConstants.PADEG_V);
				votingItemsResult += "За кандидата " + candidateName;
				if (votingItem.votesPercent >= percentForWin) {
					winners.push({name : candidate.fullName, isFullVictory : isFullVictory});
				}
			} else {
				votingItemsResult += "За вариант \"" + votingItem.value + "\"";
				if (votingItem.votesPercent >= percentForWin) {
					winners.push({name : votingItem.value, isFullVictory : isFullVictory});
				}
			}
			
			votingItemsResult += " " + stringForms(voterIds.length, "проголосовал", "проголосовало", "проголосовало") + " - " + voterIds.length + " " + stringForms(voterIds.length, "человек", "человека", "человек") + " (" + getPercent(votingItem.votesPercent) + "%)<br/>";
			if (!isSecretVoting && voterIds.length > 0) {
				votingItemsResult += "(" + getUsersFioByComma(voterIds) + ")<br/>";
			}
		}
        }
	if (abstainVotingItem != null) {
		votingItemsResult += stringForms(abstainUserIds.length, "Воздержался", "Воздержалось", "Воздержались") + " - " + abstainUserIds.length + " " + stringForms(abstainUserIds.length, "человек", "человека", "человек") + " (" + getPercent(abstainVotingItem.votesPercent) + "%)<br/>";
		if (!isSecretVoting && abstainUserIds.length > 0) {
			votingItemsResult += "(" + getUsersFioByComma(abstainUserIds) + ")<br/>";
		}
	}
	
	if (isValid) {
		var winnerText1 = "<br/><b>Победили следующие варианты:</b><br/>";
		var winnerText2 = "<br/><b>Победил вариант:</b> ";
		var winnerText3 = "\"";
		var winnerText4 = "\"";
		if (isCandidate) {
			winnerText1 = "<br/><b>Победили следующие кандидаты:<br/>";
			winnerText2 = "<br/><b>Победил кандидат:</b> ";
			winnerText3 = "";
			winnerText4 = "";
		}
		var winnersNameArr = [];
		if (winners.length > 1) {
			votingItemsResult += winnerText1;
			for (var index in winners) {
				var winner = winners[index];
				var i = parseInt(index) + 1;
				votingItemsResult += i + ") " + winnerText3 + winner.name + winnerText4 + " - " + (winner.isFullVictory ? "решение принято единогласно" : "решение принято большинством") +  "<br/>";
				winnersNameArr.push(winner.name);
			}
		} else if (winners.length == 1) {
			var winner = winners[0];
			votingItemsResult += winnerText2 + winnerText3 + winner.name + winnerText4 + " - " + (winner.isFullVictory ? "решение принято единогласно" : "решение принято большинством") +  "<br/>";
			winnersNameArr.push(winner.name);
		}
		if (successDecree != null) {
			votingItemsResult += "<br/><b>Постановили:</b><br/>" + successDecree + " " + winnersNameArr.join("; ") + ".";
		}
	} else {
		votingItemsResult += "Решение не принято. " + getFailReason(voting) + ".";
		if (failDecree != null) {
			votingItemsResult += "<br/><br/><b>Постановили:</b><br/>" + failDecree;
		}
	}
	votingItemsResult += "<br/><br/>";
	
	
	return sentenceResult + "<br/>" + votingItemsResult;
}

function getProContraResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree) {
	var result = "";
	var proUserIds = [];
	var contraUserIds = [];
	var abstainUserIds = [];
	var isFullVictory = false;
	
	var proVotingItem = null;
	var contraVotingItem = null;
	var abstainVotingItem = null;
	
	successDecree = successDecree == null ? voting.additionalData.successDecree : successDecree;
	failDecree = failDecree == null ? voting.additionalData.failDecree : failDecree;
	
	var percentForWin = 51;
	if (voting.additionalData.percentForWin != null) {
		percentForWin = parseInt(voting.additionalData.percentForWin);
	}
	isValid = false;
	var failReason = "";
	for (var index in voting.votingItems) {
		var votingItem = voting.votingItems[index];
		switch (votingItem.value) {
			case "PRO": {
				isFullVictory = votingItem.votesPercent == 100;
				proUserIds = getVoterIds(votingItem);
				proVotingItem = votingItem;
				isValid = votingItem.votesPercent >= percentForWin;
				if (!isValid) {
					failReason = "Недостаточное количество голосов за вариант \"ЗА\"";
				}
				break;
			}
			case "CONTRA": {
				contraUserIds = getVoterIds(votingItem);
				contraVotingItem = votingItem;
				break;
			}
			case "ABSTAIN": {
				abstainUserIds = getVoterIds(votingItem);
				abstainVotingItem = votingItem;
				break;
			}
		}
        }
	result += "<b>Проголосовали:</b><br/>";
	result += "ЗА - " + proUserIds.length + " " + stringForms(proUserIds.length, "человек", "человека", "человек") + " (" + getPercent(proVotingItem.votesPercent) + "%)<br/>";
	if (!isSecretVoting && proUserIds.length > 0) {
		result += "(" + getUsersFioByComma(proUserIds) + ")<br/>";
	}
	result += "ПРОТИВ - " + contraUserIds.length + " " + stringForms(contraUserIds.length, "человек", "человека", "человек") + " (" + getPercent(contraVotingItem.votesPercent) + "%)<br/>";
	if (!isSecretVoting && contraUserIds.length > 0) {
		result += "(" + getUsersFioByComma(contraUserIds) + ")<br/>";
	}
	
	if (abstainVotingItem != null) {
	    result += "ВОЗДЕРЖАЛИСЬ - " + abstainUserIds.length + " " + stringForms(abstainUserIds.length, "человек", "человека", "человек") + " (" + getPercent(abstainVotingItem.votesPercent) + "%)<br/>";
	    if (!isSecretVoting && abstainUserIds.length > 0) {
		result += "(" + getUsersFioByComma(abstainUserIds) + ")<br/>";
	    }
	}
	
	if (isValid) {
		if (isFullVictory) {
			result += "<br/><b>Победил вариант \"ЗА\"</b>. Решение принято единогласно";
		} else {
			result += "<br/><b>Победил вариант \"ЗА\"</b>. Решение принято большинством";
		}
		if (successDecree != null) {
			result += "<br/><br/><b>Постановили:</b><br/>" + successDecree;
		}
	} else {
		failReason = failReason == "" ? getFailReason(voting) : failReason;
		result += "Решение не принято. " + failReason + ".";
		if (failDecree != null) {
			result += "<br/><br/><b>Постановили:</b><br/>" + failDecree;
		}
	}
	result += "<br/><br/>";

	return result;
}
function getSingleSelectionResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree) {
	return getSimpleResultVoting(voting, isSecretVoting, isValid, false, successDecree, failDecree);
}
function getCandidateResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree) {
	return getSimpleResultVoting(voting, isSecretVoting, isValid, true, successDecree, failDecree);
}
function getMultipleSelectionResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree){
	return getSimpleResultVoting(voting, isSecretVoting, isValid, false, successDecree, failDecree);
}
function getInterviewResultVoting(voting, isSecretVoting, successDecree, failDecree) {
	
	successDecree = successDecree == null ? voting.additionalData.successDecree : successDecree;
	failDecree = failDecree == null ? voting.additionalData.failDecree : failDecree;	
	var result = "";
	var newVotingItems = [];
	for (var index in voting.votingItems) {
	        var votingItem = voting.votingItems[index];
		if (votingItem.value != "ABSTAIN") {
			newVotingItems.push(votingItem);
		}
	}
	if (isSecretVoting) {
		if (newVotingItems != null && newVotingItems.length > 0) {
			result = "<b>Предложили следующие варианты:</b><br/>";
			for (var index in newVotingItems) {
				var votingItem = newVotingItems[index];
				result += votingItem.value + "<br/>";
			}
			if (voting.additionalData.successDecree != null) {
				result += "<br/><b>Постановили:</b><br/>" + voting.additionalData.successDecree;
			}
		} else {
			result = "Никто не предложил свой вариант.<br/>";
			result += "<br/><b>Постановили:</b><br/>" + voting.additionalData.failDecree;
		}
		
	} else {
		if (newVotingItems != null && newVotingItems.length > 0) {
			result = "<b>Предложили следующие варианты:</b><br/>";
			var userItems = {};
			for (var index in newVotingItems) {
				var votingItem = newVotingItems[index];
				if (userItems[votingItem.ownerId] == null) {
					userItems[votingItem.ownerId] = [];
				}
				userItems[votingItem.ownerId].push(votingItem);
			}
			for (var voterId in userItems) {
				var userItem = userItems[voterId];
				var voter = getUser(voterId);
				result += "Участник " + voter.fullName; 
				result += (voter.sex ? " предложил " : " предложила ") + "варианты:<br/>";
				for (var index in userItem) {
					var item = userItem[index];
					result += item.value + "<br/>";
				}
			}
			if (successDecree != null) {
				result += "<br/><b>Постановили:</b><br/>" + successDecree;
			}
		} else {
			result = "Никто не предложил свой вариант.<br/>";
			if (failDecree != null) {
				result += "<br/><b>Постановили:</b><br/>" + failDecree;
			}
		}
	}
	result += "<br/><br/>";
	return result;
}

function getVotingProtocol(batchVoting, voting, isValid, successDecree, failDecree){
	var result = "";
	var isSecretVoting = batchVoting.parameters.secretVoting;
	switch (voting.parameters.votingType.toString()) {
		case "PRO_CONTRA": { // Голосование За\Против\Воздержался
			result = getProContraResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree);
			break;
		}
		case "SINGLE_SELECTION": { // Голосование с еденичным выбором
			result = getSingleSelectionResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree);
			break;
		}
		case "CANDIDATE": { // Голосование за кандидата
			result = getCandidateResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree);
			break;
		}
		case "MULTIPLE_SELECTION": {
			result = getMultipleSelectionResultVoting(voting, isSecretVoting, isValid, successDecree, failDecree);
			break;
		}
		case "INTERVIEW": {
			result = getInterviewResultVoting(voting, isSecretVoting, successDecree, failDecree);
			break;
		}
		default:
			
			break;
	}
	return result;
}

function getFailReason(voting) {
	var failReason = "";
	switch(voting.result.resultType.toString()) {
		case "INVALID_NO_VOTES":
			failReason = "Никто не проголосовал";
			break;
		case "INVALID_NO_QUORUM":
			failReason = "Недостаточное количество голосов";
			break;
		case "INVALID_DEAD_HEAT":
			failReason = "Голосование завершилось ничьей";
			break;
		case "INVALID_OUT_OF_DATE_RANGE":
			failReason = "Время голосования вышло";
			break;
		case "INVALID_WRONG_RESULT":
			failReason = "Возникла ошибка при проведении голосования";
			break;
	}
	return failReason;
}

function getPercent(floatValue) {
	var result = parseFloat(floatValue.toFixed(2));
	return isNaN(result) ? "00.00" : result;
}

var protocol = "";
var isValid = voting.result.resultType.toString() == "VALID";
protocol = getVotingProtocol(batchVoting, voting, isValid, successDecree, failDecree);

' where key='voting.protocol.script';


-- //@UNDO
-- SQL to undo the change goes here.


