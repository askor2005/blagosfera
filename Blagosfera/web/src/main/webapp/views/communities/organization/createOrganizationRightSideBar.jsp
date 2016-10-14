<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<t:insertAttribute name="sharerRightSidebar" />
<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Создание юр лица</h3>
    </div>
    <div class="panel-body">
        <div class="form-group" style="text-align: center;">
            <button id="saveTempDataCreateOrganizationButton" class="btn btn-primary" >Сохранить изменения в форме</button>
        </div>
        <div class="form-group" style="text-align: center;">
            <button id="clearTempDataCreateOrganizationButton" class="btn btn-primary" >Очистить форму</button>
        </div>
        <div class="form-group" style="visibility: hidden;height: 16px;padding: 0px;margin: 0px;" id="createOrganizationFormSaveTimeBlock">
            <div style="text-align: center; display: none;" id="saveOrganizationDataInProcess">
                <img src="/i/ajax-loader-small.gif" />
            </div>
            <p class="help-block"
               id="onSuccessSaveTempData"
               style="
                display: none;
                font-size: 13px;
                padding: 0px;
                margin: 0px;">Последнее сохранение было в <span id="createOrganizationFormSaveTime"></span></p>
            <p class="help-block"
               id="onErrorSaveTempData"
               style="
                display: none;
                font-size: 13px;
                padding: 0px;
                margin: 0px;">При сохранении данных произошла ошибка</p>
        </div>
    </div>
</div>