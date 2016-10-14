<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeUserProblems = Ext.create('Ext.data.Store', {
                id		: 'userProblemsStore',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : ['id', 'description',
                    {name: 'tag_object_id', mapping: 'tag_object.id'},
                    {name: 'tag_object_name', mapping: 'tag_object.name'},
                    {name: 'tag_many_id', mapping: 'tag_many.id'},
                    {name: 'tag_many_name', mapping: 'tag_many.name'},
                    {name: 'community_id', mapping: 'community.id'},
                    {name: 'community_name', mapping: 'community.name'}],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: '/cyberbrain/taskManagement/get_user_problems.json',
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
                        var communityId = $("#questions-combobox-communities option:selected").attr("data-community-id");
                        if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                            store.proxy.extraParams.communityId = communityId;
                        } else {
                            store.proxy.extraParams.communityId = -1;
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title: 'Мои проблемы',
                store: storeUserProblems,
                columns: [{
                    text     : 'Я не могу сделать',
                    dataIndex: 'description',
                    flex: 1
                }, {
                    text     : 'Над объектом',
                    dataIndex: 'tag_object_name',
                    flex: 1
                }, {
                    text     : 'Который относится к множеству',
                    dataIndex: 'tag_many_name',
                    flex: 1
                }],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeUserProblems,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        showProblemModalWindow(record.data);
                    }
                },
                renderTo: 'userProblems-grid'
            });
        });
    });
</script>

<div id="userProblems-grid"></div>