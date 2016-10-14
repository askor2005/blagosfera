<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    var batchVotingListStoreInited = false;
    var selectedTemplateId = null;
    var batchVotingListStore = null;
    var batchVotingListGrid = null;
    function clearBatchVotingListGrid() {
        if (batchVotingListStore != null) {
            batchVotingListStore.removeAll();
            batchVotingListStore.sync();
        }
    }
    function initBatchVotingListGrid(seoLink, templateId) {
        selectedTemplateId = templateId;
        if (!batchVotingListStoreInited) {
            batchVotingListStoreInited = true;
            Ext.onReady(function () {
                batchVotingListStore = Ext.create('Ext.data.Store', {
                    id: 'batchVotingListStore',
                    autoLoad: {start: 0, limit: 25},
                    fields: ['id', 'subject'],
                    pageSize: 25,
                    proxy: {
                        type: 'ajax',
                        url: '/group/' + seoLink + '/getBatchVotingList.json',
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
                            $("#batchVotingListGridSearchResult").hide();
                            store.proxy.extraParams.templateId = selectedTemplateId;
                        },
                        load: function (component, dataList) {
                            if (dataList == null || dataList.length == 0) {
                                $("#batchVotingListGridSearchResult").show();
                                // Ничего не найдено
                                $("#batchVotingListGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                });

                batchVotingListGrid = Ext.create('Ext.grid.Panel', {
                    id: 'batchVotingListGrid',
                    title: 'Созданные собрания по шаблону',
                    store: batchVotingListStore,
                    columns: [{
                        text: 'Код',
                        dataIndex: 'id',
                        width: "7%",
                    }, {
                        text: 'Наименование собрания',
                        dataIndex: 'subject',
                        width: "92%",
                        renderer: function (value, myDontKnow, record) {
                            return "<a href='/votingsystem/batchVotingPage?batchVotingId=" + record.data.id + "'>" + record.data.subject + "</a>";
                        }
                    }],
                    dockedItems: [{
                        xtype: 'pagingtoolbar',
                        store: batchVotingListStore,
                        dock: 'bottom',
                        displayInfo: true,
                        displayMsg: '{0} - {1} из {2}'
                    }],
                    viewConfig: {},
                    listeners: {},
                    frame: true,
                    renderTo: 'batchVotingList-grid'
                });
            });
        } else {
            batchVotingListStore.reload();
        }
    };
</script>