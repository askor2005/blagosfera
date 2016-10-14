<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    $(document).ready(function() {
        Ext.onReady(function () {
            storeQuestionsProperties = Ext.create('Ext.data.Store', {
                id		: 'questionsPropertiesStore',
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
                    url: 'knowledgeRepository/get_questions_properties.json',
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
                        store.proxy.extraParams.propertiesMany = document.getElementById("questions-properties-many-filter").value;
                        store.proxy.extraParams.propertiesTag = document.getElementById("questions-properties-tag-filter").value;
                        store.proxy.extraParams.propertiesProperty = document.getElementById("questions-properties-property-filter").value;
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                title	: 'Вопросы по свойствам',
                store: storeQuestionsProperties,
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
                tbar: [
                    {
                        id: 'newPropertyManyName',
                        emptyText:'Множество',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        id: 'newTagOwnerName',
                        emptyText:'Тег владелец',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        id: 'newPropertyName',
                        emptyText:'Свойство',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        id: 'newTagName',
                        emptyText:'Тег',
                        xtype: 'textfield',
                        flex : 1
                    }, '-',
                    {
                        id: 'meraValue',
                        emptyText:'Мера',
                        xtype: 'numberfield',
                        flex : 1
                    }, '-',
                    {
                        text: 'Добавить',
                        handler: function(){
                            var communityId = $("#questions-combobox-communities option:selected").attr("data-community-id");
                            if (communityId != -1 && communityId != "undefined" && communityId != undefined) {
                                var newPropertyManyName = Ext.getCmp("newPropertyManyName").getValue();
                                var newTagOwnerName = Ext.getCmp("newTagOwnerName").getValue();
                                var newPropertyName = Ext.getCmp("newPropertyName").getValue();
                                var newTagName = Ext.getCmp("newTagName").getValue();
                                var meraValue = Ext.getCmp("meraValue").getValue();

                                if (newPropertyManyName == null) {
                                    newPropertyManyName = "";
                                }

                                if (newTagOwnerName == null) {
                                    newTagOwnerName = "";
                                }

                                if (newPropertyName == null) {
                                    newPropertyName = "";
                                }

                                if (newTagName == null) {
                                    newTagName = "";
                                }

                                if (meraValue == null) {
                                    meraValue = "";
                                }

                                if (newPropertyManyName != "") {
                                    var data = {};

                                    data.newPropertyManyName = newPropertyManyName;
                                    data.newTagOwnerName = newTagOwnerName;
                                    data.newPropertyName = newPropertyName;
                                    data.newTagName = newTagName;
                                    data.meraValue = meraValue;
                                    data.community_id = communityId;

                                    Ext.Ajax.request({
                                        url: "knowledgeRepository/addNewProperty",
                                        jsonData: JSON.stringify(data),
                                        success: function (response) {
                                            storeQuestions.load();
                                            storeQuestionsMany.load();
                                            storeQuestionsProperties.load();
                                            storeQuestionsTracks.load();

                                            Ext.getCmp("newPropertyManyName").setValue("");
                                            Ext.getCmp("newTagOwnerName").setValue("");
                                            Ext.getCmp("newPropertyName").setValue("");
                                            Ext.getCmp("newTagName").setValue("");
                                            Ext.getCmp("meraValue").setValue("");

                                            // обновим счетчики
                                            getCountsRecordsAndScore();

                                            bootbox.alert("Запись успешно добавленна.");
                                        }
                                    });
                                }
                            } else {
                                bootbox.alert("Перед тем как добавить новую запись нужно выбрать объединение в рамках которого будет добавленна запись.");
                            }
                        }
                    }
                ],
                maxHeight: 400,
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: storeQuestionsProperties,
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
                                        storeQuestions.load();
                                        storeQuestionsTracks.load();

                                        // обновим счетчики
                                        getCountsRecordsAndScore();
                                    }
                                });
                            }
                        }
                    })
                ],
                renderTo: 'questionsProperties-grid'
            });
        });
    });
</script>

<div id="questionsProperties-grid"></div>