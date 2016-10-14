if (window.communityEventsManager == null) {
    communityEventsManager = {};
}
function save(url, community, $mainOkved, $additionalOkveds, parentCommunityId, fieldFiles) {

    var mainOkved = null;
    if ($mainOkved != null && $mainOkved.length > 0 && $mainOkved.data('okvedInput').value != null && $mainOkved.data('okvedInput').value.length == 1) {
        var values = $mainOkved.data('okvedInput').value;
        mainOkved = {id : values[0]};
    }
    var additionalOkveds = null;
    if ($additionalOkveds != null && $additionalOkveds.length > 0 && $additionalOkveds.data('okvedInput').value != null && $additionalOkveds.data('okvedInput').value.length > 0) {
        var values = $additionalOkveds.data('okvedInput').value;
        additionalOkveds = [];
        for (var i in values) {
            var value = values[i];
            additionalOkveds.push({id : value});
        }
    }
    community.mainOkved = mainOkved;
    community.additionalOkveds = additionalOkveds;

    community.visible = !$("[name=invisible]").is(":checked");
    community.accessType = $("[name=access_type]:checked").val();

    if (parentCommunityId != null) {
        community.parentId = parentCommunityId;
    }

    var fieldValues = {};
    $("input[data-field-id], textarea[data-field-id]").each(function(i){
        var $this = $(this);
        var value = $this.val();
        if ($this.attr("data-value-text") == "true") {
            var $div = $("<div>");
            $div.html(value);
            value = $div.text();
        }
        fieldValues[$this.attr("data-field-id")] = value;
        if ($this.attr("data-source-value") != null) {
            fieldValues[$this.attr("data-field-id")] = $this.attr("data-source-value");
        }
    });

    for (var groupIndex in community.fieldGroups) {
        var group = community.fieldGroups[groupIndex];
        for (var fieldIndex in group.fields) {
            var field = group.fields[fieldIndex];
            field.value = fieldValues[field.id];
            if (fieldFiles != null) {
                field.files = fieldFiles[field.id];
            }
        }
    }

    $("div.alert").html("").slideUp();
    $("span.help-block-info").slideDown();
    $("span.help-block-error").slideUp();
    $(".has-error").removeClass("has-error");

    /*
    $("input[data-field-type^='PARTICIPANTS_LIST']").each(function(index) {
        var fieldValue = $(this).attr("data-field-value");
        $(this).val(fieldValue);
    });

    var stringData = $("form#create-community-form").serialize();
    if (additionalFormStringData != null && additionalFormStringData != "") {
        stringData += "&" + additionalFormStringData;
    }
    */
    community.selfMember = null;
    $.radomJsonPostWithWaiter(
        url,
        JSON.stringify(community),
        function(response) {
            $("div.alert").html("").slideUp();
            //$("form#create-community-form").changesChecker("refresh");
            $("input[data-field-id], textarea[data-field-id]").each(function(i){
                var $this = $(this);
                $this.attr("data-field-value", $this.val());
                if ($this.attr("data-source-value") != null) {
                    $this.attr("data-field-value", $this.attr("data-source-value"));
                }
            });
            $(communityEventsManager).trigger("community_saved", response);
        },
        function(response){
            if (response.errors) {
                var scrollTop = 9999;
                var errorArr = [];
                $.each(response.errors, function (index, error) {
                    var $parent = $("[data-field-name=" + index + "]").parents("div.form-group");

                    var top = $parent.offset().top;
                    if (top < scrollTop) {
                        scrollTop = top;
                    }
                    $parent.addClass("has-error");
                    $parent.find("span.help-block-info").slideUp();
                    $parent.find("span.help-block-error").html(error).slideDown();
                    errorArr.push(error);
                });
                $.scrollTo({top: scrollTop - 50, left: 0}, 800);
                if (errorArr.length > 0) {
                    bootbox.alert("При выполнении запроса выозникли ошибки:<br/>- " + errorArr.join("<br/>- "));
                }
            } else {
                if (response.message) {
                    bootbox.alert(response.message);
                } else {
                    bootbox.alert("Ошибка при сохранении");
                }
            }
        },
        {
            contentType : 'application/json'
        }
    );
    return false;
}

/*
function getTopicMarkup(topic, level) {
    var indent = "";
    for (i = 0; i < level; i++) {
        indent += "&nbsp;&nbsp;&nbsp;";
    }
    var markup = "<option value='" + topic.id + "'>" + indent + topic.name + "</option>";
    if (topic.children.length > 0) {
        $.each(topic.children, function (index, child) {
            markup += getTopicMarkup(child, level + 1);
        });
    }
    return markup;
}*/

// TODO Переделать получение и отображение полей при создании объединения
/*
function getAdditionalFieldsGroup(rameraListEditorItemId) {
    $.ajax({
        url: "/communities/fields_group.json?rameraListEditorItemId=" + rameraListEditorItemId,
        type: "post",
        data: "{}",
        success: function (response) {
            if (response.result != "error") {
                updateFieldsGroup(response);
            } else {
                bootbox.alert(response.message);
            }
        },
        error: function () {
            console.log("ajax error");
        }
    });
}

function updateFieldsGroup(response) {
    var elementToAppendAfter = $("#access-form-group");

    $("[name='additional-field-row']").remove();
    $("[name='additional-fields-group']").remove();

    response.fields.forEach(function (field) {
        var groupId = field.group.id;
        var groupInternalName = field.group.internalName;
        var groupName = field.group.name;
        var groupElement = $("div[data-group-name='" + groupInternalName + "']");

        if (groupElement.size() == 0) {
            var groupElementHtml = "\
                <div class='panel panel-default' name='additional-fields-group' id='fields-group-panel-" + groupId + "' data-group-name='" + groupInternalName + "'>\
                    <div class='panel-heading'>\
                        <h4 class='panel-title'>\
                            " + groupName + "\
                            <a data-toggle='tooltip' data-placement='left' title='' href='#' onclick='return slideBlock($(this));'\
                               class='glyphicon glyphicon-arrow-up hidden-group-eye' data-original-title='Скрыть блок'></a>\
                        </h4>\
                    </div>\
                    <div id='collapse-profile-" + groupId + "' class='panel-collapse in'>\
                        <div class='panel-body'>\
                        </div>\
                    </div>\
                </div>";

            //вставляем группу перед "Доступ к объединению сторонним лицам"
            elementToAppendAfter.before(groupElementHtml);
        }

        var fieldElementHtml = "";
        if (field.internalName == "COMMUNITY_CHAIRMAN_OF_THE_BOARD1_ID" || field.internalName == "COMMUNITY_CHAIRMAN_OF_THE_BOARD2_ID") {
            fieldElementHtml = communityGetSharerIdField(field);
        } else {
            fieldElementHtml = "\
            <div class='row' name='additional-field-row'>\
                <div class='col-xs-12'>\
                    <div class='form-group ' data-required='" + field.required + "' data-has-points='" + field.points + "' data-field-type='" + field.type + "'>\
                        <label>" + field.name + "</label>";

            if (field.type == "SHARER") {
                fieldElementHtml += communityGetSharerField(field);
            } else if (field.type.substring(0, 'PARTICIPANTS_LIST'.length) == 'PARTICIPANTS_LIST') {
                fieldElementHtml += communityGetParticipantsField(field);
            } else {
                fieldElementHtml += communityGetTextField(field);
            }

            fieldElementHtml += "<ul class='typeahead dropdown-menu'></ul>\
                        <span class='help-block'></span>\
                        <span style='display : none;' class='help-block help-block-error'></span>\
                    </div>\
                </div>\
            </div>";
        }

        $("div[data-group-name='" + groupInternalName + "']").find(".panel-body").append(fieldElementHtml);
    });

    if ($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_TIP_OF_THE_CONSUMER_SOCIETY]").length > 0) {
        communityPrepareSharerControl($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_TIP_OF_THE_CONSUMER_SOCIETY]"));
    }
    if ($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_BOARD_CONSUMER_SOCIETY]").length > 0) {
        communityPrepareSharerControl($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_BOARD_CONSUMER_SOCIETY]"));
    }

    communityInitDateInputs();
    communityInitParticipantsList();
}

function communityGetTextField(field) {
    return "<input type='text' class='form-control'\
                               value='' name='f:" + field.id + "' data-field-name='" + field.internalName + "'\
                               data-field-type='" + field.type + "' data-field-id='" + field.id + "' data-field-value=''\
                               placeholder='" + field.example + "'>";
}

function communityGetSharerIdField(field) {
    return "<input type='hidden' class='form-control' id='" + field.group.internalName + "_SHARER_ID'\
                               value='-1' name='f:" + field.id + "' data-field-name='" + field.internalName + "'\
                               data-field-type='" + field.type + "' data-field-id='" + field.id + "' data-field-value='-1'>";
}

function communityGetSharerField(field) {
    return "<input type='text' class='form-control' id='" + field.group.internalName + "_SHARER'\
                               value='' name='f:" + field.id + "' data-field-name='" + field.internalName + "'\
                               data-field-type='" + field.type + "' data-field-id='" + field.id + "' data-field-value=''\
                               placeholder='" + field.example + "'>";
}

function communityGetParticipantsField(field) {
    return "<input type='text' class='form-control' id='" + field.internalName + "' \
                               value='' name='f:" + field.id + "' data-field-name='" + field.internalName + "'\
                               data-field-type='" + field.type + "' data-field-id='" + field.id + "' data-field-value=''\
                               placeholder='" + field.example + "'><br><ul id='" + field.internalName + "_" + field.type + "'></ul>";
}
*/

/*
function communityInitDateInputs() {
    $.each($("input[data-field-type=DATE]"), function(index, input){
        var $input = $(input);
        $input.radomDateInput({
            startView : 2
        });
    });
}*/
/*
function prepareSharerContol($input, communityId) {
    var $inputSharerId = $("[data-field-name=" + $input.attr("data-field-name") + "_ID]");
    $input.typeahead({
        onSelect: function(item) {
            $input.attr("data-sharer-id", item.value);
            $input.val(item.text);
            $inputSharerId.val(item.value);
        },
        ajax: {
            url: "/communities/" + communityId + "/members.json",
            timeout: 500,
            displayField: "fullName",
            triggerLength: 1,
            method: "post",
            loadingClass: "loading-circle",
            preDispatch: function (query) {
                return {
                    query : query,
                    include_context_sharer : true
                }
            },
            preProcess: function (response) {
                if (response.result == "error") {
                    console.log("ajax error");
                    return false;
                }
                return response;
            }
        }
    });
}

function communityPrepareSharerControl($block) {
    var communityId = $("[name=id]").val();
    if (communityId == undefined) {
        communityId = "-1";
    }
    var groupName = $block.attr("data-group-name");
    var $input = $block.find("input#" + groupName + "_SHARER");
    $input.each(function(){
        prepareSharerContol($(this), communityId);
    });
}
*/

/*
function prepareSharerControlParticipantsList($input, url) {
    $input.typeahead({
        onSelect: function(item) {
            var fieldType = $input.attr("data-field-type");
            var fieldName = $input.attr("data-field-name");
            if ($("a[data-" + fieldName + "-id=" + item.value + "]").size() == 0) {
                var fieldValue = $input.attr("data-field-value");
                if (fieldValue == "") {
                    fieldValue = item.value;
                } else {
                    fieldValue = fieldValue + ";" + item.value;
                }
                $input.attr("data-field-value", fieldValue);
                var li = "<li>" + item.text + " <a data-" + fieldName + "-id='" + item.value + "' data-input-id='" + fieldName + "' class='" + fieldName + "-delete-link glyphicon glyphicon-remove' href='#'></a></li>";
                $("ul#" + fieldName + "_" + fieldType).append(li);
            }
        },
        updater: function(item) {
            return "";
        },
        ajax: {
            url: url,
            timeout: 500,
            displayField: "fullName",
            triggerLength: 1,
            method: "post",
            loadingClass: "loading-circle",
            preDispatch: function (query) {
                return {
                    query : query,
                    include_context_sharer : true
                }
            },
            preProcess: function (response) {
                if (response.result == "error") {
                    console.log("ajax error");
                    return false;
                }
                return response;
            }
        }
    });
}
*/
function slideBlock($link) {
    var $panel = $link.closest(".panel-heading").next(".panel-collapse");
    if($link.hasClass("glyphicon-arrow-up")) {
        $panel.slideUp();
        $link.removeClass("glyphicon-arrow-up").addClass("glyphicon-arrow-down");
    } else if ($link.hasClass("glyphicon-arrow-down")) {
        $panel.slideDown();
        $link.removeClass("glyphicon-arrow-down").addClass("glyphicon-arrow-up");
    }
    return false;
}

/*
function communityInitParticipantsList() {
    //подготовка полей с типом PARTICIPANTS_LIST
    $("input[data-field-type^='PARTICIPANTS_LIST']").each(function(index) {
        var url = "/contacts/search.json";
        var fieldName = $(this).attr("data-field-name");
        var fieldType = $(this).attr("data-field-type");

        // Почему то сделан общий поиск для PARTICIPANTS_LIST
        //if (type == "PARTICIPANTS_LIST_COMMUNITY") {
            var communityId = $("[name=id]").val();
            if (communityId == undefined) {
                communityId = "-1";
            }
            url = "/communities/" + communityId + "/members.json";
        //}
        prepareSharerControlParticipantsList($(this), url);

        $(document).on("click", "a." + fieldName + "-delete-link", function() {
            var id = parseInt($(this).attr("data-" + fieldName + "-id"));
            var input = "#" + $(this).attr("data-input-id");
            $("a[data-" + fieldName + "-id=" + id + "]").parent().remove();

            var fieldValue = $(input).attr("data-field-value");
            var array = fieldValue.split(";");
            var index = array.indexOf(id.toString());
            if (index > -1) {
                array.splice(index, 1);
            }
            fieldValue = array.toString();
            fieldValue = fieldValue.replace(/,/g, ";");
            $(input).attr("data-field-value", fieldValue);
            return false;
        });
    });
}*/

function changeHidden($link, communityId) {
    var currentHidden = $link.attr("data-hidden") == "false" ? false : true;
    var fieldId = $link.attr("data-field-id");
    currentHidden = !currentHidden;
    $.radomJsonPost(
        "/group/" + communityId + "/setFieldVisible.json",
        {
            fieldId : fieldId,
            hidden : currentHidden
        },
        function(response) {
            updateFieldMarkup($link.parent(), currentHidden);
        },
        function(response){
            console.log("hidden change error");
            if (response.message) {
                bootbox.alert(response.message);
            } else {
                bootbox.alert("Ошибка при сохранении");
            }
        }
    );
    return false;
}

function updateFieldMarkup($item, currentHidden) {
    var $link = $item.find("a.hidden-field-eye");
    $link.removeClass().addClass("hidden-field-eye").addClass("glyphicon").addClass(currentHidden ? "glyphicon-eye-close" : "glyphicon-eye-open");
    $link.attr("data-hidden", currentHidden);
    $link.attr('data-original-title', currentHidden ? "Сейчас это поле скрыто" : "Сейчас это поле видно всем");
    var $tooltip = $("#" + $link.attr("aria-describedby"));
    $tooltip.find("div.tooltip-inner").html(currentHidden ? "Сейчас это поле скрыто" : "Сейчас это поле видно всем");
    var $input = $item.find("input[type=text]");
    if (currentHidden) {
        $input.addClass("hidden-field");
    } else {
        $input.removeClass("hidden-field");
    }
}

function communityChangeGroupHidden($link, communityId, fieldsGroupId, currentHidden) {
    $.radomJsonPost(
        "/group/" + communityId + "/setFieldGroupVisible.json",
        {
            fieldsGroupId : fieldsGroupId,
            hidden : currentHidden
        },
        function(response) {
            $.each($link.parents("div.panel").find("div.form-group.hideable"), function (index, div) {
                updateFieldMarkup($(div).closest(".row"), currentHidden);
            });
        },
        function(response){
            console.log("hidden change error");
            if (response.message) {
                bootbox.alert(response.message);
            } else {
                bootbox.alert("Ошибка при сохранении");
            }
        }
    );
    return false;
}

/*
$(document).ready(function () {
    $("form#create-community-form").changesChecker();
    $("input[name=seo_link]").filteredInput("/?");
    $("input#inn").mask("9999999999", {placeholder: "_"}); // TODO Переделать
    $('textarea#description').radomTinyMCE({checkChange: true});
    $('textarea#announcement').maxlength({
        alwaysShow: true,
        placement: 'top'
    });
    $('textarea#charter_description').radomTinyMCE({checkChange: true});
    //$.radomKladr($("div#geo-block"), $("input[name=geo_location]"), $("input[name=geo_position]"), $("div#map"));


    $("input#mainOkved").okvedInput({
        singleSelect: true,
        title: "Выбор основного вида деятельности"
    });
    $("input#additionalOkveds").okvedInput({
        title: "Выбор дополнительных видов деятельности"
    });

    $("input[name=rules]").change(function () {
        if ($("input[name=rules]:checked").length > 0) {
            $("a#create-button").removeAttr("disabled");
        } else {
            $("a#create-button").attr("disabled", "disabled");
        }
    });
    $("[name=association_forms_group]").change(function () {
        var groupId = $("[name=association_forms_group]:checked").val();
        $.radomJsonPost("/communities/association_forms.json", {
            group_id: groupId,
            parent_community_id: $("input[name=parent_id]").val()
        }, function (response) {
            $("#association-form").empty();
            $.each(response, function (index, associationForm) {
                $("#association-form").append("<option value='" + associationForm.id + "'>" + associationForm.name + "</option>");
            });
        });
    });
    $('a.hidden-field-eye').radomTooltip();
    $('a.hidden-group-eye').radomTooltip();

    if ($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_TIP_OF_THE_CONSUMER_SOCIETY]").length > 0) {
        communityPrepareSharerControl($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_TIP_OF_THE_CONSUMER_SOCIETY]"));
    }
    if ($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_BOARD_CONSUMER_SOCIETY]").length > 0) {
        communityPrepareSharerControl($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_BOARD_CONSUMER_SOCIETY]"));
    }
    if ($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_COOPERATIVE_PLOT_MANAGERS]").length > 0) {
        communityPrepareSharerControl($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_COOPERATIVE_PLOT_MANAGERS]"));
    }
    if ($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_REVISOR_CONSUMER_SOCIETY]").length > 0) {
        communityPrepareSharerControl($("[data-group-name=COMMUNITY_ADDITIONAL_GROUP_REVISOR_CONSUMER_SOCIETY]"));
    }
    communityInitDateInputs();
    communityInitParticipantsList();
});*/