<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeStep6 = Ext.create('Ext.data.Store', {
                id		: 'step6Store',
                autoLoad: {start: 0, limit: myPageSize},
                fields  : [
                    'knowledge_rep_id',
                    'thesaurus_tag_many_id',
                    'thesaurus_tag_many',
                    'thesaurus_tag_owner_id',
                    'thesaurus_tag_owner',
                    'thesaurus_tag_property_id',
                    'thesaurus_tag_property',
                    'thesaurus_tag_id',
                    'thesaurus_tag',
                    'value_mera'],
                pageSize: myPageSize,
                proxy: {
                    type: 'ajax',
                    url: 'taskManagement/new_object_wizard_form_questions_properties.json',
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
                        if (tagId > 0) {
                            store.proxy.extraParams.tagOwnerId = tagId;
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'step6Grid',
                title	: 'Теперь мне нужно узнать конкретные свойства этого объекта или сместите даты получения свойств во времени',
                store: storeStep6,
                columns: [{
                    header: 'Множество',
                    dataIndex: 'thesaurus_tag_many',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Tег владелец',
                    dataIndex: 'thesaurus_tag_owner',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Свойство',
                    dataIndex: 'thesaurus_tag_property',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Тег',
                    dataIndex: 'thesaurus_tag',
                    editor: 'textfield',
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    },
                    flex: 1,
                    sortable: false,
                    menuDisabled: true
                }, {
                    header: 'Мера',
                    dataIndex: 'value_mera',
                    editor: 'numberfield',
                    flex: 1,
                    sortable: false,
                    menuDisabled: true,
                    renderer: function(val) {
                        if (val === null || val === undefined)
                            return '?';
                        return val;
                    }
                }],
                maxHeight: 300,
                height: 300,
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeStep6,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                plugins: [
                    Ext.create('Ext.grid.plugin.RowEditing', {
                        clicksToEdit: 1,
                        saveBtnText: 'Сохранить',
                        cancelBtnText : "Отмена",
                        errorsText : "Ошибка",
                        listeners: {
                            edit: function (editor, context, eOpts) {
                                var record = context.record.data;
                                var store = context.store;

                                Ext.Ajax.request({
                                    url: 'knowledgeRepository/answerTheQuestionProperty',
                                    jsonData : record,
                                    success  : function(response){
                                        store.load();
                                    }
                                });
                            }
                        }
                    })
                ],
                renderTo: 'step6-grid'
            });
        });
    });
</script>