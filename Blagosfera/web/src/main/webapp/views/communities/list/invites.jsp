<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<t:insertAttribute name="item"/>

<script type="text/javascript">
    var userId = null;

    function initScrollListener() {
        $("div#communities-list").empty();

        ScrollListener.init("/communities/list.json", "post", function () {
            var params = {};
            params.sharer_id = userId;
            params.status = "INVITE";
            params.check_parent = false;
            return params;
        }, function () {

        }, function (response) {
            $("div.list-not-found").remove();

            $.each(response.list, function (index, item) {
                CommunitiesListItem.append(item, $("div#communities-list"));
            });

            if ($("div#communities-list").find(".community-item").length == 0) {
                $("div#communities-list").append("<div style='display: block;' class='row list-not-found'><div class='panel panel-default'><div class='panel-body'>Список пуст</div></div></div>");
            }
        });
    }

    $(document).ready(function () {
        $(eventManager).bind("inited", function (event, user) {
            userId = user.id;
            initIvites();
        });
    });

    function initIvites() {
        initScrollListener();

        $("input#query").callbackInput(500, 4, function () {
            initScrollListener();
        });

        $("input#query").radomTooltip();

        $("input#creator").change(function () {
            initScrollListener();
        });

        $(radomEventsManager).bind("community-member.event", function(event, data) {
            if (data.member.user.id == userId) {
                if (data.community == null) {
                    data.community = {id: data.member.communityId};
                }
                switch(data.eventType) {
                    case "accept_invite":
                    case "reject_invite":
                    case "cancel_invite":
                        CommunitiesListItem.remove(data.community, data.member);
                        break;
                    case "invite":
                        CommunitiesListItem.prepend(data.community, data.member, $("div#communities-list"));
                        break;
                }
            }
        });


        /*$(radomEventsManager).bind("community-member.accept-invite", function (event, data) {
            data.community = {id: data.member.communityId};

            if (data.member.user.id == userId) {
                CommunitiesListItem.remove(data.community, data.member);
            }
        });

        $(radomEventsManager).bind("community-member.reject-invite", function (event, data) {
            data.community = {id: data.member.communityId};

            if (data.member.user.id == userId) {
                CommunitiesListItem.remove(data.community, data.member);
            }
        });

        $(radomEventsManager).bind("community-member.cancel-invite", function (event, data) {
            if (data.member.user.id == userId) {
                CommunitiesListItem.remove(data.community, data.member);
            }
        });

        $(radomEventsManager).bind("community-member.invite", function (event, data) {
            if (data.member.user.id == userId) {
                CommunitiesListItem.prepend(data.community, data.member, $("div#communities-list"));
            }
        });*/
    }
</script>

<h1>Приглашения</h1>
<hr/>

<div id="communities-list">
</div>