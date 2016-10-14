<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<style type="text/css">

</style>

<script id="postRequestAppointTemplate" type="x-tmpl-mustache">
    {{#errorMessage}}
        <h3>{{errorMessage}}</h3>
    {{/errorMessage}}
    {{^errorMessage}}
        <div class="pageContainer">
            <h3>Приглашение на работу</h3>
            <p>{{#userSex}}Уважаемый{{/userSex}}{{^userSex}}Уважаемая{{/userSex}} {{userName}}, <br>
                    {{senderUserName}} приглашает Вас на должность {{postName}} в объединении {{communityName}}.
                Для того, чтобы принять данное приглашение, нажимте кнопку "Принять приглашение".
                Для того, чтобы отказаться от данного приглашения, нажмите кнопку "Отказаться".</p>
            <div id="controlButtons" style="float: right;">
                <a href="javascript:void(0)" class="btn btn-primary" id="approveRequest">Принять приглашение</a>
                <a href="javascript:void(0)" class="btn btn-danger" id="cancelRequest">Отказаться</a>
            </div>
            <div style="clear: both;"></div>
        </div>
    {{/errorMessage}}
</script>

<script>
    var requestId = getParameterByName("request_id");

    function loadPostRequestAppointPageData(requestId, callBack) {
        $.radomJsonPost(
                "/communities/requests/appoint_page_data.json",
                {
                    request_id : requestId
                },
                callBack
        );
    }

    $(document).ready(function() {
        loadPostRequestAppointPageData(requestId, function (communityPostRequestAppointPageData) {
            var postRequestAppointTemplate = $("#postRequestAppointTemplate").html();
            Mustache.parse(postRequestAppointTemplate);

            var model = communityPostRequestAppointPageData;
            var markup = Mustache.render(postRequestAppointTemplate, model);

            $("#postRequestAppointBlock").append($(markup));

            $("#approveRequest").click(function(){
                $("#controlButtons").hide();
                $.radomJsonPost("/communities/requests/approve_post_appoint.json", {request_id : requestId}, function(response){
                    //document.location.href = response.link;
                });
            });
            $("#cancelRequest").click(function(){
                $("#controlButtons").hide();
                $.radomJsonPost("/communities/requests/cancel_post_appoint.json", {request_id : requestId}, function(){
                    //document.location.href = communityPostRequestAppointPageData.communityLink;
                });
            });
        });
    });

</script>
<div id="postRequestAppointBlock"></div>