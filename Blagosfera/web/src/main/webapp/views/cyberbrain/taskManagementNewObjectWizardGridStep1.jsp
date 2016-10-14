<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeStep1 = Ext.create('Ext.data.Store', {
                id		: 'step1Store',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : ['id','tag_owner_name', 'mera'],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: '/cyberbrain/taskManagement/get_many_list.json',
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
                        var communityId = $("#new-object-combobox-communities option:selected").attr("data-community-id");
                        if (communityId == "undefined" || communityId == "") {
                            communityId = -1;
                        }

                        store.proxy.extraParams.communityId = communityId;
                        store.proxy.extraParams.filterMany = Ext.getCmp("filterMany").getValue();
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'step1Grid',
                title: 'Посмотрите сведения о чем у меня уже есть (множества)',
                store: storeStep1,
                columns: [{
                    text     : 'Наименование множества',
                    dataIndex: 'tag_owner_name',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }],
                tbar: [
                    {
                        id: 'filterMany',
                        emptyText:'Введите наименование множества для поиска',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Найти',
                        handler: function(){
                            storeStep1.load()
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeStep1,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        $("#new-object-new-many-lbl").html($("#new-object-new-many-lbl").attr("data-caption-selected"));
                        $("#new-object-new-many").attr("data-object-id", record.data.id);
                        $("#new-object-new-many").attr("data-is-numbered-value", record.data.mera);
                        $("#new-object-new-many").attr("disabled", "disabled");
                        $("#new-object-new-many").val(record.data.tag_owner_name);

                        $("#new-object-new-many-is-numbered").attr("disabled", "disabled");
                        $("#new-object-new-many-is-numbered").prop("checked", false);
                        $("#new-object-new-many-is-numbered").prop("checked", record.data.tag_owner_is_numbered);

                        $("#new-object-wizard").bootstrapWizard("disable", 1);
                        $("#new-object-wizard").bootstrapWizard("enable", 3);

                        $("#new-object-new-many-properties").val("-1");
                        $("#new-object-new-many-properties").attr("data-skip", true);
                    }
                },
                height: 300,
                maxHeight: 300,
                frame: true,
                renderTo: 'step1-grid'
            });
        });
    });
</script>