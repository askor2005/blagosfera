<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<link rel="stylesheet" type="text/css" href="/css/discuss.css"/>

<script language="JavaScript" src="/js/discuss/discuss.js"></script>


<div class="panel-heading">
    <small class="pull-right">Код обсуждения: ${discussion.id}</small>
    <h1>
        <%--<small>Объединение пайщиков ПО РОС "РА-ДОМ"</small>--%>
        <p>${discussion.title}</p></h1>
    </h4>
    <%--<h4>Тема: ремесло</h4>--%>

    <h4>Обсуждение создано: <fmt:formatDate pattern="dd.MM.yyyy HH:mm"
                                            value="${discussion.createdAt}"></fmt:formatDate></h4>
    <h4>Создатель: <a href="/sharer/${discussion.owner.ikp}">${discussion.owner.fullName}</a></h4>

</div>

<script language="JavaScript">
    $(function () {
        $("#messageForm").submit(function () {
            //discuss.addComment($("#messageForm").serialize());
            //discuss.
            return false;
        });
    });
</script>

<script language="JavaScript">
    <%-- Реакция на кнопки "ответить" --%>
    $(document).ready(function () {

        $("#replyForm").find("textarea").bind("change paste keyup", function () {
            $("#replyForm").find("button[type=submit]").prop('disabled', $(this).val().length == 0);
        });

        discuss.init({
            "discussion": "${discussion.id}",
            "container": $("#commentsList"),
            "topic": "${discussionTopic}",
            "currentUser": "${currentUser.id}",
        });
        discuss.bindReplyButtons();
        //discuss.bindVoters();
        discuss.bindEditButtons();
        discuss.bindEditables();
        renderRatingControl();
    })
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
                            <img data-src="holder.js/64x/64" alt="64x64"
                                 src="{{ownerAvatar}}" data-holder-rendered="true"
                                 style="width: 64px;" class="media-object img-thumbnail tooltiped-avatar"
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
            <div class="rating pull-right" data-type="COMMENT" data-id="{{id}}" data-title="{{text}}" data-all="{{ratingSum}}" data-weight="{{ratingWeight}}"></div>
        </div>
    </div>
</script>
<div class="row">
    <div class="panel" style="margin-top: 25px;" id="replyForm">
        ${discussion.root.message}
        <div class="panel-footer" style="margin-top: 25px;" id="footer_${discussion.root.id}">
            <button type="button" class="btn btn-default btn-reply" comment-id="${discussion.root.id}">Мой ответ
            </button>
            <div class="rating pull-right" data-type="COMMENT" data-id="${discussion.root.id}" data-title="${discussion.title}" data-all="${ratingSum}" data-weight="${ratingWeight}"></div>
        </div>
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

<div class="row" id="commentsList">
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
                                <img data-src="holder.js/64x64" alt="64x64"
                                     src='${radom:resizeImage(users[c.ownerIkp].avatar, "c64")}' data-holder-rendered="true"
                                     style="height: 64px;"
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
                <div class="rating pull-right" data-type="COMMENT" data-id="${c.id}" data-title="${c.message}" data-all="${c.ratingSum}" data-weight="${c.ratingWeight}"></div>
            </div>
        </div>

        <c:set var="prev" scope="page" value="${c}"/>
    </c:forEach>
    
    <br/><br/>
</div>
