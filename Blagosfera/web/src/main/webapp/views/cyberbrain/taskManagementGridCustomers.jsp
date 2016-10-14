<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeCustomers = Ext.create('Ext.data.Store', {
                id		: 'customersStore',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : ['id',
                    {name: 'performer_id', mapping: 'performer.id'},
                    {name: 'performer_name', mapping: 'performer.name'},
                    {name: 'customer_id', mapping: 'customer.id'},
                    {name: 'customer_name', mapping: 'customer.name'},
                    {name: 'object_id', mapping: 'object.id'},
                    {name: 'object_name', mapping: 'object.name'},
                    'description',
                    {name: 'date_execution', type: 'date'},
                    {name: 'lifecycle_id', mapping: 'lifecycle.id'},
                    {name: 'lifecycle_name', mapping: 'lifecycle.name'},
                    'stress',
                    {name: 'community_id', mapping: 'community.id'},
                    {name: 'community_name', mapping: 'community.name'},
                    {name: 'from_status_id', mapping: 'status_from.id'},
                    {name: 'from_status_name', mapping: 'status_from.name'},
                    {name: 'to_status_id', mapping: 'status_to.id'},
                    {name: 'to_status_name', mapping: 'status_to.name'}],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: '/cyberbrain/taskManagement/get_my_customers.json',
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
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title: 'Мои заказчики',
                store: storeCustomers,
                columns: [{
                    text     : 'Кто',
                    dataIndex: 'customer_name',
                    flex: 1
                }, {
                    text     : 'Что',
                    dataIndex: 'description',
                    flex: 1
                }, {
                    text     : 'Когда',
                    dataIndex: 'date_execution',
                    xtype: 'datecolumn',
                    format: 'Y-m-d',
                    width: 150
                }, {
                    text     : 'ЖЦ Задачи',
                    dataIndex: 'lifecycle_name',
                    flex: 1
                }, {
                    text     : 'Стресс',
                    dataIndex: 'stress',
                    flex: 1
                }],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeCustomers,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                listeners: {
                    itemdblclick: function(dataview, record, item, index, e) {
                        record.data.is_customer = 0;
                        showTaskModalWindow(record.data);
                    }
                },
                renderTo: 'customers-grid'
            });
        });
    });
</script>

<div id="customers-grid"></div>