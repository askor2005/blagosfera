<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<style>
    .documentTypesTreeComboStyle .x-form-field:not(.x-field-form-focus) {
        display: block;
        width: 100%;
        height: 34px;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.42857143;
        color: #555;
        background-color: #fff;
        background-image: none;
        border: 1px solid #ccc;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
        -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    }

    .documentTypesTreeComboStyle .x-form-field {
        display: block;
        width: 100%;
        height: 34px;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.42857143;
        color: #555;
        background-color: #fff;
        background-image: none;
        border: 1px solid #ccc;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
        -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    }

    .documentTypesTreeComboStyle .x-form-item-body:not(.x-form-trigger-wrap-focus) .x-form-trigger {
        border-color: #66afe9;
        outline: 0;
        /*-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6);*/
        /*box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6);*/
    }

    .documentTypesTreeComboStyle .x-form-item-body .x-form-trigger {
        border-color: #66afe9;
        outline: 0;
        /*-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6);*/
        /*box-shadow: inset 0 1px 1px rgba(0,0,0,.075), 0 0 8px rgba(102, 175, 233, .6);*/
    }
</style>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeComboDocumentTypes = Ext.create('Ext.data.TreeStore', {
                id		: 'documentTypesStoreCombo',
                fields  : ['id', 'name', 'key'],
                proxy: {
                    type: 'ajax',
                    url: '/admin/flowOfDocuments/documentTypes.json',
                    actionMethods: {
                        read: 'POST'
                    }
                },
                folderSort: true,
                listeners: {
                    beforeload: function(store, options) {
//                        if (Ext.getCmp("filterName") != undefined) {
//                            store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
//                        }
                    }
                }
            });

            Ext.create('Ext.ux.TreePicker', {
                id: 'documentTypesTreeCombo',
                panelId: 'documentTypesTreePanel',
                renderTo: 'documentTypes-treeCombo',
                width: "100%",
                displayField: 'name',
                valueField: 'id',
                value: '',
                minPickerHeight: 200,
                store: storeComboDocumentTypes,
                cls: 'documentTypesTreeComboStyle'
            });
        });
    });
</script>