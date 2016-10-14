<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

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
                    url: 'journalAttention/get_journal_attention.json',
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
                        store.proxy.extraParams.fixTimeKvantBegin = fixTimeKvantBegin.getValue();
                        store.proxy.extraParams.fixTimeKvantEnd = fixTimeKvantEnd.getValue();
                        store.proxy.extraParams.tagKvant = document.getElementById("tag-kvant-filter").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                store: storeJournalAttention,
                columns: [{
                    header: 'Теговое описание задачи',
                    dataIndex: 'tagKvant',
                    flex: 1
                },{
                    header: 'Краткое описание задачи',
                    dataIndex: 'textKvant',
                    flex: 1
                },{
                    header: 'Исполнитель события',
                    dataIndex: 'performerKvant',
                    width: 200
                },{
                    header: 'Квант внимания',
                    dataIndex: 'attentionKvant',
                    width: 150
                },{
                    header: 'Дата создания',
                    dataIndex: 'fixTimeKvant',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 150
                }],
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