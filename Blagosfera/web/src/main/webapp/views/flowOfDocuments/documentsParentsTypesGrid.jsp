<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>

<script type="text/javascript" language="javascript">
    var storeComboDocumentTypes = null;
    var parentTreePanel = null;
    var selectedParentClassDocumentId = null;

    $(document).ready(function(){
        $('#editDocumentParentTypeWindow').on('shown.bs.modal', function() {
            if (storeComboDocumentTypes == null) {
                initParentClassTreePanel(selectedParentClassDocumentId);
            } else {
                storeComboDocumentTypes.proxy.setExtraParam("full_path_parent_id", selectedParentClassDocumentId);
                storeComboDocumentTypes.load();
            }
        });
        $('#chooseParentClassId').click(function(){
            var selectedItems = parentTreePanel.getSelectionModel().selected.items;
            var selectedItem = null;
            if (selectedItems.length > 0) {
                selectedItem = selectedItems[0]
            }
            selectRootClass(selectedItem);
        });
    });

    function selectRootClass(selectedItem) {
        if (selectedItem == null) {
            bootbox.alert("Необходимо выбрать родительский класс!");
        } else {
            setParentClassDocument(selectedItem.data.id, selectedItem.data.name, selectedItem.data.pathName);
            $("#editDocumentParentTypeWindow").modal("hide");
        }
    }

    function viewParentClassTreePanel(parentId) {
        selectedParentClassDocumentId = parentId;
        $("#editDocumentParentTypeWindow").modal("show");
    };

    function initParentClassTreePanel(parentId) {
        storeComboDocumentTypes = Ext.create('Ext.data.TreeStore', {
            id: 'documentTypesStoreCombo',
            fields: ['id', 'name', 'pathName', 'key'],
            proxy: {
                type: 'ajax',
                url: '/admin/flowOfDocuments/parentDocumentTypes.json',
                actionMethods: {
                    read: 'POST'
                }/*,
                extraParams : {full_path_parent_id : getParentClassDocumentId()}*/
            },
            folderSort: true,
            listeners: {
                beforeload: function (store, options) {
                },
                load : function(){
                    // Необходимо выделить выбранный родительский элемент
                    var rowIndex = storeComboDocumentTypes.find('id', selectedParentClassDocumentId);
                    if (rowIndex > -1) {
                        parentTreePanel.getSelectionModel().select(rowIndex, true);
                    }
                }
            }
        });
        storeComboDocumentTypes.proxy.setExtraParam("full_path_parent_id", parentId);

        parentTreePanel = Ext.create('Ext.tree.Panel', {
            id: 'documentTypesTreeCombo',
            title: 'Классы документов',
            useArrows: true,
            rootVisible: false,
            multiSelect: false,
            singleExpand: true,
            store: storeComboDocumentTypes,
            viewConfig: {
                listeners: {
                    refresh: function(view) {
                        // Необходимо выделить выбранный родительский элемент
                        var rowIndex = storeComboDocumentTypes.find('id', selectedParentClassDocumentId);
                        if (rowIndex > -1) {
                            parentTreePanel.getSelectionModel().select(rowIndex, true);
                        }
                    }
                }
            },
            columns: [{
                xtype: 'treecolumn',
                text     : 'Наименование',
                dataIndex: 'name',
                flex: 1
            }],
            listeners: {
                itemdblclick: function(dataview, record, item, index, e) {
                    selectRootClass(record);
                },
                viewready: function (tree) {
                }
            },
            frame: true,
            renderTo: 'documentTypesParentTreeCombo'
        });

    }
</script>
<!-- Modal -->
<div class="modal fade" role="dialog" id="editDocumentParentTypeWindow" aria-labelledby="documentTypeParentLabel"
     aria-hidden="true" style="z-index: 1041;">
    <div class="modal-dialog" style="width: 800px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title" id="documentTypeParentLabel">Выбрать родительский класс документов</h4>
            </div>
            <div class="modal-body">
                <div id="documentTypesParentTreeCombo"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="chooseParentClassId" data-dismiss="modal">Выбрать</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->