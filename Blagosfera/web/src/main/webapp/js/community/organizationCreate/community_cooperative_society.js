define([], function () {

    var REGISTER_PO_ORGANIZATION_QUEUE = "registerPoOrganizationQueue";

    var associationFormCode = "community_cooperative_society";

    var shortTimeDateFormatForMoment = "DD.MM.yyyy HH:mm";
    var shortTimeDateFormat = "dd.mm.yyyy HH:MM";

    var CooperativeSocietyCreate = {};
    var jqNameInput = null; // Инпут названия ПО
    var jqNameShortInput = null;
    var jqEngNameInput = null;
    var jqEngNameShortInput = null;

    //var shortTargetsEditor = null;
    var jqShortTargets = null;
    var fullTargetsEditor = null;

    var addressBlock = null;
    var officeOwnerShipTypeValue = null;
    var officeOwnerShipTypeText = null;
    var jqOfficeRentPeriod = null;

    var factAddressBlock = null;
    var factOfficeOwnerShipTypeValue = null;
    var factOfficeOwnerShipTypeText = null;
    var jqFactOfficeRentPeriod = null;

    var jqStartDateBatchVoting = null;
    //var jqCountHoursBatchVoting = null;
    var jqRegistrationEndDateBatchVoting = null;
    var jqEndDateBatchVoting = null;

    var jqParentNode = null;

    var founders = []; // Учредители
    var currentSharer = {}; // Текущий пользователь
    var organizationRegulationsEditor = null;
    var writeOrganizationRegulationsEditor = null;
    var foundersTemplate = null;
    var deleteFounderButtonClass = "";

    var jqMainOkved = null;
    var jqAdditionalOkveds = null;

    var jqActivityScopes = null;

    var registerPoResponseTemplate = null;

    var batchVotingDescriptionEditor = null;

    var participantsInSoviet = [];
    var participantsInAuditCommittee = [];

    var utils = null;

    var eventManager = {};

    var jqEntranceShareFees = null;
    var jqMinShareFees = null;
    var jqMembershipFees = null;
    var jqCommunityEntranceShareFees = null;
    var jqCommunityMinShareFees = null;
    var jqCommunityMembershipFees = null;

    var jqHasStamp = null;
    var whoApprovePositionValue = null;
    var whoApprovePositionId = null;
    var jqCountDaysToQuiteFromPo = null;
    var whoApproveDatePayValue = null;
    var whoApproveDatePayId = null;
    var jqCountMonthToSharerPay = null;
    var startPeriodPayValue = null;
    var startPeriodPayId = null;
    var onShareDeathSelector = null;
    var jqMinCreditApproveSovietPO = null;
    var jqMinContractSumApproveSovietPO = null;
    var sovietOfficePeriodValue = null;
    var sovietOfficePeriodId = null;
    var presidentOfSovietKindWorkingValue = null;
    var presidentOfSovietKindWorkingId = null;
    var participantsOfBoardOfficePeriodValue = null;
    var participantsOfBoardOfficePeriodId = null;
    var jqCountDaysPerMeetingOfBoard = null;
    var jqQuorumMeetingOfBoard = null;
    var boardReportFrequencyValue = null;
    var boardReportFrequencyId = null;
    var participantsAuditCommitteeOfficePeriodValue = null;
    var participantsAuditCommitteeOfficePeriodId = null;

    var jqNeedCreateChat = null;
    var jqAddChatToProtocol = null;

    var jqCountParticipantsInSoviet = null;
    var jqCountParticipantsInAuditCommittee = null;
    var jqChooseSourcePoRegulation = null;
    var jqWriteRegulationParametersBlock = null;
    var jqGenerateRegulationParametersBlock = null;

    var jqParticipantsSourceType = null;

    var jqActivityTypesText = null;


    var jqSecretVoting = null;

    var directorPositionId = null;

    var activityScopeIds = null;

    var branchesModel = [];
    var representationsModel = [];

    var initedSourceRequlationForm = false;

    clearLocalVars = function() {
        jqNameInput = null; // Инпут названия ПО
        jqNameShortInput = null;
        jqEngNameInput = null;
        jqEngNameShortInput = null;

        //shortTargetsEditor = null;
        fullTargetsEditor = null;

        addressBlock = null;
        officeOwnerShipTypeValue = null;
        officeOwnerShipTypeText = null;
        jqOfficeRentPeriod = null;

        factAddressBlock = null;
        factOfficeOwnerShipTypeValue = null;
        factOfficeOwnerShipTypeText = null;
        jqFactOfficeRentPeriod = null;

        jqStartDateBatchVoting = null;
        //jqCountHoursBatchVoting = null;
        jqRegistrationEndDateBatchVoting = null;
        jqEndDateBatchVoting = null;

        jqParentNode = null;
        
        organizationRegulationsEditor = null;
        writeOrganizationRegulationsEditor = null;
        foundersTemplate = null;
        deleteFounderButtonClass = "";

        jqMainOkved = null;
        jqAdditionalOkveds = null;

        jqActivityScopes = null;

        registerPoResponseTemplate = null;

        batchVotingDescriptionEditor = null;

        utils = null;

        eventManager = {};

        jqEntranceShareFees = null;
        jqMinShareFees = null;
        jqMembershipFees = null;
        jqCommunityEntranceShareFees = null;
        jqCommunityMinShareFees = null;
        jqCommunityMembershipFees = null;

        jqHasStamp = null;
        whoApprovePositionValue = null;
        whoApprovePositionId = null;
        jqCountDaysToQuiteFromPo = null;
        whoApproveDatePayValue = null;
        whoApproveDatePayId = null;
        jqCountMonthToSharerPay = null;
        startPeriodPayValue = null;
        startPeriodPayId = null;
        onShareDeathSelector = null;
        jqMinCreditApproveSovietPO = null;
        jqMinContractSumApproveSovietPO = null;
        sovietOfficePeriodValue = null;
        sovietOfficePeriodId = null;
        presidentOfSovietKindWorkingValue = null;
        presidentOfSovietKindWorkingId = null;
        participantsOfBoardOfficePeriodValue = null;
        participantsOfBoardOfficePeriodId = null;
        jqCountDaysPerMeetingOfBoard = null;
        jqQuorumMeetingOfBoard = null;
        boardReportFrequencyValue = null;
        boardReportFrequencyId = null;
        participantsAuditCommitteeOfficePeriodValue = null;
        participantsAuditCommitteeOfficePeriodId = null;

        jqActivityTypesText = null;

        directorPositionId = null;

        activityScopeIds = null;

        initedSourceRequlationForm = false;
    };

    clearFormListData = function() {
        founders = []; // Учредители
        currentSharer = {}; // Текущий пользователь
        participantsInSoviet = [];
        participantsInAuditCommittee = [];

        branchesModel = [];
        representationsModel = [];
    };

    slideBlock = function($link) {
        var $panel = $link.closest(".panel-heading").next(".panel-collapse");
        if ($link.hasClass("glyphicon-arrow-up")) {
            $panel.slideUp();
            $link.removeClass("glyphicon-arrow-up").addClass("glyphicon-arrow-down");
        } else if ($link.hasClass("glyphicon-arrow-down")) {
            $panel.slideDown();
            $link.removeClass("glyphicon-arrow-down").addClass("glyphicon-arrow-up");
        }
        return false;
    };

    // Инициализация поиска учредителей
    var initSearchFounders = function(jqInput, onSelect){
        if (jqInput.data('typeahead') != null) {
            return;
        }
        jqInput.typeahead({
            delay: 200,
            autoSelect: true,
            updater: function(item){
                onSelect({
                    id: item.id,
                    fullName: item.fullName
                });
                return item;
            },
            matcher: function () {
                return true;//считаем что с сервера пришли правильные данные
            },
            highlight: false,
            source: function (query, process) {
                var ids = [];
                for (var index in founders) {
                    var founder = founders[index];
                    ids.push(founder.id);
                }
                var data = {
                    query: query,
                    "id[]" : ids
                };
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/sharer/searchVerified.json",
                    data: data,
                    success: function (data) {
                        for (var index in data) {
                            var item = data[index];
                            item.name = item.fullName;
                        }
                        return process(data);
                    },
                    error: function () {
                        console.log("ajax error");
                    }
                });
            }
        });
    };

    // Отрисовка таблицы учредителей
    var drawFounders = function(foundersTemplate, founders, jqParentNode) {
        for (var index in founders) {
            var founder = founders[index];
            founder["index"] = parseInt(index) + 1;
        }
        var jqTableNode = $(Mustache.render(foundersTemplate,
            {
                founders: founders
            }
        ));
        jqParentNode.empty();
        jqParentNode.append(jqTableNode);
        jqTableNode.fixMe();
        $("." + deleteFounderButtonClass, jqParentNode).radomTooltip({
            container: "body",
            delay: {"show": 100, "hide": 100}
        });
    };

    var selectedFounder = null;
    // Инициализация блока с учредителями
    var initFounders = function (jqFoundersTemplate, jqFounderNameInput, jqAddFounder, jqFoundersTable, createPoPageData, jqMinCountFoundersText, deleteFounderButtonClassLocal, me, founders) {
        deleteFounderButtonClass = deleteFounderButtonClassLocal;
        foundersTemplate = jqFoundersTemplate.html();
        Mustache.parse(foundersTemplate);

        try {
            currentSharer = JSON.parse(me);
            var foundMe = false;
            for (var founderIndex in founders) {
                var founder = founders[founderIndex];
                if (currentSharer.id == founder.id) {
                    founder.canDelete = false;
                    foundMe = true;
                }
                $(eventManager).trigger("founder.add", {founder : founder});
            }
            if (!foundMe) {
                currentSharer.canDelete = false;
                founders.push(currentSharer);
                $(eventManager).trigger("founder.add", {founder : currentSharer});
            }
        } catch (e) {
            //
        }
        drawFounders(foundersTemplate, founders, jqFoundersTable);


        // Инициализация поиска учредителей
        initSearchFounders(jqFounderNameInput, function (founder) {
            selectedFounder = founder;
            jqAddFounder.prop("disabled", false);
        });
        // Инициализация добавления учредителя
        jqAddFounder.off();
        jqAddFounder.click(function () {
            if (selectedFounder == null) {
                bootbox.alert("Необходимо выбрать учредителя!");
                return false;
            }
            selectedFounder.canDelete = true;
            founders.push(selectedFounder);
            drawFounders(foundersTemplate, founders, jqFoundersTable);
            jqAddFounder.prop("disabled", true);
            jqFounderNameInput.val("");
            $(eventManager).trigger("founder.add", {founder: selectedFounder});
            selectedFounder = null;
        });
        // Удалить учредителя из таблицы
        jqParentNode.on("click", "." + deleteFounderButtonClass, function () {
            $(this).tooltip('destroy');
            var founderId = $(this).attr("founder_id");
            utils.removeItemFromArray(founders, "id", founderId);
            drawFounders(foundersTemplate, founders, jqFoundersTable);

            $(eventManager).trigger("founder.remove", {founderId: founderId});
        });
        jqMinCountFoundersText.text(createPoPageData.minCountFounders + " " + stringForms(createPoPageData.minCountFounders, "человек", "человека", "человек"));
    };

    // Инициализация блока с видами дейтельности юр лица
    var initOkveds = function(jqMainOkved, jqAdditionalOkveds, tempFormData) {
        var mainOkved = "";
        var additionalOkveds = "";
        if (tempFormData != null) {
            if (tempFormData["mainOkved"] != null) {
                mainOkved = tempFormData["mainOkved"];
            }
            if (tempFormData["additionalOkveds"] != null) {
                additionalOkveds = tempFormData["additionalOkveds"];
            }
        }
        jqMainOkved.val(mainOkved);
        jqAdditionalOkveds.val(additionalOkveds);

        jqMainOkved.okvedInput('clear');
        jqAdditionalOkveds.okvedInput('clear');
        jqMainOkved.okvedInput({
            singleSelect: true,
            title: "Выбор основного вида деятельности"
        });
        jqAdditionalOkveds.okvedInput({
            title: "Выбор дополнительных видов деятельности"
        });
    };

    // Инициализация редакторов на странице
    var initTinyEditors = function(){
        if (window.tinyMCE != null && window.tinyMCE.editors != null) {
            window.tinyMCE.editors=[];
        }
    };

    // Инициализация целей организации
    var initTargets = function(jqShortTargets, jqFullTargets, tempFormData) {
        setTextInputTempData(tempFormData, jqShortTargets);
        /*if (shortTargetsEditor == null) {
            jqShortTargets.radomTinyMCE({
                onCreate: function (editor) {
                    shortTargetsEditor = editor;
                    setTextEditorTempData(tempFormData, shortTargetsEditor, "shortTargets");
                }
            });
        } else {
            setTextEditorTempData(tempFormData, shortTargetsEditor, "shortTargets");
        }*/
        if (fullTargetsEditor == null) {
            jqFullTargets.radomTinyMCE({
                onCreate: function (editor) {
                    fullTargetsEditor = editor;
                    setTextEditorTempData(tempFormData, fullTargetsEditor, "fullTargets");
                }
            });
        } else {
            setTextEditorTempData(tempFormData, fullTargetsEditor, "fullTargets");
        }
    };

    // Инициализация механизма устава
    var initRegulations = function(jqGenerateOrganizationRegulations, jqCopyOrganizationRegulations,
                                   jqOrganizationRegulations, jqWriteOrganizationRegulations,
                                   jqChooseSourcePoRegulation,
                                   jqWriteRegulationParametersBlock, jqGenerateRegulationParametersBlock,
                                   tempFormData) {
        var generateRegulations = function() {
            generatePoRegulations(function(documentDto){
                organizationRegulationsEditor.setContent(documentDto.content);
            });
        };

        // Сгенерировать устав
        jqGenerateOrganizationRegulations.off();
        jqGenerateOrganizationRegulations.click(function () {
            // Если уже есть текст устава
            if (organizationRegulationsEditor != null && organizationRegulationsEditor.getContent() != "") {
                bootbox.confirm("Будет сгененирован устав с потерей всех изменений!<br/>Вы уверены?", function (result) {
                    if (result) {
                        jqCopyOrganizationRegulations.prop("disabled", false);
                        generateRegulations();
                    }
                });
            } else {
                jqCopyOrganizationRegulations.prop("disabled", false);
                generateRegulations();
            }
        });

        jqCopyOrganizationRegulations.off();
        jqCopyOrganizationRegulations.click(function () {
            bootbox.confirm("<div style='text-align: justify;'>Вы будете перенесены в режим \"Написать Устав Потребительского общества самостоятельно\" - текст созданного Устава будет перенесён в редактируемое текстовое поле. Вы можете редактировать любые разделы созданного Устава.</div>" +
                            "<div style='text-align: center; font-weight: bold;'>ОДНАКО:</div>" +
                            "<div style='text-align: justify;'>Мы не рекомендуем самостоятельно редактировать те разделы, которые соответствуют ранее заполненным Вами полям, расположенным выше (Название организации, Цели и Задачи и т.д.), так как их редактирование может привести к различию информации в разных документах в итоговом пакете документов.</div>", function (result) {
                if (result) {
                    if (writeOrganizationRegulationsEditor == null) {
                        jqWriteOrganizationRegulations.radomTinyMCE({
                            height: "400",
                            onCreate: function (editor) {
                                writeOrganizationRegulationsEditor = editor;
                                $("#writeRegulation").prop("checked", true).trigger("click");
                                writeOrganizationRegulationsEditor.setContent(organizationRegulationsEditor.getContent());

                            }
                        });
                    } else {
                        $("#writeRegulation").prop("checked", true).trigger("click");
                        writeOrganizationRegulationsEditor.setContent(organizationRegulationsEditor.getContent());
                    }
                    $.scrollTo($("#regulationBlock"));
                }
            });
        });

        /*if (organizationRegulationsEditor == null) {
            jqOrganizationRegulations.radomTinyMCE({
                readonly: true,
                height: "400",
                onCreate: function (editor) {
                    organizationRegulationsEditor = editor;
                    setTextEditorTempData(tempFormData, organizationRegulationsEditor, "organizationRegulations");
                }
            });
        } else {
            setTextEditorTempData(tempFormData, organizationRegulationsEditor, "organizationRegulations");
        }

        if (writeOrganizationRegulationsEditor == null) {
            jqWriteOrganizationRegulations.radomTinyMCE({
                height: "400",
                onCreate: function (editor) {
                    writeOrganizationRegulationsEditor = editor;
                    setTextEditorTempData(tempFormData, writeOrganizationRegulationsEditor, "writeOrganizationRegulations");
                }
            });
        } else {
            setTextEditorTempData(tempFormData, writeOrganizationRegulationsEditor, "writeOrganizationRegulations");
        }*/


        jqChooseSourcePoRegulation.off();
        //jqChooseSourcePoRegulation.on("change", function() {
        jqChooseSourcePoRegulation.on("click", function(event) {
            var chooseSourceRegulationId = $(this).attr("id");
            if (event.originalEvent != null) {
                if (chooseSourceRegulationId == "generateRegulation") {
                    bootbox.confirm("При возвращении в режим генерации устава изменённый текст не сохранится!", function (result) {
                        if (result) {
                            $("#generateRegulation").trigger("click");
                        }
                    });
                    return false;
                }
            }


            jqWriteRegulationParametersBlock.hide();
            jqGenerateRegulationParametersBlock.hide();

            if (!initedSourceRequlationForm) {
                if (tempFormData != null && tempFormData["organizationRegulations"] != null && tempFormData["organizationRegulations"] != "") {
                    jqCopyOrganizationRegulations.prop("disabled", false);
                } else {
                    jqCopyOrganizationRegulations.prop("disabled", true);
                }
            }

            if (chooseSourceRegulationId == "writeRegulation") {
                jqWriteRegulationParametersBlock.show();
                if (writeOrganizationRegulationsEditor != null) {
                    try {
                        writeOrganizationRegulationsEditor.destroy();
                    } catch (e) {
                    }
                }
                jqWriteOrganizationRegulations.radomTinyMCE({
                    height: "400",
                    onCreate: function (editor) {
                        var needLoadFromTemp = writeOrganizationRegulationsEditor == null || !initedSourceRequlationForm;
                        writeOrganizationRegulationsEditor = editor;
                        if (needLoadFromTemp) {
                            setTextEditorTempData(tempFormData, writeOrganizationRegulationsEditor, "writeOrganizationRegulations");
                        }
                        initedSourceRequlationForm = true;
                    }
                });

            } else if (chooseSourceRegulationId == "generateRegulation") {
                jqGenerateRegulationParametersBlock.show();
                if (organizationRegulationsEditor != null) {
                    try {
                        organizationRegulationsEditor.destroy();
                    } catch (e) {
                    }
                }
                jqOrganizationRegulations.radomTinyMCE({
                    readonly: true,
                    height: "400",
                    onCreate: function (editor) {
                        var needLoadFromTemp = organizationRegulationsEditor == null || !initedSourceRequlationForm;
                        organizationRegulationsEditor = editor;
                        if (needLoadFromTemp) {
                            setTextEditorTempData(tempFormData, organizationRegulationsEditor, "organizationRegulations");
                        }
                        initedSourceRequlationForm = true;
                    }
                });

            }
        });
        var tempSelectData = null;
        if (tempFormData != null && tempFormData['chooseSourcePoRegulation'] != null) {
            tempSelectData = tempFormData['chooseSourcePoRegulation'];
        }
        if (tempSelectData == null) {
            tempSelectData = "generateRegulation";
        }
        //$("#" + tempSelectData).prop("checked", true).trigger("click");
        $("#" + tempSelectData).trigger("click");
    };

    // Инициализация адрессного блока
    var initAddressBlock = function(
        addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName,
        onChangeCountryField, officeInternalName, regionCodeInternalName,
        districtDescriptionShortInternalName, cityDescriptionShortInternalName, streetDescriptionShortInternalName,
        jqOfficeOwnerShipType, jqOfficeRentPeriod,
        tempFormAddressData,
        needClearInputs,
        onChangeOfficeOwnerShipType
    ){
        var addressBlock = initCountryField(
            addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName,
            onChangeCountryField, null, RoomTypes.OFFICE, officeInternalName,

            regionCodeInternalName,

            streetDescriptionShortInternalName,
            districtDescriptionShortInternalName,
            cityDescriptionShortInternalName,

            tempFormAddressData,
            needClearInputs
        );

        var officeOwnerShipTypeSelectedItems = [];
        if (tempFormAddressData != null && tempFormAddressData.ownerShipTypeFieldName != null) {
            officeOwnerShipTypeSelectedItems.push(tempFormAddressData.ownerShipTypeFieldName);
        }
        jqOfficeOwnerShipType.empty();
        RameraListEditorModule.init(
            jqOfficeOwnerShipType,
            {
                labelClasses: ["checkbox-inline"],
                labelStyle: "margin-left: 10px;",
                selectedItems: officeOwnerShipTypeSelectedItems,
                selectClasses: ["form-control"],
            },
            function (event, data) {
                if (event == RameraListEditorEvents.VALUE_CHANGED) {
                    onChangeOfficeOwnerShipType(data.value, data.text);
                    if (data.code.indexOf("rent_apartment") > -1) { // Арендованные площади
                        jqOfficeRentPeriod.closest(".row").show();
                    } else if (data.code.indexOf("own_apartment") > -1) { // Свои площади
                        jqOfficeRentPeriod.closest(".row").hide();
                    }
                }
            }
        );

        var dateRangeParameters = {
            minView: 'month',
            timePicker: false,
            timePicker24Hour: false,
            format: "dd.mm.yyyy",
            minDate: dateFormat(new Date(0), "dd.mm.yyyy")
        };
        if (tempFormAddressData.officeRentPeriod != null) {
            var dateParts = tempFormAddressData.officeRentPeriod.split(" - ");
            dateRangeParameters["startDate"] = dateParts[0];
            dateRangeParameters["endDate"] = dateParts[1];
        } else {
            dateRangeParameters["startDate"] = dateFormat("dd.mm.yyyy");
            dateRangeParameters["endDate"] = dateFormat("dd.mm.yyyy");
        }
        jqOfficeRentPeriod.radomDateRangeInput(dateRangeParameters, function(startDate, endDate) {
        });

        return addressBlock
    };

    // Инициализация времени проведения собрания
    var initDateField = function(jqDateInput, tempFormData, onChange) {
        if (onChange == null) {
            onChange = function(){};
        }
        var startDate = createDate();

        if (tempFormData != null && tempFormData[jqDateInput.attr("id")] != null) {
            var tempDate = createFormattedDate(tempFormData[jqDateInput.attr("id")], shortTimeDateFormatForMoment);
            if (startDate.getTime() < tempDate.getTime()) {
                startDate = tempDate;
            }
        }

        //startDate.setTime(startDate.getTime() - startDate.getTimezoneOffset() * 60 * 1000 - serverTime.timeZoneOffset);
        //jQuery.data(jqDateInput.get(0), "date_time", startDate.getTime());
        jqDateInput.radomDateTimeInput({
            defaultDate : startDate,
            minDate : startDate
        }, function(date){
            //jqDateInput.data("DateTimePicker").date(startDate);
            //jQuery.data(jqDateInput.get(0), "date_time", date.getTime());
            onChange(date);
        });
        jqDateInput.data("DateTimePicker").date(startDate);
    };

    // Отрисовка членов совета и ревизионной комиссии
    var drawParticipantsTable = function(participants, participantsTemplate, jqParticipantTableContainer){
        for (var index in participants) {
            var participant = participants[index];
            participant["index"] = parseInt(index) + 1;
        }

        var jqParticipantsContent = $(Mustache.render(participantsTemplate, {
            participants : participants
        }));

        jqParticipantTableContainer.empty();
        jqParticipantTableContainer.append(jqParticipantsContent);
        jqParticipantsContent.fixMe();
    };

    // Инициализация блока по добавлению членов совета Общества и ревизионной комиссии
    var initParticipantsBlock = function(
        jqSelectParticipants, jqAddParticipantButton, jqParticipantTableContainer, jqParticipantsTemplate, participants,
        selfAddParticipantEvent, selfRemoveParticipantEvent,
        otherAddParticipantEvent, otherRemoveParticipantEvent
    ){
        var participantsTemplate = jqParticipantsTemplate.html();
        Mustache.parse(participantsTemplate);

        jqSelectParticipants.empty();
        jqSelectParticipants.selectpicker("refresh");
        jqSelectParticipants.selectpicker("val", null);
        var jqSelectedOption = null;
        jqAddParticipantButton.off();
        jqAddParticipantButton.click(function(){
            jqSelectedOption.prop("disabled", true);

            jqSelectParticipants.selectpicker("refresh");
            jqSelectParticipants.selectpicker("val", null);

            var participant = {id : jqSelectedOption.attr("value"), fullName : jqSelectedOption.text()};
            participants.push(participant);

            drawParticipantsTable(participants, participantsTemplate, jqParticipantTableContainer);

            jqAddParticipantButton.prop("disabled", true);

            $(eventManager).trigger(selfAddParticipantEvent, {participantId : participant.id});
        });
        jqSelectParticipants.off();
        jqSelectParticipants.change(function(){
            jqAddParticipantButton.prop("disabled", false);
            jqSelectedOption = $(this).find("option:selected");
        });

        jqParticipantTableContainer.off('click', '.deleteParticipant');
        jqParticipantTableContainer.on('click', '.deleteParticipant', function(){
            var participantId = $(this).attr("participant_id");
            jqSelectParticipants.find("option[value=" + participantId + "]").prop("disabled", false);
            jqSelectParticipants.selectpicker("refresh");
            jqSelectParticipants.selectpicker("val", null);
            utils.removeItemFromArray(participants, "id", participantId);
            drawParticipantsTable(participants, participantsTemplate, jqParticipantTableContainer);

            $(eventManager).trigger(selfRemoveParticipantEvent, {participantId : participantId});
        });

        $(eventManager).bind("founder.add", function(event, data) {
            jqSelectParticipants.find("option[value=" + data.founder.id + "]").remove();
            jqSelectParticipants.append("<option value='" + data.founder.id + "'>" + data.founder.fullName + "</option>");
            jqSelectParticipants.selectpicker("refresh");
            jqSelectParticipants.selectpicker("val", null);
        });
        $(eventManager).bind("founder.remove", function(event, data) {
            utils.removeItemFromArray(participants, "id", data.founderId);
            drawParticipantsTable(participants, participantsTemplate, jqParticipantTableContainer);
            jqSelectParticipants.find("option[value=" + data.founderId + "]").remove();
            jqSelectParticipants.selectpicker("refresh");
            jqSelectParticipants.selectpicker("val", null);
        });

        // При удалении в одном списке участника в текущем он должен стать доступным
        $(eventManager).bind(otherRemoveParticipantEvent, function(event, data) {
            jqSelectParticipants.find("option[value=" + data.participantId + "]").prop("disabled", false);
            jqSelectParticipants.selectpicker("refresh");
            jqSelectParticipants.selectpicker("val", null);
            jqAddParticipantButton.prop("disabled", true);
            jqSelectedOption = null;
        });
        $(eventManager).bind(otherAddParticipantEvent, function(event, data) {
            jqSelectParticipants.find("option[value=" + data.participantId + "]").prop("disabled", true);
            jqSelectParticipants.selectpicker("refresh");
            jqSelectParticipants.selectpicker("val", null);
            jqAddParticipantButton.prop("disabled", true);
            jqSelectedOption = null;
        });
    };

    // Отрисовка участников в таблицу и отправка событий о том, что они добавлены
    var initTempParticipantsList = function(
        jqSelectParticipants, jqParticipantTableContainer, jqParticipantsTemplate, participants,
        selfAddParticipantEvent
    ) {
        var participantsTemplate = jqParticipantsTemplate.html();
        Mustache.parse(participantsTemplate);

        if (participants != null) {
            drawParticipantsTable(participants, participantsTemplate, jqParticipantTableContainer);
            for (var index in participants)  {
                var participant = participants[index];
                var jqSelectedOption = jqSelectParticipants.find("option[value=" + participant.id + "]");
                jqSelectedOption.prop("disabled", true);
                $(eventManager).trigger(selfAddParticipantEvent, {participantId : participant.id});
            }
        }
        jqSelectParticipants.selectpicker("refresh");
        jqSelectParticipants.selectpicker("val", null);
    };

    var initResponseBatchVoting = function(jqRegisterPoResponseTemplate, jqRegisterPoResponseModal, serverTime) {
        registerPoResponseTemplate = jqRegisterPoResponseTemplate.html();
        Mustache.parse(registerPoResponseTemplate);

        // Подписываемся на событие создания собрания по регистрации ПО
        radomStompClient.subscribeToUserQueue(REGISTER_PO_ORGANIZATION_QUEUE, function(batchVoting){
            var startDate = createTimestampDate(batchVoting.startDate);

            //startDate.setTime(startDate.getTime() - startDate.getTimezoneOffset() * 60 * 1000 - serverTime.timeZoneOffset);

            var endDate = createTimestampDate(batchVoting.endDate);
            //endDate.setTime(endDate.getTime() - endDate.getTimezoneOffset() * 60 * 1000 - serverTime.timeZoneOffset);

            var registrationDateEnd = createTimestampDate(batchVoting.registrationDateEnd);
            //registrationDateEnd.setTime(registrationDateEnd.getTime() + registrationDateEnd.getTimezoneOffset() * 60 * 1000 + serverTime.timeZoneOffset);

            var currentServerDate = createTimestampDate(batchVoting.currentServerDate);
            //currentServerDate.setTime(currentServerDate.getTime() + currentServerDate.getTimezoneOffset() * 60 * 1000 + serverTime.timeZoneOffset);

            batchVoting.startDate = startDate.format("dd.mm.yyyy в HH:MM");
            batchVoting.endDate = endDate.format("dd.mm.yyyy в HH:MM");

            batchVoting.batchVotingStartAfterRegistration = startDate.getTime() < currentServerDate.getTime();

            var jqResponseContent = $(Mustache.render(registerPoResponseTemplate, batchVoting));
            jqRegisterPoResponseModal.find(".modal-body").empty();
            jqRegisterPoResponseModal.find(".modal-body").append(jqResponseContent);
            jqRegisterPoResponseModal.modal("show");
        });
    };

    var initBatchVotingDescription = function(jqBatchVotingDescription, tempFormData){
        if (batchVotingDescriptionEditor == null) {
            jqBatchVotingDescription.radomTinyMCE({
                onCreate: function (editor) {
                    batchVotingDescriptionEditor = editor;
                    setTextEditorTempData(tempFormData, batchVotingDescriptionEditor, "batchVotingDescription");
                }
            });
        } else {
            setTextEditorTempData(tempFormData, batchVotingDescriptionEditor, "batchVotingDescription");
        }
    };

    var getOkvedsIds = function(rawValue) {
        var ids = [];
        if (rawValue != null) {
            var idsStr = rawValue.split(";");
            for (var i = 0; i < idsStr.length; i++) {
                if (idsStr[i] != null && idsStr[i] != "") {
                    ids.push(idsStr[i]);
                }
            }
        }
        return ids;
    };

    var getMainOkvedId = function() {
        return jqMainOkved.val();
    };

    var getAdditionalOkvedsIds = function() {
        return getOkvedsIds(jqAdditionalOkveds.val());
    };

    var initListEditor = function(jqListEditorBlock, tempFormData, fieldName, onChangeValue) {
        var selectedItems = [];
        if (tempFormData != null && tempFormData[fieldName] != null) {
            if ($.isArray(tempFormData[fieldName])) {
                selectedItems = tempFormData[fieldName];
            } else {
                selectedItems.push(tempFormData[fieldName]);
            }
        }
        jqListEditorBlock.empty();
        RameraListEditorModule.init(
            jqListEditorBlock,
            {
                labelClasses: ["checkbox-inline"],
                labelStyle: "margin-left: 10px;",
                selectedItems: selectedItems,
                selectClasses: ["form-control"],
            },
            function (event, data) {
                if (event == RameraListEditorEvents.VALUE_CHANGED) {
                    onChangeValue(data.value, data.code);
                }
            }
        );
    };

    var setTextInputTempData = function(tempFormData, jqTextInput) {
        if (tempFormData != null && tempFormData[jqTextInput.attr("id")] != null) {
            jqTextInput.val(tempFormData[jqTextInput.attr("id")]);
        } else {
            jqTextInput.val("");
        }
    };

    var setCheckboxInputTempData = function(tempFormData, jqCheckboxInput) {
        if (tempFormData != null && tempFormData[jqCheckboxInput.attr("id")] != null) {
            var value = false;
            if (tempFormData[jqCheckboxInput.attr("id")] == "true" || tempFormData[jqCheckboxInput.attr("id")] == true) {
                value = true;
            }
            jqCheckboxInput.prop("checked", value);
        } else {
            jqCheckboxInput.prop("checked", false);
        }
    };

    var setTextEditorTempData = function(tempFormData, editor, editorId) {
        if (tempFormData != null && tempFormData[editorId] != null) {
            try {
                editor.setContent(tempFormData[editorId]);
            } catch (e) {
                setTimeout(function(){
                    setTextEditorTempData(tempFormData, editor, editorId);
                }, 100);
            }
        } else {
            try {
                editor.setContent("");
            } catch (e) {
                setTimeout(function(){
                    setTextEditorTempData(tempFormData, editor, editorId);
                }, 100);
            }
        }
    };

    var initTempFormData = function(tempFormData) {
        tempFormData = tempFormData == null ? {} : tempFormData;
        setTextInputTempData(tempFormData, jqNameInput);
        setTextInputTempData(tempFormData, jqNameShortInput);
        setTextInputTempData(tempFormData, jqEngNameInput);
        setTextInputTempData(tempFormData, jqEngNameShortInput);

        founders = [];
        if (tempFormData["founders"] != null) {
            for (var index in tempFormData["founders"]) {
                founders.push(tempFormData["founders"][index]);
            }
        }

        participantsInSoviet = tempFormData["participantsInSoviet"];
        participantsInAuditCommittee = tempFormData["participantsInAuditCommittee"];
        if (founders == null) {
            founders = [];
        }
        if (participantsInSoviet == null) {
            participantsInSoviet = [];
        }
        if (participantsInAuditCommittee == null) {
            participantsInAuditCommittee = [];
        }

        setCachedFieldFilesData(tempFormData["fieldFilesData"]);

        setTextInputTempData(tempFormData, jqEntranceShareFees);
        setTextInputTempData(tempFormData, jqMinShareFees);
        setTextInputTempData(tempFormData, jqMembershipFees);
        setTextInputTempData(tempFormData, jqCommunityEntranceShareFees);
        setTextInputTempData(tempFormData, jqCommunityMinShareFees);
        setTextInputTempData(tempFormData, jqCommunityMembershipFees);

        setCheckboxInputTempData(tempFormData, jqHasStamp);

        if (tempFormData != null && tempFormData["onShareDeath"] != null) {
            $(onShareDeathSelector + "[value=" + tempFormData["onShareDeath"] + "]").prop("checked", true);
        } else {
            $(onShareDeathSelector).prop("checked", false);
        }


        setTextInputTempData(tempFormData, jqCountDaysToQuiteFromPo);
        setTextInputTempData(tempFormData, jqCountMonthToSharerPay);
        setTextInputTempData(tempFormData, jqMinCreditApproveSovietPO);
        setTextInputTempData(tempFormData, jqMinContractSumApproveSovietPO);
        setTextInputTempData(tempFormData, jqCountDaysPerMeetingOfBoard);
        setTextInputTempData(tempFormData, jqQuorumMeetingOfBoard);

        setTextInputTempData(tempFormData, jqCountParticipantsInSoviet);
        setTextInputTempData(tempFormData, jqCountParticipantsInAuditCommittee);

        setTextInputTempData(tempFormData, jqActivityTypesText);
    };

    var initOnlyEngCharInput = function(jqTextInput){
        var engCharsRegexp = /[а-яА-Я]+/;
        jqTextInput.on('keypress', function(evt){
            evt = evt || window.event;
            var charCode = evt.which || evt.keyCode;
            var charTyped = String.fromCharCode(charCode);
            var result = true;
            if (engCharsRegexp.test(charTyped)) {
                result = false;
            }
            return result;
        });
        jqTextInput.bind('paste', function(evt) {
            setTimeout(function(){
                var count = 0;
                while (engCharsRegexp.test(jqTextInput.val())) {
                    count++;
                    jqTextInput.val(jqTextInput.val().replace(engCharsRegexp, ""));
                    if (count == 100) {
                        break;
                    }
                }
            }, 100);
        });
    };

    var drawBranchesTable = function(modelArr, jqTemplate, jqBlockNode){
        var template = jqTemplate.html();
        Mustache.parse(template);

        var jqMarkup = $(Mustache.render(template,
            {
                branches : modelArr
            }
        ));
        jqBlockNode.empty();
        jqBlockNode.append(jqMarkup);
        jqMarkup.fixMe();
    };

    var drawBranchFunctionsTable = function(modelArr, jqTemplate, jqBlockNode){
        var template = jqTemplate.html();
        Mustache.parse(template);

        var jqMarkup = $(Mustache.render(template,
            {
                functions : modelArr
            }
        ));
        jqBlockNode.empty();
        jqBlockNode.append(jqMarkup);
        jqMarkup.fixMe();
    };

    var initBranchesBlock = function(
        branchNameId,
        branchTypeId,
        branchIndexId,
        jqAddBranchModal, jqEditBranchModal, jqAddRepresentationModal, jqEditRepresentationModal,

        branchAddressBlockInternalName,
        branchCountryInternalName,
        branchPostalCodeInternalName,
        branchOfficeInternalName,
        branchRegionCodeInternalName,
        branchDistrictDescriptionShortInternalName,
        branchCityDescriptionShortInternalName,
        branchStreetDescriptionShortInternalName,

        jqAddBranchButton,
        jqAddRepresentation,

        jqSaveBranch,
        jqSaveRepresentation,
        jqUpdateBranch,
        jqUpdateRepresentation,

        editBranchClass,
        deleteBranchClass,

        jqBranchesTable,
        jqRepresentationsTable,

        branchFunctionTextId,
        addBranchFunctionButtonId,
        editBranchFunctionButtonClass,
        saveBranchFunctionButtonClass,
        deleteBranchFunctionButtonClass,
        branchFunctionsTableId,
        jqBranchFunctionsTableTemplate,

        jqBranchesTableTemplate,
        jqBranchTemplate,
        branchesModelArr, representationModelArr) {

        // Текущий массив с функциями филиала
        var currentFunctionsModelArr = null;

        var branchTemplate = jqBranchTemplate.html();
        Mustache.parse(branchTemplate);

        var branchAddressBlock = null;

        var showModalAddBranch = function(branchTypeName, branchType, jqModal){
            var jqMarkup = $(Mustache.render(branchTemplate,
                {
                    branchTypeName: branchTypeName,
                    branchType : branchType
                }
            ));

            jqAddBranchModal.find(".modal-body").empty();
            jqAddRepresentationModal.find(".modal-body").empty();
            jqEditBranchModal.find(".modal-body").empty();
            jqEditRepresentationModal.find(".modal-body").empty();

            jqModal.modal("show");
            jqModal.find(".modal-body").empty();
            jqModal.find(".modal-body").append(jqMarkup);

            branchAddressBlock = initCountryField(
                branchAddressBlockInternalName, branchCountryInternalName, branchPostalCodeInternalName,
                null, null, RoomTypes.OFFICE, branchOfficeInternalName,

                branchRegionCodeInternalName,

                branchStreetDescriptionShortInternalName,
                branchDistrictDescriptionShortInternalName,
                branchCityDescriptionShortInternalName,

                {}
            );
            currentFunctionsModelArr = [];
            drawBranchFunctionsTable(currentFunctionsModelArr, jqBranchFunctionsTableTemplate, $("#" + branchFunctionsTableId));
        };

        jqAddBranchButton.off();
        jqAddBranchButton.click(function(){
            showModalAddBranch("филиала", "branch", jqAddBranchModal);
        });
        jqAddRepresentation.off();
        jqAddRepresentation.click(function(){
            showModalAddBranch("представительства", "representation", jqAddRepresentationModal);
        });

        var updateBranch = function(branchModel, jqBlockNode, modelArr){
            var branchName = $("#" + branchNameId).val();
            $.extend(branchModel, {
                branchName : branchName,
                address : {
                    country : branchAddressBlock.getJqCountryInput().val(),
                    postalCode : branchAddressBlock.getJqPostalCodeInput().val(),
                    region : branchAddressBlock.getJqRegionInput().val(),
                    district : branchAddressBlock.getJqDistrictInput().val(),
                    city : branchAddressBlock.getJqCityInput().val(),
                    street : branchAddressBlock.getJqStreetInput().val(),
                    building : branchAddressBlock.getJqBuildingInput().val(),
                    room : branchAddressBlock.getJqOfficeInput().val(),
                    geoLocation: branchAddressBlock.getJqGeoLocation().val(),
                    geoPosition: branchAddressBlock.getJqGeoPosition().val()
                },
                branchCountryId : branchAddressBlock.getJqCountryIdInput().val(),
                branchFunctions: currentFunctionsModelArr
            });
            currentFunctionsModelArr = null;
            drawBranchesTable(modelArr, jqBranchesTableTemplate, jqBlockNode);
        };
        var getErrorBranchFunctionInput = function() {
            var result = "В поле \"Добавить функцию филиала\" есть несохраненный текст";
            if ($("#" + branchTypeId).val() == "representation") {
                result = "В поле \"Добавить функцию представительства\" есть несохраненный текст";
            }
            return result;
        };
        var getErrorBranchNameInput = function() {
            var result = "Поле \"Название филиала\" не может быть пустым";
            if ($("#" + branchTypeId).val() == "representation") {
                result = "Поле \"Название представительства\" не может быть пустым";
            }
            return result;
        };

        var clickSaveBranch = function(modelArr, jqBlockNode){
            if ($("#" + branchFunctionTextId).val() != "") {
                bootbox.alert(getErrorBranchFunctionInput());
                return false;
            }
            if ($("#" + branchNameId).val() == null || $("#" + branchNameId).val() == "") {
                bootbox.alert(getErrorBranchNameInput());
                return false;
            }
            if (currentFunctionsModelArr == null || currentFunctionsModelArr.length == 0) {
                bootbox.alert("Необходимо добавить хотябы одну функцию");
                return false;
            }
            var branchModel = {
                index : modelArr.length + 1
            };
            modelArr.push(branchModel);
            updateBranch(branchModel, jqBlockNode, modelArr);
            $(".modal").filter(":visible").modal("hide");
        };
        var clickUpdateBranch = function(jqBlockNode, modelArr){
            if ($("#" + branchFunctionTextId).val() != "") {
                bootbox.alert(getErrorBranchFunctionInput());
                return false;
            }
            if ($("#" + branchNameId).val() == null || $("#" + branchNameId).val() == "") {
                bootbox.alert(getErrorBranchNameInput());
                return false;
            }
            if (currentFunctionsModelArr == null || currentFunctionsModelArr.length == 0) {
                bootbox.alert("Необходимо добавить хотябы одну функцию");
                return false;
            }
            var branchIndex = parseInt($("#" + branchIndexId).val()) - 1;
            var branchModel = modelArr[branchIndex];
            updateBranch(branchModel, jqBlockNode, modelArr);
            $(".modal").filter(":visible").modal("hide");
        };

        jqSaveBranch.off();
        jqSaveBranch.click(function(){
            clickSaveBranch(branchesModelArr, jqBranchesTable);
        });
        jqSaveRepresentation.off();
        jqSaveRepresentation.click(function(){
            clickSaveBranch(representationModelArr, jqRepresentationsTable);
        });

        var clickEditBranch = function(jqModal, branchModelArr, jqButton){
            var index = parseInt(jqButton.attr("branch_index"));
            var editBranchModel = branchModelArr[index - 1];

            var jqMarkup = $(Mustache.render(branchTemplate, editBranchModel));

            jqAddBranchModal.find(".modal-body").empty();
            jqAddRepresentationModal.find(".modal-body").empty();
            jqEditBranchModal.find(".modal-body").empty();
            jqEditRepresentationModal.find(".modal-body").empty();

            jqModal.modal("show");
            jqModal.find(".modal-body").empty();
            jqModal.find(".modal-body").append(jqMarkup);

            branchAddressBlock = initCountryField(
                branchAddressBlockInternalName, branchCountryInternalName, branchPostalCodeInternalName,
                null, null, RoomTypes.OFFICE, branchOfficeInternalName,

                branchRegionCodeInternalName,

                branchStreetDescriptionShortInternalName,
                branchDistrictDescriptionShortInternalName,
                branchCityDescriptionShortInternalName,

                {}
            );
            currentFunctionsModelArr = editBranchModel.branchFunctions;
            drawBranchFunctionsTable(currentFunctionsModelArr, jqBranchFunctionsTableTemplate, $("#" + branchFunctionsTableId));
        };

        var clickDeleteBranch = function(branchModelArr, jqBlockNode, jqButton) {
            var index = parseInt(jqButton.attr("branch_index"));
            utils.removeItemFromArray(branchModelArr, "index", index);
            for (var i in branchModelArr) {
                var branchModel = branchModelArr[i];
                branchModel["index"] = parseInt(i) + 1;
            }
            drawBranchesTable(branchModelArr, jqBranchesTableTemplate, jqBlockNode);
        };

        jqBranchesTable.off();
        jqBranchesTable.on("click", "." + editBranchClass, function(){
            clickEditBranch(jqEditBranchModal, branchesModelArr, $(this));
        });
        jqBranchesTable.off("click", "." + deleteBranchClass);
        jqBranchesTable.on("click", "." + deleteBranchClass, function(){
            clickDeleteBranch(branchesModelArr, jqBranchesTable, $(this));
        });

        jqRepresentationsTable.off("click", "." + editBranchClass);
        jqRepresentationsTable.on("click", "." + editBranchClass, function(){
            clickEditBranch(jqEditRepresentationModal, representationModelArr, $(this));
        });
        jqRepresentationsTable.off("click", "." + deleteBranchClass);
        jqRepresentationsTable.on("click", "." + deleteBranchClass, function(){
            clickDeleteBranch(representationModelArr, jqRepresentationsTable, $(this));
        });

        jqUpdateBranch.off();
        jqUpdateBranch.click(function(){
            clickUpdateBranch(jqBranchesTable, branchesModelArr);
        });
        jqUpdateRepresentation.off();
        jqUpdateRepresentation.click(function(){
            clickUpdateBranch(jqRepresentationsTable, representationModelArr);
        });
        drawBranchesTable(branchesModelArr, jqBranchesTableTemplate, jqBranchesTable);
        drawBranchesTable(representationModelArr, jqBranchesTableTemplate, jqRepresentationsTable);

        var jqCurrentEditFunctionInput = null;
        $("body").off("click", "#" + addBranchFunctionButtonId);
        $("body").on("click", "#" + addBranchFunctionButtonId, function(){
            var funcText = $("#" + branchFunctionTextId).val();
            if (funcText == null || funcText == "") {
                bootbox.alert("Необходимо заполнить поле с функцией");
                return false;
            }
            currentFunctionsModelArr.push({
                index : currentFunctionsModelArr.length + 1,
                text : funcText
            });
            drawBranchFunctionsTable(currentFunctionsModelArr, jqBranchFunctionsTableTemplate, $("#" + branchFunctionsTableId));
            $("#" + branchFunctionTextId).val("");
        });
        $("body").off("click", "." + editBranchFunctionButtonClass);
        $("body").on("click", "." + editBranchFunctionButtonClass, function(){
            var index = parseInt($(this).attr("function_index"));
            $(this).hide();
            $("." + saveBranchFunctionButtonClass + "[function_index=" + index + "]").show();
            $("[function_text_static_index=" + index + "]").hide();
            $("[function_text_edit_block_index=" + index + "]").show();
            jqCurrentEditFunctionInput = $("[function_text_edit_index=" + index + "]");
        });
        $("body").off("click", "." + saveBranchFunctionButtonClass);
        $("body").on("click", "." + saveBranchFunctionButtonClass, function(){
            var funcText = jqCurrentEditFunctionInput.val();
            var index = parseInt($(this).attr("function_index"));
            if (funcText == null || funcText == "") {
                bootbox.alert("Необходимо заполнить поле с функцией");
                return false;
            }
            currentFunctionsModelArr[index - 1].text = funcText;
            drawBranchFunctionsTable(currentFunctionsModelArr, jqBranchFunctionsTableTemplate, $("#" + branchFunctionsTableId));
        });
        $("body").off("click", "." + deleteBranchFunctionButtonClass);
        $("body").on("click", "." + deleteBranchFunctionButtonClass, function(){
            var index = parseInt($(this).attr("function_index"));
            utils.removeItemFromArray(currentFunctionsModelArr, "index", index);
            for (var i in currentFunctionsModelArr) {
                var branchFuncModel = currentFunctionsModelArr[i];
                branchFuncModel["index"] = parseInt(i) + 1;
            }
            drawBranchFunctionsTable(currentFunctionsModelArr, jqBranchFunctionsTableTemplate, $("#" + branchFunctionsTableId));
        });
    };

    var initSpinnerInput = function($input, min, max) {
        if ($input.val() != null && $input.val() != "") {
            if (parseInt($input.val()) > max) {
                $input.val(max);
            }
        }
        $.radomSpinner($input, min, max);
    };

    var initChatCheckBox = function(jqNeedCreateChat, jqAddChatToProtocol, tempFormData) {
        setCheckboxInputTempData(tempFormData, jqNeedCreateChat);
        setCheckboxInputTempData(tempFormData, jqAddChatToProtocol);


        var changeAddChatToProtocolHandler = function(inputValue) {
            if (inputValue) {
                jqAddChatToProtocol.prop("disabled", false);
            } else {
                jqAddChatToProtocol.prop("disabled", true);
            }
        };

        jqNeedCreateChat.click(function(){
            changeAddChatToProtocolHandler($(this).is(":checked"));
        });

        changeAddChatToProtocolHandler(jqNeedCreateChat.is(":checked"));
    };

    var initVotingType = function(jqSecretVoting, tempFormData) {
        var tempSelectData = null;
        if (tempFormData != null && tempFormData[jqSecretVoting.attr("id")] != null) {
            tempSelectData = tempFormData[jqSecretVoting.attr("id")];
        }
        jqSecretVoting.selectpicker("refresh");
        jqSecretVoting.selectpicker("val", tempSelectData);
    };

    /*var initChooseSourceRegulation = function(jqChooseSourcePoRegulation,
                                               jqWriteRegulationParametersBlock, jqGenerateRegulationParametersBlock,
                                               tempFormData
    ) {
        jqChooseSourcePoRegulation.on("change", function() {
            jqWriteRegulationParametersBlock.hide();
            jqGenerateRegulationParametersBlock.hide();
            var chooseSourceRegulationId = $(this).attr("id");
            if (chooseSourceRegulationId == "writeRegulation") {
                jqWriteRegulationParametersBlock.show();
            } else if (chooseSourceRegulationId == "generateRegulation") {
                jqGenerateRegulationParametersBlock.show();
            }
        });
        var tempSelectData = null;
        if (tempFormData != null && tempFormData['chooseSourcePoRegulation'] != null) {
            tempSelectData = tempFormData['chooseSourcePoRegulation'];
        }
        if (tempSelectData == null) {
            tempSelectData = "generateRegulation";
        }
        $("#" + tempSelectData).prop("checked", true).trigger("change");
    };*/

    var initParticipantsSourceType = function(jqParticipantsSourceType, tempFormData) {
        jqParticipantsSourceType.on("change", function(){
            var selectedSourceTypeId = $(this).attr("id");
            $(".participantsInSovietBlock").hide();
            $(".participantsInAuditCommitteeBlock").hide();
            if (selectedSourceTypeId == "acceptParticipants") {
                $("#participantsInSovietChooseBlock").show();
                $("#participantsInAuditCommitteeChooseBlock").show();
            } else if (selectedSourceTypeId == "votingForParticipants") {
                $("#participantsInSovietCountBlock").show();
                $("#participantsInAuditCommitteeCountBlock").show();
            }
        });
        var selectedType = tempFormData != null && tempFormData["participantsSourceType"] != null ? tempFormData["participantsSourceType"] : "votingForParticipants";
        $("#" + selectedType).prop("checked", true).trigger("change");
    };

    var copyAddressFields = function(addressBlock,
                                     addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName,
                                     officeInternalName,
                                     regionCodeInternalName,
                                     districtDescriptionShortInternalName, cityDescriptionShortInternalName,
                                     streetDescriptionShortInternalName,
                                     jqOfficeOwnerShipType, officeOwnerShipTypeValue, jqOfficeRentPeriod, officeRentPeriodValue,
                                     callBack) {
        var addressTempData = {};
        addressTempData["ownerShipTypeFieldName"] = officeOwnerShipTypeValue;
        addressTempData["officeRentPeriod"] = officeRentPeriodValue;
        addressTempData["countryId"] = addressBlock.getJqCountryIdInput().val();
        addressTempData["postalCode"] = addressBlock.getJqPostalCodeInput().val();
        addressTempData["region"] = addressBlock.getJqRegionInput().val();
        addressTempData["district"] = addressBlock.getJqDistrictInput().val();
        addressTempData["city"] = addressBlock.getJqCityInput().val();
        addressTempData["street"] = addressBlock.getJqStreetInput().val();
        addressTempData["building"] = addressBlock.getJqBuildingInput().val();
        addressTempData["room"] = addressBlock.getJqOfficeInput().val();
        addressTempData["geoLocation"] = addressBlock.getJqGeoLocation().val();
        addressTempData["geoPosition"] = addressBlock.getJqGeoPosition().val();
        addressTempData["regionCode"] = addressBlock.getJqRegionCodeInput().val();
        addressTempData["districtDescriptionShort"] = addressBlock.getJqDistrictDescriptionShortInput().val();
        addressTempData["cityDescriptionShort"] = addressBlock.getJqCityDescriptionShortInput().val();
        addressTempData["streetDescriptionShort"] = addressBlock.getJqStreetDescriptionShortInput().val();
        addressTempData["buildingDescription"] = addressBlock.getJqBuildingDescriptionInput().val();
        addressTempData["officeDescription"] = addressBlock.getJqOfficeDescriptionInput().val();

        // Инициализация сохранённых данных страны происходит внутри
        addressBlock = initAddressBlock(
            addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName,
            null, officeInternalName,
            regionCodeInternalName,
            districtDescriptionShortInternalName, cityDescriptionShortInternalName, streetDescriptionShortInternalName,
            jqOfficeOwnerShipType, jqOfficeRentPeriod,
            addressTempData,
            false,
            callBack);
        return addressBlock;
    };

    var internalInit = function (
                    jqNameInputLocal, jqNameShortInputLocal, jqEngNameInputLocal, jqEngNameShortInputLocal,
                    jqFoundersTemplate, jqFounderNameInput, jqAddFounder, jqFoundersTable, jqMinCountFoundersText, deleteFounderButtonClass,
                    jqMainOkvedLocal, jqAdditionalOkvedsLocal,
                    jqActivityScopesLocal,
                    jqShortTargetsLocal, jqFullTargets,
                    jqGenerateOrganizationRegulations, jqCopyOrganizationRegulations,
                    jqOrganizationRegulations, jqWriteOrganizationRegulations,

                    addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName, officeInternalName,
                    regionCodeInternalName,
                    districtDescriptionShortInternalName, cityDescriptionShortInternalName, streetDescriptionShortInternalName,
                    jqOfficeOwnerShipType, jqOfficeRentPeriodLocal,

                    factAddressBlockInternalName, factCountryValueInputInternalName, factPostalCodeInternalName, factOfficeInternalName,
                    factRegionCodeInternalName,
                    factDistrictDescriptionShortInternalName, factCityDescriptionShortInternalName, factStreetDescriptionShortInternalName,
                    jqFactOfficeOwnerShipType, jqFactOfficeRentPeriodLocal,

                    jqSelectSoviet, jqAddParticipantSovietButton, jqParticipantSovietTableContainer,
                    jqSelectAuditCommittee, jqAddParticipantAuditCommitteeButton, jqParticipantAuditCommitteeTableContainer,

                    jqParticipantsTemplate,

                    jqStartDateBatchVotingLocal, /*jqCountHoursBatchVotingLocal,*/ jqRegistrationEndDateBatchVotingLocal, jqEndDateBatchVotingLocal,
                    jqSetCurrentDateButton,
                    jqParentNodeLocal,
                    jqRegisterPoResponseTemplate, jqRegisterPoResponseModal,
                    jqBatchVotingDescription,
                    jqSecretVotingLocal,

                    jqEntranceShareFeesLocal, jqMinShareFeesLocal, jqMembershipFeesLocal,
                    jqCommunityEntranceShareFeesLocal, jqCommunityMinShareFeesLocal, jqCommunityMembershipFeesLocal,

                    jqHasStampLocal, // ПО имеет печать и штамп
                    jqWhoApprovePosition, // Кто утверждает положение ПО
                    jqCountDaysToQuiteFromPoLocal, // Количество дней для рассмотрения заялвения о выходе из ПО
                    jqWhoApproveDatePay, // Кто утверждает дату выплат пайщику
                    jqCountMonthToSharerPayLocal, // Количество месяцев на выплату
                    jqStartPeriodPay, // Момент с которого начинает отсчитываться время на выплату
                    onShareDeathSelectorLocal, // Что происходит в случае смерти пайщика
                    jqMinCreditApproveSovietPOLocal, // Сумма заёмных средств решение взять которуюй принимает Общее собрание
                    jqMinContractSumApproveSovietPOLocal, // Количество МРОТ в сделке решение по которой принимает Общее собрание
                    jqSovietOfficePeriod, // Срок на который избираются члены и председатель совета
                    jqPresidentOfSovietKindWorking, // На какой основе работает председатель совета
                    jqParticipantsOfBoardOfficePeriod, // Срок на который избираются члены и председатель правления
                    jqCountDaysPerMeetingOfBoardLocal, // Количество дней между заседаниями правления
                    jqQuorumMeetingOfBoardLocal, // Кворум членов правления на заседаниях (в процентах)
                    jqBoardReportFrequency, // Частота отчета правления перед обществом
                    jqParticipantsAuditCommitteeOfficePeriod,  // Срок на который избираются члены ревизионной комиссии
                    jqDirectorPosition, // Выпадающий список с должностями директора

                    branchNameId,
                    branchTypeId,
                    branchIndexId,
                    jqAddBranchModal, jqEditBranchModal, jqAddRepresentationModal, jqEditRepresentationModal,

                    branchAddressBlockInternalName,
                    branchCountryInternalName,
                    branchPostalCodeInternalName,
                    branchOfficeInternalName,
                    branchRegionCodeInternalName,
                    branchDistrictDescriptionShortInternalName,
                    branchCityDescriptionShortInternalName,
                    branchStreetDescriptionShortInternalName,

                    jqAddBranchButton,
                    jqAddRepresentation,

                    jqSaveBranch,
                    jqSaveRepresentation,
                    jqUpdateBranch,
                    jqUpdateRepresentation,

                    editBranchClass,
                    deleteBranchClass,

                    jqBranchesTable,
                    jqRepresentationsTable,

                    branchFunctionTextId,
                    addBranchFunctionButtonId,
                    editBranchFunctionButtonClass,
                    saveBranchFunctionButtonClass,
                    deleteBranchFunctionButtonClass,
                    branchFunctionsTableId,
                    jqBranchFunctionsTableTemplate,

                    jqBranchesTableTemplate,
                    jqBranchTemplate,

                    jqNeedCreateChatLocal,
                    jqAddChatToProtocolLocal,

                    jqCountParticipantsInSovietLocal, jqCountParticipantsInAuditCommitteeLocal,

                    jqChooseSourcePoRegulationLocal,
                    jqWriteRegulationParametersBlockLocal, jqGenerateRegulationParametersBlockLocal,

                    jqParticipantsSourceTypeLocal,

                    jqActivityTypesTextLocal,

                    jqInitFormData,

                    tempFormData

    ) { 
        if (jqInitFormData.val() == "false") {
            clearLocalVars();
        }
        jqInitFormData.val("true");
        clearFormListData();
        initedSourceRequlationForm = false;
        jqParentNode = jqParentNodeLocal;

        jqNameInput = jqNameInputLocal;
        jqNameShortInput = jqNameShortInputLocal;
        jqEngNameInput = jqEngNameInputLocal;
        jqEngNameShortInput = jqEngNameShortInputLocal;


        jqStartDateBatchVoting = jqStartDateBatchVotingLocal;
        //jqCountHoursBatchVoting = jqCountHoursBatchVotingLocal;
        jqRegistrationEndDateBatchVoting = jqRegistrationEndDateBatchVotingLocal;
        jqEndDateBatchVoting = jqEndDateBatchVotingLocal;

        jqMainOkved = jqMainOkvedLocal;
        jqAdditionalOkveds = jqAdditionalOkvedsLocal;

        jqActivityScopes = jqActivityScopesLocal;

        jqOfficeRentPeriod = jqOfficeRentPeriodLocal;
        jqFactOfficeRentPeriod = jqFactOfficeRentPeriodLocal;

        jqEntranceShareFees = jqEntranceShareFeesLocal;
        jqMinShareFees = jqMinShareFeesLocal;
        jqMembershipFees = jqMembershipFeesLocal;
        jqCommunityEntranceShareFees = jqCommunityEntranceShareFeesLocal;
        jqCommunityMinShareFees = jqCommunityMinShareFeesLocal;
        jqCommunityMembershipFees = jqCommunityMembershipFeesLocal;

        jqHasStamp = jqHasStampLocal;
        jqCountDaysToQuiteFromPo = jqCountDaysToQuiteFromPoLocal;
        jqCountMonthToSharerPay = jqCountMonthToSharerPayLocal;
        onShareDeathSelector = onShareDeathSelectorLocal;
        jqMinCreditApproveSovietPO = jqMinCreditApproveSovietPOLocal;
        jqMinContractSumApproveSovietPO = jqMinContractSumApproveSovietPOLocal;
        jqCountDaysPerMeetingOfBoard = jqCountDaysPerMeetingOfBoardLocal;
        jqQuorumMeetingOfBoard = jqQuorumMeetingOfBoardLocal;

        jqNeedCreateChat = jqNeedCreateChatLocal;
        jqAddChatToProtocol = jqAddChatToProtocolLocal;

        jqCountParticipantsInSoviet = jqCountParticipantsInSovietLocal;
        jqCountParticipantsInAuditCommittee = jqCountParticipantsInAuditCommitteeLocal;

        jqShortTargets = jqShortTargetsLocal;
        jqSecretVoting = jqSecretVotingLocal;

        jqChooseSourcePoRegulation = jqChooseSourcePoRegulationLocal;

        jqWriteRegulationParametersBlock = jqWriteRegulationParametersBlockLocal;
        jqGenerateRegulationParametersBlock = jqGenerateRegulationParametersBlockLocal;

        jqParticipantsSourceType = jqParticipantsSourceTypeLocal;

        jqActivityTypesText = jqActivityTypesTextLocal;

        loadPageData(function(createPoPageData){
            require(
                [
                    "utils/utils",
                    "text!/sharer/me.json"/*,
                    "css!/css/jquery.bootstrap-touchspin.css",
                    "/js/jquery.bootstrap-touchspin.js"*/
                ],
                function (utilsLocal, me) {
                    utils = utilsLocal;
    
                    initOnlyEngCharInput(jqEngNameInput);
                    initOnlyEngCharInput(jqEngNameShortInput);
    
                    initDateField(jqStartDateBatchVoting, tempFormData, function(date){
                        var newRegistrationMinDate = new Date(date);
                        newRegistrationMinDate.setHours(newRegistrationMinDate.getHours() + 1);
                        jqRegistrationEndDateBatchVoting.data("DateTimePicker").minDate(newRegistrationMinDate);
                        //jqEndDateBatchVoting.data("DateTimePicker").minDate(newRegistrationMinDate);
                        var currentRegistrationEndDate = jqRegistrationEndDateBatchVoting.data("DateTimePicker").date().toDate();//moment(jqRegistrationEndDateBatchVoting.val(), shortTimeDateFormatForMoment).toDate();
                        //var currentBatchVotingEndDate = jqEndDateBatchVoting.data("DateTimePicker").date().toDate();
    
                        if (currentRegistrationEndDate.getTime() < newRegistrationMinDate.getTime()) {
                            jqRegistrationEndDateBatchVoting.data("DateTimePicker").date(newRegistrationMinDate);
                        }
                        /*if (currentBatchVotingEndDate < newRegistrationMinDate) {
                            jqEndDateBatchVoting.data("DateTimePicker").date(newRegistrationMinDate);
                        }*/
                    });
                    initDateField(jqRegistrationEndDateBatchVoting, tempFormData, function(date){
                        var newBatchVotingMinEndDate = new Date(date);
                        newBatchVotingMinEndDate.setHours(newBatchVotingMinEndDate.getHours() + 1);
                        jqEndDateBatchVoting.data("DateTimePicker").minDate(newBatchVotingMinEndDate);

                        var currentBatchVotingEndDate = jqEndDateBatchVoting.data("DateTimePicker").date().toDate();

                        if (currentBatchVotingEndDate.getTime() < newBatchVotingMinEndDate.getTime()) {
                            jqEndDateBatchVoting.data("DateTimePicker").date(newBatchVotingMinEndDate);
                        }
                    });
                    initDateField(jqEndDateBatchVoting, tempFormData);
    
                    jqSetCurrentDateButton.click(function(){
                        var currentDate = createDate();
                        jqStartDateBatchVoting.data("DateTimePicker").minDate(currentDate);
                        jqStartDateBatchVoting.data("DateTimePicker").date(currentDate);
                    });
    
                    initOkveds(jqMainOkved, jqAdditionalOkveds, tempFormData);
                    initListEditor(jqActivityScopes, tempFormData, "activityScopeIds", function(value){
                        activityScopeIds = [value];
                    });
    
                    initTinyEditors();
                    initTargets(jqShortTargets, jqFullTargets, tempFormData);
                    initRegulations(
                        jqGenerateOrganizationRegulations, jqCopyOrganizationRegulations,
                        jqOrganizationRegulations, jqWriteOrganizationRegulations,

                        jqChooseSourcePoRegulation,
                        jqWriteRegulationParametersBlock, jqGenerateRegulationParametersBlock,

                        tempFormData
                    );
    
                    initResponseBatchVoting(jqRegisterPoResponseTemplate, jqRegisterPoResponseModal);
                    initBatchVotingDescription(jqBatchVotingDescription, tempFormData);
    
                    initListEditor(jqDirectorPosition, tempFormData, "directorPosition", function(value){
                        directorPositionId = value;
                    });
                    initListEditor(jqWhoApprovePosition, tempFormData, "whoApprovePosition", function(value, code){
                        whoApprovePositionId = value;
                        whoApprovePositionValue = code;
                    });
                    initListEditor(jqWhoApproveDatePay, tempFormData, "whoApproveDatePay", function(value, code){
                        whoApproveDatePayId = value;
                        whoApproveDatePayValue = code;
                    });
                    initListEditor(jqStartPeriodPay, tempFormData, "startPeriodPay", function(value, code){
                        startPeriodPayId = value;
                        startPeriodPayValue = code;
                    });
                    initListEditor(jqSovietOfficePeriod, tempFormData, "sovietOfficePeriod", function(value, code){
                        sovietOfficePeriodId = value;
                        sovietOfficePeriodValue = code;
                    });
                    initListEditor(jqPresidentOfSovietKindWorking, tempFormData, "presidentOfSovietKindWorking", function(value, code){
                        presidentOfSovietKindWorkingId = value;
                        presidentOfSovietKindWorkingValue = code;
                    });
                    initListEditor(jqParticipantsOfBoardOfficePeriod, tempFormData, "participantsOfBoardOfficePeriod", function(value, code){
                        participantsOfBoardOfficePeriodId = value;
                        participantsOfBoardOfficePeriodValue = code;
                    });
                    initListEditor(jqBoardReportFrequency, tempFormData, "boardReportFrequency", function(value, code){
                        boardReportFrequencyId = value;
                        boardReportFrequencyValue = code;
                    });
                    initListEditor(jqParticipantsAuditCommitteeOfficePeriod, tempFormData, "participantsAuditCommitteeOfficePeriod", function(value, code){
                        participantsAuditCommitteeOfficePeriodId = value;
                        participantsAuditCommitteeOfficePeriodValue = code;
                    });
    
                    // Инициализация сохранённых данных простых полей в форму
                    initTempFormData(tempFormData);
    
                    // Инициализация списка членов совета
                    initParticipantsBlock(
                        jqSelectSoviet, jqAddParticipantSovietButton, jqParticipantSovietTableContainer,
                        jqParticipantsTemplate, participantsInSoviet,
                        "soviet.participant.add", "soviet.participant.remove",
                        "revisor.participant.add", "revisor.participant.remove"
                    );
                    // Инициализация списка членов ревизионной комиссии
                    initParticipantsBlock(
                        jqSelectAuditCommittee, jqAddParticipantAuditCommitteeButton, jqParticipantAuditCommitteeTableContainer,
                        jqParticipantsTemplate, participantsInAuditCommittee,
                        "revisor.participant.add", "revisor.participant.remove",
                        "soviet.participant.add", "soviet.participant.remove"
                    );
    
                    // Инициализация списка учредителей и загрузка сохранённых данных в список
                    initFounders(
                        jqFoundersTemplate, jqFounderNameInput, jqAddFounder, jqFoundersTable,
                        createPoPageData, jqMinCountFoundersText, deleteFounderButtonClass, me,
                        founders
                    );
    
                    // Инициализация сохранённых данных списка членов совета
                    initTempParticipantsList(
                        jqSelectSoviet, jqParticipantSovietTableContainer,
                        jqParticipantsTemplate, participantsInSoviet,
                        "soviet.participant.add");
                    // Инициализация сохранённых данных списка членов ревизионной комиссии
                    initTempParticipantsList(
                        jqSelectAuditCommittee, jqParticipantAuditCommitteeTableContainer,
                        jqParticipantsTemplate, participantsInAuditCommittee,
                        "revisor.participant.add");
    
                    var tempFormAddressData = {};
                    if (tempFormData != null) {
                        tempFormAddressData["ownerShipTypeFieldName"] = tempFormData["shipTypeFieldName"];
                        tempFormAddressData["countryId"] = tempFormData["countryId"];
                        tempFormAddressData["postalCode"] = tempFormData["postalCode"];
                        tempFormAddressData["region"] = tempFormData["region"];
                        tempFormAddressData["district"] = tempFormData["district"];
                        tempFormAddressData["city"] = tempFormData["city"];
                        tempFormAddressData["street"] = tempFormData["street"];
                        tempFormAddressData["building"] = tempFormData["building"];
                        tempFormAddressData["room"] = tempFormData["room"];
                        tempFormAddressData["geoLocation"] = tempFormData["geoLocation"];
                        tempFormAddressData["geoPosition"] = tempFormData["geoPosition"];
                        tempFormAddressData["officeRentPeriod"] = tempFormData["officeRentPeriod"];
                        tempFormAddressData["regionCode"] = tempFormData["regionCode"];
                        tempFormAddressData["districtDescriptionShort"] = tempFormData["districtDescriptionShort"];
                        tempFormAddressData["cityDescriptionShort"] = tempFormData["cityDescriptionShort"];
                        tempFormAddressData["streetDescriptionShort"] = tempFormData["streetDescriptionShort"];
                        tempFormAddressData["buildingDescription"] = tempFormData["buildingDescription"];
                        tempFormAddressData["officeDescription"] = tempFormData["officeDescription"];
                    }
                    // Инициализация сохранённых данных страны происходит внутри
                    addressBlock = initAddressBlock(
                        addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName,
                        null, officeInternalName,
                        regionCodeInternalName,
                        districtDescriptionShortInternalName, cityDescriptionShortInternalName, streetDescriptionShortInternalName,
                        jqOfficeOwnerShipType, jqOfficeRentPeriod,
                        tempFormAddressData,
                        true,
                        function(value, text){
                            officeOwnerShipTypeValue = value;
                            officeOwnerShipTypeText = text;
                        });

                    $("#copyFromFactAddress").click(function(){
                        // TODO Костыль
                        var officeOwnerShipTypeValue = jqOfficeOwnerShipType.find('option:contains(' + factOfficeOwnerShipTypeText + ')').val();

                        addressBlock = copyAddressFields(factAddressBlock,
                            addressBlockInternalName, countryValueInputInternalName, postalCodeInternalName,
                            officeInternalName,
                            regionCodeInternalName,
                            districtDescriptionShortInternalName, cityDescriptionShortInternalName,
                            streetDescriptionShortInternalName,
                            jqOfficeOwnerShipType, officeOwnerShipTypeValue, jqOfficeRentPeriod, jqFactOfficeRentPeriod.val(),
                            function(value, text){
                                officeOwnerShipTypeValue = value;
                                officeOwnerShipTypeText = text;
                            });
                        return false;
                    });
    
                    var factTempFormAddressData = {};
                    if (tempFormData != null) {
                        factTempFormAddressData["ownerShipTypeFieldName"] = tempFormData["factShipTypeFieldName"];
                        factTempFormAddressData["countryId"] = tempFormData["factCountryId"];
                        factTempFormAddressData["postalCode"] = tempFormData["factPostalCode"];
                        factTempFormAddressData["region"] = tempFormData["factRegion"];
                        factTempFormAddressData["district"] = tempFormData["factDistrict"];
                        factTempFormAddressData["city"] = tempFormData["factCity"];
                        factTempFormAddressData["street"] = tempFormData["factStreet"];
                        factTempFormAddressData["building"] = tempFormData["factBuilding"];
                        factTempFormAddressData["room"] = tempFormData["factRoom"];
                        factTempFormAddressData["geoLocation"] = tempFormData["factGeoLocation"];
                        factTempFormAddressData["geoPosition"] = tempFormData["factGeoPosition"];
                        factTempFormAddressData["officeRentPeriod"] = tempFormData["factOfficeRentPeriod"];
                        factTempFormAddressData["regionCode"] = tempFormData["factRegionCode"];
                        factTempFormAddressData["districtDescriptionShort"] = tempFormData["factDistrictDescriptionShort"];
                        factTempFormAddressData["cityDescriptionShort"] = tempFormData["factCityDescriptionShort"];
                        factTempFormAddressData["streetDescriptionShort"] = tempFormData["factStreetDescriptionShort"];
                        factTempFormAddressData["buildingDescription"] = tempFormData["factBuildingDescription"];
                        factTempFormAddressData["officeDescription"] = tempFormData["factOfficeDescription"];
                    }

                    factAddressBlock = initAddressBlock(
                        factAddressBlockInternalName, factCountryValueInputInternalName,
                        factPostalCodeInternalName, null, factOfficeInternalName,
                        factRegionCodeInternalName,
                        factDistrictDescriptionShortInternalName, factCityDescriptionShortInternalName, factStreetDescriptionShortInternalName,
                        jqFactOfficeOwnerShipType, jqFactOfficeRentPeriod,
                        factTempFormAddressData,
                        true,
                        function(value, text){
                            factOfficeOwnerShipTypeValue = value;
                            factOfficeOwnerShipTypeText = text;
                        });

                    $("#copyFromUrAddress").click(function(){
                        // TODO Костыль
                        var officeOwnerShipTypeValue = jqFactOfficeOwnerShipType.find('option:contains(' + officeOwnerShipTypeText + ')').val();

                        factAddressBlock = copyAddressFields(addressBlock,
                            factAddressBlockInternalName, factCountryValueInputInternalName, factPostalCodeInternalName,
                            factOfficeInternalName,
                            factRegionCodeInternalName,
                            factDistrictDescriptionShortInternalName, factCityDescriptionShortInternalName,
                            factStreetDescriptionShortInternalName,
                            jqFactOfficeOwnerShipType, officeOwnerShipTypeValue, jqFactOfficeRentPeriod, jqOfficeRentPeriod.val(),
                            function(value, text){
                                factOfficeOwnerShipTypeValue = value;
                                factOfficeOwnerShipTypeText = text;
                            });
                        return false;
                    });
    
                    if (tempFormData != null) {
                        branchesModel = tempFormData["branchesModel"] == null ? [] : tempFormData["branchesModel"];
                        representationsModel = tempFormData["representationsModel"] == null ? [] : tempFormData["representationsModel"];
                    }
    
                    initBranchesBlock(
                        branchNameId,
                        branchTypeId,
                        branchIndexId,
                        jqAddBranchModal, jqEditBranchModal, jqAddRepresentationModal, jqEditRepresentationModal,
    
                        branchAddressBlockInternalName,
                        branchCountryInternalName,
                        branchPostalCodeInternalName,
                        branchOfficeInternalName,
                        branchRegionCodeInternalName,
                        branchDistrictDescriptionShortInternalName,
                        branchCityDescriptionShortInternalName,
                        branchStreetDescriptionShortInternalName,
    
                        jqAddBranchButton,
                        jqAddRepresentation,
    
                        jqSaveBranch,
                        jqSaveRepresentation,
                        jqUpdateBranch,
                        jqUpdateRepresentation,
    
                        editBranchClass,
                        deleteBranchClass,
    
                        jqBranchesTable,
                        jqRepresentationsTable,
    
                        branchFunctionTextId,
                        addBranchFunctionButtonId,
                        editBranchFunctionButtonClass,
                        saveBranchFunctionButtonClass,
                        deleteBranchFunctionButtonClass,
                        branchFunctionsTableId,
                        jqBranchFunctionsTableTemplate,
    
                        jqBranchesTableTemplate,
                        jqBranchTemplate,
                        branchesModel, representationsModel);
                    
                    initSpinnerInput(jqCountDaysPerMeetingOfBoard, 1, 1000000);
                    initSpinnerInput(jqQuorumMeetingOfBoard, 1, 100);
    
                    initSpinnerInput(jqEntranceShareFees, 1, 1000000000);
                    initSpinnerInput(jqMinShareFees, 1, 10000100000000000);
                    initSpinnerInput(jqMembershipFees, 1, 1000000000);
                    initSpinnerInput(jqCommunityEntranceShareFees , 1, 1000000000);
                    initSpinnerInput(jqCommunityMinShareFees, 1, 1000000000);
                    initSpinnerInput(jqCommunityMembershipFees, 1, 1000000000);
    
                    initSpinnerInput(jqCountDaysToQuiteFromPo, 1, 1000000);
                    initSpinnerInput(jqCountMonthToSharerPay, 1, 1000000);
    
                    initSpinnerInput(jqMinCreditApproveSovietPO, 1, 1000000000);
                    initSpinnerInput(jqMinContractSumApproveSovietPO, 1, 1000000);
    
                    initChatCheckBox(jqNeedCreateChat, jqAddChatToProtocol, tempFormData);

                    // TODO
                    initSpinnerInput(jqCountParticipantsInSoviet, createPoPageData.minCountSoviet, founders.length);
                    initSpinnerInput(jqCountParticipantsInAuditCommittee, createPoPageData.minCountAudit, founders.length);

                    $(eventManager).bind("founder.add", function(event, data) {
                        initSpinnerInput(jqCountParticipantsInSoviet, createPoPageData.minCountSoviet, founders.length);
                        initSpinnerInput(jqCountParticipantsInAuditCommittee, createPoPageData.minCountAudit, founders.length);
                    });
                    $(eventManager).bind("founder.remove", function(event, data) {
                        initSpinnerInput(jqCountParticipantsInSoviet, createPoPageData.minCountSoviet, founders.length);
                        initSpinnerInput(jqCountParticipantsInAuditCommittee, createPoPageData.minCountAudit, founders.length);
                    });

                    $(".minCountAuditText").text(createPoPageData.minCountAudit);
                    $(".minCountSovietText").text(createPoPageData.minCountSoviet);

                    initVotingType(jqSecretVoting, tempFormData);

                    initParticipantsSourceType(jqParticipantsSourceType, tempFormData);


                    /*initChooseSourceRegulation(
                        jqChooseSourcePoRegulation,
                        jqWriteRegulationParametersBlock, jqGenerateRegulationParametersBlock,
                        tempFormData
                    );*/
    
                    // После инициализации отображаем блок с формой
                    jqParentNode.fadeIn("fast");
                },
                function (error) {
                    console.log(error);
                }
            );
        });
    };

    // API метод инициализация модуля
    CooperativeSocietyCreate.init = function (tempFormData) {
        internalInit(
            $("#organizationName"), // Нименование ПО
            $("#organizationNameShort"),
            $("#organizationEngName"),
            $("#organizationEngNameShort"),

            $("#foundersTemplate"), // Шаблон таблицы учредителей
            $("#founderName"), // Поле поиска учредителя
            $("#addFounder"), // Кнопка - добавить учредителя
            $("#foundersTable"), // Нода для вставки таблицы учредителей
            $("#minCountFoundersText"),
            "deleteFounder", // Наименование класса кнопки удаления учредителя из таблицы

            $("#mainOkved"), // Основной вид деятельности
            $("#additionalOkveds"), // Дополнительные виды

            $("#activityScopes"), // Сферы деятельности

            $('textarea#shortTargets'), // Краткое описание целей и задач
            $('textarea#fullTargets'), // Полное описание целей и задач

            $("#generateOrganizationRegulations"), // Кнопка генерации устава
            $("#copyOrganizationRegulations"),
            $("#organizationRegulations"), // Поле ввода устава
            $("#writeOrganizationRegulations"),

            "organizationAddress",
            "organizationCountry",
            "organizationPostalCode",
            "organizationOffice",
            "regionCode",
            "districtDescriptionShort",
            "cityDescriptionShort",
            "streetDescriptionShort",

            $("#officeOwnerShipType"),
            $("#officeRentPeriod"),

            "organizationFactAddress",
            "organizationFactCountry",
            "organizationFactPostalCode",
            "organizationFactOffice",
            "factRegionCode",
            "factDistrictDescriptionShort",
            "factCityDescriptionShort",
            "factStreetDescriptionShort",

            $("#factOfficeOwnerShipType"),
            $("#factOfficeRentPeriod"),

            $("#participantsSoviet"),
            $("#addParticipantSoviet"),
            $("#participantsSovietTable"),

            $("#participantsAuditCommittee"),
            $("#addParticipantAuditCommittee"),
            $("#participantsAuditCommitteeTable"),

            $("#participantsTemplate"),

            $("#startDateBatchVoting"), // Дата начала собрания
            $("#registrationBatchVotingEndDate"),
            $("#endDateBatchVoting"),
            $("#set-start-date-to-current"),
            //$("#countHoursBatchVoting"), // Количество часов на собрание

            $("#createPoFieldsBlock"),

            $("#registerPoResponseTemplate"),
            $("#registerPoResponseModal"),
            $("#batchVotingDescription"),
            $("#secretVoting"),

            $("#entranceShareFees"),
            $("#minShareFees"),
            $("#membershipFees"),
            $("#communityEntranceShareFees"),
            $("#communityMinShareFees"),
            $("#communityMembershipFees"),

            $("#hasStamp"), // ПО имеет печать и штамп
            $("#whoApprovePosition"), // Кто утверждает положение ПО
            $("#countDaysToQuiteFromPo"), // Количество дней для рассмотрения заялвения о выходе из ПО
            $("#whoApproveDatePay"), // Кто утверждает дату выплат пайщику
            $("#countMonthToSharerPay"), // Количество месяцев на выплату
            $("#startPeriodPay"), // Момент с которого начинает отсчитываться время на выплату
            "[name=onShareDeath]", // Что происходит в случае смерти пайщика
            $("#minCreditApproveSovietPO"), // Сумма заёмных средств решение взять которуюй принимает Общее собрание
            $("#minContractSumApproveSovietPO"), // Количество МРОТ в сделке решение по которой принимает Общее собрание
            $("#sovietOfficePeriod"), // Срок на который избираются члены и председатель совета
            $("#presidentOfSovietKindWorking"), // На какой основе работает председатель совета
            $("#participantsOfBoardOfficePeriod"), // Срок на который избираются члены и председатель правления
            $("#countDaysPerMeetingOfBoard"), // Количество дней между заседаниями правления
            $("#quorumMeetingOfBoard"), // Кворум членов правления на заседаниях (в процентах)
            $("#boardReportFrequency"), // Частота отчета правления перед обществом
            $("#participantsAuditCommitteeOfficePeriod"), // Срок на который избираются члены ревизионной комиссии
            $("#directorPosition"), // Наименование должности директора ПО

            "branchName", // ИД инпута названия филиала
            "branchType", // ИД инпута с типом филиала
            "branchIndex", // ИД инпута индекса филиала
            $("#addBranchModal"), // Модальное окно добавления филиала
            $("#editBranchModal"), // Модальное окно редактирования филиала
            $("#addRepresentationModal"),// Модальное окно добавления представительства
            $("#editRepresentationModal"), // Модальное окно редактирования представительства

            // Поля адресного блока филиала
            "branchAddress",
            "branchCountry",
            "branchPostalCode",
            "branchOffice",
            "branchRegionCode",
            "branchDistrictDescriptionShort",
            "branchCityDescriptionShort",
            "branchStreetDescriptionShort",

            $("#addBranch"), // Пнопка добавления филиала
            $("#addRepresentation"), // Пнопка добавления представительства

            $("#saveBranch"), // Кнопка сохранения нового филиала
            $("#saveRepresentation"), // Кнопка сохранения нового представительства
            $("#updateBranch"), // Кнопка изменения филиала
            $("#updateRepresentation"), // Кнопка изменения представительства

            "editBranch", // Класс кнопки редактирования филиала
            "deleteBranch", // Класс кнопки удаления филиала

            $("#poBranches"), // Контейнер для таблицы с филиалами
            $("#poRepresentations"), // Контейнер для таблицы с представительствами

            "branchFunctionText", // ИД инпута для добавления функции филиала
            "addBranchFunctionButton", // Кнопка для добавления функции
            "editFunction", // Класс кнопки редактирования функции
            "saveFunction",
            "deleteFunction", // Класс кнопки удаления функции
            "branchFunctionsTable", // Контейнер для таблицы с функциями
            $("#branchFunctionsTableTemplate"), // Шаблон таблицы с функциями филиалов

            $("#branchesTableTemplate"), // Шаблон таблицы с филиалами
            $("#branchTemplate"),

            $("#isNeedCreateChat"),
            $("#addChatToProtocol"),

            $("#countParticipantsInSoviet"),
            $("#countParticipantsInAuditCommittee"),

            $(".chooseSourcePoRegulation"),

            $("#writeRegulationParametersBlock"),
            $("#generateRegulationParametersBlock"),

            $("[name=participantsSourceType]"),

            $("#activityTypesText"),

            $("#initFormData"),

            tempFormData
        );
    };

    // Создать параметры запроса
    var getRequestData = function() {
        var founderIds = [];
        for (var index in founders) {
            var founder = founders[index];
            founderIds.push(founder.id);
        }
        var participantsInSovietIds = [];
        for (var index in participantsInSoviet) {
            var participant = participantsInSoviet[index];
            participantsInSovietIds.push(participant.id);
        }
        var participantsInAuditCommitteeIds = [];
        for (var index in participantsInAuditCommittee) {
            var participant = participantsInAuditCommittee[index];
            participantsInAuditCommitteeIds.push(participant.id);
        }

        var fieldFilesData = getFieldFilesData();
        var officeFieldFilesData = null;
        var factOfficeFieldFilesData = null;
        if (fieldFilesData != null) {
            try {
                fieldFilesData = JSON.parse(fieldFilesData);
            } catch (e) {}
            officeFieldFilesData = fieldFilesData["officeOwnerShipType"];
            factOfficeFieldFilesData = fieldFilesData["factOfficeOwnerShipType"];
        }

        var isGeneratedRegulations = false;
        var organizationRegulations = null;
        if (jqWriteRegulationParametersBlock.is(":visible")) {
            organizationRegulations = writeOrganizationRegulationsEditor.getContent();
        } else {
            isGeneratedRegulations = true;
            organizationRegulations = organizationRegulationsEditor.getContent();
        }

        return {
            name : jqNameInput.val(),
            nameShort : jqNameShortInput.val(),
            engName : jqEngNameInput.val(),
            engNameShort : jqEngNameShortInput.val(),
            founderIds : founderIds,
            shortTargets : jqShortTargets.val(),
            fullTargets : fullTargetsEditor.getContent(),
            mainOkvedId : getMainOkvedId(),
            additionalOkvedIds : getAdditionalOkvedsIds(),
            activityScopeIds : activityScopeIds,
            organizationRegulations : organizationRegulations,
            isGeneratedRegulations : isGeneratedRegulations,
            address : {
                country : addressBlock.getJqCountryInput().val(),
                postalCode : addressBlock.getJqPostalCodeInput().val(),
                region : addressBlock.getJqRegionInput().val(),
                district : addressBlock.getJqDistrictInput().val(),
                city : addressBlock.getJqCityInput().val(),
                street : addressBlock.getJqStreetInput().val(),
                building : addressBlock.getJqBuildingInput().val(),
                room : addressBlock.getJqOfficeInput().val(),
                geoLocation: addressBlock.getJqGeoLocation().val(),
                geoPosition: addressBlock.getJqGeoPosition().val()
            },
            countryId : addressBlock.getJqCountryIdInput().val(),
            regionCode : addressBlock.getJqRegionCodeInput().val(),

            districtDescriptionShort : addressBlock.getJqDistrictDescriptionShortInput().val(),
            cityDescriptionShort : addressBlock.getJqCityDescriptionShortInput().val(),
            streetDescriptionShort : addressBlock.getJqStreetDescriptionShortInput().val(),
            buildingDescription : addressBlock.getJqBuildingDescriptionInput().val(),
            officeDescription : addressBlock.getJqOfficeDescriptionInput().val(),

            officeOwnerShipType: officeOwnerShipTypeValue,
            officeRentPeriod: jqOfficeRentPeriod.val(),
            officeDocuments: officeFieldFilesData,
            factAddress : {
                country : factAddressBlock.getJqCountryInput().val(),
                postalCode : factAddressBlock.getJqPostalCodeInput().val(),
                region : factAddressBlock.getJqRegionInput().val(),
                district : factAddressBlock.getJqDistrictInput().val(),
                city : factAddressBlock.getJqCityInput().val(),
                street : factAddressBlock.getJqStreetInput().val(),
                building : factAddressBlock.getJqBuildingInput().val(),
                room : factAddressBlock.getJqOfficeInput().val(),
                geoLocation: factAddressBlock.getJqGeoLocation().val(),
                geoPosition: factAddressBlock.getJqGeoPosition().val()
            },
            factCountryId : factAddressBlock.getJqCountryIdInput().val(),
            factRegionCode : factAddressBlock.getJqRegionCodeInput().val(),

            factDistrictDescriptionShort : factAddressBlock.getJqDistrictDescriptionShortInput().val(),
            factCityDescriptionShort : factAddressBlock.getJqCityDescriptionShortInput().val(),
            factStreetDescriptionShort : factAddressBlock.getJqStreetDescriptionShortInput().val(),
            factBuildingDescription : factAddressBlock.getJqBuildingDescriptionInput().val(),
            factOfficeDescription : factAddressBlock.getJqOfficeDescriptionInput().val(),

            factOfficeOwnerShipType : factOfficeOwnerShipTypeValue,
            factOfficeRentPeriod : jqFactOfficeRentPeriod.val(),
            factOfficeDocuments: factOfficeFieldFilesData,

            participantsInSovietIds : participantsInSovietIds,
            participantsInAuditCommitteeIds : participantsInAuditCommitteeIds,


            startDateBatchVoting : toServerDate(jqStartDateBatchVoting.data("DateTimePicker").date().toDate()).format(shortTimeDateFormat),
            //countHoursBatchVoting : jqCountHoursBatchVoting.val(),
            registrationEndDateBatchVoting : toServerDate(jqRegistrationEndDateBatchVoting.data("DateTimePicker").date().toDate()).format(shortTimeDateFormat),
            endDateBatchVoting : toServerDate(jqEndDateBatchVoting.data("DateTimePicker").date().toDate()).format(shortTimeDateFormat),

            registerPoOrganizationQueue : REGISTER_PO_ORGANIZATION_QUEUE,

            batchVotingDescription : batchVotingDescriptionEditor.getContent(),
            secretVoting : jqSecretVoting.val(),

            entranceShareFees : jqEntranceShareFees.val(),
            minShareFees : jqMinShareFees.val(),
            membershipFees : jqMembershipFees.val(),
            communityEntranceShareFees : jqCommunityEntranceShareFees.val(),
            communityMinShareFees : jqCommunityMinShareFees.val(),
            communityMembershipFees : jqCommunityMembershipFees.val(),

            associationFormCode : associationFormCode,

            hasStamp: jqHasStamp.is(":checked"), // ПО имеет печать и штамп
            whoApprovePosition: whoApprovePositionValue, // Кто утверждает положение ПО
            countDaysToQuiteFromPo: jqCountDaysToQuiteFromPo.val(), // Количество дней для рассмотрения заялвения о выходе из ПО
            whoApproveDatePay: whoApproveDatePayValue, // Кто утверждает дату выплат пайщику
            countMonthToSharerPay: jqCountMonthToSharerPay.val(), // Количество месяцев на выплату
            startPeriodPay: startPeriodPayValue, // Момент с которого начинает отсчитываться время на выплату
            onShareDeath: $(onShareDeathSelector + ":checked").val(), // Что происходит в случае смерти пайщика
            minCreditApproveSovietPO: jqMinCreditApproveSovietPO.val(), // Сумма заёмных средств решение взять которуюй принимает Общее собрание
            minContractSumApproveSovietPO: jqMinContractSumApproveSovietPO.val(), // Количество МРОТ в сделке решение по которой принимает Общее собрание
            sovietOfficePeriod: sovietOfficePeriodValue, // Срок на который избираются члены и председатель совета
            presidentOfSovietKindWorking: presidentOfSovietKindWorkingValue, // На какой основе работает председатель совета
            participantsOfBoardOfficePeriod: participantsOfBoardOfficePeriodValue, // Срок на который избираются члены и председатель правления
            countDaysPerMeetingOfBoard: jqCountDaysPerMeetingOfBoard.val(), // Количество дней между заседаниями правления
            quorumMeetingOfBoard: jqQuorumMeetingOfBoard.val(), // Кворум членов правления на заседаниях (в процентах)
            boardReportFrequency: boardReportFrequencyValue, // Частота отчета правления перед обществом
            participantsAuditCommitteeOfficePeriod: participantsAuditCommitteeOfficePeriodValue,

            directorPositionId : directorPositionId, // ИД должности директора

            branches: branchesModel, // Список филиалов ПО
            representations: representationsModel, // Список представительств ПО

            isNeedCreateChat : jqNeedCreateChat.is(":checked"),
            addChatToProtocol : jqAddChatToProtocol.is(":checked"),

            countParticipantsInSoviet : jqCountParticipantsInSoviet.val(),
            countParticipantsInAuditCommittee : jqCountParticipantsInAuditCommittee.val(),

            participantsSourceType : jqParticipantsSourceType.filter(":checked").attr("id"),

            activityTypesText : jqActivityTypesText.val(),

            errorBlockPoName : "poName",
            errorBlockPoNameShort : "poNameShort",
            errorBlockPoEngName : "poEngName",
            errorBlockPoEngNameShort : "poEngNameShort",

            errorBlockPoFounders : "poFounders",
            errorBlockPoShortTargets : "poShortTargets",
            errorBlockPoFullTargets : "poFullTargets",
            errorBlockPoMainOkved : "poMainOkved",
            errorBlockPoAdditionalOkveds : "poAdditionalOkveds",

            errorBlockActivityScopes : "poActivityScope",

            errorBlockPoAddress : "poAddress",
            errorBlockPoCountry : "poCountry",
            errorBlockPoRegion : "poRegion",
            errorBlockPoCity : "poCity",
            errorBlockPoStreet : "poStreet",
            errorBlockPoBuilding : "poBuilding",
            errorBlockPoRoom : "poRoom",
            errorBlockPoOfficeOwnerShipType : "poOfficeOwnerShipType",
            errorBlockPoOfficeRentPeriod : "poOfficeRentPeriod",

            errorBlockPoOrganizationRegulations : "poOrganizationRegulations",

            errorBlockPoParticipantsInSoviet : "poParticipantsInSoviet",
            errorBlockPoParticipantsInAuditCommittee : "poParticipantsInAuditCommittee",

            errorBlockPoStartDateBatchVoting : "poStartDateBatchVoting",
            errorBlockPoRegistrationEndDateBatchVoting : "poRegistrationBatchVotingEndDate",
            errorBlockPoEndDateBatchVoting : "poEndDateBatchVoting",
            errorBlockPoBatchVotingDescription : "poBatchVotingDescription",
            errorBlockPoSecretVoting : "poSecretVoting",

            errorBlockPoEntranceShareFees : "poEntranceShareFees",
            errorBlockPoMinShareFees : "poMinShareFees",
            errorBlockPoMembershipFees : "poMembershipFees",
            errorBlockPoCommunityEntranceShareFees : "poCommunityEntranceShareFees",
            errorBlockPoCommunityMinShareFees : "poCommunityMinShareFees",
            errorBlockPoCommunityMembershipFees : "poCommunityMembershipFees",

            errorBlockPoAssociationFormBlock : "associationFormBlock",

            errorBlockPoSovietOfficePeriod : "poSovietOfficePeriod",
            errorBlockPoPresidentOfSovietKindWorking : "poPresidentOfSovietKindWorking",
            errorBlockPoParticipantsOfBoardOfficePeriod : "poParticipantsOfBoardOfficePeriod",
            errorBlockPoCountDaysPerMeetingOfBoard : "poCountDaysPerMeetingOfBoard",
            errorBlockPoQuorumMeetingOfBoard : "poQuorumMeetingOfBoard",
            errorBlockPoBoardReportFrequency : "poBoardReportFrequency",
            errorBlockPoParticipantsAuditCommitteeOfficePeriod : "poParticipantsAuditCommitteeOfficePeriod",
            errorBlockPoHasStamp : "poHasStamp",
            errorBlockPoWhoApprovePosition : "poWhoApprovePosition",
            errorBlockPoCountDaysToQuiteFromPo : "poCountDaysToQuiteFromPo",
            errorBlockPoWhoApproveDatePay : "poWhoApproveDatePay",
            errorBlockPoCountMonthToSharerPay : "poCountMonthToSharerPay",
            errorBlockPoStartPeriodPay :  "poStartPeriodPay",
            errorBlockPoOnShareDeath :  "poOnShareDeath",
            errorBlockPoMinCreditApproveSovietPO : "poMinCreditApproveSovietPO",
            errorBlockPoMinContractSumApproveSovietPO : "poMinContractSumApproveSovietPO",

            errorBlockPoDirectorPosition : "poDirectorPosition",

            errorBlockPoIsNeedCreateChat : "poIsNeedCreateChat",
            errorBlockPoAddChatToProtocol : "poAddChatToProtocol",

            errorBlockPoCountParticipantsInSoviet : "poCountParticipantsInSoviet",
            errorBlockPoCountParticipantsInAuditCommittee : "poCountParticipantsInAuditCommittee"
        };
    };

    //
    var generatePoRegulations = function(callBack) {
        var requestData = getRequestData();
        $.radomJsonPostWithWaiter(
            "/organization/generate_po_regulations.json",
            JSON.stringify(requestData),
            function(response){
                onSuccessRequest(response, callBack);
            },
            onErrorRequest,
            {
                contentType : 'application/json'
            }
        );
    };

    var onSuccessRequest = function(response, callBack){
        if ($(".error-block").length > 0) {
            $(".error-block").remove();
            $(".has-error").removeClass("has-error");
        }
        if (callBack != null) {
            callBack(response);
        }
    };

    var onErrorRequest = function(response) {
        var message = response.result == "error" && response.message != null ? response.message : response;
        if (response.errors != null && response.errors.errorField != null && $("#" + response.errors.errorField).length > 0) {
            if ($(".error-block").length > 0) {
                $(".error-block").remove();
                $(".has-error").removeClass("has-error");
            }

            var jqErrorField = $("#" + response.errors.errorField);
            jqErrorField.addClass("has-error");
            jqErrorField.append("<span class='error-block help-block'>" + message + "</span>");

            // Скролим до поля с ошибкой
            $('html, body').animate({
                scrollTop: jqErrorField.offset().top - 50
            }, 1000);
        } else {
            bootbox.alert(response.message);
        }
    };

    var loadPageData = function(callBack, errorCallback) {
        errorCallback = errorCallback == null ? function(){} : errorCallback;
        $.radomJsonPost(
            "/organization/get_create_po_page_data.json",
            {},
            callBack,
            errorCallback
        );
    };

    // API метод - создание запроса на регистрацию юр лица
    CooperativeSocietyCreate.createRequest = function(callBack){
        var requestData = getRequestData();
        $(".form-group").removeClass("has-error");
        $(".error-block").remove();
        $.radomJsonPostWithWaiter(
            "/organization/register_po.json",
            JSON.stringify(requestData),
            function(response){
                onSuccessRequest(response, callBack);
            },
            onErrorRequest,
            {
                contentType : 'application/json'
            }
        );
    };

    var appendTextInputTempData = function(tempFormData, jqTextInput) {
        if (tempFormData != null) {
            tempFormData[jqTextInput.attr("id")] = jqTextInput.val();
        }
    };

    var appendDateInputTempData = function(tempFormData, jqTextInput) {
        if (tempFormData != null) {
            var val = toServerDate(jqTextInput.data("DateTimePicker").date().toDate()).format(shortTimeDateFormat)
            tempFormData[jqTextInput.attr("id")] = val;
        }
    };

    var appendTextEditorTempData = function(tempFormData, editor, editorId) {
        if (tempFormData != null) {
            try {
                if (editor != null && editor.getContent() != null) {
                    tempFormData[editorId] = editor.getContent();
                }
            } catch (e) {}
        }
    };

    CooperativeSocietyCreate.getTempData = function() {
        var result = {};
        appendTextInputTempData(result, jqNameInput);
        appendTextInputTempData(result, jqNameShortInput);
        appendTextInputTempData(result, jqEngNameInput);
        appendTextInputTempData(result, jqEngNameShortInput);

        result["mainOkved"] = jqMainOkved.val();
        result["additionalOkveds"] = jqAdditionalOkveds.val();

        result["activityScopeIds"] = activityScopeIds;

        //appendTextEditorTempData(result, shortTargetsEditor, "shortTargets");
        appendTextInputTempData(result, jqShortTargets);
        appendTextEditorTempData(result, fullTargetsEditor, "fullTargets");
        appendTextEditorTempData(result, organizationRegulationsEditor, "organizationRegulations");
        appendTextEditorTempData(result, writeOrganizationRegulationsEditor, "writeOrganizationRegulations");


        // юр адрес
        result["shipTypeFieldName"] = officeOwnerShipTypeValue;
        result["countryId"] = addressBlock.getJqCountryIdInput().val();
        result["postalCode"] = addressBlock.getJqPostalCodeInput().val();
        result["region"] = addressBlock.getJqRegionInput().val();
        result["district"] = addressBlock.getJqDistrictInput().val();
        result["city"] = addressBlock.getJqCityInput().val();
        result["street"] = addressBlock.getJqStreetInput().val();
        result["building"] = addressBlock.getJqBuildingInput().val();
        result["room"] = addressBlock.getJqOfficeInput().val();
        result["geoLocation"] = addressBlock.getJqGeoLocation().val();
        result["geoPosition"] = addressBlock.getJqGeoPosition().val();
        result["officeRentPeriod"] = jqOfficeRentPeriod.val();
        result["regionCode"] = addressBlock.getJqRegionCodeInput().val();
        result["districtDescriptionShort"] = addressBlock.getJqDistrictDescriptionShortInput().val();
        result["cityDescriptionShort"] = addressBlock.getJqCityDescriptionShortInput().val();
        result["streetDescriptionShort"] = addressBlock.getJqStreetDescriptionShortInput().val();
        result["buildingDescription"] = addressBlock.getJqBuildingDescriptionInput().val();
        result["officeDescription"] = addressBlock.getJqOfficeDescriptionInput().val();

        // факт. адрес
        result["factShipTypeFieldName"] = factOfficeOwnerShipTypeValue;
        result["factCountryId"] = factAddressBlock.getJqCountryIdInput().val();
        result["factPostalCode"] = factAddressBlock.getJqPostalCodeInput().val();
        result["factRegion"] = factAddressBlock.getJqRegionInput().val();
        result["factDistrict"] = factAddressBlock.getJqDistrictInput().val();
        result["factCity"] = factAddressBlock.getJqCityInput().val();
        result["factStreet"] = factAddressBlock.getJqStreetInput().val();
        result["factBuilding"] = factAddressBlock.getJqBuildingInput().val();
        result["factRoom"] = factAddressBlock.getJqOfficeInput().val();
        result["factGeoLocation"] = factAddressBlock.getJqGeoLocation().val();
        result["factGeoPosition"] = factAddressBlock.getJqGeoPosition().val();
        result["factOfficeRentPeriod"] = jqFactOfficeRentPeriod.val();
        result["factRegionCode"] = factAddressBlock.getJqRegionCodeInput().val();
        result["factDistrictDescriptionShort"] = factAddressBlock.getJqDistrictDescriptionShortInput().val();
        result["factCityDescriptionShort"] = factAddressBlock.getJqCityDescriptionShortInput().val();
        result["factStreetDescriptionShort"] = factAddressBlock.getJqStreetDescriptionShortInput().val();
        result["factBuildingDescription"] = factAddressBlock.getJqBuildingDescriptionInput().val();
        result["factOfficeDescription"] = factAddressBlock.getJqOfficeDescriptionInput().val();

        result["fieldFilesData"] = getCachedFieldFilesData();

        result["founders"] = founders;
        result["participantsInSoviet"] = participantsInSoviet;
        result["participantsInAuditCommittee"] = participantsInAuditCommittee;

        appendDateInputTempData(result, jqStartDateBatchVoting);
        //appendTextInputTempData(result, jqCountHoursBatchVoting);
        appendDateInputTempData(result, jqRegistrationEndDateBatchVoting);
        appendDateInputTempData(result, jqEndDateBatchVoting);

        appendTextEditorTempData(result, batchVotingDescriptionEditor, "batchVotingDescription");

        result[jqSecretVoting.attr("id")] = jqSecretVoting.val();

        appendTextInputTempData(result, jqEntranceShareFees);
        appendTextInputTempData(result, jqMinShareFees);
        appendTextInputTempData(result, jqMembershipFees);
        appendTextInputTempData(result, jqCommunityEntranceShareFees);
        appendTextInputTempData(result, jqCommunityMinShareFees);
        appendTextInputTempData(result, jqCommunityMembershipFees);

        appendTextInputTempData(result, jqCountDaysToQuiteFromPo);
        appendTextInputTempData(result, jqCountMonthToSharerPay);
        appendTextInputTempData(result, jqMinCreditApproveSovietPO);
        appendTextInputTempData(result, jqMinContractSumApproveSovietPO);
        appendTextInputTempData(result, jqCountDaysPerMeetingOfBoard);
        appendTextInputTempData(result, jqQuorumMeetingOfBoard);

        result["hasStamp"] = jqHasStamp.is(":checked");

        result["onShareDeath"] = $(onShareDeathSelector + ":checked").val();

        result["directorPosition"] = directorPositionId;
        result["whoApprovePosition"] = whoApprovePositionId;
        result["whoApproveDatePay"] = whoApproveDatePayId;
        result["startPeriodPay"] = startPeriodPayId;
        result["sovietOfficePeriod"] = sovietOfficePeriodId;
        result["presidentOfSovietKindWorking"] = presidentOfSovietKindWorkingId;
        result["participantsOfBoardOfficePeriod"] = participantsOfBoardOfficePeriodId;
        result["boardReportFrequency"] = boardReportFrequencyId;
        result["participantsAuditCommitteeOfficePeriod"] = participantsAuditCommitteeOfficePeriodId;

        result["branchesModel"] = branchesModel;
        result["representationsModel"] = representationsModel;

        result["isNeedCreateChat"] = jqNeedCreateChat.is(":checked");
        result["addChatToProtocol"] = jqAddChatToProtocol.is(":checked");

        //result["chooseSourcePoRegulation"] = jqChooseSourcePoRegulation.filter(".active").attr("id");

        jqChooseSourcePoRegulation.each(function(){
            if ($(this).parent().hasClass("active")) {
                result["chooseSourcePoRegulation"] = $(this).attr("id");
            }
        });

        appendTextInputTempData(result, jqCountParticipantsInSoviet);
        appendTextInputTempData(result, jqCountParticipantsInAuditCommittee);

        appendTextInputTempData(result, jqActivityTypesText);

        result["participantsSourceType"] = jqParticipantsSourceType.filter(":checked").attr("id");

        return result;
    };

    return CooperativeSocietyCreate;
});