<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<%@include file="cyberbrainGridHeaderToolTip.jsp" %>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var myPageSize = 25;

            storeKnowledgeRepository = Ext.create('Ext.data.Store', {
                id		: 'questionsStore',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : [ 'id',
                    'fix_time_change',
                    'time_ready',
                    {name: 'ownerId', mapping: 'owner.id'},
                    {name: 'ownerName', mapping: 'owner.name'},
                    {name: 'tagOwnerId', mapping: 'tag_owner.id'},
                    {name: 'tagOwnerName', mapping: 'tag_owner.name'},
                    {name: 'attributeId', mapping: 'attribute.id'},
                    {name: 'attributeName', mapping: 'attribute.name'},
                    {name: 'tagId', mapping: 'tag.id'},
                    {name: 'tagName', mapping: 'tag.name'},
                    'mera',
                    {name: 'taskId', mapping: 'task.id'},
                    {name: 'taskName', mapping: 'task.name'},
                    'next',
                    {name: 'changeIfId', mapping: 'change_if.id'},
                    {name: 'changeIfName', mapping: 'change_if.name'},
                    'status',
                    'stress',
                    'attention',
                    'show_in_questions'],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'myKnowledge/get_my_knowledge.json',
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
                        store.proxy.extraParams.filterText = document.getElementById("search").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title: 'Хранилище знаний',
                store: storeKnowledgeRepository,
                columns: [{
                    header: 'Дата фиксации',
                    tooltip: 'Дата фиксации',
                    dataIndex: 'fix_time_change',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 100,
                    sortable: false
                },{
                    header: 'Имя владельца',
                    tooltip: 'Имя владельца',
                    dataIndex: 'ownerName',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Имя тега (владелец)',
                    tooltip: 'Имя тега (владелец)',
                    dataIndex: 'tagOwnerName',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Имя тега (атрибут)',
                    tooltip: 'Имя тега (атрибут)',
                    dataIndex: 'attributeName',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Имя тега',
                    tooltip: 'Имя тега',
                    dataIndex: 'tagName',
                    flex: 1,
                    sortable: false
                },{
                    header: 'Мера',
                    tooltip: 'Мера',
                    dataIndex: 'mera',
                    flex: 1,
                    sortable: false
                }],
                plugins: ['headertooltip'],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeKnowledgeRepository,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                renderTo: 'knowledgeRepository-grid'
            });
        });
    });
</script>

<div id="knowledgeRepository-grid"></div>