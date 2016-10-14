<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<t:insertAttribute name="communityHeader" />
<hr/>
<iframe id="app-iframe" src="${application.iframeUrl}" width="653" height="600"></iframe>
<hr/>