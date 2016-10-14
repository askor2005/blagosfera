<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="/js/email.js" type="text/javascript"></script>
<script type="text/javascript">
    $(document).ready(function() {
        $("table.table").fixMe();
    });
</script>
<div class="page-header">
    <h1><a id="createTemplate" href="/email/edit" class="btn btn-primary pull-right">Создать</a>Email шаблоны</h1>
</div>

<div class="table-responsive">
    <table class="table table-hover table-striped">
        <thead>
            <tr>
                <th>Название</th>
                <th width="100"></th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${not empty templates}">
                <c:forEach items="${templates}" var="template">
                    <tr id="${template.id}">
                        <td>${template.title}</td>
                        <td class="text-right">
                            <div class="btn-group btn-group-sm">
                                <a class="btn btn-default" href="/email/edit?id=${template.id}"><i class="fa fa-fw fa-pencil"></i></a>
                                <a class="btn btn-default remove" href="#"><i class="fa fa-fw fa-times"></i></a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            <c:if test="${empty templates}">
                <tr>
                    <td colspan="2">Шаблонов нет</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>