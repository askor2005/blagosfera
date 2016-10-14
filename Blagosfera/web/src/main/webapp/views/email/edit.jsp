<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="/js/email.js" type="text/javascript"></script>

<div class="page-header">
    <h1>Редактирование Email шаблона</h1>
</div>

<form:form cssClass="form-horizontal" action="/email/save" method="POST">
    <form:hidden path="id" />
    <div class="form-group">
        <form:label path="title" cssClass="col-sm-3 control-label">Название</form:label>
            <div class="col-sm-9">
            <form:input path="title" cssClass="form-control" placeholder="Название"/>
        </div>
    </div>
    <div class="form-group">
        <form:label path="from" cssClass="col-sm-3 control-label">От кого</form:label>
            <div class="col-sm-9">
            <form:input path="from" cssClass="form-control" placeholder="От кого"/>
        </div>
    </div>
    <div class="form-group">
        <form:label path="subject" cssClass="col-sm-3 control-label">Тема письма</form:label>
            <div class="col-sm-9">
            <form:input path="subject" cssClass="form-control" placeholder="Тема письма"/>
        </div>
    </div>
    <div class="form-group">
        <form:label path="body" cssClass="col-sm-3 control-label">Шаблон письма</form:label>
            <div class="col-sm-9">
            <form:textarea path="body" cssClass="form-control" cssStyle="height: 300px;" placeholder="Шаблон письма"></form:textarea>
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-3 col-sm-9">
                <button type="submit" class="btn btn-primary">Сохранить</button>
                <a href="/email/" class="btn btn-default">Назад</a>
            </div>
        </div>
</form:form>