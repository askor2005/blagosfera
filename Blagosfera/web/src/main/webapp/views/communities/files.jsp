<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<link rel="stylesheet" type="text/css" media="screen" href="/css/elfinder/css/elfinder.min.css">
<script type="text/javascript" src="/js/elfinder/elfinder.min.js"></script>
<%--<script type="text/javascript" src="/js/elfinder/elfinder.full.js"></script>--%>

<link rel="stylesheet" type="text/css" media="screen" href="/css/elfinder/css/theme.css">
<script>
    $().ready(function() {
        var elf = $('#fileExplorer').elfinder({
            lang: 'ru',
            url : '/files/el_finder/COMMUNITY/${community.id}/handle.json'
        }).elfinder('instance');
    });
</script>
<div id="fileExplorer"></div>