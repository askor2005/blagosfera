<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<script type="text/javascript" language="javascript">
    function initOrganizationMembersGrid() {
        organizationMembersStore.load();
    }
    Ext.onReady(function () {
        var gridSearchString = "";
        organizationMembersStore = Ext.create('Ext.data.Store', {
            id: 'organizationMembersStore',
            autoLoad: {start: 0, limit: 25},
            fields: ['id', 'name', 'status', 'memberId'],
            pageSize: 25,
            proxy: {
                type: 'ajax',
                url: '/communities/getPossibleOrganizationsMembersToCommunity.json',
                actionMethods: {
                    read: 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'items',
                    totalProperty: 'total'
                }
            },
            listeners: {
                beforeload: function (store, options) {
                    $("#organizationMembersGridSearchResult").hide();
                    store.proxy.extraParams.communityId = communityId;
                    store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                },
                load: function (component, dataList) {
                    if (dataList == null || dataList.length == 0) {
                        $("#organizationMembersGridSearchResult").show();
                        // Ничего не найдено
                        if (gridSearchString != "") {
                            $("#organizationMembersGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                        } else {
                            $("#organizationMembersGridSearchResult").text("По вашему запросу ничего не найдено.");
                        }
                    }
                }
            }
        });

        Ext.create('Ext.grid.Panel', {
            id: 'organizationMembersGrid',
            title: 'Список объединений',
            store: organizationMembersStore,
            columns: [{
                text: 'Наименование объединения',
                dataIndex: 'name',
                flex: 1
            }, {
                text: 'Действие',
                dataIndex: 'name',
                flex: 1,
                renderer  : function(value, myDontKnow, record) {
                    var button = "";
                    switch (record.data.status) {
                        case null:
                            // Запрос на вступление
                            button = "<div class='btn btn-xs btn-primary requestToJoinButton' candidate_id='" + record.data.id + "'>Вступить в объединение</div>";
                            break;
                        case "MEMBER":
                            // Выход из объединения
                            button = "<div class='btn btn-xs btn-danger requestToExcludeButton' member_id='" + record.data.memberId + "'>Выйти из объединения</div>";
                            break;
                        case "REQUEST":
                        case "CONDITION_NOT_DONE_REQUEST":
                        case "CONDITION_DONE_REQUEST":
                            // Отказ от запроса на вступление
                            button = "<div class='btn btn-xs btn-danger cancelRequestButton' member_id='" + record.data.memberId + "'>Отменить запрос вступления</div>";
                            break;
                        case "REQUEST_TO_LEAVE":
                            // Отказ от выхода из объединения
                            button = "<div class='btn btn-xs btn-danger cancelRequestToLeaveButton' member_id='" + record.data.memberId + "'>Отменить запрос на выход</div>";
                            break;
                    }
                    return button;
                }
            }],
            tbar: [
                {
                    id: 'filterName',
                    emptyText: 'Фильтр по наименованию объединения',
                    xtype: 'textfield',
                    labelWidth: '0',
                    flex: 1,
                    listeners: {
                        change: {
                            fn: function (am, searchString) {
                                if (searchString.length > 2) {
                                    gridSearchString = searchString;
                                    organizationMembersStore.load();
                                }
                            },
                            scope: this,
                            buffer: 500
                        },
                        specialkey: function (component, event) {
                            if (event.getKey() == 27) { // ESCAPE
                                component.setValue("");
                                gridSearchString = "";
                                organizationMembersStore.load();
                            }
                        }
                    }
                }
            ],
            dockedItems: [{
                xtype: 'pagingtoolbar',
                store: organizationMembersStore,
                dock: 'bottom',
                displayInfo: true,
                displayMsg: '{0} - {1} из {2}'
            }],
            viewConfig: {
                listeners: {
                    refresh: function (gridview) {
                        if (gridSearchString != null && gridSearchString.length > 2) {
                            var regex = new RegExp('(' + gridSearchString + ')', 'gi');
                            $.each($("div.x-grid-cell-inner", $("#possibleOrganizationMembers-grid")), function (index, div) {
                                var $div = $(div);
                                var html = $div.html();
                                if (html.indexOf("<i ") == -1) {
                                    html = html.replace(regex, "<i style='background-color:#FFFF00;'>$1</i>");
                                    $div.html(html);
                                }
                            });
                        }
                    }
                }
            },
            listeners: {
                itemdblclick: function (dataview, record, item, index, e) {
                    // Вызвать метод с ИД объединения
                    //chooseOrganizationToMember(record.data.id);
                }
            },
            frame: true,
            renderTo: 'possibleOrganizationMembers-grid'
        });
    });

</script>