<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<script>
    var votingsPageTemplate;
    var communityId = "${communityId}";

    function loadAnyPageData(communityId, callBack) {
        $.radomJsonPost(
                "/communities/any_page_data.json",
                {
                    community_id : communityId
                },
                callBack
        );
    }

    function loadVotings(communityId, callBack) {
        $.radomJsonPost(
                "/group/" + communityId + "/votings_page_data.json",
                {},
                callBack
        );
    }

    $(document).ready(function() {
        $("#votingsTable").fixMe();
        loadAnyPageData(communityId, function(communityAnyPageData) {
            initCommunityHead(communityAnyPageData.community);
            initCommunityMenu(communityAnyPageData.community);
        });
        loadVotings(communityId, function(votingsPageData) {
            initVotingsPage(votingsPageData);
        });
    });

    function initVotingsPage(votingsPageData) {
        votingsPageTemplate = $("#votingsPageTemplate").html();
        Mustache.parse(votingsPageTemplate);

        var model = votingsPageData;
        var markup = Mustache.render(votingsPageTemplate, model);
        $("#votingsTableData").append(markup);
        $("#votingsTable").fixMe();
    }
</script>

<t:insertAttribute name="communityHeader" />
<h2>Голосования сообщества с моим участием</h2>
<hr/>
<table class="table" id="votingsTable">
    <tr>
        <th>Индекс</th>
        <th>Наименование</th>
        <th>Создатель</th>
        <th>Дата начала</th>
        <th>Дата окончания</th>
        <th>Собрание</th>
    </tr>
    <tbody id="votingsTableData"></tbody>
</table>

<script id="votingsPageTemplate" type="x-tmpl-mustache">
    {{#votings}}
        <tr>
            <td>{{id}}</td>
            <td><a href="{{baseLink}}{{id}}">{{subject}}</a></td>
            <td><a href="/sharer/{{ownerIkp}}">{{ownerName}}</a></td>
            <td>{{startDate}}</td>
            <td>{{endDate}}</td>
            <td>{{description}}</td>
        </tr>
    {{/votings}}
</script>
