<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<%@include file="cyberbrainGridHeaderToolTip.jsp" %>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var myPageSize = 25;

            storeThesaurus = Ext.create('Ext.data.Store', {
                id		: 'thesaurusStore',
                autoLoad: {start: 0, limit: myPageSize},
                remoteSort: true,
                fields  : [
                    'id',
                    'is_service_tag',
                    'essence',
                    'sinonim',
                    'fix_date_essence',
                    {name: 'essence_owner_id', mapping: 'essence_owner.id'},
                    {name: 'essence_owner_name', mapping: 'essence_owner.name'},
                    'frequency_essence',
                    'attentionFrequency',
                    'is_personal_data'
                    ],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'myKnowledge/get_thesaurus.json',
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
                    beforeload: function(store, options) {
                        store.proxy.extraParams.filterField = 'all';
                        store.proxy.extraParams.filterText = document.getElementById("search").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title: 'Мои термины',
                store: storeThesaurus,
                columns: [{
                    header: 'Дата фиксации',
                    tooltip: 'Дата фиксации',
                    dataIndex: 'fix_date_essence',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 100,
                    sortable: false
                },{
                    header: 'Tег',
                    tooltip: 'Tег',
                    dataIndex: 'essence',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Синоним',
                    tooltip: 'Синоним',
                    dataIndex: 'sinonim',
                    editor: 'textfield',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Имя владельца',
                    tooltip: 'Имя владельца',
                    dataIndex: 'essence_owner_name',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Частота',
                    tooltip: 'Частота',
                    dataIndex: 'frequency_essence',
                    width: 100,
                    sortable: false
                },{
                    header: 'Объем внимания',
                    tooltip: 'Объем внимания',
                    dataIndex: 'attention_frequency',
                    width: 150,
                    sortable: false
                }],
                plugins: ['headertooltip'],
                bbar: Ext.create('Ext.PagingToolbar', {
                    store : storeThesaurus,
                    displayInfo : true,
                    displayMsg: '{0} - {1} из {2}'
                }),
                renderTo: 'thesaurus-grid'
            });
        });
    });
</script>

<div id="thesaurus-grid"></div>