<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    var batchVotingVotersStore = null;
    var selectedBatchVotingId = null;
    var batchVotingVotersGridInited = false;
    function clearBatchVotingVotersGrid() {
        if (batchVotingVotersStore != null) {
            batchVotingVotersStore.removeAll();
            batchVotingVotersStore.sync();
        }
    }
    function initBatchVotingVotersGrid(communityId, batchVotingId) {
        selectedBatchVotingId = batchVotingId;
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
                        url: '/group/' + communityId + '/batch_voting_voters_grid_data.json',
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
                        },
                        load: function (component, dataList) {
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
                            var statusStr = "";
                            var color = "red";
                            switch (record.data.status) {
                                case "REGISTERED":
                                    if (record.data.sex) {
                                        statusStr = "Зарегистрирован в собрании";
                                    } else {
                                        statusStr = "Зарегистрирована в собрании";
                                    }
                                    color = "green";
                                    break;
                                case "UNKNOWN":
                                    if (record.data.sex) {
                                        statusStr = "Не зарегистрирован в собрании";
                                    } else {
                                        statusStr = "Не зарегистрирована в собрании";
                                    }
                                    color = "blue";
                                    break;
                            }
                            return "<div style='color: " + color + ";'>" + statusStr + "</div>";
                        }
                    }],
                    dockedItems: [{
                        xtype: 'pagingtoolbar',
                        store: batchVotingVotersStore,
                        dock: 'bottom',
                        displayInfo: true,
                        displayMsg: '{0} - {1} из {2}'
                    }],
                    tbar: [

                    ],
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