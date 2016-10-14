<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script type="text/javascript" language="javascript">

    var documentGridDataStore = null;
    var documentGridPanel = null;

    var dateStartFilterStr = "";
    var dateEndFilterStr = "";
    var nameFilterStr = "";
    var participantIdFilter = -1;
    var participantTypeFilterStr = "";
    var contentFilterStr = "";
    var currentDocumentClassId = -1;
    var currentDocumentClassName = "";

    var comboParticipantsStore = null;

    // Инициализация датапикера
    function initDateInputs(jqDateControls) {
        $.each(jqDateControls, function(index, input){
            var $input = $(input);
            $input.radomDateInput({
                startView : 2
            });
        });
    };

    function loadDocumentsGrid(documentClassId, documentClassName) {
        currentDocumentClassId = documentClassId;
        currentDocumentClassName = documentClassName;

        // Загружаем участников в фильтр
        getParticipantsOfDocuments(currentDocumentClassId, function(participants){
            var participantsArray = [];
            participantsArray.push([-1, "", "-- Выберите участника --"]);
            for (var index in participants) {
                var participant = participants[index];
                var name = participant.name;
                if (participant.type == "INDIVIDUAL") {
                    name = "Физ лицо " + name;
                } else if (participant.type == "REGISTRATOR") {
                    name = "Регистратор " + name;
                } else if (participant.type == "COMMUNITY_WITH_ORGANIZATION") {
                    name = "Объединение в рамках юр лица \"" + name + "\"";
                } else if (participant.type == "COMMUNITY_WITHOUT_ORGANIZATION") {
                    name = "Объединение \"" + name + "\"";
                }
                participantsArray.push([participant.id, participant.type, name]);
            }

            if (comboParticipantsStore == null) {
                comboParticipantsStore = new Ext.data.SimpleStore({
                    fields: ['id', 'type', 'name'],
                    data : participantsArray
                });
            }

            if (documentGridDataStore == null) {
                initDocumentGrid();
            } else {
                documentGridPanel.setTitle('Список документов "' + currentDocumentClassName + '"');
            }
            comboParticipantsStore.setData(participantsArray);
            filterDocs();
        });
    };

    function filterDocs() {
        documentGridDataStore.setData([]);
        filterDocuments(currentDocumentClassId, dateStartFilterStr, dateEndFilterStr, nameFilterStr, participantTypeFilterStr, participantIdFilter, contentFilterStr, function(filteredDocuments){
            if (filteredDocuments == null || filteredDocuments.length == 0) {
                $("#documentsGridSearchResult").show();
            } else {
                $("#documentsGridSearchResult").hide();
            }
            documentGridDataStore.setData(filteredDocuments);
        });
    };

    function initDocumentGrid() {

        Ext.define('Documents', {
            extend: 'Ext.data.Model',
            fields: ['id', 'name', 'createDate', 'tools']
        });

        documentGridDataStore = Ext.create('Ext.data.Store', {
            model : "Documents",
            //data: documents,
            listeners: {
                beforeload: function(store, options) {
                    /*$("#documentTypesGridSearchResult").hide();
                    if (Ext.getCmp("filterName") != undefined) {
                        store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                    }*/
                },
                load: function(component, dataList) {
                    /*if (dataList == null || dataList.length == 0) {
                        $("#documentTypesGridSearchResult").show();
                        // Ничего не найдено
                        $("#documentTypesGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                    }*/
                }
            }
        });

        documentGridPanel = Ext.create('Ext.grid.Panel', {
            renderTo: "documentsDiv",
            store: documentGridDataStore,
            //width: 400,
            //height: 200,
            title: 'Список документов "' + currentDocumentClassName + '"',
            columns: [
                {
                    text: 'Индекс',
                    dataIndex: 'id',
                    width: '9%'
                },
                {
                    text: 'Наименование документа',
                    dataIndex: 'name',
                    width: '65%',
                    renderer  : function(value, myDontKnow, record) {
                        return '<a href="' + documentLink + record.data.id + '">'+value+'</a>';
                    }
                },
                {
                    text: 'Дата создания',
                    dataIndex: 'createDate',
                    width: '13%'
                },
                {
                    text: 'Инструменты',
                    flex: 1,
                    dataIndex: 'tools',
                    renderer  : function(value, myDontKnow, record) {
                        var result =
                                '<a href="javascript:printDocument(' + record.data.id + ');" >Печать</a>&nbsp;' +
                                '<a href="/document/service/exportDocumentToPdf?document_id=' + record.data.id + '">PDF</a>';
                        return result;
                    }
                }
            ],
            tbar: [
                {
                    id: 'filterDateStart',
                    emptyText: 'Дата от',
                    xtype: 'textfield',
                    labelWidth: 0,
                    //flex : 1,
                    width: 85,
                    listeners: {
                        change: {
                            fn: function(am, searchString) {
                                if (searchString.length == 0) {
                                    dateStartFilterStr = "";
                                    filterDocs();
                                }
                            },
                            scope: this,
                            buffer: 500
                        },
                        specialkey: function (component, event) {
                            if (event.getKey() == 27) { // ESCAPE
                                dateStartFilterStr = "";
                                component.setValue("");
                            }
                        }
                    }
                },{
                    id: 'filterDateEnd',
                    emptyText: 'Дата до',
                    xtype: 'textfield',
                    labelWidth: 0,
                    //flex : 1,
                    width: 85,
                    listeners: {
                        change: {
                            fn: function(am, searchString) {
                                if (searchString.length == 0) {
                                    dateEndFilterStr = "";
                                    filterDocs();
                                }
                            },
                            scope: this,
                            buffer: 500
                        },
                        specialkey: function (component, event) {
                            if (event.getKey() == 27) { // ESCAPE
                                dateEndFilterStr = "";
                                component.setValue("");
                            }
                        }
                    }
                },{
                    id: 'documentFilterName',
                    emptyText: 'Название',
                    xtype: 'textfield',
                    labelWidth: 0,
                    flex : 1,
                    //fieldWidth: 150,
                    //width: 150,
                    listeners: {
                        change: {
                            fn: function(am, searchString) {
                                if (searchString.length > 2) {
                                    nameFilterStr = searchString;
                                    filterDocs();
                                } else if (searchString.length == 0) {
                                    nameFilterStr = "";
                                    filterDocs();
                                }
                            },
                            scope: this,
                            buffer: 500
                        },
                        specialkey: function (component, event) {
                            if (event.getKey() == 27) { // ESCAPE
                                nameFilterStr = "";
                                component.setValue("");
                            }
                        }
                    }
                },{
                    id: 'filterParticipant',
                    xtype: 'combo',
                    emptyText: 'Участник',
                    name: 'name',
                    labelWidth: 0,
                    mode: 'local',
                    store: comboParticipantsStore,
                    displayField: 'name',
                    matchFieldWidth : false,
                    editable: false,
                    triggerAction: 'all',
                    width: 200,
                    listeners: {
                        change: {
                            fn: function(am, searchString) {
                                participantIdFilter = parseFloat(am.selection.data.id);
                                participantTypeFilterStr = am.selection.data.type;
                                if (isNaN(participantIdFilter)) {
                                    participantIdFilter = -1;
                                }
                                filterDocs();
                            },
                            scope: this,
                            buffer: 500
                        },
                        specialkey: function (component, event) {
                            /*if (event.getKey() == 27) { // ESCAPE
                                participantIdFilter = -1;
                                component.setValue("");
                            }*/
                        }
                    }
                },{
                    id: 'filterContent',
                    emptyText: 'Текст',
                    xtype: 'textfield',
                    labelWidth: 0,
                    //flex : 1,
                    width: 200,
                    listeners: {
                        change: {
                            fn: function(am, searchString) {
                                if (searchString.length > 2) {
                                    contentFilterStr = searchString;
                                    filterDocs();
                                } else if (searchString.length == 0) {
                                    contentFilterStr = "";
                                    filterDocs();
                                }
                            },
                            scope: this,
                            buffer: 500
                        },
                        specialkey: function (component, event) {
                            if (event.getKey() == 27) { // ESCAPE
                                contentFilterStr = "";
                                component.setValue("");
                            }
                        }
                    }
                }
            ],

            listeners: {
                itemdblclick: function (dataview, record, item, index, e) {

                },
                viewready: function (tree) {
                    // Инициализация датапикера
                    initDateInputs($("#filterDateStart-inputEl"));
                    initDateInputs($("#filterDateEnd-inputEl"));
                    $("#filterDateStart-inputEl").click(function(event){
                        if (event.originalEvent == null) {
                            dateStartFilterStr = $(this).val();
                            filterDocs();
                        }
                    });
                    $("#filterDateEnd-inputEl").click(function(event){
                        if (event.originalEvent == null) {
                            dateEndFilterStr = $(this).val();
                            filterDocs();
                        }
                    });
                }
            }
        });
    };
</script>