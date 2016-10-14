<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    var batchVotingsStore = null;
    function initBatchVotingsGrid(filterParameters) {
        Ext.onReady(function () {
            batchVotingsStore = Ext.create('Ext.data.Store', {
                id: 'batchVotingsStore',
                autoLoad: {start: 0, limit: 25},
                fields: ['id', 'subject', 'state', 'shortOwnerName', 'startDate', 'endDate'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/uservotings/batch_votings_page_grid_data.json',
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
                        $("#batchVotingsGridSearchResult").hide();
                        store.proxy.extraParams.ownerId = filterParameters.ownerId;
                        store.proxy.extraParams.startDateStart = filterParameters.startDateStart;
                        store.proxy.extraParams.startDateEnd = filterParameters.startDateEnd;
                        store.proxy.extraParams.endDateStart = filterParameters.endDateStart;
                        store.proxy.extraParams.endDateEnd = filterParameters.endDateEnd;
                        store.proxy.extraParams.state = filterParameters.state;
                        store.proxy.extraParams.subject = filterParameters.subject;
                    },
                    load: function (component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#batchVotingsGridSearchResult").show();
                            // Ничего не найдено
                            $("#batchVotingsGridSearchResult").text("По вашему запросу ничего не найдено.");
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id: 'batchVotingListGrid',
                title: 'Собрания объединения',
                store: batchVotingsStore,
                columns: [{
                    text: 'Код',
                    dataIndex: 'id',
                    width: "7%",
                }, {
                    text: 'Название собрания',
                    dataIndex: 'subject',
                    width: "27%",
                    renderer  : function(value, myDontKnow, record) {
                        var result =
                                "<a href='/votingsystem/batchVotingPage?batchVotingId=" + record.data.id + "' " +
                                "title='Перейти к собранию'>" +
                                    record.data.subject +
                                "</a>";
                        return result;
                    }
                }, {
                    text: '',
                    dataIndex: 'templateLink',
                    width: "7%",
                    renderer  : function(value, myDontKnow, record) {
                        var templateLinkHtml = "";
                        if (record.data.templateLink != null) {
                            templateLinkHtml =
                                    "<a class='btn btn-info btn-xs' href='" + record.data.templateLink + "' title='Перейти к шаблону собрания'>" +
                                        "<i class='fa fa-fw fa-files-o'></i>" +
                                    "</a>";
                        }
                        return templateLinkHtml;
                    }
                }, {
                    text: 'Автор',
                    dataIndex: 'shortOwnerName',
                    width: "10%",
                    renderer  : function(value, myDontKnow, record) {
                        var result =
                            "<a href='#' class='batchVotingOwner' owner_id='" + record.data.ownerId + "' owner_name='" + record.data.shortOwnerName + "'" +
                            "title='Установить в фильтр создателя собрания - " + record.data.shortOwnerName + "'>" +
                                record.data.shortOwnerName +
                            "</a>";
                        return result;
                    }
                }, {
                    text: 'Дата начала',
                    dataIndex: 'startDate',
                    width: "13%",
                    renderer  : function(value, myDontKnow, record) {
                        var result = "";
                        if (record.data.startDate != null && record.data.startDate != "") {
                            var formattedDate = new Date(record.data.startDate).format("dd.mm.yyyy");
                            result =
                                "<a href='#' class='batchVotingStartDate' start_date='" + formattedDate + "'" +
                                "title='Установить в фильтр дату начала от " + formattedDate + " и дату начала до " + formattedDate + "'>" +
                                    formattedDate +
                                "</a>";
                        }
                        return result;
                    }
                }, {
                    text: 'Дата окончания',
                    dataIndex: 'endDate',
                    width: "13%",
                    renderer  : function(value, myDontKnow, record) {
                        var result = "";
                        if (record.data.endDate != null && record.data.endDate != "") {
                            var formattedDate = new Date(record.data.endDate).format("dd.mm.yyyy");
                            result =
                                "<a href='#' class='batchVotingEndDate' end_date='" + formattedDate + "'" +
                                "title='Установить в фильтр дату окончания от " + formattedDate + " и дату окончания до " + formattedDate + "'>" +
                                    formattedDate +
                                "</a>";
                        }
                        return result;
                    }
                }, {
                    text: 'Участники',
                    dataIndex: 'votersCount',
                    width: "7%",
                    renderer  : function(value, myDontKnow, record) {
                        var votersCountForm = stringForms(record.data.votersCount, "человек", "человека", "человек");
                        var registeredVotersCountForm = stringForms(record.data.registeredVotersCount, "человек", "человека", "человек");
                        var result =
                                "<a href='#' class='batchVotingVoters' batch_voting_id='" + record.data.id + "' " +
                                "title='Заявлено для участия " + record.data.votersCount + " " + votersCountForm +
                                ". Участвовали " + record.data.registeredVotersCount  + " " + registeredVotersCountForm +"'>" +
                                    record.data.votersCount + "/" + record.data.registeredVotersCount  +
                                "</a>";
                        return result;
                    }
                }, {
                    text: 'Статус',
                    dataIndex: 'state',
                    width: "15%",
                    renderer  : function(value, myDontKnow, record) {
                        var result = "";
                        var color = "";
                        switch(record.data.state) {
                            case "VOTING":
                                result = "Активное";
                                color = "red";
                                break;
                            case "VOTERS_REGISTRATION":
                                result = "Регистрация участников";
                                color = "blue";
                                break;
                            case "FINISHED":
                                result = "Завершено";
                                color = "green";
                                break;
                        }
                        return "<a style='color: " + color + ";' href='#' class='batchVotingState' state='" + record.data.state + "'  title='Установить в фильтр статус \"" + result + "\"'>" + result + "</a>";
                    }
                }],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: batchVotingsStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                tbar: [

                ],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (filterParameters.subject != null && filterParameters.subject.length > 2) {
                                var regex = new RegExp( '(' + filterParameters.subject + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", $("#batchVotings-grid")), function(index, div){
                                    var $div = $(div);
                                    var html = $div.html();
                                    console.log(html);
                                    if (html.indexOf("<i ") == -1) {
                                        html = html.replace( regex, "<i style='background-color:#FFFF00;'>$1</i>" );
                                        $div.html(html);
                                    }
                                });
                            }
                        }
                    }
                },
                listeners: {},
                frame: true,
                renderTo: 'batchVotings-grid'
            });
        });

    };
</script>