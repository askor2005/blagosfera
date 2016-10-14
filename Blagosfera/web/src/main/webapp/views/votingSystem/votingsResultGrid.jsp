<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    var batchVotingInResultGrid = null;
    function loadUsersByIds(ids, callBack) {
        $.radomJsonPost(
                "/sharer/searchActiveByIds.json",
                JSON.stringify(ids),
                callBack,
                function(){},
                {
                    contentType: 'application/json'
                }
        )
    }

    function getVotingResult(voting) {
        var result = "";
        var percentForWin = parseInt(voting.additionalData.percentForWin);
        var votingResultPrefix = "Голосование прошло успешно.<br/>";
        switch (voting.parameters.votingType) {
            case "MULTIPLE_SELECTION":
                if (voting.parameters.multipleWinners) {
                    for (var itemIndex in voting.votingItems) {
                        var item = voting.votingItems[itemIndex];
                        if (item.votesPercent >= percentForWin) {
                            if (item.value != "ABSTAIN") {
                                result += "Победил вариант \"" + item.value + "\". За него проголосовали " + item.votes.length + " чел. (" + item.votesPercent.toFixed(2) + "%).<br/>";
                            }
                        }
                    }
                } else {
                    var item = voting.votingItems[0];
                    result = "Победил вариант \"" + item.value + "\". За него проголосовали " + item.votes.length + " чел. (" + item.votesPercent.toFixed(2) + "%)";
                }
                break;
            case "SINGLE_SELECTION":
                var item = voting.votingItems[0];
                result = "Победил вариант \"" + item.value + "\". За него проголосовали " + item.votes.length + " чел. (" + item.votesPercent.toFixed(2) + "%)";
                break;
            case "PRO_CONTRA":
                for (var itemIndex in voting.votingItems) {
                    var item = voting.votingItems[itemIndex];
                    if (item.votesPercent >= percentForWin) {
                        var votingItemValue = "";
                        switch(item.value) {
                            case "PRO":
                                votingItemValue = "ЗА";
                                break;
                            case "CONTRA":
                                votingItemValue = "ПРОТИВ";
                                break;
                            case "PRO":
                                votingItemValue = "ВОЗДЕРЖАЛСЯ";
                                break;
                        }
                        result += "\"" + votingItemValue + "\" " + item.votes.length + " чел. (" + item.votesPercent.toFixed(2) + "%).<br/>";
                    }
                }
                break;
            case "CANDIDATE":
                var votesCount = undefined;
                result = "Победители:<br>";

                for (var i = 0; i < voting.votingItems.length; i++) {
                    var item = voting.votingItems[i];
                    if (item.value == "ABSTAIN") continue;

                    if ((votesCount === undefined) || (votesCount === voting.votingItems[i].votes.length)) {
                        votesCount = voting.votingItems[i].votes.length;
                        result += "<span class='candidateName' handled='false' candidate_id='" + item.value + "'>Загрузка...</span>. Число голосов: " + item.votes.length + " чел. (" + item.votesPercent.toFixed(2) + "%)<br>";
                    }
                }

                break;
            case "INTERVIEW":
                votingResultPrefix = "Интервью прошло успешно. Были предложены вырианты:<br/>";
                for (var i = 0; i < voting.votingItems.length; i++) {
                    var votingItem = voting.votingItems[i];
                    result += votingItem.value + "<br/>";
                }
                break;
        }
        return votingResultPrefix + result;
    }

    function initVotingsResultGrid(batchVoting) {
        setInterval(function(){
            if ($(".candidateName[handled=false]").length > 0) {
                var candidateIdsMap = {};
                $(".candidateName[handled=false]").each(function(){
                    candidateIdsMap[$(this).attr("candidate_id")] = "1";
                    $(this).attr("handled", "true");
                });
                var candidateIds = [];
                for (var candidateId in candidateIdsMap) {
                    candidateIds.push(candidateId);
                }
                loadUsersByIds(candidateIds, function(response){
                    if (response != null && response.length > 0) {
                        for (var index in response) {
                            var candidate = response[index];
                            $(".candidateName[candidate_id=" + candidate.id +"]").html(candidate.fullName);
                        }
                    }
                });
            }
        }, 100);

        batchVotingInResultGrid = batchVoting;
        Ext.onReady(function () {
            Ext.define('Voting', {
                extend: 'Ext.data.Model',
                fields: [ 'id', 'subject', 'result' ]
            });

            var votingsResultStore = Ext.create('Ext.data.Store', {
                model: 'Voting',
                data: batchVoting.response.votings
            });

            Ext.create('Ext.grid.Panel', {
                id: 'votingsResultGrid',
                title: 'Итоги голосований',
                store: votingsResultStore,
                columns: [{
                    text: 'Название голосования',
                    dataIndex: 'subject',
                    width: "70%",
                    renderer  : function(value, myDontKnow, record) {
                        return "<a href='/votingsystem/votingPage.html?votingId=" + record.data.id + "' title='Перейти на страницу голосования'>" + record.data.subject + "</a>";
                    }
                },{
                    text: 'Статус',
                    dataIndex: 'result.resultType',
                    width: "29%",
                    renderer  : function(value, myDontKnow, record) {
                        var resultStr = "";
                        switch(record.data.result.resultType) {
                            case "VALID":
                                resultStr = getVotingResult(record.data);;
                                break;
                            case "INVALID_NO_VOTES":
                                resultStr = "Никто не проголосовал";
                                break;
                            case "INVALID_NO_QUORUM":
                                resultStr = "Недостаточное количество голосов";
                                break;
                            case "INVALID_DEAD_HEAT":
                                resultStr = "Голосование завершилось ничьей";
                                break;
                            case "INVALID_OUT_OF_DATE_RANGE":
                                resultStr = "Время голосования вышло";
                                break;
                            case "INVALID_WRONG_RESULT":
                                resultStr = "Возникла ошибка при проведении голосования";
                                break;
                        }
                        return resultStr;
                    }
                }],
                listeners: {},
                frame: true,
                renderTo: 'votingsResult-grid'
            });
        });

    };
</script>