<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
    .fieldContainer {
        position: relative; width: 100%;
    }

    .fieldFileContainer {
        position: absolute; right: 0px; /*display: none;*/ top: 0px; z-index: 100;
    }

    .fieldFileContainer .browseFieldFile {
        display: inline-block;
        background: url("/img/browse.png") no-repeat;
        background-size: 39px 34px;
        width: 39px;
        height: 34px;
        cursor: pointer;
        margin-right: 2px;
    }
    .fieldFileContainer .uploadFieldFile {
        display: inline-block;
        background: url("/img/upload.png") no-repeat;
        background-size: 39px 34px;
        width: 39px;
        height: 34px;
        cursor: pointer;
        display: none;
    }
    .fieldFileContainer .deleteFieldFile {
        display: inline-block;
        background: url("/img/delete.png") no-repeat;
        background-size: 39px 34px;
        width: 39px;
        height: 34px;
        cursor: pointer;
        display: none;
    }

    .fileLink {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        width: 100px;
        display: inline-block;
        vertical-align: top;
    }
    .fileIco {
        display: inline-block;
        vertical-align: top;
        color: #595959;
    }
    #attachedFilesBlock {
        overflow: auto;
    }
    .attachedFileBlock {
        display: inline-block; vertical-align: top; padding: 15px; border: #ccc 1px solid; margin: 5px;
    }
</style>
<script type="application/javascript">

    var getAcrobatInfo = function() {

        var getBrowserName = function() {
            return this.name = this.name || function() {
                        var userAgent = navigator ? navigator.userAgent.toLowerCase() : "other";

                        if(userAgent.indexOf("chrome") > -1)        return "chrome";
                        else if(userAgent.indexOf("safari") > -1)   return "safari";
                        else if(userAgent.indexOf("msie") > -1)     return "ie";
                        else if(userAgent.indexOf("firefox") > -1)  return "firefox";
                        return userAgent;
                    }();
        };

        var getActiveXObject = function(name) {
            try { return new ActiveXObject(name); } catch(e) {}
        };

        var getNavigatorPlugin = function(name) {
            for(key in navigator.plugins) {
                var plugin = navigator.plugins[key];
                var pluginName = plugin.name;
                if (pluginName != null && pluginName.search(name) > -1) {
                    return plugin;
                }
            }
        };

        var getPDFPlugin = function() {
            return this.plugin = this.plugin || function() {
                if(getBrowserName() == 'ie') {
                    //
                    // load the activeX control
                    // AcroPDF.PDF is used by version 7 and later
                    // PDF.PdfCtrl is used by version 6 and earlier
                    return getActiveXObject('AcroPDF.PDF') || getActiveXObject('PDF.PdfCtrl');
                }
                else {
                    return getNavigatorPlugin('Adobe Acrobat') || getNavigatorPlugin('Chrome PDF Viewer') || getNavigatorPlugin('WebKit built-in PDF');
                }
            }();
        };

        var isAcrobatInstalled = function() {
            return !!getPDFPlugin();
        };

        var getAcrobatVersion = function() {
            try {
                var plugin = getPDFPlugin();

                if(getBrowserName() == 'ie') {
                    var versions = plugin.GetVersions().split(',');
                    var latest   = versions[0].split('=');
                    return parseFloat(latest[1]);
                }

                if(plugin.version) return parseInt(plugin.version);
                return plugin.name

            }
            catch(e) {
                return null;
            }
        }

        //
        // The returned object
        //
        return {
            browser:        getBrowserName(),
            acrobat:        isAcrobatInstalled() ? 'installed' : false,
            acrobatVersion: getAcrobatVersion()
        };
    };

    var DEFAULT_FIELD_FILE_TYPES = ["bmp", "gif", "png", "jpeg", "jpg", "pdf", "PDF"];
    var DEFAULT_FIELD_FILE_URL = "/files/upload.json";

    // Загрузка файла в поле
    function uploadFieldFile(fieldFileUploadUrl, fieldFileTypes, fileParameters, callBack) {
        $.radomUpload(
            "attachedFile",
            fieldFileUploadUrl,
            fieldFileTypes,
            function(response) {
                if (response.result == "success") {
                    callBack(response.url);
                    bootbox.alert("Документ прикреплён.");
                } else {
                    bootbox.alert("Ошибка загрузки документа.");
                }
            },fileParameters
        );
    }

    function drawAttachedFiles() {
        for (var index in currentFilesData) {
            currentFilesData[index]["index"] = index;
        }

        var jqAttachedFilesContent = $(Mustache.render(attachedFilesTemplate, {filesArray : currentFilesData, hasRightsToEdit : currentUserHasRightsToEdit}));
        $("#attachedFilesBlock").empty();
        $("#attachedFilesBlock").append(jqAttachedFilesContent);

        $(".removeFileLink").click(function() {
            var jqFileBlock = $(this).closest(".attachedFileBlock");
            jqFileBlock.remove();
            // Обновляем модель
            currentFilesData = [];
            $(".attachedFileBlock").each(function(){
                var fileUrl = $(".fileLink", $(this)).attr("link");
                var fileName = $(".fileLink", $(this)).attr("title");
                currentFilesData.push({name : fileName, url : fileUrl});
            });
            drawAttachedFiles();
        });
        $(".editFileName").click(function(){
            $(".fileLink", $(this).parent()).hide();
            $(".fileName", $(this).parent()).show();
            $(".acceptFileName", $(this).parent()).show();
            $(".removeFileLink", $(this).parent()).hide();
            $(this).hide();
        });
        $(".acceptFileName").click(function(){
            var indexOfModel = parseInt($(this).attr("index"));
            var newValue =  $(".fileName", $(this).parent()).val();
            if (newValue == "") {
                bootbox.alert("Введите имя файла")
                return;
            }
            currentFilesData[indexOfModel].name = newValue;
            $(".fileLink", $(this).parent()).show();
            $(".fileLink", $(this).parent()).attr("title", newValue);
            $(".linkFileName", $(this).parent()).text(newValue);
            $(".editFileName", $(this).parent()).show();
            $(".removeFileLink", $(this).parent()).show();
            $(".fileName", $(this).parent()).hide();
            $(this).hide();
        });
        $(".fileLink").click(function(){
            var fileUrl = $(this).attr("link");
            var contentNodeHtml = "";
            $("#modalBodyAttachedFile").empty();
            if (fileUrl.toLowerCase().search("pdf") > -1) { // Pdf файл
                if (getAcrobatInfo().acrobat == false) { // Скачиваем документ
                    contentNodeHtml = "<a href='" + fileUrl + "'>Скачать файл</a><br/>Для просмотра pdf файлов в браузере вам необходимо установить <a target='_blank' href='https://get.adobe.com/ru/reader/'>Acrobat Reader</a>.<br/>" +
                            "Если Acrobat Reader не работает, попробуйте выполнить настройку согласно <a target='_blank' href='https://helpx.adobe.com/acrobat/using/display-pdf-in-browser.html'>Инструкции</a>.";
                } else { // Отображаем в iframe
                    contentNodeHtml = '<iframe src="' + fileUrl + '" style="width: 1000px; height: ' + ($(window).height() - 200) + 'px; border: none;" ></iframe>';
                }
            } else { // Картинки
                contentNodeHtml = '<img src="' + fileUrl + '" style="max-width: 1000px;" >';
            }
            $("#modalBodyAttachedFile").append(contentNodeHtml);
            $('#imagemodal').modal('show');
        });
        if (currentUserHasRightsToEdit) {
            if (currentFileCountLimit > 0) {
                if (currentFilesData.length == currentFileCountLimit) { // Скрыть форму добавления файла
                    $("#addFilesBlock").hide();
                } else {
                    $("#addFilesBlock").show();
                }
            }
        } else {
            $("#addFilesBlock").hide();
        }
    }

    // Регистрация иных источников файлов
    function registerFilesSource(sourceName, openSourceDialog) {
        otherFilesSources.push({sourceName : sourceName, openSourceDialog : openSourceDialog});
        return addNewFileInField;
    }

    // Метод добавления файла из других источников
    function addNewFileInField(fileName, fileUrl) {
        // Добавляем данные в модель
        currentFilesData.push({name : fileName, url : fileUrl});
        // Отрисовка данных
        drawAttachedFiles();
    }

    // Загрузить поля
    function loadFieldFiles(fieldId, fieldFileUrlLoadUrl, callBack) {
        // Если урл не передан, то файлы полей берутся из кеша
        if (fieldFileUrlLoadUrl == null || fieldFileUrlLoadUrl == "") {
            if (cachedFieldFilesData[fieldId] == null) {
                cachedFieldFilesData[fieldId] = [];
            }
            callBack(cachedFieldFilesData[fieldId]);
        } else {
            $.radomJsonPostWithWaiter(fieldFileUrlLoadUrl, {}, callBack);
        }
    }

    // Сохранить поля
    function saveFieldFiles(fieldId, fieldFileUrlSaveUrl, fieldFiles, callBack) {
        // Если урл не передан, то файлы полей кешируются в переменную
        if (fieldFileUrlSaveUrl == null || fieldFileUrlSaveUrl == "") {
            cachedFieldFilesData[fieldId] = fieldFiles;
            callBack();
        } else {
            var fieldFilesJson = JSON.stringify(fieldFiles);
            $.radomJsonPostWithWaiter(
                    fieldFileUrlSaveUrl,
                    fieldFilesJson,
                    callBack,
                    null,
                    {
                        contentType : 'application/json'
                    }
            );
        }
    }

    // Массив данных с файлами полей
    // Структура:
    /*
    {
        fieldId : [
            {
                 index: 0
                 name: "name"
                 url: "fileUrl"
            }
        ]
    }
    */
    function getFieldFilesData() {
        return JSON.stringify(cachedFieldFilesData);
    }

    function getCachedFieldFilesData() {
        return cachedFieldFilesData;
    }

    function setCachedFieldFilesData(fieldFilesData) {
        if (fieldFilesData == null) {
            fieldFilesData = {};
        }
        cachedFieldFilesData = fieldFilesData;
    }

    var otherFilesSources = [];

    var attachedFilesTemplate = null; // Шаблон с файлами
    //var currentFieldFilesId = null; // ИД инпута, в котором хранится строка json с файлами
    var currentFieldId = null; // ИД поля
    var currentFieldFileUploadUrl = null; // ссылка загрузки файлов
    var currentFieldFileTypes = []; // допустимые типы файлов
    var currentFileParameters = {}; // Параметры файлов
    var currentFilesData = []; // Модель данных
    var currentUserHasRightsToEdit = false; // Права на редактирование файлов
    var currentFileCountLimit = -1; // Лимит прикрепления файлов
    var currentFieldFileUrlLoadUrl = null; // Ссылка загрузки файлов полей
    var currentFieldFileUrlSaveUrl = null; // Ссылка сохранения файлов полей
    var cachedFieldFilesData = {}; // Кешированные данные по прикреплённым файлам к полям (используется при создании объединения)
    $(document).ready(function() {
        attachedFilesTemplate = $("#attachedFilesTemplate").html();
        Mustache.parse(attachedFilesTemplate);

        $("body").on("click", ".browseFieldFile", function () {

            currentFieldFileUrlLoadUrl = $(this).attr("field_files_url");
            currentFieldFileUrlSaveUrl = $(this).attr("field_files_save_url");

            var fieldFileUploadAttr = $(this).attr("field_upload_url");
            currentFieldFileUploadUrl = DEFAULT_FIELD_FILE_URL;
            if (fieldFileUploadAttr != null && fieldFileUploadAttr != "") {
                currentFieldFileUploadUrl = fieldFileUploadAttr;
            }

            var fieldFileTypesAttr = $(this).attr("field_types");
            currentFieldFileTypes = DEFAULT_FIELD_FILE_TYPES;
            if (fieldFileTypesAttr != null && fieldFileTypesAttr != "") {
                currentFieldFileTypes = fieldFileTypesAttr.split(",");
            }

            currentFileParameters = {};
            if ($(this).attr("min_width") != null && $(this).attr("min_width") != "") {
                currentFileParameters["min_width"] = $(this).attr("min_width");
            }
            if ($(this).attr("min_height") != null && $(this).attr("min_height") != "") {
                currentFileParameters["min_height"] = $(this).attr("min_height");
            }
            if ($(this).attr("max_width") != null && $(this).attr("max_width") != "") {
                currentFileParameters["max_width"] = $(this).attr("max_width");
            }
            if ($(this).attr("max_height") != null && $(this).attr("max_height") != "") {
                currentFileParameters["max_height"] = $(this).attr("max_height");
            }

            // Права на редактирование файлов
            currentUserHasRightsToEdit = $(this).attr("has_rights_to_edit");
            currentUserHasRightsToEdit = currentUserHasRightsToEdit == "true" ? true : false;

            // Ограничение по количеству файлов
            currentFileCountLimit = parseInt($(this).attr("file_limit"));

            // ИД поля
            currentFieldId = $(this).attr("field_id");

            // Отрисовка иных источников загрузки файлов
            $("#otherFilesSources").empty();
            for (var index in otherFilesSources) {
                //sourceName : sourceName, openSourceDialog : openSourceDialog, addFileCallBack : addFileCallBack
                var fileSource = otherFilesSources[index];
                var jqNode = $("<button style='margin-right: 5px;' class='btn btn-success'>" + fileSource.sourceName + "</button>");
                $("#otherFilesSources").append(jqNode);
                jqNode.click(function(){
                    fileSource.openSourceDialog();
                });
            }
            // Если есть права на редактирование
            if (currentUserHasRightsToEdit) {
                $("#addFilesBlock").show();
                $("#saveFiles").show();
            } else {
                $("#addFilesBlock").hide();
                $("#saveFiles").hide();
            }

            // Загружаем данные
            loadFieldFiles(currentFieldId, currentFieldFileUrlLoadUrl, function(data){
                currentFilesData = data;
                $('#filesBrowserModal').modal('show');
            })
        });

        $("#filesBrowserModal").on("show.bs.modal", function () {
            // Отрисовка данных
            drawAttachedFiles();

            $("#fileBrowserBody").height($(window).height() - 300);
            $("#attachedFilesBlock").height($(window).height() - 402);
        });

        $("#uploadFileButton").click(function(){
            if ($("#fileName").val() == "") {
                bootbox.alert("Необходимо ввести имя файла!");
                return false;
            }
            uploadFieldFile(currentFieldFileUploadUrl, currentFieldFileTypes, currentFileParameters, function (src) {
                currentFilesData.push({name : $("#fileName").val(), url : src});
                // Отрисовка данных
                drawAttachedFiles();
                $("#fileName").val("");
            });

        });

        $("#saveFiles").click(function(){
            saveFieldFiles(currentFieldId, currentFieldFileUrlSaveUrl, currentFilesData, function(){
                $("#filesBrowserModal").modal('hide');
            });
        });
    });
</script>
<script id="attachedFilesTemplate" type="x-tmpl-mustache">
    {{#filesArray}}
        <div class="attachedFileBlock">
            <a href="javascript:void(0)" link="{{url}}" class="fileLink" title="{{name}}">
                <div class="glyphicon glyphicon-file fileIco" aria-hidden="true"></div>
                <span class='linkFileName'>{{name}}</span>
            </a>
            <input type="text" class="form-control fileName" style="display: none;" value="{{name}}" />
            {{#hasRightsToEdit}}
                <a class="glyphicon glyphicon-ok acceptFileName" index="{{index}}" style="display: none;" href="javascript:void(0)" title="Применить имя файла"></a>
                <a class="glyphicon glyphicon-pencil editFileName" href="javascript:void(0)" title="Редатировать имя файла"></a>
                <a class="glyphicon glyphicon-remove removeFileLink" href="javascript:void(0)" title="Удалить прикреплённый файл"></a>
            {{/hasRightsToEdit}}
        </div>
    {{/filesArray}}
</script>
<div class="modal fade" id="filesBrowserModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 970px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title" >Прикреплённые файлы</h4>
            </div>
            <div class="modal-body" id="fileBrowserBody">
                <div id="attachedFilesBlock"></div>
                <div id="addFilesBlock" style="height: 115px;">

                    <div class="form-group">
                        <label>Наименование файла</label>
                        <input type="text" class="form-control" id="fileName" placeholder="Наименование файла" />
                    </div>
                    <div class="form-group">
                        <button class="btn btn-success" id="uploadFileButton">
                            <span class="glyphicon glyphicon-upload"></span> Добавить файл
                        </button>
                        <div style="display: inline-block; margin-left: 10px;" id="otherFilesSources">

                        </div>
                    </div>

                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="saveFiles" style="display: none;">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="imagemodal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog" style="width: 1150px;">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Закрыть</span></button>
                <h4 class="modal-title" id="myModalLabel">Просмотр прикреплённого файла</h4>
            </div>
            <div class="modal-body" style="text-align: center" id="modalBodyAttachedFile"></div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
            </div>
        </div>
    </div>
</div>