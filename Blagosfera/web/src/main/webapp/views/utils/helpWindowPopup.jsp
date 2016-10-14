<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<html>
<head>
    <title>${page.title}</title>
    <t:insertAttribute name="favicon" />
</head>
<body>
    ${page.content}
</body>
</html>
