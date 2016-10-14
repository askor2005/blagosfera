<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    function initSelectTemplatesGrid(seoLink) {
        Ext.onReady(function () {
            var gridSearchString = "";
            batchVotingTemplatesStore = Ext.create('Ext.data.Store', {
                id		: 'batchVotingTemplatesStore',
                autoLoad: {start: 0, limit: 25},
                fields  : ['id', 'subject'],
                pageSize: 25,
                proxy: {
                    type: 'ajax',
                    url: '/group/' + seoLink + '/getBatchVotingTemplates.json',
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
                        $("#batchVotingTemplateGridSearchResult").hide();
                        store.proxy.extraParams.name = Ext.getCmp("filterName").getValue();
                    },
                    load: function(component, dataList) {
                        if (dataList == null || dataList.length == 0) {
                            $("#batchVotingTemplateGridSearchResult").show();
                            // Ничего не найдено
                            if (gridSearchString != "") {
                                $("#batchVotingTemplateGridSearchResult").text("По вашему запросу \"" + gridSearchString + "\" ничего не найдено.");
                            } else {
                                $("#batchVotingTemplateGridSearchResult").text("По вашему запросу ничего не найдено.");
                            }
                        }
                    }
                }
            });

            Ext.create('Ext.grid.Panel', {
                id : 'batchVotingTemplatesGrid',
                title: 'Выбор шаблона',
                store: batchVotingTemplatesStore,
                columns: [{
                    text     : 'Код',
                    dataIndex: 'id',
                    width: "10%",
                }, {
                    text: 'Наименование шаблона',
                    dataIndex: 'subject',
                    width: "22%",
                    renderer : function(value, myDontKnow, record) {
                        return "<a href='/group/" + seoLink + "/batchVotingConstructor.html?templateId=" + record.data.id + "'>" + record.data.subject + "</a>";
                    }
                },
                    {
                        text: 'Автор',
                        dataIndex: 'creator.fullName',
                        width: "25%",
                        renderer : function(value, myDontKnow, record) {
                            var avatarSmall = Images.getResizeUrl(record.data.creator.avatar, "c28");
                            var result =
                                    "<a style='text-align: left; display: inline-block;' class='sharer-item-avatar-link' href='" + record.data.creator.link + "'>" +
                                    "<img src='" + avatarSmall + "' class='img-thumbnail'>" +
                                    "</a>" +
                                    "<a style='padding-left: 5px; display: inline-block;' href='" + record.data.creator.link + "'>" + record.data.creator.shortName + "</a>";
                            return result;
                        }
                    }, {
                        text    : "Последнее собрание",
                        dataIndex: 'lastBatchVotingDate',
                        width: "23%",
                        renderer  : function(value, myDontKnow, record) {
                            var result = "";
                            if (record.data.lastBatchVotingDate != null && record.data.lastBatchVotingDate != "") {
                                var formattedDate = new Date(record.data.lastBatchVotingDate).format("dd.mm.yyyy");
                                var maxId = -1;
                                for (var index in record.data.batchVotingIds) {
                                    var id = record.data.batchVotingIds[index];
                                    maxId = id > maxId ? id : maxId;
                                }
                                result = "<a style='display: inline-block; margin-top: 10px;' href='/votingsystem/batchVotingPage?batchVotingId=" + maxId + "' class=''>" + formattedDate + "</a>";
                            }
                            return result;
                        }
                    }, {
                        text    : "Архив",
                        dataIndex: 'batchVotingIds',
                        width: "20%",
                        renderer  : function(value, myDontKnow, record) {
                            var result = "";
                            if (record.data.batchVotingIds != null && record.data.batchVotingIds.length > -1) {
                                result = record.data.batchVotingIds.length + " " +
                                        stringForms(record.data.batchVotingIds.length, "собрание", "собрания", "собраний");
                            }
                            return "<a href='#' " +
                                    "style='display: inline-block; margin-top: 10px;' " +
                                    "class='batchVotingList' " +
                                    "title='Список собраний' " +
                                    "template_id='" + record.data.id + "'>" + result + "</a>";
                        }
                    }],
                tbar: [
                    {
                        id: 'filterName',
                        emptyText:'Фильтр по наименованию шаблона собрания',
                        xtype: 'textfield',
                        labelWidth: '0',
                        flex : 1,
                        listeners : {
                            change: {
                                fn: function(am, searchString) {
                                    if (searchString.length > 2) {
                                        gridSearchString = searchString;
                                        batchVotingTemplatesStore.load();
                                    }
                                },
                                scope: this,
                                buffer: 500
                            },
                            specialkey: function (component, event) {
                                if (event.getKey() == 27) { // ESCAPE
                                    component.setValue("");
                                    gridSearchString = "";
                                    batchVotingTemplatesStore.load();
                                }
                            }
                        }
                    }
                ],
                dockedItems: [{
                    xtype: 'pagingtoolbar',
                    store: batchVotingTemplatesStore,
                    dock: 'bottom',
                    displayInfo: true,
                    displayMsg: '{0} - {1} из {2}'
                }],
                viewConfig: {
                    listeners: {
                        refresh: function(gridview) {
                            if (gridSearchString != null && gridSearchString.length > 2) {
                                var regex = new RegExp( '(' + gridSearchString + ')', 'gi' );
                                $.each($("div.x-grid-cell-inner", $("#batchVotingTemplates-grid")), function(index, div){
                                    var $div = $(div);
                                    var html = $div.html();
                                    if (html.indexOf("<i ") == -1) {
                                        html = html.replace( regex, "<i style='background-color:#FFFF00;'>$1</i>" );
                                        $div.html(html);
                                    }
                                });
                            }
                        }
                    }
                },
                listeners: {
                    itemclick: function(dataview, record, item, index, e) {
                        window.location.href = '/group/' + seoLink + '/batchVotingConstructor.html?templateId=' + record.data.id;
                    }
                },
                frame: true,
                renderTo: 'batchVotingTemplates-grid'
            });
            $('#selectTemplateWindow').on('shown.bs.modal', function() {
                Ext.getCmp('batchVotingTemplatesGrid').doLayout();
            });
            $("#fullFillFromTemplate").click(function(){
                $("#selectTemplateWindow").modal("show");
            });
        });

    };
</script>