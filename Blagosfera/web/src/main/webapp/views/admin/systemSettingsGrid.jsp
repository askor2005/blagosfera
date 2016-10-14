<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            var storeSystemSettings = Ext.create('Ext.data.Store', {
                id		: 'systemSettingsStore',
                autoLoad: true,
                fields  : ['id', 'key', 'value', 'description'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/admin/systemSettings/systemSettings.json',
                    actionMethods: {
                        read: 'POST'
                    },
                    reader: {
                        type: 'json',
                        root: 'items'
                    }
                },
                listeners: {
                    beforeload: function(store, options) {
                        store.proxy.extraParams.key = Ext.getCmp("filterKey").getValue();
                        store.proxy.extraParams.description = Ext.getCmp("filterDescription").getValue();
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'systemSettingsGrid',
                title: 'Параметры таблицы system_settings',
                store: storeSystemSettings,
                columns: [{
                    text     : 'Параметр',
                    dataIndex: 'key',
                    flex: 1
                }, {
                    text     : 'Значение',
                    dataIndex: 'value',
                    flex: 1,
                    renderer: function (value) {return '<span style="overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">' + value + '</span>';}
                }, {
                    text     : 'Описание',
                    dataIndex: 'description',
                    flex: 1
                }],
                tbar: [
                    {
                        id: 'filterKey',
                        emptyText:'Фильтр по параметру',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        id: 'filterDescription',
                        emptyText:'Фильтр по описанию',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Найти',
                        handler: function(){
                            storeSystemSettings.load()
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeSystemSettings,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        $("#field-SystemSettingId").val(record.data.id);

                        $("#field-key").val(record.data.key);
                        $("#field-key").attr("readonly", "");

                        $("#field-value").val(record.data.value);
                        $("#field-description").val(record.data.description);

                        $("#delete-systemSetting-button").css({"display": "inline"});

                        $("#systemSettingLabel").html("Редактировать");
                        $("#editSystemSettingWindow").modal({backdrop:false, keyboard:false});
                    }
                },
                frame: true,
                renderTo: 'systemSettings-grid'
            });
        });
    });
</script>