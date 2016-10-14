<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<%@include file="cyberbrainGridHeaderToolTip.jsp" %>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var myPageSize = 25;

            storeJournalAttention = Ext.create('Ext.data.Store', {
                id		: 'thesaurusStore',
                autoLoad: {start: 0, limit: myPageSize},
                remoteSort: true,
                fields  : ['id',
                    {name: 'tagKvant'},
                    {name: 'textKvant'},
                    {name: 'performerKvantId', mapping: 'performerKvant.id'},
                    {name: 'performerKvant', mapping: 'performerKvant.name'},
                    {name: 'attentionKvant'},
                    {name: 'fixTimeKvant'}],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'myKnowledge/get_journal_attention.json',
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
                title: 'Мое внимание',
                store: storeJournalAttention,
                columns: [{
                    header: 'Дата фиксации',
                    tooltip: 'Дата фиксации',
                    dataIndex: 'fixTimeKvant',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 100,
                    sortable: false
                },{
                    header: 'Теговое описание задачи',
                    tooltip: 'Теговое описание задачи',
                    dataIndex: 'tagKvant',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Краткое описание задачи',
                    tooltip: 'Краткое описание задачи',
                    dataIndex: 'textKvant',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Исполнитель события',
                    tooltip: 'Исполнитель события',
                    dataIndex: 'performerKvant',
                    width: 200,
                    sortable: false
                },{
                    header: 'Квант внимания',
                    tooltip: 'Квант внимания',
                    dataIndex: 'attentionKvant',
                    width: 150,
                    sortable: false
                }],
                plugins: ['headertooltip'],
                bbar: Ext.create('Ext.PagingToolbar', {
                    store : storeJournalAttention,
                    displayInfo : true,
                    displayMsg: '{0} - {1} из {2}'
                }),
                renderTo: 'journalAttention-grid'
            });
        });
    });
</script>

<div id="journalAttention-grid"></div>