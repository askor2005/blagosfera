<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div id="communityHeadBlock"></div>
<script type="text/javascript">
    function initCommunityHead(community) {
        var verifierName = "";
        var verificationDateFormatted = "";
        if (community.verified && community.verifier != null && community.verificationDate != null) {
            var rn = new RussianName(community.verifier.name);
            verifierName = rn.fullName(rn.gcaseTvor);
            verificationDateFormatted = dateFormat(new Date(community.verificationDate), "dd.mm.yyyy HH:MM:ss");
        }
        var communityHeadTemplate = $("#communityHeadTemplate").html();
        Mustache.parse(communityHeadTemplate);
        var markup = Mustache.render(communityHeadTemplate, {
            community : community,
            verifierName : verifierName,
            verificationDateFormatted : verificationDateFormatted
        });
        $("#communityHeadBlock").append(markup);
    }
</script>

<script id="communityHeadTemplate" type="x-tmpl-mustache">
    <h1 style="font-size: 30px;">
        {{community.name}}
    </h1>
    {{#community.verified}}
        <h4>
            <small>
                Cертифицированна Регистратором <a href="/sharer/{{community.verifier.ikp}}">{{verifierName}}</a>
                {{verificationDateFormatted}}
            </small>
        </h4>
    {{/community.verified}}
</script>
