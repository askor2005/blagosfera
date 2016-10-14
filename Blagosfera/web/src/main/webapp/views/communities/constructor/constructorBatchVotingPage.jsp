<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ include file="copyTemplateBatchVotingGrid.jsp" %>
<style type="text/css">

    .attrNameContainer {
        display: inline-block;
        vertical-align: bottom;
    }

    .attrValueContainer {
        display: inline-block;
        margin-left: 10px;
        vertical-align: bottom;
    }

    .attr_name, .attr_value {
        width: 200px;
    }

    .upVoting, .downVoting {
        cursor: pointer;
        display: block;
    }

    .spinner {
        /*width: 100px;*/
    }

    .spinner input {
        text-align: right;
    }

    .input-group-btn-vertical {
        position: relative;
        white-space: nowrap;
        width: 1%;
        vertical-align: middle;
        display: table-cell;
    }

    .input-group-btn-vertical > .btn {
        display: block;
        float: none;
        width: 100%;
        max-width: 100%;
        padding: 8px;
        margin-left: -1px;
        position: relative;
        border-radius: 0;
    }

    .input-group-btn-vertical > .btn:first-child {
        border-top-right-radius: 4px;
    }

    .input-group-btn-vertical > .btn:last-child {
        margin-top: -2px;
        border-bottom-right-radius: 4px;
    }

    .input-group-btn-vertical i {
        position: absolute;
        top: 0;
        left: 4px;
    }

    .visibleVoting {
        font-size: 28px;
    }
</style>
<script>
    var batchVotingTemplatesStore;
    var seoLink = "${seoLink}";
    var votingTypeDescriptions = {
        "MULTIPLE_SELECTION": "В голосовании можно выбрать несколько вариантов", "PRO_CONTRA": "" +
        "Можно выбрать любой из этих вариантов", "INTERVIEW": "Каждый может написать свой вариант",
        "SINGLE_SELECTION": "В голосовании можно выбрать один вариант", "CANDIDATE": "Голосование за кандидатов"
    };
    var modeDescriptions = {
        "PARALLEL": "Голосования проводятся параллельно",
        "SEQUENTIAL": "Голосования проводятся последовательно"
    };
    var secretDescriptions = {
        true: "Голосование будет тайным",
        false: "Голосование будет открытым: в результатах голосований и в протоколе будет отображаться, кто за что голосовал"
    };
    var includeSubgroups = true;
    var currentConstructorPageData;
    var childGroupsVotersMap = {};
    var possibleVotersMap = {};
    var communityId = "${communityId}";
    var templateId = "${param['templateId']}";

    var currentUserId;
    var defaultStartDate;
    // Время мск
    var defaultEndDate;
    var defaultRegistrationEndDate;
    //
    var constructorPageTemplate;
    //
    var voterTemplate;
    var possibleVotersTemplate;
    var possibleCandidatesTemplate;
    //
    var attributesTemplate;
    //
    var votingsTemplate;
    //
    var candidatesTemplate;
    //
    var votingItemsTemplate;
    var votingOptionTemplate;
    //
    var defaultVoters;
    //
    var selectedVoters;
    //
    var selectedSignProtocolMap = {};
    // Индекс открытого голосования для редактирования
    var currentVotingIndex = -1;
    // Варианты у открытого голосования
    var currentVotingItems = [];
    // Атрибуты у открытого голосования
    var currentVotingAttributes = [];

    // Форма менялась
    var formIsDirty = false;
    // Типы голосований
    var votingTypes = {};

    //
    var votersNeedBeVerified = false;

    var loadedCurrentUser = null;

    var sentenceHelpText = null;
    var successDecreeHelpText = null;
    var failDecreeHelpText = null;

    // Текущие параметры собрания
    var currentBatchVoting = {
        id: null,
        subject: "",
        description: "",
        behavior: null,
        isNeedCreateChat: false,
        quorum: "",
        startDate: null,//defaultStartDate.format("dd.mm.yyyy HH:MM"),
        batchVotingHoursCount: 3 * 24,
        isCanFinishBeforeEndDate: true,
        votersAllowed: [],
        mode: "SEQUENTIAL",
        //registrationHoursCount : 24,
        votingRestartCount: 5,
        attributes: [],
        secretVoting: false,
        votings: [],
        isNeedAddAdditionalVotings: true,
        useBiometricIdentificationInAdditionalVotings: false,
        useBiometricIdentificationInRegistration: false,
        addChatToProtocol: false,
        testBatchVotingMode: false
    };

    function updateSourceVotingIndexes(firstVoting, newFirstVotingIndex, secondVoting, newSecondVotingIndex) {
        for (var i in currentBatchVoting.votings) {
            var selectFromType = getVotingAttributeValue(currentBatchVoting.votings[i].attributes, 'selectFromType');
            var sourceVotingIndex = getVotingAttributeValue(currentBatchVoting.votings[i].attributes, 'sourceVotingIndex');

            if (selectFromType != "addMine" && sourceVotingIndex) {
                if (parseInt(sourceVotingIndex) === firstVoting.index) {
                    setVotingAttributeValue(currentBatchVoting.votings[i], "sourceVotingIndex", newFirstVotingIndex + "");
                }
                else if (parseInt(sourceVotingIndex) === secondVoting.index) {
                    setVotingAttributeValue(currentBatchVoting.votings[i], "sourceVotingIndex", newSecondVotingIndex + "");
                }
            }
        }
    }
    ;

    function validateSourceVotingIndexes(firstVoting, newFirstVotingIndex, secondVoting, newSecondVotingIndex) {
        for (var i in currentBatchVoting.votings) {
            var index = currentBatchVoting.votings[i].index;
            if (index == firstVoting.index) {
                index = newFirstVotingIndex;
            }
            else if (index == secondVoting.index) {
                index = newSecondVotingIndex;
            }

            var selectFromType = getVotingAttributeValue(currentBatchVoting.votings[i].attributes, 'selectFromType');
            var sourceVotingIndex = getVotingAttributeValue(currentBatchVoting.votings[i].attributes, 'sourceVotingIndex');

            //var sourceInterviewIndex = getVotingAttributeValue(currentBatchVoting.votings[i].attributes, "sourceInterviewIndex");
            if (selectFromType != "addMine" && sourceVotingIndex) {
                var dependsFromIndex = parseInt(sourceVotingIndex);
                if (dependsFromIndex == firstVoting.index) {
                    dependsFromIndex = newFirstVotingIndex;
                }
                else if (dependsFromIndex == secondVoting.index) {
                    dependsFromIndex = newSecondVotingIndex;
                }
                if (!(index > dependsFromIndex)) {
                    return false;
                }
            }
        }
        return true;
    }


    function initDefaultParameters(constructorPageData, currentUser) {
        currentUserId = currentUser.id;

        defaultStartDate = createDate();

        defaultEndDate = new Date(defaultStartDate.getTime() + 3 * 24 * 60 * 60 * 1000);
        defaultRegistrationEndDate = new Date(defaultStartDate.getTime() + 24 * 60 * 60 * 1000);

        // Участники собрания
        defaultVoters = [{
            name: currentUser.fullName,
            id: currentUser.id,
            handleVoter: false,
            avatar: currentUser.avatar,
            link: currentUser.link,
            avatarSmall: Images.getResizeUrl(currentUser.avatar, "c48"),
            isSignProtocol: true
        }];
        selectedVoters = defaultVoters;

        // Типы голосований
        votingTypes = constructorPageData.votingTypes;

        currentBatchVoting.subject = "";
        currentBatchVoting.description = "";
        currentBatchVoting.behavior = null;
        currentBatchVoting.isNeedCreateChat = false;
        currentBatchVoting.quorum = 51;
        currentBatchVoting.batchVotingHoursCount = 3 * 24;
        currentBatchVoting.isCanFinishBeforeEndDate = true;
        currentBatchVoting.votersAllowed = [];
        currentBatchVoting.mode = "SEQUENTIAL";
        currentBatchVoting.votingRestartCount = 5;
        currentBatchVoting.attributes = [];
        currentBatchVoting.secretVoting = false;
        currentBatchVoting.votings = [];
        currentBatchVoting.isNeedAddAdditionalVotings = true;

        currentBatchVoting.startDate = defaultStartDate.format("dd.mm.yyyy HH:MM");
        currentBatchVoting.registrationEndDate = defaultRegistrationEndDate.format("dd.mm.yyyy HH:MM");
        currentBatchVoting.endDate = defaultEndDate.format("dd.mm.yyyy HH:MM");


        currentBatchVoting.useBiometricIdentificationInAdditionalVotings = false;
        currentBatchVoting.useBiometricIdentificationInRegistration = false;
        currentBatchVoting.addChatToProtocol = false;

        currentBatchVoting.testBatchVotingMode = false;

        sentenceHelpText = constructorPageData.sentenceHelpText;
        successDecreeHelpText = constructorPageData.successDecreeHelpText;
        failDecreeHelpText = constructorPageData.failDecreeHelpText;
    }

    function initParameters(constructorPageData, currentUser) {
        initDefaultParameters(constructorPageData, currentUser);

        if (constructorPageData.batchVotingTemplate != null) {
            for (var votingIndex in constructorPageData.batchVotingTemplate.votings) {
                var loadedVoting = constructorPageData.batchVotingTemplate.votings[votingIndex];
                var voting = {
                    id: loadedVoting.id,
                    subject: htmlDecode(loadedVoting.subject),
                    description: loadedVoting.description,
                    votingType: loadedVoting.votingType,
                    isVoteCancellable: loadedVoting.voteCancellable,
                    notRestartable: loadedVoting.notRestartable,
                    isVoteCommentsAllowed: loadedVoting.voteCommentsAllowed,
                    addAbstain : loadedVoting.addAbstain,
                    minSelectionCount: loadedVoting.minSelectionCount,
                    maxSelectionCount: loadedVoting.maxSelectionCount,
                    minWinnersCount: loadedVoting.minWinnersCount,
                    maxWinnersCount: loadedVoting.maxWinnersCount,
                    stopBatchVotingOnFailResult: loadedVoting.stopBatchVotingOnFailResult,
                    percentForWin: loadedVoting.percentForWin,
                    index: loadedVoting.index,
                    isVisible: loadedVoting.visible,
                    votingItems: [],
                    attributes: [],
                    votingState: "NEW",
                    multipleWinners: loadedVoting.multipleWinners,
                    useBiometricIdentification: loadedVoting.useBiometricIdentification,
                    sentence: loadedVoting.sentence,
                    successDecree: loadedVoting.successDecree,
                    failDecree: loadedVoting.failDecree,
                    skipResults: loadedVoting.skipResults
                };

                if (loadedVoting.attributes != null) {
                    for (var attrIndex in loadedVoting.attributes) {
                        var attr = loadedVoting.attributes[attrIndex];
                        voting.attributes.push({
                            id: attr.id,
                            name: htmlDecode(attr.name),
                            value: htmlDecode(attr.value)
                        });
                    }
                }

                if (loadedVoting.votingItems != null) {
                    for (var votingIndex in loadedVoting.votingItems) {
                        var item = loadedVoting.votingItems[votingIndex];
                        voting.votingItems.push({
                            id: item.id,
                            value: htmlDecode(item.value)
                        });
                    }
                }
                currentBatchVoting.votings.push(voting);

            }
            var votersAllowed = [];
            if (constructorPageData.batchVotingTemplate.votersAllowed != null) {
                for (var voterIndex in constructorPageData.batchVotingTemplate.votersAllowed) {
                    var voterAllowed = constructorPageData.batchVotingTemplate.votersAllowed[voterIndex];
                    var voterId = voterAllowed.voterId;
                    votersAllowed.push(voterId);
                    selectedSignProtocolMap[voterId] = voterAllowed.isSignProtocol;
                }
            }

            currentBatchVoting.id = constructorPageData.batchVotingTemplate.id;
            currentBatchVoting.subject = htmlDecode(constructorPageData.batchVotingTemplate.subject);
            currentBatchVoting.description = constructorPageData.batchVotingTemplate.description;
            currentBatchVoting.behavior = constructorPageData.batchVotingTemplate.behavior;
            currentBatchVoting.isNeedCreateChat = constructorPageData.batchVotingTemplate.needCreateChat;
            currentBatchVoting.quorum = constructorPageData.batchVotingTemplate.quorum;
            currentBatchVoting.startDate = constructorPageData.batchVotingTemplate.startDate;
            currentBatchVoting.registrationEndDate = constructorPageData.batchVotingTemplate.registrationEndDate;
            currentBatchVoting.endDate = constructorPageData.batchVotingTemplate.endDate;
            currentBatchVoting.batchVotingHoursCount = constructorPageData.batchVotingTemplate.batchVotingHoursCount;
            currentBatchVoting.isCanFinishBeforeEndDate = constructorPageData.batchVotingTemplate.canFinishBeforeEndDate;
            currentBatchVoting.votersAllowed = votersAllowed;
            currentBatchVoting.mode = "SEQUENTIAL"; //constructorPageData.batchVotingTemplate.mode;
            //currentBatchVoting.registrationHoursCount = constructorPageData.batchVotingTemplate.registrationHoursCount;
            currentBatchVoting.votingRestartCount = constructorPageData.batchVotingTemplate.votingRestartCount;
            currentBatchVoting.attributes = []; // TODO
            currentBatchVoting.secretVoting = constructorPageData.batchVotingTemplate.secretVoting;
            currentBatchVoting.isNeedAddAdditionalVotings = constructorPageData.batchVotingTemplate.needAddAdditionalVotings;
            currentBatchVoting.useBiometricIdentificationInAdditionalVotings = constructorPageData.batchVotingTemplate.useBiometricIdentificationInAdditionalVotings;
            currentBatchVoting.addChatToProtocol = constructorPageData.batchVotingTemplate.addChatToProtocol;
            currentBatchVoting.useBiometricIdentificationInRegistration = constructorPageData.batchVotingTemplate.useBiometricIdentificationInRegistration;
            currentBatchVoting.testBatchVotingMode = constructorPageData.batchVotingTemplate.testBatchVotingMode;
        }
    }

    var startBatchVotingMessage =
            "Собрание создано. <a href='/votingsystem/registrationInVoting.html?batchVotingId={batchVotingId}' class='btn btn-primary'>Ссылка для перехода в собрание</a>";

    var saveBatchVotingTemplateMessage =
            "Шаблон сохранён. <a href='/group/" + communityId + "/batchVotingConstructor.html?templateId={batchVotingTemplateId}' class='btn btn-primary'>Ссылка для перехода в сохранённый шаблон</a>";

    var saveAndStartBatchVotingTemplateMessage =
            "Шаблон сохранён. <a href='/group/" + communityId + "/batchVotingConstructor.html?templateId={batchVotingTemplateId}' class='btn btn-primary'>Ссылка для перехода в сохранённый шаблон</a><br/><br/>" +
            "Собрание создано. <a href='/votingsystem/registrationInVoting.html?batchVotingId={batchVotingId}' class='btn btn-primary'>Ссылка для перехода в собрание</a>";

    function getSaveConfirmText() {
        var confirmText =
                "Вы действительно хотите сохранить это собрание как шаблон?<br/>" +
                "Если Вы сохраните это собрание как шаблон, то в дальнейшем вы сможете использовать этот шаблон \"" + currentBatchVoting.subject + "\" для создания новых собраний с такими-же параметрами.";
        return confirmText;
    }

    function htmlDecode(strinForDecode) {
        return $("<div/>").html(strinForDecode).text();
    }

    // Запустить собрание
    function startBatchVoting(createBatchVotingRequest, callBack) {
        $.radomJsonPostWithWaiter(
                "/group/" + communityId + "/startBatchVoting.json",
                JSON.stringify(createBatchVotingRequest),
                callBack,
                null,
                {
                    contentType: 'application/json',
                    neededParameters: ["batchVotingId"],
                    content: startBatchVotingMessage
                }
        );
    }

    // Запустить собрание из шаблона
    function startBatchVotingFromTemplate(templateId, callBack) {
        $.radomJsonPostWithWaiter(
                "/group/" + communityId + "/startBatchVotingFromTemplate.json",
                {
                    template_id: templateId
                },
                callBack
        );
    }

    // Сохранить и запустить собрание
    function saveAndStartBatchVotingTemplate(createBatchVotingRequest, withMessageTemplate, callBack) {
        var requestParameters = {contentType: 'application/json'};
        if (withMessageTemplate) {
            requestParameters = {
                contentType: 'application/json',
                neededParameters: ["batchVotingId", "batchVotingTemplateId"],
                content: saveAndStartBatchVotingTemplateMessage
            };
        } else {
            requestParameters = {
                contentType: 'application/json',
                neededParameters: ["batchVotingId"],
                content: startBatchVotingMessage
            };
        }

        $.radomJsonPostWithWaiter(
                "/group/" + communityId + "/saveAndStartBatchVotingTemplate.json",
                JSON.stringify(createBatchVotingRequest),
                callBack,
                null,
                requestParameters
        );
    }

    // Сохранить собрание
    function saveBatchVotingTemplate(createBatchVotingRequest, withMessage, callBack) {
        var requestParameters = {contentType: 'application/json'};
        if (withMessage) {
            requestParameters = {
                contentType: 'application/json',
                neededParameters: ["batchVotingTemplateId"],
                content: saveBatchVotingTemplateMessage
            };
        } else {
            requestParameters = {
                contentType: 'application/json'
            };
        }

        $.radomJsonPostWithWaiter(
                "/group/" + communityId + "/saveBatchVotingTemplate.json",
                JSON.stringify(createBatchVotingRequest),
                callBack,
                null,
                requestParameters
        );
    }

    function getCreateVotingsRequest(voting) {
        var votingItems = [];

        for (var i in voting.votingItems) {
            votingItems.push({value: voting.votingItems[i].value});
        }

        return {
            votingType: voting.votingType,
            isVoteCancellable: voting.isVoteCancellable,
            notRestartable: voting.notRestartable,
            isVoteCommentsAllowed: voting.isVoteCommentsAllowed,
            minSelectionCount: voting.minSelectionCount,
            maxSelectionCount: voting.maxSelectionCount,
            minWinnersCount: voting.minWinnersCount,
            maxWinnersCount: voting.maxWinnersCount,
            stopBatchVotingOnFailResult: voting.stopBatchVotingOnFailResult,
            percentForWin: voting.percentForWin,
            index: voting.index - 1,
            subject: voting.subject,
            description: voting.description,
            votingState: voting.votingState,
            isVisible: voting.isVisible,
            votingItems: votingItems,
            attributes: voting.attributes,
            multipleWinners: voting.multipleWinners,
            useBiometricIdentification: voting.useBiometricIdentification
        };
    }

    function isInt(value) {
        var nmb = Number(value);
        return nmb % 1 === 0;
    }

    // Получить параметры создания собрания
    function getCreateBatchVotingRequest(checkFields) {
        var errorMessages = [];
        var regxp = RegExp('^[0-9]*$');

        var quorum = parseInt(currentBatchVoting.quorum);
        var testQuorum = regxp.test(currentBatchVoting.quorum);

        var votingRestartCount = parseInt(currentBatchVoting.votingRestartCount);
        var testVotingRestartCount = regxp.test(currentBatchVoting.votingRestartCount);
        var subject = currentBatchVoting.subject;
        var description = window.tinymce.EditorManager.get("description").getContent();
        currentBatchVoting.description = description;

        var startDate = parseFormattedDate(currentBatchVoting.startDate, shortTimeDateFormatForMoment);
        var endDate = parseFormattedDate(currentBatchVoting.endDate, shortTimeDateFormatForMoment);
        var registrationEndDate = parseFormattedDate(currentBatchVoting.registrationEndDate, shortTimeDateFormatForMoment);

        var currentDate = createDate();

        if (checkFields == true || checkFields == null) {
            if (!testQuorum || quorum < 1 || quorum > 100 || isNaN(quorum)) {
                errorMessages.push("Параметр 'Кворум' должен быть числом от 1 до 100");
            }
            if (!testVotingRestartCount || votingRestartCount < 0 || isNaN(votingRestartCount)) {
                errorMessages.push("Параметр 'аксимальное число перезапусков собрания' должен быть числом от 0");
            }
            if (subject == null || subject == "") {
                errorMessages.push("Не указана тема голосования");
            }
            if (subject.length < 10) {
                errorMessages.push("Тема голосования должна состоять минимум из 10 символов");
            }
            if (description == null || description == "") {
                errorMessages.push("Не указано описание голосования");
            }
            if (description.length < 128) {
                errorMessages.push("Описание должно состоять минимум из 128 символов");
            }
            if (description.length > 5000) {
                errorMessages.push("Описание должно быть не более 5000 символов");
            }
            if (selectedVoters.length < 3) {
                errorMessages.push("В собрании должно участвовать минимум 3 человека");
            }
            if (currentDate.getTime() >= endDate.getTime()) {
                errorMessages.push("Дата окончания собрания должна быть больше текущей даты");
            }
            if (currentDate.getTime() >= registrationEndDate.getTime()) {
                errorMessages.push("Дата окончания регистрации в собрании должна быть больше текущей даты");
            }
            if (registrationEndDate.getTime() >= endDate.getTime()) {
                errorMessages.push("Дата окончания регистрации в собрании должна быть больше даты окончания собрания");
            }
            if (errorMessages.length > 0) {
                bootbox.alert(errorMessages.join("<br/>"));
                return null;
            }
        }

        currentBatchVoting.quorum = quorum;
        currentBatchVoting.votingRestartCount = votingRestartCount;
        var result = jQuery.extend(true, {}, currentBatchVoting);
        result.votersAllowed = [];

        for (var index in currentBatchVoting.votersAllowed) {
            var voterId = currentBatchVoting.votersAllowed[index];
            var voterAllowed = {
                voterId: voterId,
                isSignProtocol: selectedSignProtocolMap[voterId] == null ? true : selectedSignProtocolMap[voterId]
            };
            result.votersAllowed.push(voterAllowed);
        }

        result.startDate = toServerDate(startDate).format(shortTimeDateFormat);
        result.endDate = toServerDate(endDate).format(shortTimeDateFormat);
        result.registrationEndDate = toServerDate(registrationEndDate).format(shortTimeDateFormat);
        return result;
    }

    function initTemplates() {
        voterTemplate = $("#voterTemplate").html();
        attributesTemplate = $("#attributesTemplate").html();
        votingsTemplate = $("#votingsTemplate").html();
        candidatesTemplate = $("#candidatesTemplate").html();
        votingItemsTemplate = $("#votingItemsTemplate").html();
        votingOptionTemplate = $("#votingOptionTemplate").html();
        constructorPageTemplate = $("#constructorPageTemplate").html();
        possibleVotersTemplate = $("#possibleVotersTemplate").html();
        possibleCandidatesTemplate = $("#possibleCandidatesTemplate").html();

        Mustache.parse(possibleCandidatesTemplate);
        Mustache.parse(possibleVotersTemplate);
        Mustache.parse(voterTemplate);
        Mustache.parse(attributesTemplate);
        Mustache.parse(votingsTemplate);
        Mustache.parse(candidatesTemplate);
        Mustache.parse(votingOptionTemplate);
        Mustache.parse(votingItemsTemplate);
        Mustache.parse(constructorPageTemplate);
    }

    var shortTimeDateFormatForMoment = "DD.MM.yyyy HH:mm";
    var shortTimeDateFormat = "dd.mm.yyyy HH:MM";

    var upButtonSpinner = null;
    var downButtonSpinner = null;

    function upSpinner(jqSpinner) {
        var maxValue = parseInt(jqSpinner.attr("max"));
        var currentValue = parseInt($('input', jqSpinner).val(), 10);
        if (maxValue == currentValue || currentValue > maxValue) {
            $('input', jqSpinner).val(maxValue);
            currentBatchVoting[$('input', jqSpinner).attr("id")] = $('input', jqSpinner).val();
            return false;
        }
        $('input', jqSpinner).val(currentValue + 1);
        $('input', jqSpinner).select();
        currentBatchVoting[$('input', jqSpinner).attr("id")] = $('input', jqSpinner).val();
        formIsDirty = true;
    }
    function downSpinner(jqSpinner) {
        var minValue = parseInt(jqSpinner.attr("min"));
        var currentValue = parseInt($('input', jqSpinner).val(), 10);
        if (minValue == currentValue || currentValue < minValue) {
            $('input', jqSpinner).val(minValue);
            currentBatchVoting[$('input', jqSpinner).attr("id")] = $('input', jqSpinner).val();
            return false;
        }
        $('input', jqSpinner).val(currentValue - 1);
        $('input', jqSpinner).select();
        currentBatchVoting[$('input', jqSpinner).attr("id")] = $('input', jqSpinner).val();
        formIsDirty = true;
    }
    function initControlsValidation() {
        $("#subjectMinSymbols").hide();
        //filterLettersOnly($("#subject"));
        filterSubject($("#subject"), $("#subjectMinSymbols"), 10, 128, $("#subjectForm"), "has-error");
        filterDigitsOnly($("#quorum"));
        filterDigitsOnly($("#minSelectionCount"));
        filterDigitsOnly($("#maxSelectionCount"));
        filterDigitsOnly($("#minWinnersCount"));
        filterDigitsOnly($("#maxWinnersCount"));

        filterDiapason($("#quorum"), 1, 100);
        filterDiapason($("#minSelectionCount"), 1, 1000);
        filterDiapason($("#maxSelectionCount"), 1, 1000);
        filterDiapason($("#minWinnersCount"), 1, 1000);
        filterDiapason($("#maxWinnersCount"), 1, 1000);

        initControlsValidationModal();
    }
    function filterLettersOnly($node, $nodeError, $form, errorClass) {
        $node.keypress(function (e) {
            if ((e.which == 8) || (e.which == 46) || (e.which == 0)) {
                return true;
            } else {
                var txt = String.fromCharCode(e.which);
                if (!txt.match(/[A-Za-z0-9А-Яа-я0-9ёЁ\.\,\-\!\?\=/\t /]/)) {
                    return false;
                }
            }
        });
    }
    function filterSubject($node, $nodeError, minSymbols, maxSymbols, $form, errorClass) {
        initInputDirty($node);
        $node.keyup(function () {
            setInputDirty($node);
        });
        setInterval(function () {
            var isDirty = getInputDirty($node);
            if (isDirty) {
                var text = $node.val() == null ? "" : $node.val();
                var errorLetter = false;
                if (!text.match(/^[A-Za-z0-9А-Яа-я0-9ёЁ'"""\.\,\-\!\?\=/\t /]*$/)) {
                    errorLetter = true
                }
                var errorLength = text.length < minSymbols || text.length > maxSymbols;
                var hasError = errorLetter || errorLength;
                if ($form) {
                    if (hasError && !$form.hasClass(errorClass)) {
                        $form.addClass(errorClass);
                        $nodeError.show();
                    } else if (!hasError && $form.hasClass(errorClass)) {
                        $form.removeClass(errorClass);
                        $nodeError.hide();
                    }
                }
            } else if ($form) {
                $form.removeClass(errorClass);
                $nodeError.hide();
            }
        }, 100);
    }
    function initControlsValidationModal() {
        //filterLettersOnly($("#votingSubject"));
        filterSubject($("#votingSubject"), $("#votingSubjectMinSymbols"), 10, 128, $("#votingSubjectForm"), "has-error");
    }
    function filterDigitsOnly($node) {
        $node.keypress(function (e) {
            if ((e.which == 8) || (e.which == 0)) {
                return true;
            }
            var txt = String.fromCharCode(e.which);
            if (!txt.match(/[0-9]/)) {
                return false;
            }
        });
    }
    function filterDiapason($node, min, max) {
        $node.on('input', function (e) {
            var digit = parseInt($node.val());
            if (!isNaN(digit)) {
                if (digit < min) {
                    $node.val(min);
                } else if (max != null && digit > max) {
                    $node.val(max);
                }
            }
        });
    }
    function initControls() {
        initControlsValidation();
        setInterval(function () {
            if (upButtonSpinner != null) {
                upSpinner(upButtonSpinner);
            } else if (downButtonSpinner != null) {
                downSpinner(downButtonSpinner);
            }
        }, 100);

        $('.spinner .btn:first-of-type').on('mousedown', function () {
            upButtonSpinner = $(this).closest(".spinner");
        });
        $('.spinner .btn:first-of-type').on('mouseup', function () {
            upButtonSpinner = null;
        });
        $('.spinner .btn:last-of-type').on('mousedown', function () {
            downButtonSpinner = $(this).closest(".spinner");
        });
        $('.spinner .btn:last-of-type').on('mouseup', function () {
            downButtonSpinner = null;
        });
        $('.spinner input').on('mousewheel', function (event) {
            event.preventDefault();
            event.stopPropagation();
            var jqSpinner = $(this).closest(".spinner");
            if (event.originalEvent.wheelDelta / 120 > 0) {
                upSpinner(jqSpinner);
            }
            else {
                downSpinner(jqSpinner);
            }
            return false;
        });

        $("#votingDescription").val("");
        $("#votingDescription").css("height", "200px").radomTinyMCE({});
        $("#votingSentence").val("");
        $("#votingSentence").css("height", "200px").radomTinyMCE({});
        $("#votingSuccessDecree").val("");
        $("#votingSuccessDecree").css("height", "200px").radomTinyMCE({});
        $("#votingFailDecree").val("");
        $("#votingFailDecree").css("height", "200px").radomTinyMCE({});


        $("#description").val(currentBatchVoting.description);
        $("#description").css("height", "200px").radomTinyMCE({});
        $("#isNeedCreateChat").prop("checked", currentBatchVoting.isNeedCreateChat);
        if (!currentBatchVoting.isNeedCreateChat) {
            $("#addChatToProtocol").prop("disabled", true);
        }
        if (currentBatchVoting.quorum == null || currentBatchVoting.quorum == "") {
            currentBatchVoting.quorum = 51;
        }

        $("#subject").val(currentBatchVoting.subject);
        $("#quorum").val(currentBatchVoting.quorum);

        $("#isCanFinishBeforeEndDate").prop("checked", currentBatchVoting.isCanFinishBeforeEndDate);
        $("#votingRestartCount").selectpicker("refresh");
        $("#votingRestartCount").selectpicker("val", currentBatchVoting.votingRestartCount);
        $("#isNeedAddAdditionalVotings").prop("checked", currentBatchVoting.isNeedAddAdditionalVotings);
        $("#useBiometricIdentificationInAdditionalVotings").prop("checked", currentBatchVoting.useBiometricIdentificationInAdditionalVotings);
        $("#addChatToProtocol").prop("checked", currentBatchVoting.addChatToProtocol);
        $("#useBiometricIdentificationInRegistration").prop("checked", currentBatchVoting.useBiometricIdentificationInRegistration);
        $("#testBatchVotingMode").prop("checked", currentBatchVoting.testBatchVotingMode);

        var startDate = createFormattedDate(currentBatchVoting.startDate, shortTimeDateFormatForMoment);
        var endDate = createFormattedDate(currentBatchVoting.endDate, shortTimeDateFormatForMoment);
        var registrationEndDate = createFormattedDate(currentBatchVoting.registrationEndDate, shortTimeDateFormatForMoment);

        var nowDate = createDate();
        if (startDate.getTime() < nowDate.getTime()) {
            startDate = nowDate;
        }
        console.log(currentBatchVoting.startDate);

        $("#startDate").radomDateTimeInput({
            defaultDate: startDate, minDate: nowDate
        }, function (date) {
            var newRegistrationMinDate = new Date(date);
            newRegistrationMinDate.setHours(newRegistrationMinDate.getHours() + 1);
            $("#registrationEndDate").data("DateTimePicker").minDate(newRegistrationMinDate);
            var currentRegistrationEndDate = moment($("#registrationEndDate").val(), shortTimeDateFormatForMoment).toDate();

            if (currentRegistrationEndDate < newRegistrationMinDate) {
                $("#registrationEndDate").data("DateTimePicker").date(newRegistrationMinDate);
            }

            currentBatchVoting.startDate = date.format(shortTimeDateFormat);
            formIsDirty = true;
        });

        var minRegDate = new Date(startDate);
        minRegDate.setHours(minRegDate.getHours() + 1);
        if (minRegDate.getTime() > registrationEndDate.getTime()) {
            minRegDate = new Date(registrationEndDate.getTime());
        }
        $("#registrationEndDate").radomDateTimeInput({
            defaultDate: registrationEndDate, minDate: minRegDate, useCurrent: false
        }, function (date) {
            var newMinEndDate = new Date(date);
            newMinEndDate.setHours(newMinEndDate.getHours() + 1);
            $("#endDate").data("DateTimePicker").minDate(newMinEndDate);
            var currentEndDate = moment($("#endDate").val(), shortTimeDateFormatForMoment).toDate();
            if (currentEndDate < newMinEndDate) {
                $("#endDate").data("DateTimePicker").date(newMinEndDate);
            }
            currentBatchVoting.registrationEndDate = date.format(shortTimeDateFormat);
            formIsDirty = true;
        });
        var minEndDate = new Date(registrationEndDate);
        minEndDate.setHours(minEndDate.getHours() + 1);
        $("#endDate").radomDateTimeInput({
            defaultDate: endDate, minDate: minEndDate, useCurrent: false
        }, function (date) {
            currentBatchVoting.endDate = date.format(shortTimeDateFormat);
            formIsDirty = true;
        });

        $("#batchVotingHoursCount").val(currentBatchVoting.batchVotingHoursCount);
        //  $("#registrationHoursCount").val(currentBatchVoting.registrationHoursCount);

        /*$('#dateRange').radomDateRangeInput({
         startDate: startDate,
         endDate: endDate,
         minDate: minDate
         }, function(startDate, endDate) {
         currentBatchVoting.startDate = startDate.format(shortTimeDateFormat);
         currentBatchVoting.endDate = endDate.format(shortTimeDateFormat);
         formIsDirty = true;
         });
         var votersRegistrationEndDate = moment(currentBatchVoting.votersRegistrationEndDate, shortTimeDateFormatForMoment).toDate();
         $("#votersRegistrationEndDate").radomDateTimeInput({
         defaultDate : votersRegistrationEndDate
         }, function(date){
         currentBatchVoting.votersRegistrationEndDate = date.format(shortTimeDateFormat);
         formIsDirty = true;
         });*/

        //$("#behavior").selectpicker("refresh");
        //$("#behavior").selectpicker("val", currentBatchVoting.behavior);

        $("#mode").selectpicker("refresh");
        $("#modeDescription").text(modeDescriptions["SEQUENTIAL"]);
        $("#mode").selectpicker("val", "SEQUENTIAL");

        $("#secretVoting").selectpicker("refresh");
        $("#secretVotingDescription").text(secretDescriptions[currentBatchVoting.secretVoting]);
        $("#secretVoting").selectpicker("val", currentBatchVoting.secretVoting.toString());

        //$("#secretVoting").val(currentBatchVoting.secretVoting);

        // Изменение поля тема собрания
        $("#subject").on("input", function () {
            currentBatchVoting.subject = $(this).val();
            formIsDirty = true;
        });
        //
        $("#isNeedCreateChat").click(function () {
            if ($(this).prop("checked")) {
                currentBatchVoting.isNeedCreateChat = true;
                $("#addChatToProtocol").removeProp("disabled");
            } else {
                currentBatchVoting.isNeedCreateChat = false;
                $("#addChatToProtocol").prop("disabled", true);
            }
            formIsDirty = true;
        });
        //
        $("#batchVotingHoursCount").on("input", function () {
            currentBatchVoting.batchVotingHoursCount = $(this).val();
            formIsDirty = true;
        });
        //
        /*  $("#registrationHoursCount").on("input", function() {
         currentBatchVoting.registrationHoursCount = $(this).val();
         formIsDirty = true;
         });*/

        //
        $("#mode").change(function () {
            if (($(this).val() === "PARALLEL") && (interviewExists())) {
                currentBatchVoting.mode = null;
                $("#mode").selectpicker("refresh");
                $("#mode").selectpicker("val", null);
                bootbox.alert("Параллельное голосование для интервью не поддерживается!");
            }
            else {
                currentBatchVoting.mode = $(this).val();
                $("#modeDescription").text(modeDescriptions[$(this).val()]);
                formIsDirty = true;
            }
        });
        //
        $("#quorum").on("input", function () {
            currentBatchVoting.quorum = $(this).val();
            formIsDirty = true;
        });
        //
        $("#isCanFinishBeforeEndDate").click(function () {
            currentBatchVoting.isCanFinishBeforeEndDate = $(this).prop("checked");
            formIsDirty = true;
        });
        $("#isShowSubgroupsUsers").click(function () {
            includeSubgroups = $(this).prop("checked");
            refreshPossibleVoters();
        });
        $("#addAllPossibleVoters").click(function () {
            setModelAllVoters();
        });
        //
        $("#secretVoting").change(function () {
            currentBatchVoting.secretVoting = $(this).val() === "true" ? true : false;
            $("#secretVotingDescription").text(secretDescriptions[currentBatchVoting.secretVoting]);
            formIsDirty = true;
        });
        //
        $("#votingRestartCount").change(function () {
            currentBatchVoting.votingRestartCount = $(this).val();
            formIsDirty = true;
        });

        $("#isNeedAddAdditionalVotings").click(function () {
            currentBatchVoting.isNeedAddAdditionalVotings = $(this).prop("checked");
            if (!currentBatchVoting.isNeedAddAdditionalVotings) {
                if ($(".userSignProtocol:checked").length == 0 ||
                        ($(".userSignProtocol[voter_id=" + currentUserId + "]").is(":checked") && $(".userSignProtocol:checked").length == 1)) {
                    $(".userSignProtocol[voter_id=" + currentUserId + "]").prop("disabled", true);
                    $(".userSignProtocol[voter_id=" + currentUserId + "]").prop("checked", true);
                    selectedSignProtocolMap[currentUserId] = true;
                }
            } else {
                $(".userSignProtocol[voter_id=" + currentUserId + "]").prop("disabled", false);
            }

            formIsDirty = true;
        });

        $("#useBiometricIdentificationInAdditionalVotings").click(function () {
            currentBatchVoting.useBiometricIdentificationInAdditionalVotings = $(this).prop("checked");
            formIsDirty = true;
        });
        $("#addChatToProtocol").click(function () {
            currentBatchVoting.addChatToProtocol = $(this).prop("checked");
            formIsDirty = true;
        });

        $("#useBiometricIdentificationInRegistration").click(function () {
            currentBatchVoting.useBiometricIdentificationInRegistration = $(this).prop("checked");
            formIsDirty = true;
        });

        $("#testBatchVotingMode").click(function () {
            currentBatchVoting.testBatchVotingMode = $(this).prop("checked");
            formIsDirty = true;
        });

        $("#multipleWinnersMinMaxBlock").hide();
        $("#multipleWinners").change(function () {
            if ($(this).is(":checked")) {
                $("#multipleWinnersMinMaxBlock").show();
            } else {
                $("#multipleWinnersMinMaxBlock").hide();
            }
        });

        $("#percentForWin").spinner({
            min: 1,
            max: 100
        });

        var onChangeMinSelectionCount = function() {
            setTimeout(function(){
                if ($("#maxSelectionCount").val() != null && $("#maxSelectionCount").val() != "") {
                    var maxSelectionCnt = parseInt($("#maxSelectionCount").val());
                    var minSelectionCnt = parseInt($("#minSelectionCount").val());
                    if (maxSelectionCnt < minSelectionCnt) {
                        $("#maxSelectionCount").spinner( "value", minSelectionCnt );
                    }
                }
            }, 100);
        };
        var onChangeMaxSelectionCount = function() {
            setTimeout(function(){
                if ($("#minSelectionCount").val() != null && $("#minSelectionCount").val() != "") {
                    var maxSelectionCnt = parseInt($("#maxSelectionCount").val());
                    var minSelectionCnt = parseInt($("#minSelectionCount").val());
                    if (maxSelectionCnt < minSelectionCnt) {
                        $("#minSelectionCount").spinner( "value", maxSelectionCnt );
                    }
                }
            }, 100);
        };

        var onChangeMinWinnersCount = function() {
            setTimeout(function(){
                if ($("#maxWinnersCount").val() != null && $("#maxWinnersCount").val() != "") {
                    var maxWinnersCnt = parseInt($("#maxWinnersCount").val());
                    var minWinnersCnt = parseInt($("#minWinnersCount").val());
                    if (maxWinnersCnt < minWinnersCnt) {
                        $("#maxWinnersCount").spinner( "value", minWinnersCnt );
                    }
                }
            }, 100);
        }
        var onChangeMaxWinnersCount = function() {
            setTimeout(function(){
                if ($("#minWinnersCount").val() != null && $("#minWinnersCount").val() != "") {
                    var maxWinnersCnt = parseInt($("#maxWinnersCount").val());
                    var minWinnersCnt = parseInt($("#minWinnersCount").val());
                    if (maxWinnersCnt < minWinnersCnt) {
                        $("#minWinnersCount").spinner( "value", maxWinnersCnt );
                    }
                }
            }, 100);
        }

        $("#minSelectionCount").spinner({
            min: 1,
            max: 1000,
            numberFormat: "n",
            spin: onChangeMinSelectionCount
        });
        $("#minSelectionCount").on('input', function(){
            onChangeMinSelectionCount();
        });
        $("#maxSelectionCount").spinner({
            min: 1,
            max: 1000,
            numberFormat: "n",
            spin: onChangeMaxSelectionCount
        });
        $("#maxSelectionCount").on('input', function(){
            onChangeMaxSelectionCount();
        });

        $("#minWinnersCount").spinner({
            min: 1,
            max: 1000,
            numberFormat: "n",
            spin: onChangeMinWinnersCount
        });
        $("#minWinnersCount").on('input', function(){
            onChangeMinWinnersCount();
        });
        $("#maxWinnersCount").spinner({
            min: 1,
            max: 1000,
            numberFormat: "n",
            spin: onChangeMaxWinnersCount
        });
        $("#maxWinnersCount").on('input', function(){
            onChangeMaxWinnersCount();
        });



        $("body").on("click", ".userSignProtocol", function () {
            var voterId = $(this).attr("voter_id");
            selectedSignProtocolMap[voterId] = $(this).is(":checked");
            if (!currentBatchVoting.isNeedAddAdditionalVotings && ($(".userSignProtocol:checked").length == 0 ||
                    ($(".userSignProtocol[voter_id=" + currentUserId + "]").is(":checked") && $(".userSignProtocol:checked").length == 1))) {
                $(".userSignProtocol[voter_id=" + currentUserId + "]").prop("disabled", true);
                $(".userSignProtocol[voter_id=" + currentUserId + "]").prop("checked", true);
                selectedSignProtocolMap[currentUserId] = true;
            } else {
                $(".userSignProtocol[voter_id=" + currentUserId + "]").prop("disabled", false);
            }
        });

        selectedVoters = defaultVoters;
        // Нужно выбрать участников из комбобокса

        for (var i in currentBatchVoting.votersAllowed) {
            var voterId = currentBatchVoting.votersAllowed[i];
            if (currentUserId != voterId) {
                if (possibleVotersMap[voterId] != null) {
                    var jqOption = $("option[value=" + voterId + "]", $("#possibleVoters"));
                    var text = jqOption.text();
                    jqOption.prop("disabled", true);
                    addSelectedVoterInModel(possibleVotersMap[voterId], selectedSignProtocolMap[voterId]);
                }
            }
        }

        refreshSelectedVotersView();
        refreshVotingsView();
    }

    function addBatchVotingAttribute(attrName, attrValue) {
        var foundAttribute = null;
        for (var i in currentBatchVoting.attributes) {
            var batchVotingAttribute = currentBatchVoting.attributes[i];
            if (batchVotingAttribute.name == attrName) {
                foundAttribute = batchVotingAttribute;
            }
        }
        if (foundAttribute == null) {
            currentBatchVoting.attributes.push({
                name: attrName,
                value: attrValue
            })
        } else {
            foundAttribute.value = attrValue;
        }
    }

    function removeBatchVotingAttribute(attrName) {
        var result = [];
        for (var i in currentBatchVoting.attributes) {
            var batchVotingAttribute = currentBatchVoting.attributes[i];
            if (batchVotingAttribute.name != attrName) {
                result.push(batchVotingAttribute);
            }
        }
        currentBatchVoting.attributes = result;
    }

    function getBatchVotingAttributes() {
        for (var i in currentBatchVoting.attributes) {
            var batchVotingAttribute = currentBatchVoting.attributes[i];
            batchVotingAttribute["index"] = parseInt(i) + 1;
        }
        return currentBatchVoting.attributes;
    }
    function interviewExists() {
        for (var i in currentBatchVoting.votings) {
            if (currentBatchVoting.votings[i].votingType === "INTERVIEW") {
                return true;
            }
        }
        return false;
    }
    function refreshBatchVotingAttributesView() {
        var rendered = Mustache.render(attributesTemplate, {attributes: getBatchVotingAttributes()});
        $("#attributes").find("tbody").empty();
        $("#attributes").find("tbody").append(rendered);
    }

    function refreshPossibleVoters() {
        var possibleVoters = [];
        for (var i in possibleVotersMap) {
            if ((includeSubgroups) || (!childGroupsVotersMap[possibleVotersMap[i].id])) {
                possibleVoters.push(possibleVotersMap[i]);
            }
        }
        var rendered = Mustache.render(possibleVotersTemplate, {
            possibleVoters: possibleVoters,
            votersNeedBeVerified: votersNeedBeVerified
        });
        var renderedCandidates = Mustache.render(possibleCandidatesTemplate, {
            possibleCandidates: possibleVoters,
            votersNeedBeVerified: votersNeedBeVerified
        });
        $("#possibleVoters").html(rendered);
        $("#possibleCandidates").html(renderedCandidates);
        refreshSelectedVotersView();
    }

    function addSelectedVoterInModel(voter, isSignProtocol) {
        isSignProtocol = isSignProtocol == null ? true : isSignProtocol;
        var needAddVoter = true;
        for (var index in selectedVoters) {
            var selectedVoter = selectedVoters[index];
            if (selectedVoter.id == voter.id) {
                needAddVoter = false;
                break;
            }
        }
        if (needAddVoter && (!votersNeedBeVerified || votersNeedBeVerified && voter.verified)) {
            selectedVoters.push({
                name: voter.fullName,
                id: voter.id,
                link: voter.link,
                avatar: voter.avatar,
                avatarSmall: voter.avatarSmall,
                handleVoter: true,
                isSignProtocol: isSignProtocol
            });
            selectedSignProtocolMap[voter.id] = isSignProtocol;
        }
    }

    function setModelAllVoters() {
        selectedVoters = defaultVoters;
        for (var i in possibleVotersMap) {
            if (includeSubgroups || !childGroupsVotersMap[possibleVotersMap[i].id]) {
                var voter = possibleVotersMap[i];
                var jqOption = $("#possibleVoters").find("option[value=" + voter.id + "]");
                jqOption.prop("disabled", true);
                addSelectedVoterInModel(voter);
            }
        }
        refreshSelectedVotersView();
    }

    function removeSelectedVoterFromModel(voterId) {
        var result = [];
        for (var index in selectedVoters) {
            var voter = selectedVoters[index];
            if (voter.id != voterId) {
                result.push(voter);
            }
        }
        selectedVoters = result;
    }

    function getSelectedVotersModel() {
        var index = 1;
        currentBatchVoting.votersAllowed = [];
        for (var i in selectedVoters) {
            var voter = selectedVoters[i];
            currentBatchVoting.votersAllowed.push(voter.id);
            voter["index"] = index++;
            voter["voterSignProtocol"] = selectedSignProtocolMap[voter.id];
        }
        return selectedVoters;
    }

    function refreshSelectedVotersView() {
        var rendered = Mustache.render(voterTemplate, {selectedVoters: getSelectedVotersModel()});
        $("#selectedVoters").find("tbody").empty();
        $("#selectedVoters").find("tbody").append(rendered);

        $(".userSignProtocolLabel", $("#selectedVoters")).radomTooltip({
            delay: {
                show: 500,
                hide: 0
            },
            title: "Если Вы хотите, чтобы участник подписал своей ЭЦП итоговый протокол собрания, отметьте эту галочку",
            html: true,
            container: "body"
        });

        $("#possibleVoters").selectpicker("refresh");
        $("#possibleVoters").selectpicker("val", null);
    }

    function addVoting(subject, description, sentence, successDecree, failDecree, votingType, isVoteCancellable, notRestartable, isVoteCommentsAllowed, minSelectionCount, maxSelectionCount,
                       minWinnersCount, maxWinnersCount,
                       stopBatchVotingOnFailResult, percentForWin, index, isVisible, votingItems, attributes, multipleWinners, useBiometricIdentification, skipResults,addAbstain) {

        var foundVoting = null;

        for (var i in currentBatchVoting.votings) {
            var voting = currentBatchVoting.votings[i];

            if (voting.index == index) {
                foundVoting = voting;
                break;
            }
        }

        if (foundVoting != null) {
            foundVoting.addAbstain = addAbstain;
            foundVoting.subject = subject;
            foundVoting.description = description;
            foundVoting.sentence = sentence;
            foundVoting.successDecree = successDecree;
            foundVoting.failDecree = failDecree;
            foundVoting.votingType = votingType;
            foundVoting.isVoteCancellable = isVoteCancellable;
            foundVoting.notRestartable = notRestartable;
            foundVoting.isVoteCommentsAllowed = isVoteCommentsAllowed;
            foundVoting.minSelectionCount = minSelectionCount;
            foundVoting.maxSelectionCount = maxSelectionCount;
            foundVoting.minWinnersCount = minWinnersCount;
            foundVoting.maxWinnersCount = maxWinnersCount;
            foundVoting.stopBatchVotingOnFailResult = stopBatchVotingOnFailResult;
            foundVoting.percentForWin = percentForWin;
            foundVoting.index = index;
            foundVoting.isVisible = isVisible;
            foundVoting.votingItems = votingItems;
            foundVoting.attributes = attributes;
            foundVoting.votingState = "NEW";
            foundVoting.multipleWinners = multipleWinners;
            foundVoting.skipResults = skipResults;
            foundVoting.useBiometricIdentification = useBiometricIdentification;
        } else {
            console.log(votingItems);
            currentBatchVoting.votings.push({
                addAbstain: addAbstain,
                subject: subject,
                description: description,
                sentence: sentence,
                successDecree: successDecree,
                failDecree: failDecree,
                votingType: votingType,
                isVoteCancellable: isVoteCancellable,
                notRestartable: notRestartable,
                isVoteCommentsAllowed: isVoteCommentsAllowed,
                minSelectionCount: minSelectionCount,
                maxSelectionCount: maxSelectionCount,
                minWinnersCount: minWinnersCount,
                maxWinnersCount: maxWinnersCount,
                stopBatchVotingOnFailResult: stopBatchVotingOnFailResult,
                percentForWin: percentForWin,
                index: index,
                isVisible: isVisible,
                votingItems: votingItems,
                attributes: attributes,
                votingState: "NEW",
                multipleWinners: multipleWinners,
                skipResults: skipResults,
                useBiometricIdentification: useBiometricIdentification
            });
        }
    }

    function getVotingByIndex(index) {
        index = parseInt(index);
        var result = null;
        var foundIndex = -1;
        for (var i in currentBatchVoting.votings) {
            var voting = currentBatchVoting.votings[i];
            if (parseInt(voting.index) == index) {
                result = voting;
                foundIndex = parseInt(voting.index);
                break;
            }
        }
        return {voting: result, index: foundIndex};
    }

    function removeVoting(index) {
        index = parseInt(index);
        var result = [];
        var votingToDelete = null;
        for (var i in currentBatchVoting.votings) {
            var voting = currentBatchVoting.votings[i];
            if (parseInt(voting.index) != index) {
                if (parseInt(voting.index) > index) {
                    voting.index = parseInt(voting.index) - 1;
                }
                result.push(voting);
            }
            else {
                votingToDelete = voting;
            }
        }
        if (votingToDelete) {
            for (var i in currentBatchVoting.votings) {
                var voting = currentBatchVoting.votings[i];
                var selectFromType = getVotingAttributeValue(voting.attributes, "selectFromType");
                var sourceVotingIndex = getVotingAttributeValue(voting.attributes, "sourceVotingIndex");
                if (selectFromType != "addMine" && sourceVotingIndex) {
                    if (votingToDelete.index == parseInt(sourceVotingIndex)) {
                        bootbox.alert("Вы пытаетесть удалить интервью, от результатов которого зависит собрание по теме: " + voting.subject);
                        return;
                    }
                }
            }
        }
        currentBatchVoting.votings = result;
    }
    function getVotingAttributeValue(attributes, key) {
        for (var i in attributes) {
            if (attributes[i].name === key) {
                return attributes[i].value;
            }
        }
    }
    function setVotingAttributeValue(voting, name, value) {
        if (!voting.attributes) {
            voting.attributes = [];
        }
        for (var i in voting.attributes) {
            if (voting.attributes[i].name === name) {
                voting.attributes[i].value = value;
            }
            return;
        }
        voting.attributes.push({name: name, value: value, id: null});
    }
    function getVotings() {
        // Сортируем по индексу
        currentBatchVoting.votings.sort(function (a, b) {
            var result = -1;
            if (a.index > b.index) {
                result = 1;
            }
            return result;
        });
        // Фиксим кривые индексы
        for (var i in currentBatchVoting.votings) {
            var voting = currentBatchVoting.votings[i];
            voting.index = parseInt(i) + 1;
        }

        for (var i in currentBatchVoting.votings) {
            var voting = currentBatchVoting.votings[i];
            voting["index_table"] = parseInt(i) + 1;
            voting["votingTypeName"] = votingTypes[voting["votingType"]];


            if (currentBatchVoting.votings.length == 1) {
                voting["showUpButton"] = false;
                voting["showDownButton"] = false;
            } else if (i == 0) {
                voting["showUpButton"] = false;
                voting["showDownButton"] = true;
            } else if (i == currentBatchVoting.votings.length - 1) {
                voting["showUpButton"] = true;
                voting["showDownButton"] = false;
            } else {
                voting["showUpButton"] = true;
                voting["showDownButton"] = true;
            }
        }
        return currentBatchVoting.votings;
    }

    function refreshVotingsView() {
        var rendered = Mustache.render(votingsTemplate, {votings: getVotings()});
        $("#votings").find("tbody").empty();
        $("#votings").find("tbody").append(rendered);
    }

    function setCandidateVoting(voting, isVisible) {
        isVisible = isVisible == true ? true : false;
        $("#possibleCandidates option").prop("disabled", false);
        if (voting == null) {
            $("#candidateVariants").empty();
            $("#possibleCandidates").selectpicker("refresh");
            $("#possibleCandidates").selectpicker("val", null);
        } else {
            for (var i in currentVotingItems) {
                var candidate = currentVotingItems[i];
                var jqOption = $("option[value=" + candidate.value + "]", $("#possibleCandidates"));
                jqOption.prop("disabled", true);
                addCandidate(possibleVotersMap[jqOption.val()]);
            }
            refreshCandidatesView();
        }
        if (isVisible) {
            $("#candidateParameters").show();
            $("#multipleSelectionParameters").show();
            $("#multipleWinnersBlock").show();
            $("#votingParameters").show();
            refreshSelectFromWinnersOtherVotings(getVotings(), "CANDIDATE", voting);
            refreshSelectFromLosersOtherVotings(getVotings(), "CANDIDATE", voting);
        } else {
            $("#candidateParameters").hide();
            $("#multipleSelectionParameters").hide();
            $("#multipleWinnersBlock").hide();
            $("#votingParameters").hide();
        }
    }

    function setProContraAbstainVoting(voting, isVisible) {
        isVisible = isVisible == true ? true : false;
        if (isVisible) {
            $("#votingParameters").show();
        } else {
            $("#votingParameters").hide();
        }
    }
    function setMultipleSelectionVoting(voting, isVisible) {
        isVisible = isVisible == true ? true : false;

        if (voting == null) {
            $("#multipleSelectionVariants").empty();
            //$("#minSelectionCount").val("1");
            //$("#maxSelectionCount").val("1");
        } else {
            refreshVotingItemsView();
        }

        if (isVisible) {
            $("#votingItemsParameters").show();
            $("#multipleSelectionParameters").show();
            $("#multipleWinnersBlock").show();
            $("#votingParameters").show();
            refreshSelectFromWinnersOtherVotings(getVotings(), "MULTIPLE_SELECTION", voting);
            refreshSelectFromLosersOtherVotings(getVotings(), "MULTIPLE_SELECTION", voting);
        } else {
            $("#votingItemsParameters").hide();
            $("#multipleSelectionParameters").hide();
            $("#multipleWinnersBlock").hide();
            $("#votingParameters").hide();
        }
    }

    function setSingleSelectionVoting(voting, isVisible) {
        isVisible = isVisible == true ? true : false;
        if (voting == null) {
            $("#votingItems").empty();
        } else {
            refreshVotingItemsView();
        }
        if (isVisible) {
            $("#votingItemsParameters").show();
            $("#singleSelectionParameters").show();
            $("#votingParameters").show();
            refreshSelectFromWinnersOtherVotings(getVotings(), "SINGLE_SELECTION", voting);
            refreshSelectFromLosersOtherVotings(getVotings(), "SINGLE_SELECTION", voting);
        } else {
            $("#votingItemsParameters").hide();
            $("#singleSelectionParameters").hide();
            $("#votingParameters").hide();
        }
    }

    function setInterviewVoting(voting, isVisible) {
        isVisible = isVisible == true ? true : false;

        if (voting == null) {
            $("#interviewVariants").empty();
        }

        if (isVisible) {
            $("#interviewParameters").show();
        } else {
            $("#interviewParameters").hide();
        }
    }

    function initVotingType(voting, votingType) {
        // Инициализация типа голосования
        var intVotingTypeEmptyFunctions = {
            setCandidateVoting: function () {
                setCandidateVoting(null, false);
            },
            setProContraAbstainVoting: function () {
                setProContraAbstainVoting(null, false);
            },
            setMultipleSelectionVoting: function () {
                setMultipleSelectionVoting(null, false);
            },
            setSingleSelectionVoting: function () {
                setSingleSelectionVoting(null, false);
            },
            setInterviewVoting: function () {
                setInterviewVoting(null, false);
            }
        };
        var initVotingTypeExistsFunction = null;
        switch (votingType) {
            case "PRO_CONTRA":
                initVotingTypeExistsFunction = setProContraAbstainVoting;
                delete intVotingTypeEmptyFunctions["setProContraAbstainVoting"];
                break;
            case "CANDIDATE":
                initVotingTypeExistsFunction = setCandidateVoting;
                delete intVotingTypeEmptyFunctions["setCandidateVoting"];
                break;
            case "INTERVIEW":
                initVotingTypeExistsFunction = setInterviewVoting;
                delete intVotingTypeEmptyFunctions["setInterviewVoting"];
                break;
            case "SINGLE_SELECTION":
                initVotingTypeExistsFunction = setSingleSelectionVoting;
                delete intVotingTypeEmptyFunctions["setSingleSelectionVoting"];
                break;
            case "MULTIPLE_SELECTION":
                initVotingTypeExistsFunction = setMultipleSelectionVoting;
                delete intVotingTypeEmptyFunctions["setMultipleSelectionVoting"];
                break;
        }
        for (var funcName in intVotingTypeEmptyFunctions) {
            var initFunction = intVotingTypeEmptyFunctions[funcName];
            initFunction();
        }
        initVotingTypeExistsFunction(voting, true);
    }

    function initNewVoting() { // Очистить форму для нового голосования
        refreshSelectFromInterviewView(getVotings(), null);
        $("#votingSubject").val("");
        $("#votingDescription").val("");
        $("#votingSentence").val("");
        $("#votingSuccessDecree").val("");
        $("#votingFailDecree").val("");

        //$("#votingDescription").css("height", "200px").radomTinyMCE({});

        $("#votingType").selectpicker("refresh");
        $("#votingType").selectpicker("val", null);

        $("#isVoteCancellable").prop("checked", true);
        $("#notRestartable").prop("checked", false);
        $("#isVoteCommentsAllowed").prop("checked", false);
        $("#isAddAbstain").prop("checked", false);
        $("#isVisible").prop("checked", true);
        $("#multipleWinners").prop("checked", false).trigger('change');
        $("#skipResults").prop("checked", false);
        $("input[type='radio'][name='variantSelectType'][value='genFromInterview']").removeProp("checked");
        $("input[type='radio'][name='variantSelectType'][value='genFromWinnersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='variantSelectType'][value='genFromLosersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='variantSelectType'][value='addMine']").prop("checked", true).trigger('change');

        $("input[type='radio'][name='candidateVariantSelectType'][value='genFromWinnersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='candidateVariantSelectType'][value='genFromLosersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='candidateVariantSelectType'][value='addMine']").prop("checked", true).trigger('change');

        $("#useBiometricIdentification").prop("checked", false);
        $("#stopBatchVotingOnFailResult").prop("checked", true);
        $("#percentForWin").val("51");
        setCandidateVoting(null, false);
        setProContraAbstainVoting(null, false);
        setMultipleSelectionVoting(null, false);
        setSingleSelectionVoting(null, false);
        setInterviewVoting(null, false);
    }

    function initExistsVoting(voting) {
        refreshSelectFromInterviewView(getVotings(), voting.index);
        $("#votingSubject").val(voting.subject);
        $("#votingDescription").val(voting.description);
        $("#votingSentence").val(voting.sentence);
        $("#votingSuccessDecree").val(voting.successDecree);
        $("#votingFailDecree").val(voting.failDecree);
        //$("#votingDescription").css("height", "200px").radomTinyMCE({});

        $("#votingType").selectpicker("refresh");
        $("#votingType").selectpicker("val", voting.votingType);

        $("#isVoteCancellable").prop("checked", voting.isVoteCancellable);
        $("#notRestartable").prop("checked", voting.notRestartable);
        $("#isVoteCommentsAllowed").prop("checked", voting.isVoteCommentsAllowed);
        $("#isAddAbstain").prop("checked", voting.addAbstain);
        $("#isVisible").prop("checked", voting.isVisible);
        $("#multipleWinners").prop("checked", voting.multipleWinners).trigger('change');
        $("#skipResults").prop("checked", voting.skipResults);
        $("#useBiometricIdentification").prop("checked", voting.useBiometricIdentification);
        $("#stopBatchVotingOnFailResult").prop("checked", voting.stopBatchVotingOnFailResult);
        if (voting.percentForWin == null || voting.percentForWin == "") {
            voting.percentForWin = 51;
        }
        $("#percentForWin").val(voting.percentForWin);
        $("#minSelectionCount").val(voting.minSelectionCount);
        $("#maxSelectionCount").val(voting.maxSelectionCount);
        $("#minWinnersCount").val(voting.minWinnersCount);
        $("#maxWinnersCount").val(voting.maxWinnersCount);
        $("input[type='radio'][name='variantSelectType'][value='genFromInterview']").removeProp("checked");
        $("input[type='radio'][name='variantSelectType'][value='genFromWinnersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='variantSelectType'][value='genFromLosersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='variantSelectType'][value='addMine']").prop("checked", true).trigger('change');

        $("input[type='radio'][name='candidateVariantSelectType'][value='genFromWinnersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='candidateVariantSelectType'][value='genFromLosersOtherVoting']").removeProp("checked");
        $("input[type='radio'][name='candidateVariantSelectType'][value='addMine']").prop("checked", true).trigger('change');

        var selectFromType = getVotingAttributeValue(voting.attributes, 'selectFromType');
        var sourceVotingIndex = getVotingAttributeValue(voting.attributes, 'sourceVotingIndex');
        selectFromType = selectFromType == null ? 'addMine' : selectFromType;
        $("input[type='radio'][name='variantSelectType'][value='" + selectFromType + "']").prop("checked", true).trigger('change');
        $("input[type='radio'][name='candidateVariantSelectType'][value='" + selectFromType + "']").prop("checked", true).trigger('change');

        var sourceSuffix = "";
        if (voting.votingType == 'CANDIDATE') {
            sourceSuffix = "Candidate";
        }

        var $selectFrom = null;
        switch(selectFromType) {
            case 'genFromInterview':
                $selectFrom = $("#interviewsSelectFrom");
                break;
            case 'genFromWinnersOtherVoting':
                $selectFrom = $("#selectFromWinnersOtherVoting" + sourceSuffix);
                break;
            case 'genFromLosersOtherVoting':
                $selectFrom = $("#selectFromLosersOtherVoting" + sourceSuffix);
                break;
        }
        if ($selectFrom != null) {
            $selectFrom.selectpicker("refresh");
            $selectFrom.val(sourceVotingIndex);
            $selectFrom.selectpicker("refresh");
        }
        initVotingType(voting, voting.votingType);
    }

    function addAllCandidates() {
        currentVotingItems = [];
        for (var i in possibleVotersMap) {
            if (includeSubgroups || !childGroupsVotersMap[possibleVotersMap[i].id]) {
                var candidate = possibleVotersMap[i];
                var jqOption = $("#possibleCandidates").find("option[value=" + candidate.id + "]");
                jqOption.prop("disabled", true);

                addCandidate(candidate);
            }
        }
        refreshCandidatesView();
    }

    function addCandidate(user) {
        var foundCandidate = null;
        for (var i in currentVotingItems) {
            var candidate = currentVotingItems[i];
            if (candidate.value == user.id) {
                foundCandidate = candidate;
                break;
            }
        }
        if (foundCandidate == null) {
            currentVotingItems.push({
                value: user.id,
                name: user.fullName,
                avatar: user.avatar,
                avatarSmall: user.avatarSmall,
                link: user.link
            });
        }
    }
    function removeCandidate(id) {
        if (currentVotingItems != null) {
            var result = [];
            for (var i in currentVotingItems) {
                var candidate = currentVotingItems[i];
                if (candidate.value != id) {
                    result.push(candidate);
                }
            }
            currentVotingItems = result;
        }
    }
    function getCandidates() {
        if (currentVotingItems == null) {
            currentVotingItems = [];
        }
        for (var i in currentVotingItems) {
            var candidate = currentVotingItems[i];
            var value = candidate["value"];
            var candidateUser = possibleVotersMap[parseInt(candidate["value"])];
            candidate["index"] = parseInt(i) + 1;
            candidate.name = candidateUser.fullName;
            candidate.fullName = candidateUser.fullName;
            candidate.avatar = candidateUser.avatar;
            candidate.avatarSmall = Images.getResizeUrl(candidate.avatar, "c48");
            currentVotingItems[i] = candidate;
        }
        return currentVotingItems;
    }
    function refreshCandidatesView() {
        var rendered = Mustache.render(candidatesTemplate, {candidates: getCandidates()});
        console.log(getCandidates());
        $("#candidateVariants").empty();
        $("#candidateVariants").append(rendered);

        $("#possibleCandidates").selectpicker("refresh");
        $("#possibleCandidates").selectpicker("val", null);
    }

    function addVotingItem(value) {
        currentVotingItems.push({
            value: value
        });
    }
    function updateVotingItem(index, value) {
        currentVotingItems[index].value = value;
    }
    function removeVotingItem(value) {
        if (currentVotingItems != null) {
            var result = [];
            for (var i in currentVotingItems) {
                var votingItem = currentVotingItems[i];
                if (votingItem.value != value) {
                    result.push(votingItem);
                }
            }
            currentVotingItems = result;
        }
    }
    function getVotingItems() {
        if (currentVotingItems == null) {
            currentVotingItems = [];
        }

        for (var i in currentVotingItems) {
            var votingItem = currentVotingItems[i];
            votingItem["index"] = parseInt(i) + 1;
        }
        return currentVotingItems;
    }
    function refreshVotingItemsView() {
        var rendered = Mustache.render(votingItemsTemplate, {votingItems: getVotingItems()});
        $("#votingItems").empty();
        $("#votingItems").append(rendered);
    }
    function refreshSelectFromInterviewView(votings, index) {
        var votingsInterview = [];
        for (var i in votings) {
            if ((votings[i].votingType === 'INTERVIEW') && ((!index) || (votings[i].index < index))) {
                votingsInterview.push(votings[i]);
            }
        }
        var rendered = Mustache.render(votingOptionTemplate, {votings: votingsInterview});
        $("#interviewsSelectFrom").html(rendered);
        $("#interviewsSelectFrom").selectpicker("refresh");
        $("#interviewsSelectFrom").selectpicker("val", null);
    }
    function refreshSelectFromWinnersOtherVotings(votings, votingType, voting) {
        var index = voting == null ? 1000000 : voting.index;

        var selectedIndex = null;
        if (voting != null) {
            var selectFromType = getVotingAttributeValue(voting.attributes, "selectFromType");
            if (selectFromType == "genFromWinnersOtherVoting") {
                selectedIndex = getVotingAttributeValue(voting.attributes, "sourceVotingIndex");
            }
        }
        var sourceVotings = [];
        if (votingType == "SINGLE_SELECTION" || votingType == "MULTIPLE_SELECTION") {
            for (var i in votings) {
                var voting = votings[i];
                if ((voting.votingType == 'SINGLE_SELECTION' || voting.votingType == 'MULTIPLE_SELECTION') && voting.multipleWinners === true && ((!index) || (voting.index < index))) {
                    sourceVotings.push(voting);
                }
            }
        } else if (votingType == "CANDIDATE") {
            for (var i in votings) {
                var voting = votings[i];
                if ((voting.votingType == 'CANDIDATE') && voting.multipleWinners === true && ((!index) || (voting.index < index))) {
                    sourceVotings.push(voting);
                }
            }
        }

        var sourceSuffix = "";
        if (votingType == 'CANDIDATE') {
            sourceSuffix = "Candidate";
        }

        var rendered = Mustache.render(votingOptionTemplate, {votings: sourceVotings});
        $("#selectFromWinnersOtherVoting" + sourceSuffix).html(rendered);
        $("#selectFromWinnersOtherVoting" + sourceSuffix).selectpicker("refresh");
        $("#selectFromWinnersOtherVoting" + sourceSuffix).selectpicker("val", selectedIndex);
    }
    function refreshSelectFromLosersOtherVotings(votings, votingType, voting) {
        var index = voting == null ? 1000000 : voting.index;
        var selectedIndex = null;
        if (voting != null) {
            var selectFromType = getVotingAttributeValue(voting.attributes, "selectFromType");
            if (selectFromType == "genFromLosersOtherVoting") {
                selectedIndex = getVotingAttributeValue(voting.attributes, "sourceVotingIndex");
            }
        }
        var sourceVotings = [];
        if (votingType == "SINGLE_SELECTION" || votingType == "MULTIPLE_SELECTION") {
            for (var i in votings) {
                var voting = votings[i];
                if ((voting.votingType == 'SINGLE_SELECTION' || voting.votingType == 'MULTIPLE_SELECTION') && ((!index) || (voting.index < index))) {
                    sourceVotings.push(voting);
                }
            }
        } else if (votingType == "CANDIDATE") {
            for (var i in votings) {
                var voting = votings[i];
                if ((voting.votingType == 'CANDIDATE') && ((!index) || (voting.index < index))) {
                    sourceVotings.push(voting);
                }
            }
        }

        var sourceSuffix = "";
        if (votingType == 'CANDIDATE') {
            sourceSuffix = "Candidate";
        }

        var rendered = Mustache.render(votingOptionTemplate, {votings: sourceVotings});
        $("#selectFromLosersOtherVoting" + sourceSuffix).html(rendered);
        $("#selectFromLosersOtherVoting" + sourceSuffix).selectpicker("refresh");
        $("#selectFromLosersOtherVoting" + sourceSuffix).selectpicker("val", selectedIndex);
    }
    //----


    function initIntInput(jqInput) {
        jqInput.on("keypress", function (event) {
            var inputValue = $(this).val();
            if (inputValue == "0") {
                return false;
            }
            var regxp = RegExp('[0-9]');
            return regxp.test(String.fromCharCode(event.charCode));
        });
    }

    function loadConstructorPageData(communityId, callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/constructor_batch_voting_page_data.json",
                {
                    template_id: templateId
                },
                callBack
        );
    }

    function saveDraftTemplate(batchVotingTemplateDraft, callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/save_batch_voting_template_draft.json",
                JSON.stringify(batchVotingTemplateDraft),
                callBack,
                null,
                {
                    contentType: 'application/json'
                }
        );
    }

    /*function initDescriptionArea() {
        var descriptionEditor = tinymce.EditorManager.get("description");
        currentBatchVoting.description = descriptionEditor.getContent();
        descriptionEditor.on('keyup', function (e) {
            currentBatchVoting.description = descriptionEditor.getContent();
        });
    }*/

    $(document).ready(function () {
        $(eventManager).bind("inited", function (event, currentUser) {
            loadConstructorPageData(communityId, function (constructorPageData) {
                currentConstructorPageData = constructorPageData;
                loadedCurrentUser = currentUser;
                initCommunityHead(constructorPageData.community);
                initCommunityMenu(constructorPageData.community);
                initConstructorPage(constructorPageData, currentUser);
            });
        });
    });

    function initInputDirty($input) {
        $input.prop("is_dirty", false);
    }

    function setInputDirty($input) {
        $input.prop("is_dirty", true);
    }

    function getInputDirty($input) {
        return $input.prop("is_dirty");
    }

    function showHelpDialog(helpHtmlText) {
        $("#helpTextBlock").html(helpHtmlText);
        $("#helpTextWindow").modal("show");
    }

    function showSentenceHelpText() {
        showHelpDialog(sentenceHelpText);
    }

    function showSuccessDecreeHelpText() {
        showHelpDialog(successDecreeHelpText);
    }

    function showFailDecreeHelpText() {
        showHelpDialog(failDecreeHelpText);
    }

    function initConstructorPage(constructorPageData, currentUser) {
        initTemplates();

        votersNeedBeVerified = constructorPageData.votersNeedBeVerified;

        var model = constructorPageData;
        model.votersNeedBeVerified = votersNeedBeVerified;
        model.possibleVoters.push(currentUser);
        for (var i in  model.possibleVoters) {
            if (model.possibleVoters[i].id == currentUser.id) {
                model.possibleVoters[i].currentUser = true;
            } else {
                model.possibleVoters[i].currentUser = false;
            }
            model.possibleVoters[i].avatarSmall = Images.getResizeUrl(model.possibleVoters[i].avatar, "c48");
            model.possibleVoters[i].name = model.possibleVoters[i].fullName;
            possibleVotersMap[model.possibleVoters[i].id] = model.possibleVoters[i];
        }
        for (var i in model.childGroupsVoters) {
            childGroupsVotersMap[model.childGroupsVoters[i].id] = model.childGroupsVoters[i];
        }
        model.batchVotingTemplateExists = constructorPageData.batchVotingTemplate != null;
        var modesArr = [];
        for (var modeName in constructorPageData.modes) {
            var mode = {
                name: modeName,
                value: constructorPageData.modes[modeName]
            };
            modesArr.push(mode);
        }
        constructorPageData.modesArr = modesArr;

        var votingTypesArr = [];
        for (var typeName in constructorPageData.votingTypes) {
            var votingType = {
                name: typeName,
                value: constructorPageData.votingTypes[typeName]
            };
            votingTypesArr.push(votingType);
        }
        constructorPageData.votingTypesArr = votingTypesArr;

        model.isSavedTemplatePage = templateId != null && templateId != "";

        var markup = Mustache.render(constructorPageTemplate, model);
        $("#candidatePageBlock").append(markup);

        if (templateId) {
            $('div#create-from-template-info').hide();
        }

        $('button#set-start-date-to-current').click(function () {
            var $startDate = $('input#startDate');
            var $registrationEndDate = $('input#registrationEndDate');
            var $endDate = $('input#endDate');

            var startDate = $startDate.data("DateTimePicker").date().toDate();
            var registrationEndDate = $registrationEndDate.data("DateTimePicker").date().toDate();
            var endDate = $endDate.data("DateTimePicker").date().toDate();

            var registrationDateOffset = registrationEndDate.getTime() - startDate.getTime();
            var endDateOffset = endDate.getTime() - registrationEndDate.getTime();
            var currentDate = createDate();

            $startDate.data("DateTimePicker").date(createDate());

            if (registrationDateOffset > 0) {
                registrationEndDate = new Date(currentDate.getTime() + registrationDateOffset);
                $registrationEndDate.data("DateTimePicker").date(registrationEndDate);
            }

            if (endDateOffset > 0) {
                $endDate.data("DateTimePicker").date(new Date(registrationEndDate.getTime() + endDateOffset));
            }
        });

        refreshPossibleVoters();

        initParameters(constructorPageData, currentUser);

        // Изменение данных в описании собрания
        /*var tinyMceInterval = setInterval(function () {
            if (window.tinymce != null && window.tinymce.EditorManager != null && window.tinymce.EditorManager.get("description") != null) {
                initDescriptionArea();
                clearInterval(tinyMceInterval);
            }
        }, 100);*/

        // Добавить участника в собрание
        $("#addPossibleVoterButton").click(function () {
            var jqOption = $("#possibleVoters").find("option:selected");
            if (jqOption.length == 0) {
                bootbox.alert("Необходимо выбрать участника.");
                return false;
            }
            jqOption.prop("disabled", true);
            addSelectedVoterInModel(possibleVotersMap[parseInt(jqOption.val())]);
            refreshSelectedVotersView();
            formIsDirty = true;
        });

        // Удалить участника
        $("body").on("click", ".removeVoter", function () {
            var jqRow = $(this).closest("tr");
            var voterId = jqRow.attr("voter_id");
            selectedSignProtocolMap[voterId] = true;
            $("#possibleVoters option[value=" + voterId + "]").prop("disabled", false);
            removeSelectedVoterFromModel(voterId);
            refreshSelectedVotersView();
            formIsDirty = true;
        });

        // Добавить атрибут
        $("#addBatchVotingAttributeButton").click(function () {
            $("#batchVotingAttributeTextLabel").text("Создать атрибут");
            $("#batchVotingAttributeWindow input").val("");
            $("#saveAttrButton").text("Добавить");
            $("#batchVotingAttributeWindow").modal("show");
        });
        // Сохранить атрибут
        $("#saveAttrButton").click(function () {
            $("#batchVotingAttributeWindow").modal("hide");
            var attrName = $("#attr_name", $("#batchVotingAttributeWindow")).val();
            var attrValue = $("#attr_value", $("#batchVotingAttributeWindow")).val();
            addBatchVotingAttribute(attrName, attrValue);
            refreshBatchVotingAttributesView();
            formIsDirty = true;
        });
        // Удалить атрибут
        $("body").on("click", ".removeAttribute", function () {
            var jqRow = $(this).closest("tr");
            var attrName = jqRow.attr("attr_name");
            removeBatchVotingAttribute(attrName);
            refreshBatchVotingAttributesView();
            formIsDirty = true;
        });
        // Редактировать атрибут
        $("body").on("click", ".editBatchVotingAttribute", function () {
            var jqRow = $(this).closest("tr");
            var attrName = $(".attr_name", jqRow).attr("attr_name");
            var attrValue = $(".attr_value", jqRow).attr("attr_value");

            $("#batchVotingAttributeTextLabel").text("Редактировать атрибут");
            $("#attr_name", $("#batchVotingAttributeWindow")).val(attrName);
            $("#attr_value", $("#batchVotingAttributeWindow")).val(attrValue);
            $("#saveAttrButton").text("Сохранить");
            $("#batchVotingAttributeWindow").modal("show");
        });

        // Добавить голосование
        $("#addVotingButton").click(function () {
            initNewVoting();
            currentVotingItems = [];
            currentVotingIndex = currentBatchVoting.votings.length + 1;
            $("#votingWindow").modal("show");
            initInputDirty($("#votingSubject"));
        });

        $("#votingWindow").on("shown.bs.modal", function () {
            $("#votingSubject").focus();
        });

        // Сохранить голосование
        $("#saveVotingButton").click(function () {
            var successDecree = $("#votingSuccessDecree").val();
            var failDecree = $("#votingFailDecree").val();
            var errorMessage = '';

            if (!successDecree) errorMessage += 'Поле "Постановление голосования в случае успешного завершения" не заполнено.<br>';
            else if (!failDecree) errorMessage += 'Поле "Постановление голосования в случае неудачного завершения" не заполнено.<br>';

            if (errorMessage) {
                errorMessage += 'Постановление не будет включено в протокол собрания.';

                bootbox.confirm(errorMessage, function (result) {
                    if (result) saveVoting();
                });
            } else {
                saveVoting();
            }
        });

        function saveVoting() {
            var subject = $("#votingSubject").val();
            var description = $("#votingDescription").val();
            var sentence = $("#votingSentence").val();
            var successDecree = $("#votingSuccessDecree").val();
            var failDecree = $("#votingFailDecree").val();
            var votingType = $("#votingType").val();
            var isVoteCancellable = $("#isVoteCancellable").prop("checked");
            var notRestartable = $("#notRestartable").prop("checked");
            var useBiometricIdentification = $("#useBiometricIdentification").prop("checked");
            var isVoteCommentsAllowed = $("#isVoteCommentsAllowed").prop("checked");
            var addAbstain = $("#isAddAbstain").prop("checked");
            var multipleWinners = $("#multipleWinners").prop("checked");

            var minSelectionCount = 1;
            var maxSelectionCount = 1;
            var minWinnersCount = null;
            var maxWinnersCount = null;

            if ((votingType == "MULTIPLE_SELECTION") || (votingType == "CANDIDATE")) {
                minSelectionCount = $("#minSelectionCount").val();
                maxSelectionCount = $("#maxSelectionCount").val();
                if (multipleWinners) {
                    minWinnersCount = $("#minWinnersCount").val();
                    maxWinnersCount = $("#maxWinnersCount").val();
                }
            }

            var stopBatchVotingOnFailResult = $("#stopBatchVotingOnFailResult").prop("checked");
            var percentForWin = $("#percentForWin").val();
            var index = parseInt(currentVotingIndex);
            var isVisible = $("#isVisible").prop("checked");
            var votingItems = currentVotingItems;
            var attributes = []; // TODO

            var skipResults = $("#skipResults").prop("checked");
            var selectFromType = $("input[type='radio'][name='variantSelectType']:checked").val();

            var sourceVotingIndex = -1;

            var sourceSuffix = "";
            if (votingType == 'CANDIDATE') {
                sourceSuffix = "Candidate";
                selectFromType = $("input[type='radio'][name='candidateVariantSelectType']:checked").val();
            }

            switch (selectFromType) {
                case "addMine":
                    break;
                case "genFromInterview":
                    sourceVotingIndex = $("#interviewsSelectFrom").val()
                    break;
                case "genFromWinnersOtherVoting":
                    sourceVotingIndex = $("#selectFromWinnersOtherVoting" + sourceSuffix).val()
                    break;
                case "genFromLosersOtherVoting":
                    sourceVotingIndex = $("#selectFromLosersOtherVoting" + sourceSuffix).val()
                    break;
            }

            var errorMessages = [];

            if (subject == null || subject == "") {
                errorMessages.push("Не указана тема голосования");
            }
            if (subject.length < 10) {
                errorMessages.push("Тема голосования должна состоять минимум из 10 символов!");
            }
            if (description == null || description == "") {
                errorMessages.push("Не указано описание голосования");
            }
            if (description.length < 128) {
                errorMessages.push("Описание должно состоять минимум из 128 символов!");
            }
            if (description.length > 5000) {
                errorMessages.push("Описание должно быть не более 5000 символов!");
            }
            if (votingType == null || votingType == "") {
                errorMessages.push("Не указан тип голосования");
            }
            if (votingType != null && (((votingType === "SINGLE_SELECTION" || votingType === "MULTIPLE_SELECTION") && (selectFromType === "addMine")) || (votingType === "CANDIDATE" && selectFromType === "addMine")) &&
                    (votingItems == null || votingItems.length == 0)) {
                errorMessages.push("Не указаны варианты голосования");
            }
            if ((votingType != null) && (votingType === "SINGLE_SELECTION" || votingType === "MULTIPLE_SELECTION") && (selectFromType != "addMine") && (!sourceVotingIndex)) {
                errorMessages.push("Не указано интервью или голосование из которого генерируются варианты");
            }

            if (minSelectionCount < 1) {
                errorMessages.push("Минимальное количество выбранных вариантов не может быть меньше 1");
            }
            if (maxSelectionCount < 1) {
                errorMessages.push("Максимальное количество выбранных вариантов не может быть меньше 1");
            }
            if (maxSelectionCount < minSelectionCount) {
                errorMessages.push("Максимальное количество выбранных вариантов не может быть меньше минимального количества");
            }
            if (minWinnersCount != null && minWinnersCount < 1) {
                errorMessages.push("Минимальное количество победителей не может быть меньше 1");
            }
            if (maxWinnersCount != null && maxWinnersCount < 1) {
                errorMessages.push("Максимальное количество победителей не может быть меньше 1");
            }
            if (maxWinnersCount != null && maxWinnersCount != "" && minWinnersCount != null && maxWinnersCount < minWinnersCount) {
                errorMessages.push("Максимальное количество победителей не может быть меньше минимального количества");
            }

            if (minSelectionCount > minSelectionCount) {
                errorMessages.push("Минимальное количество выбранных вариантов не может быть больше максимального");
            }
            if (!$.isNumeric(percentForWin)) {
                errorMessages.push("Параметр \"Процент для победы\" должен быть целым числом");
            }
            percentForWin = parseInt(percentForWin);
            if (percentForWin < 1 || percentForWin > 100) {
                errorMessages.push("Значение параметра \"Процент для победы\" должен быть в диапазоне от 1 до 100");
            }

            setInputDirty($("#votingSubject"));

            if (errorMessages.length > 0) {
                var messageString = errorMessages.join("<br/>");
                bootbox.alert(messageString);
                return false;
            }

            attributes.push({name: 'selectFromType', value: selectFromType, id: null});
            attributes.push({name: 'sourceVotingIndex', value: sourceVotingIndex, id: null});

            addVoting(
                    subject,
                    description,
                    sentence,
                    successDecree,
                    failDecree,
                    votingType,
                    isVoteCancellable,
                    notRestartable,
                    isVoteCommentsAllowed,
                    minSelectionCount,
                    maxSelectionCount,
                    minWinnersCount,
                    maxWinnersCount,
                    stopBatchVotingOnFailResult,
                    percentForWin,
                    index,
                    isVisible,
                    votingItems,
                    attributes,
                    multipleWinners,
                    useBiometricIdentification,
                    skipResults,
                    addAbstain
            );

            $("#votingWindow").modal("hide");
            refreshVotingsView();
            formIsDirty = true;
        }

        // Редактировать голосование
        $("body").on("click", ".editVoting", function () {
            var jqRow = $(this).closest("tr");
            var index = parseInt(jqRow.attr("index"));
            var votingParams = getVotingByIndex(index);
            if (votingParams.voting != null) {
                currentVotingIndex = votingParams.index;
            }
            currentVotingItems = votingParams.voting.votingItems;
            currentVotingAttributes = votingParams.voting.attributes;

            initExistsVoting(votingParams.voting);

            $("#votingWindow").modal("show");
            initInputDirty($("#votingSubject"));
        });
        // Удалить голосование
        $("body").on("click", ".removeVoting", function () {
            var jqRow = $(this).closest("tr");
            var index = parseInt(jqRow.attr("index"));
            removeVoting(index);
            refreshVotingsView();
            formIsDirty = true;
        });
        $("body").on("click", ".visibleVoting", function () {
            var isVisible = $(this).hasClass("glyphicon-eye-open");
            isVisible = !isVisible;
            var index = parseInt($(this).attr("index"));
            var searchVoting = getVotingByIndex(index);

            if (searchVoting.voting != null) {
                var voting = searchVoting.voting;
                voting.isVisible = isVisible;
                refreshVotingsView();
                formIsDirty = true;
            }
            return false;
        });
        // Сортировка голосований
        $("body").on("click", ".upVoting", function () {
            var jqRow = $(this).closest("tr");
            var index = parseInt(jqRow.attr("index"));
            for (var i in currentBatchVoting.votings) {
                var voting = currentBatchVoting.votings[i];
                if (voting.index == index) {
                    var newFirstVotingIndex = parseInt(index) - 1;
                    var newSecondVotingIndex = parseInt(index);
                    if (!(validateSourceVotingIndexes(voting, newFirstVotingIndex, currentBatchVoting.votings[parseInt(i) - 1], newSecondVotingIndex))) {
                        bootbox.alert("Интервью или голосование от которого зависит голосование не может быть позже голосования!");
                        return;
                    }
                    updateSourceVotingIndexes(voting, newFirstVotingIndex, currentBatchVoting.votings[parseInt(i) - 1], newSecondVotingIndex);
                    voting.index = parseInt(index) - 1;
                    currentBatchVoting.votings[parseInt(i) - 1]["index"] = index;
                    break;
                }
            }
            refreshVotingsView();
            formIsDirty = true;
        });
        $("body").on("click", ".downVoting", function () {
            var jqRow = $(this).closest("tr");
            var index = parseInt(jqRow.attr("index"));
            for (var i in currentBatchVoting.votings) {
                var voting = currentBatchVoting.votings[i];
                if (voting.index == index) {
                    var newFirstVotingIndex = parseInt(index) + 1;
                    var newSecondVotingIndex = parseInt(index);
                    if (!(validateSourceVotingIndexes(voting, newFirstVotingIndex, currentBatchVoting.votings[parseInt(i) + 1], newSecondVotingIndex))) {
                        bootbox.alert("Интервью или голосование от которого зависит голосование не может быть позже голосования!");
                        return;
                    }
                    updateSourceVotingIndexes(voting, newFirstVotingIndex, currentBatchVoting.votings[parseInt(i) + 1], newSecondVotingIndex);
                    voting.index = parseInt(index) + 1;
                    currentBatchVoting.votings[parseInt(i) + 1]["index"] = index;
                    break;
                }
            }
            refreshVotingsView();
            formIsDirty = true;
        });

        // Сменить тип голосования
        $("#votingType").on("change", function () {
            var votingType = $("#votingType").val();
            $("#voteTypeDescription").text(votingTypeDescriptions[votingType]);
            currentVotingItems = [];
            initVotingType(null, votingType);
        });
        // Добавить кандидата в голосование
        $("#addPossibleCandidateButton").click(function () {
            var jqOption = $("#possibleCandidates").find("option:selected");
            if (jqOption.length == 0) {
                bootbox.alert("Необходимо выбрать участника.");
                return false;
            }
            jqOption.prop("disabled", true);

            addCandidate(possibleVotersMap[parseInt(jqOption.val())]);
            refreshCandidatesView();
        });
        $("input[name='variantSelectType']").change(function () {
            var sourceVotingVariants = $(this).attr('value');
            var sourceVotingVariantsBlock = sourceVotingVariants + 'Block';
            $(".sourceVotingVariantsBlock").hide();
            $("#" + sourceVotingVariantsBlock).show();
        });
        $("input[name='candidateVariantSelectType']").change(function () {
            var sourceVotingVariants = $(this).attr('value');
            var sourceVotingVariantsBlock = sourceVotingVariants + 'CandidateBlock';
            $(".sourceCandidateVotingVariantsBlock").hide();
            $("#" + sourceVotingVariantsBlock).show();
        });
        $("#addAllPossibleCandidates").click(function () {
            addAllCandidates();
        });
        // Удалить кандидата из голосования
        $("body").on("click", ".removeCandidate", function () {
            var jqRow = $(this).closest("tr");
            var candidateId = jqRow.attr("candidate_id");
            $("#possibleCandidates option[value=" + candidateId + "]").prop("disabled", false);
            removeCandidate(candidateId);
            refreshCandidatesView();
        });

        // Добавить вариант голосования
        $("#addVotingItemButton").click(function () {
            var votingItemValue = $("#votingItemValue").val();
            if (votingItemValue == null || votingItemValue == "") {
                bootbox.alert("Необходимо указать текст варианта голосования.");
                return false;
            }
            $("#votingItemValue").val("");
            addVotingItem(votingItemValue);
            refreshVotingItemsView();
        });
        // Редактирование варианта голосования
        $("body").on("click", ".editVotingItem", function () {
            $(this).hide();
            var jqRow = $(this).closest("tr");
            $(".editVotingItemContainer", jqRow).show();
        });
        // Обновить вариант голосования
        $("body").on("click", ".updateVotingItem", function () {
            var jqRow = $(this).closest("tr");
            var newVotingItemValue = $("input", jqRow).val();
            var index = parseInt(jqRow.attr("index")) - 1;
            updateVotingItem(index, newVotingItemValue);
            refreshVotingItemsView();
        });
        // Отмена редактирования варианта голосования
        $("body").on("click", ".cancelUpdateVotingItem", function () {
            refreshVotingItemsView();
        });
        // Удалить вариант голосования
        $("body").on("click", ".removeVotingItem", function () {
            var jqRow = $(this).closest("tr");
            var votingItemValue = jqRow.attr("voting_item_value");
            removeVotingItem(votingItemValue);
            refreshVotingItemsView();
        });

        // Запустить собрание
        $("#startBatchVotingButton").click(function () {
            checkDecrees(function() {
                setInputDirty($("#subject"));
                var createBatchVotingRequest = getCreateBatchVotingRequest();

                if (createBatchVotingRequest == null) return false;

                if (currentBatchVoting.id != null && formIsDirty) {
                    var confirmText = "Вы хотите сохранить это собрание как шаблон?<br/>" +
                            "Если Вы сохраните это собрание как шаблон, то в дальнейшем вы сможете использовать этот шаблон \"" + currentBatchVoting.subject + "\" для создания новых собраний с такими-же параметрами.";

                    bootbox.confirm(confirmText, function (result) {
                        if (result) {
                            saveAndStartBatchVotingTemplate(createBatchVotingRequest, true, function () {
                                formIsDirty = false;
                            });
                        } else {
                            startBatchVoting(createBatchVotingRequest, function () {
                            });
                        }
                    });
                } else {
                    startBatchVoting(createBatchVotingRequest, function () {
                    });
                }
            });
        });

        // Сохранить и запустить собрание
        $("#saveAndStartBatchVotingButton").click(function () {
            checkDecrees(function() {
                var confirmText = getSaveConfirmText();
                setInputDirty($("#subject"));
                var createBatchVotingRequest = getCreateBatchVotingRequest();

                if (createBatchVotingRequest == null) return false;

                var saveAndStartBatchVoting = function (withMessageTemplate) {
                    saveAndStartBatchVotingTemplate(createBatchVotingRequest, withMessageTemplate, function (response) {
                        formIsDirty = false;
                    });
                };

                if (templateId != null && templateId != "") {
                    saveAndStartBatchVoting(createBatchVotingRequest, false);
                } else {
                    bootbox.confirm(
                            confirmText,
                            function (result) {
                                if (result) {
                                    saveAndStartBatchVoting(createBatchVotingRequest, true);
                                }
                            }
                    );
                }
            });
        });

        function checkDecrees(callback) {
            var errorMessage = '';

            for (var i = 0; i < currentBatchVoting.votings.length; i++) {
                var voting = currentBatchVoting.votings[i];
                if (!voting.successDecree) errorMessage += 'В голосовании \"' + voting.subject + '\" не заполнено поле "Постановление голосования в случае успешного завершения".<br>';
                else if (!voting.failDecree) errorMessage += 'В голосовании \"' + voting.subject + '\" не заполнено поле "Постановление голосования в случае неудачного завершения".<br>';

                if (errorMessage) break;
            }

            if (errorMessage) {
                errorMessage += 'Постановление не будет включено в протокол собрания.';

                bootbox.confirm(errorMessage, function (result) {
                    if (result) callback();
                });
            } else {
                callback();
            }
        }

        // Сохранить собрание
        $("#saveBatchVotingTemplateButton").click(function () {
            var createBatchVotingRequest = getCreateBatchVotingRequest();
            if (createBatchVotingRequest == null) {
                return false;
            }
            var saveVotingTemplate = function (createBatchVotingRequest, withMessage) {
                setInputDirty($("#subject"));
                saveBatchVotingTemplate(createBatchVotingRequest, withMessage, function (response) {
                    formIsDirty = false;
                });
            };

            var confirmText = getSaveConfirmText();

            if (templateId != null && templateId != "") {
                saveVotingTemplate(createBatchVotingRequest, false);
            } else {
                bootbox.confirm(
                        confirmText,
                        function (result) {
                            if (result) {
                                saveVotingTemplate(createBatchVotingRequest, true);
                            }
                        }
                );
            }
        });


        // Сохранить черновик шаблона
        if (templateId == null || templateId == "") {
            var saveDraft = function () {
                var createBatchVotingRequest = getCreateBatchVotingRequest(false);
                if (createBatchVotingRequest == null) {
                    return false;
                }
                saveDraftTemplate(createBatchVotingRequest, function (response) {
                    var dateNow = new Date();
                    var formattedDate = dateNow.format("dd.mm.yyyy HH:MM:ss");
                    $("#lastDateSaveDraft").html("Дата последнего сохранения черновика: " + formattedDate);
                });
            };
            $("#saveDraftTemplateButton").click(function () {
                saveDraft();
            });
            $("#clearDraftTemplateButton").click(function () {
                bootbox.confirm("Очистить черновик шаблона собрания?", function (result) {
                    if (result) {
                        initDefaultParameters(currentConstructorPageData, loadedCurrentUser);
                        initControls();
                        saveDraft();
                    }
                });
            });
            setInterval(function () {
                saveDraft();
            }, 60000);
        }


       $("#sentenceHelpTextBtn").click(function(){
          showSentenceHelpText();
       });
        $("#successDecreeHelpTextBtn").click(function(){
            showSuccessDecreeHelpText();
        });
        $("#failDecreeHelpTextBtn").click(function(){
            showFailDecreeHelpText();
        });


        initControls();
        initSelectTemplatesGrid(seoLink);
        $("#subject").focus();
    }
</script>

<t:insertAttribute name="communityHeader"/>
<h2>Конструктор собраний</h2>
<hr/>
<div id="candidatePageBlock"></div>

<script id="possibleVotersTemplate" type="x-tmpl-mustache">
    {{#possibleVoters}}
        {{^currentUser}}
            <option value="{{id}}" {{#votersNeedBeVerified}}{{^verified}}disabled="disabled"{{/verified}}{{/votersNeedBeVerified}}>{{fullName}}</option>
        {{/currentUser}}
    {{/possibleVoters}}



</script>
<script id="possibleCandidatesTemplate" type="x-tmpl-mustache">
    {{#possibleCandidates}}
        <option value="{{id}}">{{fullName}}</option>
    {{/possibleCandidates}}



</script>
<script id="constructorPageTemplate" type="x-tmpl-mustache">
    <h3>
        {{#isSavedTemplatePage}}
            Редактирование шаблона собрания
        {{/isSavedTemplatePage}}
        {{^isSavedTemplatePage}}
            Создание электронного собрания
        {{/isSavedTemplatePage}}
    </h3>
    <div>
        <div class="form-group">
            <div class="alert alert-info alert-dismissible" role="alert" style="text-align : justify;">
                <div>
                    Вы создаете новое электронное собрание внутри объединения <a href="{{community.link}}">{{community.name}}</a>.
                    Участниками собрания могут быть только действительные члены этого объединения и его дочерних подразделений.
                    (<a href="https://blagosfera.su/Благосфера/Конструктор%20собраний%20(инструкция)" target="_blank">Руководство по созданию нового электронного собрания</a>).
                </div>
                <div style="margin-top: 10px; overflow: hidden;" id="create-from-template-info">
                    <div>
                        Вы можете создать новое электронное собрание из уже существующего шаблона.
                        Чтобы создать новый шаблон, необходимо сначала создать собрание, а потом сохранить его как шаблон.

                        <button type="button" class="btn btn-primary" id="fullFillFromTemplate">Создать из шаблона</button>
                    </div>
                    <div style="clear: both;"></div>
                </div>
            </div>

        </div>
        <div class="form-group" id="subjectForm">
            <label>Тема собрания</label>
            <input type="text" maxlength="128" class="form-control" id="subject" placeholder="Тема собрания" />
            <div style="color:#a94442;display:none;" id="subjectMinSymbols"  class="error">
                Ошибка! Тема собрания не может быть менее 10 символов и более 128 символов.
                Допустимо использование только буквы, цифры и знаки пунктуации и припинания.
            </div>
        </div>
        <div class="form-group">
            <label>Описание собрания(от 128 символов)</label>
            <textarea id="description">
                {{#batchVotingTemplateExists}}
                    {{batchVotingTemplate.description}}
                {{/batchVotingTemplateExists}}
            </textarea>
        </div>

        <div class="form-group">
            <label>
                <input type="checkbox" id="isNeedCreateChat" />
                Создать чат для собрания
            </label>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" id="addChatToProtocol"/>
                Прикрепить стенограмму собрания к протоколу
            </label>
        </div>

        <div class="form-group spinner" max="100" min="1">
            <label style="display: block;">Кворум</label>
            <input type="text" class="form-control" id="quorum" style="display: inline-block; width: 67px; vertical-align: top;" />
            <div class="input-group-btn-vertical" style="margin-left: -5px; display: inline-block; vertical-align: top;">
                <button class="btn btn-default" type="button"><i class="fa fa-caret-up"></i></button>
                <button class="btn btn-default" type="button"><i class="fa fa-caret-down"></i></button>
            </div>
            <div style="display: inline-block; vertical-align: top; margin-top: 6px; margin-left: 11px;">
                %
            </div>
            <div class="text-muted" style="margin-top:10px;">В процентах от числа приглашенных. От 1% до 100%</div>
        </div>

        <div class="input-group date" style="width:100%; padding-top: 10px; padding-bottom: 10px;">
            <span class="input-group-addon">Дата начала собрания</span>
            <input type="text" class="form-control" id="startDate" />
            <span class="input-group-btn">
                <button class="btn btn-warning" type="button" id="set-start-date-to-current">текущая дата</button>
            </span>
        </div>
        <div class="input-group date" style="width:100%;padding-top: 10px;padding-bottom: 10px;">
            <span class="input-group-addon">Дата окончания регистрации в собрании</span>
            <input type="text" class="form-control" id="registrationEndDate" />
        </div>
        <div class="input-group date" style="width:100%; padding-top: 10px; padding-bottom: 10px;">
            <span class="input-group-addon">Дата окончания собрания</span>
            <input type="text" class="form-control" id="endDate" />
        </div>
        <div class="input-group date" >
            <label>
                <input type="checkbox" id="isCanFinishBeforeEndDate" />
                Возможность закончить собрание до окончания срока собрания
            </label>
        </div>
        <div class="input-group date" >
            <label>
                <input type="checkbox" checked="checked" id="isShowSubgroupsUsers" />
                Показывать кандидатов из подгрупп
            </label>
        </div>
        <div class="form-group">
            <label>Список участников собрания{{#votersNeedBeVerified}} (доступны только идентифицированные участники){{/votersNeedBeVerified}}</label>
            <div style="height: 50px; position: relative;">
                <div style="position: absolute; left: 0px; right: 100px;">
                    <select id="possibleVoters" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                    </select>
                </div>
                <div style="position: absolute; right: 0px;">
                    <button type="button" class="btn btn-primary" id="addPossibleVoterButton">Добавить</button><br/>
                </div>
            </div>
          <div class="form-group date">
         <button type="button" class="btn btn-primary" id="addAllPossibleVoters" style="margin-top:10px;">Добавить всех</button><br/>
        </div>
            <p>По умолчанию итоговый протокол собрания подписывают секретарь собрания и председатель собрания, если они были выбраны. Если Вы хотите, чтобы протокол подписывал кто-то из списка участников собрания (или все), поставьте галочку на пункте "ЭЦП" напротив имени этого участника.</p>
            <table class="standardTable" id="selectedVoters">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>ФИО участника</th>
                        <th></th>
                        <th>ЭЦП</th>
                        <th style="with: 91px;"></th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
        <div class="form-group">
            <label>Вид проведения собрания</label>
            <select id="mode" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%" disabled="disabled">
                {{#modesArr}}
                    <option value="{{name}}">{{value}}</option>
                {{/modesArr}}
            </select>
           <label id="modeDescription" style="font-weight: normal;padding-top: 10px; "></label>
        </div>
        <div class="form-group">
            <label>Вид голосования</label>
            <select id="secretVoting" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
              <option value="true">Тайное голосование</option>
               <option value="false">Открытое голосование</option>
            </select>
             <label id="secretVotingDescription" style="font-weight: normal;padding-top: 10px; "></label>
        </div>
        <div class="form-group">
            <label>Максимальное число перезапусков собрания. Определяет, сколько раз можно перезапустить голосование заново в случае его неудачного завершения</label>
            <select id="votingRestartCount">
            <option value="0">0</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="25">25</option>
            <option value="50">50</option>
            <option value="2147483647">Бесконечное</option>
            </select>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" id="isNeedAddAdditionalVotings" />
                Создать голосования по выборам председателя собрания, секретаря собрания и за повестку дня
            </label>
            <label id="modeDescription" style="font-weight: normal;padding-top: 10px; ">При выборе этой опции создаются дополнительные
            голосования: сначала выбирают председателя собрания, затем секретаря собрания, затем повестку дня</label>
        </div>

        <div class="form-group">
            <label>
                <input type="checkbox" id="useBiometricIdentificationInAdditionalVotings"/>
                Использовать биометрическую идентификацию в голосованиях по выборам председателя собрания, секретаря собрания и за повестку дня
            </label>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" id="useBiometricIdentificationInRegistration"/>
                Использовать биометрическую идентификацию при регистрации участников собрания
            </label>
        </div>
        <div class="form-group">
            <label>
                <input type="checkbox" id="testBatchVotingMode"/>
                Тестовый режим собрания (при подписании протокола ЭЦП не устанавливаются)
            </label>
        </div>
        <div class="form-group" style="display:none;">
            <label>Атрибуты собрания</label>
            <div id="attributesContainer">
                <table class="standardTable" id="attributes">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Наименование атрибута</th>
                            <th>Значение атрибута</th>
                            <th style="width: 91px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <button type="button" class="btn btn-primary" id="addBatchVotingAttributeButton" style="margin-top: 10px;">Добавить атрибут</button><br/>
        </div>
        <div class="form-group">
            <label>Голосования</label>
            <div id="votingsContainer">
                <table class="standardTable" id="votings">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Наименование голосования</th>
                            <th>Тип голосования</th>
                            <th>Видимость голосования</th>
                            <th style="width: 100px;">Сортировка</th>
                            <th style="width: 91px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <button type="button" class="btn btn-primary" id="addVotingButton" style="margin-top: 10px;">Добавить голосование</button><br/>
        </div>
        <hr/>
        {{^isSavedTemplatePage}}
            <div class="form-group">
                <button type="button" class="btn btn-primary" id="saveDraftTemplateButton" style="margin-right: 5px;">Сохранить черновик шаблона</button>
                <button type="button" class="btn btn-primary" id="clearDraftTemplateButton" style="margin-right: 5px;">Очистить черновик шаблона</button>
            </div>
            <div class="form-group">
                <label id="lastDateSaveDraft"></label>
            </div>
        {{/isSavedTemplatePage}}

        <div class="form-group">
            <button type="button" class="btn btn-primary" id="startBatchVotingButton" style="margin-right: 5px;">Старт собрания</button>
            <button type="button" class="btn btn-primary" id="saveBatchVotingTemplateButton" style="margin-right: 5px;">Сохранить шаблон</button>
            <button type="button" class="btn btn-primary" id="saveAndStartBatchVotingButton">Сохранить шаблон и запустить собрание</button>
        </div>
    </div>
    <hr/>


    <!-- Модальное окно создания и редактирования голосования-->
    <div class="modal fade" role="dialog" id="votingWindow" aria-labelledby="votingTextLabel"
         aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title" id="votingTextLabel">Редактировать голосование</h4>
                </div>
                <div class="modal-body">
                    <div class="form-group" id="votingSubjectForm">
                        <label>Наименование голосования</label>
                        <input type="text" class="form-control" id="votingSubject" maxlength="128"/>
                         <div style="color:#a94442;display:none;" id="votingSubjectMinSymbols"  class="error">Необходимо ввести минимум 10 символов!</div>
                    </div>
                    <div class="form-group">
                        <label>Описание голосования(от 128 символов)</label>
                        <textarea id="votingDescription"></textarea>
                    </div>
                    <div class="form-group">
                        <label>Тип голосования</label>
                        <select id="votingType" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            {{#votingTypesArr}}
                                <option value="{{name}}">{{value}}</option>
                            {{/votingTypesArr}}
                        </select>
                         <label id="voteTypeDescription" style="font-weight: normal;padding-top: 10px; "></label>
                    </div>
                    <div>
                        <label>
                            <input type="checkbox" title='Добавить вариант "Воздержался"' id="isAddAbstain"  />
                            Добавить вариант "Воздержался"
                        </label>
                    </div>
                    <div>
                        <label>
                            <input type="checkbox" title="Возможность изменить свой ответ в голосовании" id="isVoteCancellable" checked="checked" />
                            Участник может изменить свой голос до завершения голосования
                        </label>
                    </div>
                    <div>
                        <label>
                            <input type="checkbox" title="В случае неудачного голосования оно будет завершено с ошибкой даже если разрешен перезапуск голосования" id="notRestartable"/>
                            В случае безрезультатного исхода голосования, сразу переходить к следующему этапу собрания (без переголосования)
                        </label>
                    </div>
                    <!--<div>
                        <label>
                            <input type="checkbox" id="isVoteCommentsAllowed" />
                            Возможность оставить комментарий к голосованию
                        </label>
                    </div>-->
                    <div style="display: none;">
                        <label>
                            <input type="checkbox" title="Будет ли голосование видимым" id="isVisible" checked="checked" />
                            Видимость голосования
                        </label>
                    </div>
                    <div>
                        <label>
                            <input type="checkbox" title="Использовать биометрическую идентификацию" id="useBiometricIdentification"/>
                            Для проверки голоса участников использовать биометрическую идентификацию
                        </label>
                    </div>
                    <div id="votingParameters">
                        <div class="form-group">
                            <label>
                                <input type="checkbox" id="stopBatchVotingOnFailResult" />
                                В случае безрезультатного исхода голосования завершать собрание
                            </label>
                        </div>
                        <div class="form-group">
                            <label>Минимальный процент голосов для победы на голосовании</label>
                            <div>
                                <input type="text" class="form-control" id="percentForWin" />
                            </div>
                        </div>
                    </div>
                    <div id="candidateParameters">
                        <div class="form-group">
                            <div class="radio">
                                <label><input type="radio" name="candidateVariantSelectType" value="addMine" checked="true">Добавить кандидатов голосования</label>
                            </div>
                            <div class="radio">
                                <label><input type="radio" name="candidateVariantSelectType" value="genFromWinnersOtherVoting">Сгенерировать из победивших кандидатов другого голосования</label>
                            </div>
                            <div class="radio">
                                <label><input type="radio" name="candidateVariantSelectType" value="genFromLosersOtherVoting">Сгенерировать из проигравших кандидатов другого голосования</label>
                            </div>
                        </div>
                        <div style="display: none;" id="genFromWinnersOtherVotingCandidateBlock" class="form-group sourceCandidateVotingVariantsBlock">
                          <select id="selectFromWinnersOtherVotingCandidate" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            </select>
                        </div>
                        <div style="display: none;" id="genFromLosersOtherVotingCandidateBlock" class="form-group sourceCandidateVotingVariantsBlock">
                          <select id="selectFromLosersOtherVotingCandidate" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            </select>
                        </div>
                        <div style="display: none;" id="addMineCandidateBlock" class="form-group sourceCandidateVotingVariantsBlock">
                            <label>Кандидаты голосования</label>
                            <div style="height: 50px; position: relative;">
                                <div style="position: absolute; left: 0px; right: 100px;">
                                    <select id="possibleCandidates" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                                    </select>
                                </div>
                                <div style="position: absolute; right: 0px;">
                                    <button type="button" class="btn btn-primary" id="addPossibleCandidateButton">Добавить</button><br/>
                                </div>
                            </div>
                            <button type="button" class="btn btn-primary" id="addAllPossibleCandidates" style="margin-bottom:5px;">Добавить всех</button><br/>
                            <table class="standardTable" id="candidateVariantsVoters">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>ФИО кандидата</th>
                                     <th></th>
                                    <th style="width: 91px;"></th>
                                </tr>
                                </thead>
                                <tbody id="candidateVariants">
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div id="votingItemsParameters">
                        <div class="form-group">
                            <div class="radio">
                                <label><input type="radio" name="variantSelectType" value="addMine" checked="true">Написать свои варианты голосования</label>
                            </div>
                            <div class="radio">
                                <label><input type="radio" name="variantSelectType" value="genFromInterview">Сгенерировать из интервью</label>
                            </div>
                            <div class="radio">
                                <label><input type="radio" name="variantSelectType" value="genFromWinnersOtherVoting">Сгенерировать из победивших вариантов другого голосования</label>
                            </div>
                            <div class="radio">
                                <label><input type="radio" name="variantSelectType" value="genFromLosersOtherVoting">Сгенерировать из проигравших вариантов другого голосования</label>
                            </div>

                        </div>
                        <div style="display: none;" id="genFromInterviewBlock" class="form-group sourceVotingVariantsBlock">
                          <select id="interviewsSelectFrom" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            </select>
                        </div>
                        <div style="display: none;" id="genFromWinnersOtherVotingBlock" class="form-group sourceVotingVariantsBlock">
                          <select id="selectFromWinnersOtherVoting" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            </select>
                        </div>
                        <div style="display: none;" id="genFromLosersOtherVotingBlock" class="form-group sourceVotingVariantsBlock">
                          <select id="selectFromLosersOtherVoting" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
                            </select>
                        </div>
                        <div id="addMineBlock" class="form-group sourceVotingVariantsBlock">
                            <label>Варианты голосования</label>
                            <div style="height: 50px; position: relative;">
                                <div style="position: absolute; left: 0px; right: 100px;">
                                    <input type="text" class="form-control" id="votingItemValue" />
                                </div>
                                <div style="position: absolute; right: 0px;">
                                    <button type="button" class="btn btn-primary" id="addVotingItemButton">Добавить</button>
                                </div>
                            </div>
                            <table class="standardTable">
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Вариант голосования</th>
                                        <th style="width: 91px;"></th>
                                    </tr>
                                </thead>
                                <tbody id="votingItems">
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div id="multipleSelectionParameters">
                        <div class="form-group">
                            <label>Минимальное количество выбираемых вариантов</label>
                            <div>
                                <input type="text" class="form-control" id="minSelectionCount"/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Максимальное количество выбираемых вариантов</label>
                            <div>
                                <input type="text" class="form-control" id="maxSelectionCount"/>
                            </div>
                        </div>
                    </div>
                    <div id="singleSelectionParameters">
                    </div>
                    <div id="interviewParameters">
                         <label>
                            <input type="checkbox" id="skipResults"/>
                            при завершении автоматически переходить на следующий этап
                        </label>
                    </div>
                    <div id="multipleWinnersBlock">
                        <label>
                            <input type="checkbox" id="multipleWinners"/>
                            Разрешить несколько победителей
                        </label>
                    </div>
                    <div id="multipleWinnersMinMaxBlock">
                        <div class="form-group">
                            <label>Минимальное количество победителей</label>
                            <input type="text" class="form-control" id="minWinnersCount"/>
                        </div>
                        <div class="form-group">
                            <label>Максимальное количество победителей</label>
                            <input type="text" class="form-control" id="maxWinnersCount"/>
                        </div>
                    </div>
                    <div>
                        <div class="form-group">
                            <label>Графа "Предложили" в протоколе</label>
                            <span class="help-block">Например: Выбрать управляющего регионального подразделения</span>
                            <div>
                                <button type="button" class="btn btn-danger" id="sentenceHelpTextBtn">
                                    <i class="fa fa-question-circle" aria-hidden="true"></i>
                                    Пояснение
                                </button>
                            </div><br/>
                            <textarea id="votingSentence"></textarea>
                        </div>
                        <div class="form-group">
                            <label>Графа "Постановили" в протоколе в случае успешного завершения голосования</label>
                            <span class="help-block">Например: Управляющим регионального подразделения назначен</span>
                            <div>
                                <button type="button" class="btn btn-danger" id="successDecreeHelpTextBtn">
                                    <i class="fa fa-question-circle" aria-hidden="true"></i>
                                    Пояснение
                                </button>
                            </div><br/>
                            <textarea id="votingSuccessDecree"></textarea>
                        </div>
                        <div class="form-group">
                            <label>Графа "Постановили" в протоколе в случае неудачного завершения голосования</label>
                            <span class="help-block">Например: Остановить собрание в связи с неудачными выборами управляющего регионального подразделения</span>
                            <div>
                                <button type="button" class="btn btn-danger" id="failDecreeHelpTextBtn">
                                    <i class="fa fa-question-circle" aria-hidden="true"></i>
                                    Пояснение
                                </button>
                            </div><br/>
                            <textarea id="votingFailDecree"></textarea>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" id="saveVotingButton">Сохранить</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->



</script>

<script id="voterTemplate" type="x-tmpl-mustache">
    {{#selectedVoters}}
        <tr {{#handleVoter}}voter_id="{{id}}"{{/handleVoter}}>
            <td>{{index}}</td>
            <td>
                <a href="{{link}}">{{name}}</a>
            </td>
            <td>
                <a class="sharer-item-avatar-link" href="{{link}}">
                    <img src="{{avatarSmall}}" class="img-thumbnail">
			    </a>
            </td>
            <td>
               <label class="userSignProtocolLabel"><input type="checkbox" voter_id="{{id}}" class="userSignProtocol" {{#voterSignProtocol}}checked="checked"{{/voterSignProtocol}} /> ЭЦП</label>
            </td>
            <td>{{#handleVoter}}<button type="button" class="btn btn-danger removeVoter">Удалить</button>{{/handleVoter}}</td>
        </tr>
    {{/selectedVoters}}
</script>
<script id="attributesTemplate" type="x-tmpl-mustache">
    {{#attributes}}
        <tr attr_name="{{name}}">
            <td>{{index}}</td>
            <td class="attr_name" attr_name="{{name}}"><a href="javascript:void(0);" class="editBatchVotingAttribute" title="Редактировать">{{name}}</a></td>
            <td class="attr_value" attr_value="{{value}}">{{value}}</td>
            <td><button type="button" class="btn btn-danger removeAttribute">Удалить</button></td>
        </tr>
    {{/attributes}}
</script>
<script id="votingsTemplate" type="x-tmpl-mustache">
    {{#votings}}
        <tr voting_subject="{{subject}}" index="{{index}}">
            <td>{{index_table}}</td>
            <td><a href="javascript:void(0)" class="editVoting">{{subject}}</a></td>
            <td>{{votingTypeName}}</td>
            <td>
                <a
                index="{{index}}"
                href="#"
                tabindex="-1"
                data-toggle="tooltip"
                data-placement="right"
                class="glyphicon visibleVoting {{#isVisible}}glyphicon-eye-open{{/isVisible}} {{^isVisible}}glyphicon-eye-close{{/isVisible}}"
                ></a>
            </td>
            <td>
                {{#showUpButton}}<div class="glyphicon glyphicon-chevron-up upVoting"></div>{{/showUpButton}}
                {{#showDownButton}}<div class="glyphicon glyphicon-chevron-down downVoting"></div>{{/showDownButton}}
            </td>
            <td><button type="button" class="btn btn-danger removeVoting">Удалить</button></td>
        </tr>
    {{/votings}}
</script>


<script id="candidatesTemplate" type="x-tmpl-mustache">
    {{#candidates}}
        <tr candidate_id="{{value}}">
            <td>{{index}}</td>
            <td><a href="{{link}}">{{name}}</a></td>
            <td>
            <a class="sharer-item-avatar-link" href="{{link}}">
				<img src="{{avatarSmall}}" class="img-thumbnail">
			</a>
            </td>
            <td><button type="button" class="btn btn-danger removeCandidate">Удалить</button></td>
        </tr>
    {{/candidates}}
</script>
<script id="votingOptionTemplate" type="x-tmpl-mustache">
    {{#votings}}
    <option value="{{index}}">{{subject}}</option>
    {{/votings}}
</script>
<script id="votingItemsTemplate" type="x-tmpl-mustache">
    {{#votingItems}}
        <tr voting_item_value="{{value}}" index="{{index}}">
            <td>{{index}}</td>
            <td>
                <a href="javascript:void(0);" class="editVotingItem">{{value}}</a>
                <div style="display: none;" class="editVotingItemContainer">
                    <input type="text" class="form-control" value="{{value}}" />
                    <button type="button" class="btn btn-primary updateVotingItem">Сохранить</button>
                    <button type="button" class="btn btn-danger cancelUpdateVotingItem" style="margin-left: 5px;">Отмена</button>
                </div>
            </td>
            <td><button type="button" class="btn btn-danger removeVotingItem">Удалить</button></td>
        </tr>
    {{/votingItems}}
</script>

<!-- Модальное окно создания и редактирования атрибута-->
<div class="modal fade" role="dialog" id="batchVotingAttributeWindow" aria-labelledby="batchVotingAttributeTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="batchVotingAttributeTextLabel">Редактировать атрибут</h4>
            </div>
            <div class="modal-body">
                <div class="div-control">
                    <div class="attrNameContainer">
                        <span>Наименование: </span>
                        <input type="text" class="form-control" id="attr_name"/>
                    </div>
                    <div class="attrValueContainer">
                        <span>Значение: </span>
                        <input type="text" class="form-control" id="attr_value"/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="saveAttrButton">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<!-- Модальное окно выбора-->
<div class="modal fade" role="dialog" id="selectTemplateWindow" aria-labelledby="selectTemplateTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="selectTemplateTextLabel">Шаблоны собраний</h4>
            </div>
            <div class="modal-body">
                <div id="batchVotingTemplates-grid"></div>
                <div id="batchVotingTemplateGridSearchResult"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->

<!-- Модальное окно текста пояснения-->
<div class="modal fade" role="dialog" id="helpTextWindow" aria-labelledby="helpTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" id="helpTextBlock">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
