<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<script type="text/javascript">
    // Загрузить корневые разделы сайта
    function loadSections(callBack, errorCallBack) {
        $.radomJsonPost(
            "/admin/root_sections/getSections.json",
            {},
            callBack,
            errorCallBack
        )
    }

    // Загрузкить возможные статичные страницы сайта
    function loadPossiblePages(titleQuery, callBack, errorCallBack) {
        $.radomJsonPost(
            "/admin/root_sections/getPossiblePages.json",
            {
                title_query: titleQuery
            },
            callBack,
            errorCallBack
        )
    }

    // Сохранить раздел
    function saveSection(id, title, hint, link, pageId, forwardUrl, published, openInNewLink,callBack) {
        $.radomJsonPostWithWaiter(
            "/admin/root_sections/saveSection.json",
            JSON.stringify({
                id: id,
                title: title,
                hint: hint,
                link: link,
                pageId: pageId,
                forwardUrl: forwardUrl,
                published: published,
                openInNewLink: openInNewLink,

            }),
            callBack,
            null,
            {
                contentType : 'application/json'
            }
        )
    }

    var sections = {};
    var sectionsTemplate = null;
    var sectionEditTemplate = null;

    // Отобразить данные на странице
    function drawSectionTable() {
        $(".table-responsive").empty();
        $("#tableDataLoaderAnimation").show();
        loadSections(function(response){
            $("#tableDataLoaderAnimation").hide();

            for (var index in response) {
                var section = response[index];
                sections[section.id] = section;
            }

            var findSections = response.length > 0;
            var jqTableNode = $(Mustache.render(sectionsTemplate,
                    {
                        sections : response,
                        findSections : findSections,
                        hasError : false,
                        errorMessage : ""
                    }
            ));
            $(".table-responsive").append(jqTableNode);

            jqTableNode.fixMe();

            $(".editSection").radomTooltip({
                container: "body",
                delay : { "show": 100, "hide": 100 }
            });

            $(".editSection").click(function(){
                var sectionId = $(this).attr("section_id");
                $("#modalContent", $("#sectionEditModal")).empty();
                $("#modalDataLoaderAnimation").show();

                loadPossiblePages(null, function(pages){
                    $("#modalDataLoaderAnimation").hide();
                    var section = sections[sectionId];
                    section.pages = pages;

                    var pageId = section.page == null ? null : section.page.id;
                    for (var index in section.pages) {
                        var possiblePage = section.pages[index];
                        if (possiblePage.title == null || possiblePage.title == "") {
                            possiblePage.title = "[Название страницы не установлено]";
                        }
                        if (pageId == possiblePage.id) {
                            possiblePage.selected = section.page.id == possiblePage.id;
                        }
                    }

                    if (pageId != null) {
                        section.pageIsNotSet = false;
                    } else {
                        section.pageIsNotSet = true;
                    }
                    var jqModalDataNode = $(Mustache.render(sectionEditTemplate, section));
                    $("#modalContent", $("#sectionEditModal")).append(jqModalDataNode);
                    $("#section_page_id").selectpicker("refresh");
                }, function(errorPagesResponse){
                    $("#modalDataLoaderAnimation").hide();
                    $("#modalContent", $("#sectionEditModal")).text(errorPagesResponse.message);
                });
                $("#sectionEditModal").modal("show");
            });
        }, function(response){
            $("#tableDataLoaderAnimation").hide();

            var jqTableNode = $(Mustache.render(sectionsTemplate,
                {
                    hasError : true,
                    errorMessage : response.message
                }
            ));
            $(".table-responsive").append(jqTableNode);

            jqTableNode.fixMe();
        });
    }

    $(document).ready(function() {
        sectionsTemplate = $("#sectionsTemplate").html();
        sectionEditTemplate = $("#sectionEditTemplate").html();

        Mustache.parse(sectionsTemplate);
        Mustache.parse(sectionEditTemplate);

        drawSectionTable();

        $("#sectionEditModalSaveButton").click(function(){
            var id = $("#section_id").val();
            var title = $("#section_title").val();
            var hint = $("#section_hint").val();
            var link = $("#section_link").val();
            var pageId = $("#section_page_id").val();
            var forwardUrl = $("#section_forward_url").val();
            var published = $("#section_published").prop("checked");
            var openInNewLink = $("#openInNewLink").prop("checked");
            // Сохранить изменения
            saveSection(
                id,
                title,
                hint,
                link,
                pageId,
                forwardUrl,
                published,
                openInNewLink,
                function() {
                    $("#sectionEditModal").modal("hide");
                    drawSectionTable();
                }
            );
        });
    });
</script>
<div class="page-header">
    <h1>${currentPageTitle}</h1>
</div>

<script id="sectionsTemplate" type="x-tmpl-mustache">
    <table class="table table-hover table-striped">
        <thead>
        <tr>
            <th>Название</th>
            <th width="100"></th>
        </tr>
        </thead>
        <tbody>
            {{#hasError}}
                <tr>
                    <td colspan="2">{{errorMessage}}</td>
                </tr>
            {{/hasError}}
            {{^hasError}}
                {{#findSections}}
                    {{#sections}}
                        <tr id="{{id}}">
                            <td>{{title}}</td>
                            <td class="text-right">
                                <div class="btn-group btn-group-sm">
                                    <button class="btn btn-default editSection" section_id="{{id}}" title="Редатировать раздел"><i class="fa fa-fw fa-pencil"></i></button>
                                </div>
                            </td>
                        </tr>
                    {{/sections}}
                {{/findSections}}
                {{^findSections}}
                    <tr>
                        <td colspan="2">Разделов нет</td>
                    </tr>
                {{/findSections}}
            {{/hasError}}
        </tbody>
    </table>
</script>

<script id="sectionEditTemplate" type="x-tmpl-mustache">
    <input type="hidden" value="{{id}}" id="section_id" />
    <div class="form-group">
        <label>Название раздела</label>
        <input type="text" class="form-control" placeholder="Название раздела" value="{{title}}" id="section_title" />
    </div>
    <div class="form-group">
        <label>Код раздела</label>
        <input type="text" class="form-control" value="{{name}}" readonly="true" />
    </div>
    <div class="form-group">
        <label>Хинт раздела</label>
        <input type="text" class="form-control" placeholder="Хинт раздела" value="{{hint}}" id="section_hint" />
    </div>
    <div class="form-group">
        <label>Ссылка раздела</label>
        <input type="text" class="form-control" placeholder="Ссылка раздела" value="{{link}}" id="section_link"/>
    </div>
    <div class="form-group">
        <label>Статичная страница раздела</label>
        <select id="section_page_id" class="selectpicker" data-live-search="true" data-hide-disabled="true" data-width="100%">
            <option value="" {{#pageIsNotSet}}selected="selected"{{/pageIsNotSet}}>Ничего не выбрано</option>
            {{#pages}}
                <option value="{{id}}" {{#selected}}selected="selected"{{/selected}}>{{title}}</option>
            {{/pages}}
        </select>
    </div>
    <div class="form-group">
        <label>Альтернативная ссылка раздела</label>
        <input type="text" class="form-control" placeholder="Альтернативная ссылка раздела" value="{{forwardUrl}}" id="section_forward_url"/>
    </div>
    <div class="form-group">
        <label>
            <input type="checkbox" id="section_published" {{#published}}checked="checked"{{/published}} />
            Опубликовать раздел (сделать видимым)
        </label>
    </div>
     <div class="form-group">
        <label>
            <input type="checkbox" id="section_published" {{#published}}checked="checked"{{/published}} />
            Опубликовать раздел (сделать видимым)
        </label>
    </div>
     <div class="form-group">
        <label>
            <input type="checkbox" id="openInNewLink" {{#openInNewLink}}checked="checked"{{/openInNewLink}} />
            Открывать раздел в новом окне
        </label>
    </div>

</script>

<div class="table-responsive"></div>
<div class="row list-loader-animation" id="tableDataLoaderAnimation" style="display: block;"></div>

<!-- Модальное окно для редактирования раздела-->
<div class="modal fade" role="dialog" id="sectionEditModal" aria-labelledby="sectionEditModalTextLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="sectionEditModalTextLabel">Редактировать</h4>
            </div>
            <div class="modal-body">
                <div id="modalContent"></div>
                <div class="row list-loader-animation" id="modalDataLoaderAnimation" style="display: block;"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="sectionEditModalSaveButton" style="float: left;">Сохранить</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->