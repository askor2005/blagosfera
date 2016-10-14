<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<link rel="stylesheet" type="text/css" href="/css/discuss.css"/>

<script src="/js/discuss/discuss.js"></script>

<script type="text/javascript">
    var categories = [];
    var attachments = [];
</script>

<c:forEach items="${categories}" var="category">
    <script type="text/javascript">
        (function () {
            var category = {};
            category.id = ${category.id};
            category.text = '${category.text}';
            categories.push(category);
        })();
    </script>
</c:forEach>

<c:forEach var="attachment" items="${news.attachments}">
    <spring:eval expression="attachment.type == T(ru.askor.blagosfera.domain.news.NewsAttachmentType).VIDEO"
                 var="isVideo"/>
    <spring:eval expression="attachment.type == T(ru.askor.blagosfera.domain.news.NewsAttachmentType).IMAGE"
                 var="isImage"/>

    <script type="text/javascript">
        var att = {};
        att.src = '${attachment.src}';
        att.width = ${attachment.width};
        att.height = ${attachment.height};

        <c:if test="${isVideo}">
        att.type = "VIDEO";
        </c:if>
        <c:if test="${isImage}">
        att.type = "IMAGE";
        </c:if>

        attachments.push(att);
    </script>

</c:forEach>


<script language="JavaScript">
    //Число картинок в одной строчке коллажа новости
    var imagesPerRowInCollage = parseInt('${radom:systemParameter("news.collage.images-per-row", "3")}');
    //uri делового портала
    var rameraUri = '${radom:getSectionLinkByName("ramera")}';

    //Слушаем событие показа/скрытия боковых меню для управления масштабом контента
    $(document).on('sideMenuResizeEvent', function () {
        //обработка коллажей
        $.each($('.radom_collage'), function () {
            $.each($(this).find('.collage_img_wrapper'), function () {
                $(this).attr('orig-width', $($(this).find('img')[0]).attr('orig-width'));
                $(this).attr('orig-height', $($(this).find('img')[0]).attr('orig-height'));
            });
            $(this).radomCollage(imagesPerRowInCollage);
            $.each($(this).find('[data-toggle="lightbox"]'), function () {
                var $this = $(this);
                $this.css('width', '');
                $this.css('height', '');
            });
        });
    });

    $(function () {
        $("#messageForm").submit(function () {
            return false;
        });
    });


    function createAttachmentsCollage(attachments) {

        if (!attachments || !attachments.length) {
            return '';
        }

        var $collage = $('<section class="radom_collage"></section>');

        for (var i = 0; i < attachments.length; ++i) {
            var $img;
            if (attachments[i].type == "IMAGE") {
                $img = $(new Image());
                $img.attr('orig-width', attachments[i].width);
                $img.attr('orig-height', attachments[i].height);
                $img.attr("src", attachments[i].src);
                $collage.append($img);
            } else if (attachments[i].type == "VIDEO") {
                var $thumbVideoContainer = $('<div class="news_thumb_video"></div>');
                $thumbVideoContainer.css("width", 480);
                $thumbVideoContainer.css("height", (attachments[i].height / attachments[i].width) * 480);
                $img = $(new Image());
                $img.css("cursor", "pointer");
                $img.css("display", "block");
                $img.css("margin", "0 auto");
                $img.attr("data-iframe-src", attachments[i].src);
                $img.attr("class", "video-link-substitute");
                $img.attr("src", RadomUtils.getYoutubePreview(attachments[i].src, "hqdefault"));

                var $wrapperA = $('<a style="width: inherit; height: inherit;"></a>')
                        .attr('data-toggle', 'lightbox')
                        .attr('href', attachments[i].src)
                        .attr('data-title', '${news.title}')
                        .attr('data-gallery', 'news');
                $wrapperA.append($img);

                $thumbVideoContainer.append($wrapperA);

                var $div = $('<div class="news_thumb_play"></div>');

                $div.bind('click', function () {
                    $img.click();
                });

                $thumbVideoContainer.append($div);
                $collage.append($thumbVideoContainer);
            }
        }

        return $collage;
    }
</script>

<script language="JavaScript">
    $(document).ready(function () {

        //Отображаем категории и делаем их кликабельными
        var $categoriesHolder = $('.news_list_item_category');

        for (var i = 0; i < categories.length; ++i) {
            (function () {
                var category = categories[i];
                var $categoryA = $('<a></a>').text(category.text);
                $categoriesHolder.append($categoryA);

                $categoryA.on('click', function () {
                    $(document).trigger("clickNewsCategory", category.id);
                    console.log(categoryId);
                });
                if (i != categories.length - 1) {
                    $categoriesHolder.append('->');
                }
            })();
        }

        //Делаем теги кликабельными
        $.each($('.news_tags div'), function () {
            var $tagDiv = $(this);
            $tagDiv.bind('click', function () {
                $(document).trigger("clickNewsTag", $tagDiv.text());
                console.log($tagDiv.text());
            });
        });

        //Подписываем документ на клики по категориям
        $(document).on("clickNewsCategory", function (event, categoryId) {
            var filterData = {
                categoryId: categoryId
            };

            saveFilterAndThenRedirect(filterData);
        });

        //Подписываем документ на клики по тегам
        $(document).on("clickNewsTag", function (event, tagText) {
            var filterData = {
                tags: [tagText]
            };

            saveFilterAndThenRedirect(filterData);
        });

        function saveFilterAndThenRedirect(filterData) {
            $.radomJsonPost(
                    //url
                    "/news/saveFilter.json",
                    //data
                    JSON.stringify(filterData),
                    function (response) {
                        if (response.result == "success") {
                            window.location.href = rameraUri;
                            return;
                        }

                        if (response.result == "error") {
                            if (response.message) {
                                bootbox.alert(response.message);
                            } else {
                                bootbox.alert("Произошла ошибка!")
                            }
                        }
                    },
                    //errorCallback
                    function (response) {
                        if (response.message) {
                            bootbox.alert(response.message);
                        } else {
                            bootbox.alert("Произошла ошибка!");
                        }
                    }, {
                        contentType: "application/json"
                    }
            );
        };

        $('#news-text').prepend(createAttachmentsCollage(attachments));

        <%-- Реакция на кнопки "ответить" --%>
        $("#replyForm").find("textarea").bind("change paste keyup", function () {
            $("#replyForm").find("button[type=submit]").prop('disabled', $(this).val().length == 0);
        });

        discuss.init({
            "discussion": "${discussion.id}",
            "container": $("#commentsList"),
            "topic": "${discussionTopic}",
            "currentUser": "${currentUser.id}"
        });
        discuss.bindReplyButtons();
        //discuss.bindVoters();
        discuss.bindEditButtons();
        discuss.bindEditables();

        renderRatingControl();

    });
</script>

<script>
    $(window).load(function () {

        //Обработка колажа
        $.each($('.radom_collage'), function () {
            $(this).radomCollage(imagesPerRowInCollage);
        });
        $('#news-text').css("opacity", 1);

        $.each($("div#news-text img"), function (index, img) {
            var $img = $(img);

            if ($img.hasClass("video-link-substitute")) {
                return;
            }

            var $imgWrapper = $('<a href="' + $img.attr('src') + '"></a>')
                    .attr('data-toggle', 'lightbox')
                    .attr('data-gallery', 'news')
                    .attr('data-title', '${news.title}');
            $imgWrapper.addClass('collage_img_wrapper');
            $img.replaceWith($imgWrapper);
            $imgWrapper.append($img);
        });

    });
</script>

<script id="commentTpl" type="text/template">
    <div class="panel panel-primary small_distance comment" style="margin-left: {{margin}}px;" depth="{{depth}}"
         id="comment_{{id}}">
        <div class="panel-body">
            <div class="media">
                <div class="media-body" style="display: block; width: 100%">
                    <h4 class="media-heading" style="width:100%;">{{ownerName}},
                        <small>
                            {{createdAt}}
                        </small>

                        <a class="media-right pull-right" href="/sharer/{{ownerIkp}}">
                            <img data-src="holder.js/90x/90" alt="90x90"
                                 src="{{ownerAvatar}}" data-holder-rendered="true"
                                 style="width: 90px;" class="media-object img-thumbnail tooltiped-avatar"
                                 data-sharer-ikp="{{ownerIkp}}" data-placement="left">
                        </a>
                    </h4>
                    {{#isOwn}}
                    <div class="msg editable" id="comment-text_{{id}}">{{text}}</div>
                    {{/isOwn}}
                    {{^isOwn}}
                    <div class="msg">{{text}}</div>
                    {{/isOwn}}

                </div>

            </div>
        </div>
        <div class="panel-footer" id="footer_{{id}}">
            <div class="btn-group btn-group-xs">
                <button type="button" class="btn btn-primary btn-reply" comment-id="{{id}}">Ответить</button>
                {{#isOwn}}
                <button type="button" class="btn btn-default btn-edit" data-id="{{id}}">
                    <i class="glyphicon glyphicon-edit"></i>&nbsp;Редактировать
                </button>
                {{/isOwn}}
            </div>
            <div class="rating pull-right" data-type="COMMENT" data-id="{{id}}" data-title="{{text}}"
                 data-all="{{ratingSum}}" data-weight="{{ratingWeight}}"></div>
        </div>
    </div>
</script>

<style type="text/css">
    h2 small.pull-right {
        line-height: 38px;
    }
</style>

<h2>
    ${news.title}
</h2>

<hr/>

<div class="row">

    <div class="col-xs-2">
        <a href="${news.author.link}">
            <img src='${radom:resizeImage(news.author.avatar, "c84")}' class="img-thumbnail tooltiped-avatar"
                 data-sharer-ikp="{{news.author.ikp}}" data-placement="right"/>
        </a>
    </div>

    <div class="col-xs-10">
        <h4><a href="${news.author.link}">${news.author.fullName}</a>

            <p class="text-muted"
               style="display: block; margin-top: 10px; padding-bottom: 5px; font-size: 75%;margin-bottom: 0;">
                Опубликовано: <fmt:formatDate pattern="dd.MM.yyyy" value="${news.date}"/></p>

            <small class="news_list_item_category">Категория: </small>
            <div class="news_tags">
                <small style="display: inline-block;">Теги:</small>
                <c:forEach items="${news.tags}" var="tag">
                    <div style="cursor: pointer;">${tag.text}</div>
                </c:forEach>
            </div>
        </h4>
    </div>

</div>

<hr/>


<div id="replyForm">
    <div id="news-text" style="opacity: 0;">
        ${radom:replaceLinks(news.text)}
    </div>
    <div class="panel-footer" id="footer_${discussion.root.id}" style="margin-top: 12px">
        <button type="button" class="btn btn-default btn-reply" comment-id="${discussion.root.id}">Мой ответ
        </button>
        <div class="rating pull-right" data-type="NEWS" data-id="${news.id}" data-title="${news.title}"
             data-all="${ratingSum}" data-weight="${ratingWeight}"></div>
    </div>
</div>


<form role="form" id="messageFormTemplate" style="display: none;">
    <input type="hidden" name="parent" value="">

    <div class="form-group">
        <label for=messageField>Мой ответ</label>
        <textarea id="messageField" class="form-control" rows="3" name="message"></textarea>
    </div>

    <button id="submitMessage" type="submit" class="btn btn-default btn-reply" comment-id="">Отправить мой ответ
    </button>
</form>

<div id="commentsList">
    <div style="display: none;" id="comment_${discussion.root.id}" depth="0">
        <%--anchor to insert new replies --%>
    </div>
    <c:forEach items="${commentsTree}" var="c">
        <c:choose>
            <c:when test="${empty prev or c.parentId==prev.id or c.parentId==prev.parentId}">
                <c:set var="marginClass" scope="page" value="small_distance"/>
            </c:when>
            <c:otherwise>
                <c:set var="marginClass" scope="page" value="large_distance"/>
            </c:otherwise>
        </c:choose>

        <div class="panel panel-primary ${marginClass} comment" style="margin-left: ${20*c.depth-20}px;"
             id="comment_${c.id}"
             depth="${c.depth}">
            <div class="panel-body">
                <div class="media">
                    <div class="media-body" style="display: block; width: 100%">
                        <h4 class="media-heading" style="width:100%;">${users[c.ownerIkp].shortName},
                            <small>
                                <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${c.createdAt}"/>
                            </small>

                            <a class="media-right pull-right" href="/sharer/${c.ownerIkp}">
                                <img data-src="holder.js/90x90" alt="90x90"
                                     src='${radom:resizeImage(users[c.ownerIkp].avatar, "c90")}'
                                     data-holder-rendered="true"
                                     style="height: 90px;"
                                     class="media-object img-thumbnail tooltiped-avatar" data-sharer-ikp="${c.ownerIkp}"
                                     data-placement="left">
                            </a>
                        </h4>
                        <spring:escapeBody htmlEscape="true" javaScriptEscape="true"/>
                        <c:if test="${c.ownerId eq sharer.id}">
                            <div class="msg editable" id="comment-text_${c.id}">${c.message}</div>
                        </c:if>
                        <c:if test="${c.ownerId ne sharer.id}">
                            <div class="msg" id="comment-text_${c.id}">${c.message}</div>
                        </c:if>

                    </div>

                </div>
            </div>
            <div class="panel-footer" id="footer_${c.id}">
                <div class="btn-group btn-group-xs">
                    <button type="button" class="btn btn-primary btn-reply" comment-id="${c.id}">Ответить</button>
                    <c:if test="${c.ownerId eq sharer.id}">
                        <button type="button" class="btn btn-default btn-edit" data-id="${c.id}">
                            <i class="glyphicon glyphicon-edit"></i>&nbsp;Редактировать
                        </button>
                    </c:if>
                </div>
                <div class="rating pull-right" data-type="COMMENT" data-id="${c.id}" data-title="${c.message}"
                     data-all="${c.ratingSum}" data-weight="${c.ratingWeight}"></div>
            </div>
        </div>

        <c:set var="prev" scope="page" value="${c}"/>
    </c:forEach>

    <br/><br/>
</div>

<hr/>
<div class="form-group">

    <c:choose>
        <c:when test='${radom:getSimpleClassName(news.scope) == "Community"}'>
            <a href="${news.scope.link}" class="btn btn-default">Вернуться к списку новостей</a>
        </c:when>
        <c:when test='${radom:getSimpleClassName(news.scope) == "Sharer"}'>
            <a href="/feed" class="btn btn-default">Вернуться к списку новостей</a>
        </c:when>
        <c:otherwise>
            <a href="#" class="btn btn-default" onclick="bootbox.alert('Функция в разработке'); return false;">Вернуться
                к списку новостей</a>
        </c:otherwise>
    </c:choose>


</div>
<hr/>