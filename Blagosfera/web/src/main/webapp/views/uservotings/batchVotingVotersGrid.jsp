<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    var batchVotingVotersStore = null;
    var selectedBatchVotingId = null;
    var selectedVotersStatus = null;
    var batchVotingVotersGridInited = false;
    function clearBatchVotingVotersGrid() {
        if (batchVotingVotersStore != null) {
            batchVotingVotersStore.removeAll();
            batchVotingVotersStore.sync();
        }
    }
    function initBatchVotingVotersGrid(batchVotingId, statusMap, votersStatus) {
        var gridSearchString = "";
        if (statusMap == null) {
            statusMap = {};
            statusMap["REGISTERED"] = {
                "male" : "Зарегистрирован в собрании",
                "female" : "Зарегистрирована в собрании",
                "color" : "green"
            };
            statusMap["UNKNOWN"] = {
                "male" : "Не зарегистрирован в собрании",
                "female" : "Не зарегистрирована в собрании",
                "color" : "blue"
            };
        }

        selectedBatchVotingId = batchVotingId;
        selectedVotersStatus = votersStatus;
        if (!batchVotingVotersGridInited) {
            batchVotingVotersGridInited = true;
            Ext.onReady(function () {
                batchVotingVotersStore = Ext.create('Ext.data.Store', {
                    id: 'batchVotingVotersStore',
                    autoLoad: {start: 0, limit: 25},
                    fields: ['id', 'fullName'],
                    pageSize: 25,
                    proxy: {
                        type: 'ajax',
                        url: '/uservotings/batch_voting_voters_grid_data.json',
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
                            store.proxy.extraParams.batchVotingId = selectedBatchVotingId;
                            //store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                            store.proxy.extraParams.votersStatus = selectedVotersStatus;
                        },
                        load: function(component, dataList) {
                            /*if (dataList == null || dataList.length == 0) {
                                $("#batchVotingVotersGridSearchResult").show();
                                // Ничего не найдено
                                if (gridSearchString != "") {
                                    $("#batchVotingVotersGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                                } else {
                                    $("#batchVotingVotersGridSearchResult").text("По вашему запросу ничего не найдено.");
                                }
                            }*/
                        }
                    }
                });

                Ext.create('Ext.grid.Panel', {
                    id: 'batchVotingVotersGrid',
                    title: 'Участники собрания',
                    store: batchVotingVotersStore,
                    columns: [{
                        text: 'ФИО участника',
                        dataIndex: 'fullName',
                        width: "70%",
                        renderer  : function(value, myDontKnow, record) {
                            var avatarSmall = Images.getResizeUrl(record.data.avatar, "c28");
                            var result =
                                    "<div style='height: 36px;'>" +
                                        "<a style='text-align: left; display: inline-block;' class='sharer-item-avatar-link' href='" + record.data.link + "'>" +
                                            "<img src='" + avatarSmall + "' class='img-thumbnail'>" +
                                        "</a>" +
                                        "<a style='padding-left: 5px; display: inline-block;' href='" + record.data.link + "'>" + record.data.fullName + "</a>" +
                                    "</div>";
                            return result;
                        }
                    },{
                        text: 'Статус',
                        dataIndex: 'fullName',
                        width: "29%",
                        renderer  : function(value, myDontKnow, record) {
                            var gender = record.data.sex ? "male" : "female";
                            var statusStr = statusMap[record.data.status][gender];
                            var color = statusMap[record.data.status]["color"];

                            return "<div style='color: " + color + ";'>" + statusStr + "</div>";
                        }
                    }],
                    /*tbar: [
                        {
                            id: 'filterName',
                            emptyText:'Имя участника собрания',
                            xtype: 'textfield',
                            labelWidth: '0',
                            flex : 1,
                            listeners : {
                                change: {
                                    fn: function(am, searchString) {
                                        if (searchString.length > 2) {
                                            gridSearchString = searchString;
                                            batchVotingVotersStore.load();
                                        }
                                    },
                                    scope: this,
                                    buffer: 500
                                },
                                specialkey: function (component, event) {
                                    if (event.getKey() == 27) { // ESCAPE
                                        component.setValue("");
                                        gridSearchString = "";
                                        batchVotingVotersStore.load();
                                    }
                                }
                            }
                        }
                    ],*/
                    dockedItems: [{
                        xtype: 'pagingtoolbar',
                        store: batchVotingVotersStore,
                        dock: 'bottom',
                        displayInfo: true,
                        displayMsg: '{0} - {1} из {2}'
                    }],
                    viewConfig: {
                        listeners: {
                            refresh: function(gridview) {
                                if (gridSearchString != null && gridSearchString.length > 2) {
                                    var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                    $.each($("div.x-grid-cell-inner", $("#batchVotingVoters-grid")), function(index, div){
                                        var $div = $(div);
                                        var html = $div.html();
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
                    renderTo: 'batchVotingVoters-grid'
                });
            });
        } else {
            batchVotingVotersStore.reload();
        }
    };
</script>