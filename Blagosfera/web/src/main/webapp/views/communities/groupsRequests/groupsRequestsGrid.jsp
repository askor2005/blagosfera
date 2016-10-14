<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<script type="text/javascript" language="javascript">
    var groupsRequestsStore = null;
    function initGroupsRequestsGrid() {
        var gridSearchString = "";
        Ext.onReady(function () {
            groupsRequestsStore = Ext.create('Ext.data.Store', {
                id: 'groupsRequestsStore',
                autoLoad: {start: 0, limit: 25},
                fields: ['id', 'communityName'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/groups/documentrequests/requests_grid_data.json',
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
                    },
                    load: function(component, dataList) {
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id: 'groupsRequestsGrid',
                title: 'Список пакетов документов',
                store: groupsRequestsStore,
                columns: [{
                    text: 'Название объединения',
                    dataIndex: 'communityName',
                    width: "50%"
                },{
                    text: '',
                    dataIndex: '',
                    width: "29%",
                    renderer  : function(value, myDontKnow, record) {
                        return "<a target='_blank' href='/groups/documentrequests/" + record.data.id + "' class='btn btn-primary btn-xs documentsPack'>Пакет документов</a>";
                    }
                },{
                    text: '',
                    dataIndex: '',
                    width: "20%",
                    renderer  : function(value, myDontKnow, record) {
                        return "<button type='button' class='btn btn-danger btn-xs removeRequest' request_id=" + record.data.id + ">Удалить запрос</button>";
                    }
                }],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: groupsRequestsStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {

                },
                listeners: {},
                frame: true,
                renderTo: 'groupsRequests-grid'
            });
        });
    };
</script>