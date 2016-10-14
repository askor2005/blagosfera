
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>

<div id="editListTarget">
</div>
<script id="editListTemplate" type="x-tmpl-mustache">
<style type="text/css">
	a.group-color-example, a.group-color-example:hover {
		width : 100px;
		float : left;
		text-decoration : none;
		color : #000;
		background-color : #fff;
		border: 1px solid #fff;		
	}
	
	div.color-wrapper {
		padding : 10px;
		opacity : 0.5;
	}
	
	div.color-wrapper.active, div.color-wrapper:hover {
		opacity : 1;
	}
</style>

<h2>
	{{#createMod}}
		Создание организационного списка
	{{/createMod}}
	{{^createMod}}
		Редактирование организационного списка {{group.name}}
	{{/createMod}}
</h2>
<hr />


<form role="form" method="post" modelAttribute="group">
	<input path="id" value="{{group.id}}" type="hidden"></input>
	<div class="form-group">
		<label>Название организационного списка</label>
		<input path="name" id="name" value="{{group.name}}"  class="form-control"
			placeholder="Введите название организационного списка" />
	</div>
	<div class="form-group">
		<label>Цвет организационного списка</label>
		<input path="color" value="{{group.color}}" type="hidden" id="color"></input>
		<div class="row">
			<div class="col-xs-12">
				{{#colors}}
					<div class="color-wrapper pull-left">
						<a href="#" data-index="{{.}}" class="group-color-example group-color-example-{{.}}">Цвет {{index}}</a>
					</div>
				{{/colors}}
			</div>
		</div>
	</div>
	<hr/>
	<a href="#" id="saveGroup" class="btn btn-primary">Сохранить</a>
	<a href="/contacts/lists" class="btn btn-default">Отмена</a>
	<hr/>
	<span id="errors" class="alert alert-danger" role="alert"  style="display : block; display: none" />

</form>
</script>
<script type="text/javascript">
	$(document).ready(function(){
		var initEditForm = function(createMod,group) {
			var template = $('#editListTemplate').html();
			Mustache.parse(template);
			var rendered = Mustache.render(template, {createMod : createMod,group: group,colors: [1,2,3,4,5,6,7,8,9,10]});
			$('#editListTarget').html(rendered);
			var selectedColor = $("input#color").val();
			if (selectedColor && selectedColor > 0) {
				$("a.group-color-example-" + selectedColor).parent().addClass("active");
				$("a.group-color-example-" + selectedColor).prepend("<span class='glyphicon glyphicon-ok'> </span> ")
			} else {
				$("input#color").val(1);
				$("a.group-color-example-1").parent().addClass("active");
				$("a.group-color-example-1").prepend("<span class='glyphicon glyphicon-ok'> </span> ")
			}

			$("a.group-color-example").click(function(){
				var index = $(this).attr("data-index");
				$("input#color").val(index);
				$("div.color-wrapper").removeClass("active");
				$(this).parent().addClass("active");
				$("div.color-wrapper .glyphicon.glyphicon-ok").remove();
				$(this).prepend("<span class='glyphicon glyphicon-ok'> </span> ")
				return false;
			});
			$("#saveGroup").click(function(){
				var params = {name :$("#name").val(),color : $("#color").val()};
                if (!createMod) {
                    params.id = groupId;
                }
				$.radomJsonPost("/contacts/lists/edit.json", params, function(response) {
                    if (response.result == "error") {
						$("#errors").css("display","inline");
						$("#errors").html(response.message);
					}
                    else {
                        window.location.href = response;
                    }
				});
			});
		}
		var groupId = "${groupId}";
		if (groupId == "") {
			initEditForm(true,{color : 1, name : ""});
		}
		else {
			$.radomJsonGet("/contacts/lists/get.json?id="+groupId, {}, function(response) {
				initEditForm(false,response);
			});
		}
	});
</script>