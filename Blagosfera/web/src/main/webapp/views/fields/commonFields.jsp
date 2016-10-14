<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
    // Листенер изменения занчения элемента универсального списка
    var universalListListener = {};

    // Поле - универальный список
    function initUniversalList(input) {
        var fieldInternalName = input.attr("data-field-name");
        var strValue = input.val();
        if (strValue == null) {
            strValue = "";
        }
        var values = strValue.split(",");
        RameraListEditorModule.init(
                $("[rameraListEditorName=" + fieldInternalName + "]"),
                {
                    labelClasses : ["checkbox-inline"],
                    labelStyle : "margin-left: 10px;",
                    selectedItems: values,
                    selectClasses: ["form-control"],
                    disableEmptyValue: false
                },
                function(event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        input.val(data.value);
                        $(universalListListener).trigger(fieldInternalName, data);
                    }
                }
        );
    }

    // Поле - диапазон дат
    function initDateRange(input) {
        var parameters = {
            minView: 'month',
            timePicker: false,
            timePicker24Hour: false,
            format: "dd.mm.yyyy HH:MM",
            minDate: dateFormat(new Date(0), "dd.mm.yyyy HH:MM")
        };

        if (input.val() == '') {
            parameters["startDate"] = dateFormat("dd.mm.yyyy HH:MM");
            parameters["endDate"] = dateFormat("dd.mm.yyyy HH:MM");
        }
        input.radomDateRangeInput(parameters, function(startDate, endDate) {
        });
    }

    // Поле - дата
    function initDateField(input) {
        input.radomDateInput({
            startView : 2
        });
    }

    // Поле - текстовый редактор
    function initHtmlEditorField(input) {
        var countCharsCheck = input.attr("data-count-chars-check") == "true";
        input.radomTinyMCE({checkChange: true, countCharsCheck : countCharsCheck});
    }

    // Поле - пользователь системы
    function initSharerField($input, currentUser) {
        var $inputSharerId = $("[data-field-name=" + $input.attr("data-field-name") + "_ID]");
        var findSharerUrl = $input.attr("data-sharer-url");

        var onSelectUser = function(userId, userName, addCurrentUserBtn){
            $input.attr("data-sharer-id", userId);
            $input.val(userName);
            $inputSharerId.val(userId);
            if (addCurrentUserBtn != null) {
                addCurrentUserBtn.removeClass("btn-primary");
                addCurrentUserBtn.addClass("btn-danger");
                addCurrentUserBtn.text("Отменить");
            }
        };

        var onClearUser = function(addCurrentUserBtn) {
            $input.removeAttr("data-sharer-id");
            $input.val("");
            $inputSharerId.val("");
            if (addCurrentUserBtn != null) {
                addCurrentUserBtn.removeClass("btn-danger");
                addCurrentUserBtn.addClass("btn-primary");
                addCurrentUserBtn.text("Назначить себя");
            }
        };

        var addCurrentUserBtn = null;
        if (currentUser != null) {
            if ($input.val() != null && $input.val() != "") {
                addCurrentUserBtn = $("<a href='#' style='float: right; width: 128px;' class='btn btn-danger'>Отменить</a>");
            } else {
                addCurrentUserBtn = $("<a href='#' style='float: right; width: 128px;' class='btn btn-primary'>Назначить себя</a>");
            }
            $input.parent().after(addCurrentUserBtn);
            $input.parent().css("right", "136px");
            addCurrentUserBtn.click(function(){
                if ($(this).hasClass("btn-primary")) {
                    onSelectUser(currentUser.id, currentUser.fullName, addCurrentUserBtn);
                } else {
                    onClearUser(addCurrentUserBtn);
                }
                $input.blur();
                return false;
            });

        }

        $input.typeahead({
            triggerLength: 1,
            delay: 500,
            autoSelect: true,
            updater: function(item) {
                onSelectUser(item.id, item.fullName, addCurrentUserBtn);
                return item;
            },
            source:  function (query, process) {
                var data = {
                    query : query,
                    include_context_user : true,
                    "excluded_user_ids[]" : []
                };
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: findSharerUrl,
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
                        return process(false);
                    }
                });
            }
        });

    }

    function initParticipantsListField($input, currentUser) {
        var fieldName = $input.attr("data-field-name");
        var addCurrentUserBtn = null;


        var onSelectUser = function(userId, userName) {
            var fieldType = $input.attr("data-field-type");
            var fieldName = $input.attr("data-field-name");
            if ($("a[data-" + fieldName + "-id=" + userId + "]").size() == 0) {
                var fieldValue = $input.attr("data-source-value");
                if (fieldValue == "") {
                    fieldValue = userId;
                } else {
                    fieldValue = fieldValue + ";" + userId;
                }
                $input.attr("data-source-value", fieldValue);
                var li = "<li>" + userName + " <a data-" + fieldName + "-id='" + userId + "' data-input-id='" + fieldName + "' class='" + fieldName + "-delete-link glyphicon glyphicon-remove' href='#'></a></li>";
                $("ul#" + fieldName + "_" + fieldType).append(li);
                $input.blur();
            }
        };

        if (currentUser != null) {
            addCurrentUserBtn = $("<a href='#' style='float: right; width: 128px;' class='btn btn-primary'>Назначить себя</a>");

            $input.parent().after(addCurrentUserBtn);
            $input.parent().css("right", "136px");
            addCurrentUserBtn.click(function () {
                onSelectUser(currentUser.id, currentUser.fullName);
                addCurrentUserBtn.attr("disabled", "disabled");
                $input.blur();
                return false;
            });
            var userIds = $input.attr("data-source-value");
            if (userIds != null && userIds != "") {
                userIds = userIds.split(";");
                for (var index in userIds) {
                    var userId = userIds[index];
                    userId = parseInt(userId);
                    if (userId == currentUser.id) {
                        addCurrentUserBtn.attr("disabled", "disabled");
                        break;
                    }
                }
            }
        }

        $(document).on("click", "a." + fieldName + "-delete-link", function() {
            var id = parseInt($(this).attr("data-" + fieldName + "-id"));
            if (addCurrentUserBtn != null && currentUser.id == id) {
                addCurrentUserBtn.removeAttr("disabled");
            }

            var input = "#" + $(this).attr("data-input-id");
            $("a[data-" + fieldName + "-id=" + id + "]").parent().remove();

            var fieldValue = $(input).attr("data-source-value");
            var array = fieldValue.split(";");
            var index = array.indexOf(id.toString());
            if (index > -1) {
                array.splice(index, 1);
            }
            fieldValue = array.join(";");
            $(input).attr("data-source-value", fieldValue);
            return false;
        });

        var findSharerUrl = $input.attr("data-sharer-url");
        $input.typeahead({
            triggerLength: 1,
            delay: 500,
            autoSelect: true,
            updater: function(item) {
                onSelectUser(item.id, item.fullName);
                return item;
            },
            source:  function (query, process) {
                var excludedUserIdsStr = $input.attr("data-source-value");
                var excludedUserIds = [];
                if (excludedUserIdsStr != null && excludedUserIdsStr != "") {
                    excludedUserIds = excludedUserIdsStr.split(";");
                }
                var data = {
                    query : query,
                    include_context_user : true,
                    "excluded_user_ids[]" : excludedUserIds
                }
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: findSharerUrl,
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
                        return process(false);
                    }
                });
            }
        });
    }

    function validateOnlyRuText(text) {
        var result = null;
        var reg = /^[^a-zA-Z]*$/;
        if (!reg.test(text)) {
            result = "В поле допустимы только русские символы";
        }
        return result;
    }

    function validateOnlyEnText(text) {
        var result = null;
        var reg = /^[^а-яА-Я]*$/;
        if (!reg.test(text)) {
            result = "В поле допустимы только английские символы";
        }
        return result;
    }

    function validateLink(text) {
        var result = null;
        var reg = /^[-а-яa-zёЁцушщхъфырэчстью0-9_\\\/.\-:~%&?=]*?$/;
        if (!reg.test(text)) {
            result = "Недопустимый символ в названии сайта";
        }
        return result;
    }

    function validateMail(text) {
        var reg = null;
        if (text.indexOf("@") > -1 && text.indexOf("@") != text.length - 1) {
            reg = /^[a-zA-Z0-9_\.+-]+@[a-zA-Z0-9-\.]+$/;
        } else {
            reg = /^[a-zA-Z0-9_\.+-@]+$/;
        }

        var result = null;
        if (!reg.test(text)) {
            result = "Недопустимый символ в электронной почте";
        }
        return result;
    }

    function validateNumber(text) {
        var result = null;
        var reg = /^[0-9]*$/;
        if (!reg.test(text)) {
            result = "В данное поле можно вводить только цифры";
        }
        return result;
    }

    function validateCurrency(text) {
        var reg = null;
        if (text.indexOf("\.") > -1 && text.indexOf("\.") != text.length - 1) {
            reg = /^[0-9]+\.[0-9]{0,2}$/;
        } else {
            reg = /^[0-9.]+$/;
        }

        var result = null;
        if (!reg.test(text)) {
            result = "В данное поле можно вводить только цифры и точку в качестве разделения целой и дробной частей";
        }
        return result;
    }

    //$("[data-field-name=COMMUNITY_INN]").mask("9999999999", {placeholder:"_"} ).attr("placeholder", "__________");
    function initFieldValidation(jqNode) {
        var formBlock = jqNode.closest(".form-group");
        if (jqNode.attr("data-field-type") != null && jqNode.attr("data-field-type") != "") {
            var validateFunc = null;
            switch (jqNode.attr("data-field-type")) {
                case "RU_TEXT": // Только русские символы
                    validateFunc = validateOnlyRuText;
                    break;
                case "EN_TEXT":
                    validateFunc = validateOnlyEnText;
                    break;
                case "LINK": // Сайт
                    validateFunc = validateLink;
                    break;
                case "MAIL":
                    validateFunc = validateMail;
                    break;
                case "MOBILE_PHONE":
                case "LANDLINE_PHONE":
                    jqNode.intlTelInput({
                        autoFormat: true,
                        defaultCountry: 'ru'
                    });
                    break;
                case "DATE":
                    jqNode.mask("39.19.2999", {placeholder: "_"}).attr("placeholder", "__.__.____");
                    break;
                case "NUMBER":
                    validateFunc = validateNumber;
                    break;
                case "CURRENCY":
                    validateFunc = validateCurrency;
                    break;
                default:
                    if (jqNode.attr("mask") != null && jqNode.attr("mask") != "") {
                        jqNode.mask(jqNode.attr("mask"), {placeholder: "_"}).attr("placeholder", jqNode.attr("mask_placeholder"));
                    }
                    break;
            }
            if (validateFunc != null) {
                onChangeInput(jqNode, validateFunc);
            }
        } else {
            jqNode.keyup(function() {
                checkField(formBlock);
            });
        }
        checkField(formBlock);
        jqNode.click(function() {
            checkField(formBlock);
        });
        jqNode.change(function() {
            checkField(formBlock);
        });
        jqNode.blur(function() {
            checkField(formBlock);
        });
    }

    function onChangeInput(jqInput, handleFunc) {
        jqInput.keypress(function(event){
            var code = event.which;
            var newVal = $(this).val() + String.fromCharCode(code);
            var error = handleFunc(newVal);
            var formBlock = $(this).closest(".form-group");
            var errorBlock = formBlock.find(".help-block-error");
            checkField(formBlock);
            if (error == null) {
                formBlock.removeClass("has-error");
                errorBlock.slideUp( "fast", function() {});
                return true;
            } else {
                errorBlock.text(error);
                formBlock.addClass("has-error");
                errorBlock.slideDown( "slow", function() {});
                return false;
            }
        });
    }

    function checkField($div) {
        $div.find("span.glyphicon").remove();
        $div.removeClass("has-error").removeClass("has-warning").removeClass("has-feedback");
        var $input = $div.find("input, select, textarea");

        if ($div.attr("data-required") == "true") {
            if (!$input.val()) {
                $div.find(".fieldContainer").append("<span class='glyphicon glyphicon-remove form-control-feedback' aria-hidden='true'></span>");
                $div.addClass("has-error").addClass("has-feedback");
                $div.find("span.glyphicon").radomTooltip({
                    title : "Данное поле обязательно к заполнению. Если оставить это поле пустым, процент заполнения полей юр. лица будет равен нулю.",
                    placement : "top",
                    container : "body"
                });
            } else {
                //$div.append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
                //$div.addClass("has-success").addClass("has-feedback");
            }
        } else if ($div.attr("data-has-points") == "true") {
            if (!$input.val()) {
                $input.after("<span class='glyphicon glyphicon-warning-sign form-control-feedback' aria-hidden='true'></span>");
                $div.addClass("has-warning").addClass("has-feedback");
                $div.find("span.glyphicon").radomTooltip({
                    title : "Данное поле следует заполнить, так как оно учитывается при расчёте процента заполнения Вашего профиля.",
                    placement : "top",
                    container : "body"
                });
            } else {
                //$div.append("<span class='glyphicon glyphicon-ok form-control-feedback' aria-hidden='true'></span>");
                //$div.addClass("has-success").addClass("has-feedback");
            }
        }
    }

    function initAllFields(currentUser){
        $.mask.definitions['0']='[0]';
        $.mask.definitions['1']='[0-1]';
        $.mask.definitions['2']='[0-2]';
        $.mask.definitions['3']='[0-3]';
        $("[data-field-type]").each(function(){
            var jqNode = $(this);
            switch (jqNode.attr("data-field-type")) {
                case "UNIVERSAL_LIST":
                    initUniversalList(jqNode);
                    break;
                case "DATE_RANGE":
                    initDateRange(jqNode);
                    break;
                case "DATE":
                    initDateField(jqNode);
                    break;
                case "HTML_TEXT":
                    initHtmlEditorField(jqNode);
                    break;
                case "SHARER":
                    initSharerField(jqNode, currentUser);
                    break;
                case "PARTICIPANTS_LIST":
                    initParticipantsListField(jqNode, currentUser);
                    break;
            }
            initFieldValidation(jqNode);

        });
    }
</script>