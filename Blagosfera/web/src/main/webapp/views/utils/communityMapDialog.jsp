<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<div id="modal-community-map-container" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="modal-dialog-label" aria-hidden="true">
  <div class="modal-dialog" style="width : 670px;">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
        <h4 class="modal-title" id="myModalLabel">Объединение на картае</h4>
      </div>
      <div class="modal-body">
        <div id="modal-community-map" style="width : 600px; height : 500px;"></div>
      </div>
    </div>
  </div>
</div>