<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>
<html>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=utf-8' />
        <script type="text/javascript" src="/js/jquery.js" ></script>
        <c:if test="${param['print'] != 'false'}">
            <script type="application/javascript" >
                $(document).ready(function(){
                    window.print();
                });
            </script>
        </c:if>
        <style>
            @page
            {
                size: auto;   /* auto is the initial value */
                margin: 15mm 15mm 15mm 20mm;  /* this affects the margin in the printer settings */
            }
        </style>
    </head>
    <title>${document.name}</title>
    <body>${document.content}</body>
</html>