<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@include file="cyberbrainSections.jsp" %>

<script type="text/javascript">
    $(document).ready(function(){
        var urlImport = "";
        var urlExport = "";
        var dictionary = "";
        var EXCEL_EXTENSION = '.xlsx .xls';

        getCommunities();
        function getCommunities() {
            $.ajax({
                type: "post",
                dataType: "json",
                data: "{}",
                url: '/cyberbrain/sections/get_user_communities.json',
                success: function (response) {
                    if (response.success == true) {
                        response.items.forEach(function (entry) {
                            $("#combobox-communities").append("<option data-community-id='" + entry.id + "'>" + entry.name + "</option>");
                        });
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
        }

        function checkExtensionFile(filename, extension) {
            var validExts = extension.split(' ');
            var fileExt = filename;
            fileExt = fileExt.substring(fileExt.lastIndexOf('.'));

            if (validExts.indexOf(fileExt) < 0) {
                if (fileExt != "") {
                    bootbox.alert("Разрешена загрузка файлов с расширениями [" + extension + "]");
                }

                return false;
            } else {
                return true;
            }
        }

        $("#dict-dropdown-menu li a").click(function(){
            dictionary = $(this).text();
            urlImport = $(this).attr('data-url-import');
            urlExport = $(this).attr('data-url-export');

            $('#file-input-lbl').text('Импорт данных: ' + dictionary);
            $(this).parents('.btn-group').find('.dropdown-toggle').html(dictionary + ' <span class="caret"></span>');
        });

        $("a#btnExport").click(function() {
            var communityId = $("#combobox-communities option:selected").attr("data-community-id");

            if (communityId != -1 && communityId != "undefined" && communityId != "") {
                if (urlExport !== "") {
                    var url = location.href;
                    var baseURL = url.substring(0, url.indexOf('/', 14));

                    var fileFrame = document.getElementById("downloadFileFrame");
                    fileFrame.src = baseURL + urlExport + '?communityId=' + communityId;
                } else {
                    if (dictionary !== "") {
                        bootbox.alert("Для справочника \"" + dictionary + "\" экспорт данных не предусмотрен.");
                    } else {
                        bootbox.alert("Перед тем как выполнить экспорт данных нужно выбрать справочник.");
                    }
                }
            } else {
                bootbox.alert("Перед тем как выполнить экспорт нужно выбрать объединение в рамках которого будут выгружены данные.");
            }

            return false;
        });

        $("#btnUpload").click(function () {
            var filename = $("#file-input").val();
            var communityId = $("#combobox-communities option:selected").attr("data-community-id");

            if (communityId != -1 && communityId != "undefined" && communityId != "") {
                if (urlImport !== "") {
                    if (checkExtensionFile(filename, EXCEL_EXTENSION)) {
                        $('#btnUpload').attr('disabled', 'disabled');
                        $('#btnExport').attr('disabled', 'disabled');
                        $("#communities-dropdown-toggle").attr("disabled", "disabled");
                        $("#dict-dropdown-toggle").attr("disabled", "disabled");

                        var fd = new FormData(document.getElementById("fileinfo"));
                        fd.append("communityId", communityId);

                        $.ajax({
                            url: urlImport,
                            type: "POST",
                            data: fd,
                            processData: false,  // tell jQuery not to process the data
                            contentType: false,  // tell jQuery not to set contentType
                            success: function (response) {
                                if (response.result !== "") {
                                    // обновим счетчики
                                    getCountsRecordsAndScore();

                                    bootbox.alert(response.result);
                                } else {
                                    bootbox.alert("Произошла неизвестная ошибка при импорте данных.");
                                }

                                $('#btnUpload').removeAttr('disabled');
                                $('#btnExport').removeAttr('disabled');
                                $("#communities-dropdown-toggle").removeAttr('disabled');
                                $("#dict-dropdown-toggle").removeAttr('disabled');
                            }
                        });
                    }
                } else {
                    if (dictionary !== "") {
                        bootbox.alert("Для справочника \"" + dictionary + "\" импорт данных не предусмотрен.");
                    } else {
                        bootbox.alert("Перед тем как выполнить импорт нужно выбрать справочник.");
                    }
                }
            } else {
                bootbox.alert("Перед тем как выполнить импорт нужно выбрать объединение в рамках которого будут загруженны новые данные.");
            }

            return false;
        });
    });
</script>

<h1>Импорт / Экспорт данных</h1>

<hr/>

<div class="row">
    <div id="communities-menu" class="col-xs-8">
        <label>Объединение:&nbsp;&nbsp;</label>
        <div class="form-group">
            <select id="combobox-communities" class="selectpicker" data-live-search="true" data-width="100%"></select>
        </div>
    </div>

    <div class="col-xs-4">
        <label>Справочник:&nbsp;&nbsp;</label>
        <div class="btn-group" style="width: 100%;">
            <a id="dict-dropdown-toggle" style="width: 100%;" class="btn btn-default dropdown-toggle btn-select" data-toggle="dropdown" href="#">Выберите справочник <span class="caret"></span></a>
            <ul id="dict-dropdown-menu" class="dropdown-menu">
                <li><a href="#" data-url-import="/cyberbrain/importExport/journalAttentionImportData" data-url-export="/cyberbrain/importExport/journalAttentionExportData" data-object-name="JOURNAL_ATTENTION">Журнал внимания</a></li>
                <li><a href="#" data-url-import="/cyberbrain/importExport/thesaurusImportData" data-url-export="/cyberbrain/importExport/thesaurusExportData" data-object-name="THESAURUS">Тезаурус (служебные теги)</a></li>
                <li><a href="#" data-url-import="/cyberbrain/importExport/knowledgeRepositoryImportData" data-url-export="/cyberbrain/importExport/knowledgeRepositoryExportData" data-object-name="KNOWLEDGE_REPOSITORY">Хранилище знаний</a></li>
                <li><a href="#" data-url-import="/cyberbrain/importExport/knowledgeRepositoryImportDataCondition" data-url-export="/cyberbrain/importExport/knowledgeRepositoryExportData" data-object-name="KNOWLEDGE_REPOSITORY_CONDITION">Хранилище знаний (связи влияния)</a></li>
                <li><a href="#" data-url-import="" data-url-export="/cyberbrain/importExport/taskManagementExportData" data-object-name="USER_TASK">Задачи пользователей</a></li>
            </ul>
        </div>
    </div>
</div>

<hr/>

<form role="form" method="post" enctype="multipart/form-data" id="fileinfo">
    <div class="form-group" id="fileInputContainer">
        <label for="file-input" id="file-input-lbl">Выберите справочник</label>
        <input type="file" id="file-input" name="file-input" class="filestyle" data-buttonText="Выберите файл"/>
    </div>
    <a href="#" class="btn btn-default" id="btnUpload">Загрузить</a>
</form>

<hr/>

<div class="form-group">
    <a href="#" class="btn btn-default btn-block" id="btnExport">Экспортировать данные из справочника</a>
</div>

<iframe id="downloadFileFrame" name="downloadFileFrame" src='about:blank' style='display:none;'></iframe>