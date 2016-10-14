<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">
    table#units-table {
        font-size: 10px;
    }

    table#units-table th {
        text-align: center;
        vertical-align: middle;
    }

    table#units-table th#actions {
        width: 40px;
    }

    table#units-table th#photoCell {
        width: 66px;
    }

    table#units-table img.unit-photo {
        min-width: 50px;
        width: 50px;
        height: auto;
    }

    table#units-table td div {
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>

<script id="inventory-table-row-template" type="x-mustache-tmpl">
    <tr data-unit-id="{{unit.id}}">
        <td>
            <img class="img-thumbnail unit-photo" src='{{unit.photo}}' />
        </td>
        <td><div style="width: 100px;">{{unit.number}} </br></br> <b>GUID:</b> {{unit.guid}}</div></td>
        <td><div style="width: 70px;">{{unit.type.name}}</div></td>
        <td>
            <div style="width: 100px;">
                <a href="{{unit.responsible.user.link}}" class="tooltiped-avatar" data-user-ikp="{{unit.responsible.user.ikp}}">{{unit.responsible.user.shortName}}</a>
            </div>
        </td>
        <td><div style="width: 100px;">{{unit.description}}</div></td>
        <td><div style="width: 100px;">{{leasingDetails}}</div></td>
        <td>
            <div style="width: 40px;">
                <a href="#" class="glyphicon glyphicon-pencil edit-link" data-unit-id="{{unit.id}}"></a>
                <a href="#" class="glyphicon glyphicon-remove delete-link" data-unit-id="{{unit.id}}"></a>
            </div>
        </td>
    </tr>
</script>

<script type="text/javascript">
    var communityId = "${communityId}";

    var InventoryPage = {
        communityId: null,
        defaultPhoto: null,

        init : function(communityId, defaultPhoto) {
            this.defaultPhoto = defaultPhoto;
            this.communityId = communityId;
            this.loadList();
        },

        loadList: function () {
            var self = this;
            var params = {};
            $("#community").val(this.communityId);
            var typeId = $("div#filter-block select#type").val();
            if (typeId) {
                params.type_id = typeId;
            }
            var query = $("div#filter-block input#query").val();
            if (query) {
                params.query = query;
            }
            $.radomJsonGet("/group/" + this.communityId + "/inventory/list.json", params, function (units) {
                var $tbody = $("table#units-table tbody");
                $tbody.empty();
                var template = $("script#inventory-table-row-template").html();
                $.each(units, function (index, unit) {
                    var leasingDetails = '';

                    if (unit.leasedToCommunityId) {
                        if (unit.leasedToCommunityId == self.communityId) {
                            leasingDetails = 'взято в аренду у ' + unit.ownerCommunityName;
                        } else {
                            leasingDetails = 'сдано в аренду в ' + unit.leasedToCommunityName;
                        }
                    } else {
                        leasingDetails = '';
                    }

                    var model = {
                        unit: unit,
                        leasingDetails: leasingDetails
                    };

                    var $markup = $(Mustache.render(template, model));

                    $markup.find("img.unit-photo").radomLightbox(50);

                    $tbody.append($markup);
                });
            });
        },

        addUnit: function () {
            InventoryPage.showDialog();
        },

        editUnit: function (unitId) {
            $.radomJsonGet("/group/" + this.communityId + "/inventory/get.json", {
                unit_id: unitId
            }, function (unit) {
                InventoryPage.showDialog(unit);
            })
        },

        deleteUnit: function (unitId) {
            var self = this;
            bootbox.confirm("Подтвердите удаление", function (result) {
                if (result) {
                    $.radomJsonPost("/group/" + self.communityId + "/inventory/delete.json", {
                        unit_id: unitId
                    }, function () {
                        var $tr = $("tr[data-unit-id=" + unitId + "]");
                        $tr.fadeOut(function () {
                            $tr.remove();
                        });
                    });
                }
            });
        },

        showDialog: function (unit) {
            $("div#inventory-dialog h4").html(unit ? "Редактирование" : "Создание");
            $("div#inventory-dialog input#unit-id").val(unit ? unit.id : "");
            $("div#inventory-dialog input#number").val(unit ? unit.number : "");
            $("div#inventory-dialog select#type-id").val(unit ? unit.type.id : "");
            $("div#inventory-dialog textarea#description").val(unit ? unit.description : "");

            var image = Images.getResizeUrl(unit ? unit.photo : InventoryPage.defaultPhoto, "h250");

            $("div#inventory-dialog input#photo").val(unit ? unit.photo : InventoryPage.defaultPhoto);
            $("div#inventory-dialog img#photo-img").attr("src", image);

            if (unit && unit.responsible) {
                $("div#inventory-dialog input#responsible-name").val(unit.responsible.user.fullName).attr("readonly", "readonly");
                $("div#inventory-dialog input#responsible-id").val(unit.responsible.id);
                $("div#inventory-dialog a#clear-responsible-link").show();
            } else {
                $("div#inventory-dialog input#responsible-name").val("").removeAttr("readonly");
                $("div#inventory-dialog input#responsible-id").val("");
                $("div#inventory-dialog a#clear-responsible-link").hide();
            }

            var readOnly = false;

            if (unit && unit.leasedToCommunityId) {
                if (unit.leasedToCommunityId == self.communityId) {
                    readOnly = true;
                    $("div#inventory-dialog input#leased_to_community_name").val("взято в аренду у " + unit.ownerCommunityName).attr("readonly", "readonly");
                    $("div#inventory-dialog input#leased_to_community_id").val("");
                } else {
                    $("div#inventory-dialog input#leased_to_community_name").val(unit.leasedToCommunityName).attr("readonly", "readonly");
                    $("div#inventory-dialog input#leased_to_community_id").val(unit.leasedToCommunityId);
                }
            } else {
                $("div#inventory-dialog input#leased_to_community_name").val("").removeAttr("readonly");
                $("div#inventory-dialog input#leased_to_community_id").val("");
                $("div#inventory-dialog a#clear-leased_to-link").hide();
            }

            if (readOnly) {
                $("div#inventory-dialog button#save-button").hide();
                $("div#inventory-dialog a#clear-responsible-link").hide();
                $("div#inventory-dialog a#clear-leased_to-link").hide();
                $("a#upload-photo-link").hide();
            } else {
                $("div#inventory-dialog a#clear-leased_to-link").show();
                $("a#upload-photo-link").show();
            }

            $("div#inventory-dialog input#leased_to_community_name").prop("disabled", readOnly);
            $("div#inventory-dialog input#responsible-name").prop("disabled", readOnly);
            $("div#inventory-dialog textarea#description").prop("disabled", readOnly);
            $("div#inventory-dialog select#type-id").prop("disabled", readOnly);
            $("div#inventory-dialog input#number").prop("disabled", readOnly);

            $("div#inventory-dialog").modal("show");
        },

        closeDialog: function () {
            $("div#inventory-dialog").modal("hide");
        },

        uploadPhoto: function () {
            $.radomUpload("photo", "/images/upload.json", ["bmp", "gif", "png", "jpeg", "jpg"], function (response) {
                if (response.result == "success") {
                    var image = Images.getResizeUrl(response.image, "h250");
                    $("div#inventory-dialog input#photo").val(response.image);
                    $("div#inventory-dialog img#photo-img").attr("src", image);
                } else {
                    bootbox.alert("Ошибка загрузки фото");
                }
            });
        },

        setResponsible: function (item) {
            $("div#inventory-dialog input#responsible-name").attr("readonly", "readonly");
            $("div#inventory-dialog input#responsible-id").val(item.memberId);
            $("div#inventory-dialog a#clear-responsible-link").show();
        },

        clearResponsible: function () {
            $("div#inventory-dialog input#responsible-name").val("").removeAttr("readonly").focus();
            $("div#inventory-dialog input#responsible-id").val("");
            $("div#inventory-dialog a#clear-responsible-link").hide();
        },

        setLeasedTo: function (item) {
            $("div#inventory-dialog input#leased_to_community_name").attr("readonly", "readonly");
            $("div#inventory-dialog input#leased_to_community_id").val(item.id);
            $("div#inventory-dialog a#clear-leased_to-link").show();
        },

        clearLeasedTo: function () {
            $("div#inventory-dialog input#leased_to_community_name").val("").removeAttr("readonly").focus();
            $("div#inventory-dialog input#leased_to_community_id").val("");
            $("div#inventory-dialog a#clear-leased_to-link").hide();
        },

        saveUnit: function () {
            //$("div#inventory-dialog :input").serialize()
            var saveUnitDto = {
                id : $("#unit-id").val(),
                communityId : $("#community").val(),
                number : $("#number").val(),
                photo : $("#photo").val(),
                responsibleId : $("#responsible-id").val(),
                description : $("#description").val(),
                typeId : $("#type-id").val(),
                leasedCommunityId : $("#leased_to_community_id").val()
            };
            $.radomJsonPost(
                    "/group/" + this.communityId + "/inventory/save.json",
                    JSON.stringify(saveUnitDto),
                    function (unit) {
                        InventoryPage.loadList();
                        InventoryPage.closeDialog();
                    },
                    null,
                    {
                        contentType : 'application/json'
                    }
            );
        }

    };

    var leasedToTypeaheadOptions = {
        triggerLength: 1,
        delay: 100,
        autoSelect: true,
        updater: function (item) {
            InventoryPage.setLeasedTo(item);
            return item;
        },
        source:  function (query, process) {
            var data = {
                query: query,
                page: 1,
                per_page: 8,
                check_parent: false
            };
            return $.ajax({
                type: "post",
                dataType: "json",
                url: "/communities/search.json",
                data: data,
                success: function (data) {
                    return process(data.list);
                },
                error: function () {
                    console.log("ajax error");
                    return process(false);
                }
            });
        }



        /*onSelect: function (item) {
            InventoryPage.setLeasedTo(item);
        },
        ajax: {
            url: "/communities/search.json",
            timeout: 100,
            displayField: "name",
            triggerLength: 4,
            method: "post",
            loadingClass: "loading-circle",
            preDispatch: function (query) {
                return {
                    query: query,
                    page: 1,
                    per_page: 8,
                    check_parent: false
                }
            },
            preProcess: function (response) {
                if (response.result == "error") {
                    console.log("ajax error");
                    return false;
                }

                var communities = [];

                $.each(response.list, function (index, item) {
                    communities.push(item);
                });

                return communities;
            }
        }*/
    };

    function loadInventoryPageData(communityId, callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/inventory_page_data.json",
                {},
                callBack
        );
    }

    $(document).ready(function () {
        loadInventoryPageData(communityId, function(communityInventoryPageData) {
            initInventoryPage(communityId, communityInventoryPageData.defaultPhoto, communityInventoryPageData.types);
            initCommunityHead(communityInventoryPageData.community);
            initCommunityMenu(communityInventoryPageData.community);
        });
    });

    function initInventoryPage(communityId, defaultPhoto, types) {
        var self = this;
        var inventoryUnitTypesTemplate = $("#inventoryUnitTypesTemplate").html();
        Mustache.parse(inventoryUnitTypesTemplate);
        var typesMarkup = Mustache.render(inventoryUnitTypesTemplate, { types : types });
        $("#type").append(typesMarkup);
        $("#type-id").append(typesMarkup);

        $("table#units-table").fixMe();

        $("table#units-table").on("click", "a.edit-link", function () {
            InventoryPage.editUnit($(this).attr("data-unit-id"));
            return false;
        }).on("click", "a.delete-link", function () {
            InventoryPage.deleteUnit($(this).attr("data-unit-id"));
            return false;
        });

        $("div#filter-block a#add-link").click(function () {
            InventoryPage.addUnit();
            return false;
        });

        $("div#filter-block input#query").callbackInput(100, 3, function () {
            InventoryPage.loadList();
        });

        $("div#filter-block select#type").change(function () {
            InventoryPage.loadList();
        });

        $("div#inventory-dialog button#cancel-button").click(function () {
            InventoryPage.closeDialog();
        });

        $("div#inventory-dialog button#save-button").click(function () {
            InventoryPage.saveUnit();
        });

        $("div#inventory-dialog a#upload-photo-link").click(function () {
            InventoryPage.uploadPhoto();
            return false;
        });

        $("div#inventory-dialog textarea#description").maxlength({
            alwaysShow: true,
            placement: "top"
        });

        $("div#inventory-dialog a#clear-responsible-link").click(function () {
            InventoryPage.clearResponsible();
            return false;
        });

        $("div#inventory-dialog a#clear-leased_to-link").click(function () {
            InventoryPage.clearLeasedTo();
            return false;
        });

        $("div#inventory-dialog input#responsible-name").typeahead({
            triggerLength: 1,
            delay: 100,
            autoSelect: true,
            updater: function (item) {
                InventoryPage.setResponsible(item);
                return item;
            },
            source:  function (query, process) {
                var data = {
                    query: query,
                    include_context_user : true
                };
                return $.ajax({
                    type: "post",
                    dataType: "json",
                    url: "/group/" + self.communityId + "/search_members.json",
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

        $("div#inventory-dialog input#leased_to_community_name").typeahead(leasedToTypeaheadOptions);

        InventoryPage.init(communityId, defaultPhoto);
    }
</script>

<script id="inventoryUnitTypesTemplate" type="x-tmpl-mustache">
    {{#types}}
        <option value="{{id}}">{{name}}</option>
    {{/types}}
</script>

<t:insertAttribute name="communityHeader"/>
<h2>Инвентаризация</h2>
<hr/>

<div class="row" id="filter-block">
    <div class="col-xs-4">
        <div class="form-group">
            <label>Фильтр по типу</label>
            <select class="form-control" id="type">
                <option value="">Все</option>
                <%--<c:forEach items="${types}" var="t">
                    <option value="${t.id}">${t.name}</option>
                </c:forEach>--%>
            </select>
        </div>
    </div>
    <div class="col-xs-5">
        <div class="form-group">
            <label>Фильтр по инвентарному номеру</label>
            <input class="form-control" type="text" id="query" placeholder="Инвентарный номер"/>
        </div>
    </div>

    <div class="col-xs-3">
        <div class="form-group">
            <label>&nbsp;</label>
            <a href="#" class="btn btn-primary btn-block" id="add-link">Добавить объект</a>
        </div>
    </div>
</div>

<hr/>

<table class="table" id="units-table">
    <thead>
    <tr>
        <th id="photoCell">Фото</th>
        <th>Инвентарный <br/> номер</th>
        <th>Тип</th>
        <th>Ответственное <br/> лицо</th>
        <th>Описание</th>
        <th>Аренда</th>
        <th id="actions"></th>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>

<div class="modal fade" id="inventory-dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title"></h4>
            </div>

            <input type="hidden" id="unit-id" name="unit_id"/>
            <input type="hidden" id="community" name="community_id"/>

            <div class="modal-body">
                <div class="form-group">
                    <label>Инвентарный номер</label>
                    <input type="text" class="form-control" id="number" name="number"/>
                </div>
                <div class="form-group">
                    <label>Тип</label>
                    <select class="form-control" id="type-id" name="type_id">
                        <option value="">Выберите тип</option>
                        <%--<c:forEach items="${types}" var="t">
                            <option value="${t.id}">${t.name}</option>
                        </c:forEach>--%>
                    </select>
                </div>
                <div class="form-group">
                    <label>Ответственное лицо</label>
                    <input type="text" id="responsible-name" class="form-control"/>
                    <input type="hidden" id="responsible-id" name="responsible_id"/>
                    <a href="#" id="clear-responsible-link" class="pull-right">Изменить</a>
                </div>
                <div class="form-group">
                    <label>Описание</label>
                    <textarea class="form-control" id="description" name="description" rows="5"
                              maxlength="1024"></textarea>
                </div>
                <div class="form-group">
                    <label>Фото</label>

                    <div class="text-center">
                        <img class="img-thumbnail" id="photo-img"/>
                    </div>
                    <input type="hidden" id="photo" name="photo"/>
                    <br/>

                    <div class="text-center">
                        <a href="#" class="btn btn-primary" id="upload-photo-link">Загрузить фото</a>
                    </div>
                </div>
                <div class="form-group">
                    <label>Аренда</label>
                    <input type="text" id="leased_to_community_name" class="form-control"/>
                    <input type="hidden" id="leased_to_community_id" name="leased_to_community_id"/>
                    <a href="#" id="clear-leased_to-link" class="pull-right">Изменить</a>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="cancel-button">Отмена</button>
                <button type="button" class="btn btn-primary" id="save-button">Сохранить</button>
            </div>
        </div>
    </div>
</div>
