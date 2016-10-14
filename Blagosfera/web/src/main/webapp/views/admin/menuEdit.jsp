<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">
	div.sections-tree span.badge {
		background-color : transparent;
	}
</style>

<script type="text/javascript">

var rootSectionId = null;
var rootSectionName = "${sectionName}";

function isURL(str) {
	var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
			'((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.?)+[a-z]{2,}|'+ // domain name
			'((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
			'(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
			'(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
			'(\\#[-a-z\\d_]*)?$','i'); // fragment locator
	return pattern.test(str);
}

function loadSectionByName(sectionName, callBack) {
	$.radomJsonPost(
			"/menu_edit/loadByName.json",
			{
				section_name : sectionName
			},
			callBack
	);
}

$(document).ready(function() {
	loadSectionByName(rootSectionName, function(rootSection){
		rootSectionId = rootSection.id;
		$("#menuEditHeader").text("Редактирование меню портала " + rootSection.title);
		initMenuAdmin();
	});
});

function initMenuAdmin() {
	$("table#gtreetable").fixMe();
	$("div#section-edit-modal input#application-name").typeahead({
	    onSelect: function(item) {
        	$("div#section-edit-modal input#application-id").val(item.value);
        	$("div#section-edit-modal input#application-name").attr("readonly", "readonly");
        	$("div#section-edit-modal a#clear-application-link").show();	
	    },
	    ajax: {
	        url: "/apps/list.json",
	        timeout: 100,
	        displayField: "name",
	        triggerLength: 0,
	        method: "get",
	        loadingClass: "loading-circle",
	        preDispatch: function (query) {
	            return {
	                query : query,
	                page : 1,
	                per_page : 8
	            }
	        },
	        preProcess: function (response) {
	            if (response.result == "error") {
	                bootbox.alert("Ошибка загрузки списка приложений");
	                return false;
	            }
	            return response;
	        }
	    }
	});
	
    jQuery('#gtreetable').gtreetable({
        'language': 'ru',
        'manyroots': true,
        'draggable': true,
        'dragCanExpand': true, 
        'source': function(id) {
            return {
                type: 'GET',
                url: '/menu_edit/tree.json',
                data: {
                    id : id,
                    root_id : rootSectionId
                },
                dataType: 'json',
                error: function(XMLHttpRequest) {
                    bootbox.alert("Ошибка загрузки разделов меню");
                }
            }
        },
        "onSave": function(oNode) {
            return {
                type: 'POST',
                url: '/menu_edit/save.json',
                data: {
                    section_id: oNode.getId(),
                    parent_id: oNode.getParent(),
                    title: oNode.getName(),
                    position: oNode.getInsertPosition(),
                    related_id: oNode.getRelatedNodeId(),
                    root_id : rootSectionId
                },
                dataType: 'json',
                error: function(XMLHttpRequest) {
                    bootbox.alert($.parseJSON(XMLHttpRequest.responseText).message);
                }
            };
        },
        "onDelete": function(oNode) {
            return {
                type: 'POST',
                url: '/menu_edit/delete.json',
                dataType: 'json',
                data: {
                    section_id: oNode.getId(),
                    root_id : rootSectionId
                },
                error: function(XMLHttpRequest) {
                    bootbox.alert($.parseJSON(XMLHttpRequest.responseText).message);
                }
            };
        },
        'onMove': function(oSource, oDestination, position) {
            return {
                type: 'POST',
                url: '/menu_edit/move.json',
                data: {
                	section_id: oSource.getId(),
                    related_id: oDestination.getId(),
                    position: position,
                    root_id : rootSectionId
                },
                dataType: 'json',
                error: function(XMLHttpRequest) {
                	bootbox.alert($.parseJSON(XMLHttpRequest.responseText).message);
                }
            };
        },


        'actions': [{
            name: 'Редактировать',
            event: function(oNode, oManager) {
                $.radomJsonGet('/menu_edit/load.json', {
                    section_id: oNode.id,
                    root_id : rootSectionId
                }, function(section) {
                    $("div#section-edit-modal input#title").val(section.title);
                    var link = section.link;
                    if (link && link.indexOf("/Благосфера/") == 0 && rootSectionName == "blagosfera") {
                    	link = link.substring("/Благосфера/".length);
                    }
                    $("div#section-edit-modal input#link").val(link);
                    $("div#section-edit-modal input#help-link").val(section.helpLink);
                   // $("div#section-edit-modal input#published").prop("checked", section.published);
					$("div#section-edit-modal input#openInNewLink").prop("checked", section.openInNewLink);
					$("div#section-edit-modal input#disabled").prop("checked", section.disabled);
					$("div#section-edit-modal input#showToAdminUsersOnly").prop("checked", section.showToAdminUsersOnly);
					$("div#section-edit-modal input#showToVerifiedUsersOnly").prop("checked", section.showToVerfiedUsersOnly);
					$("div#section-edit-modal input#showToAuthorizedUsersOnly").prop("checked", section.showToAuthorizedUsersOnly);
					$("div#section-edit-modal select#minRegistratorLevelToShow").val(section.minRegistratorLevelToShow);
                    $("div#section-edit-modal input#id").val(section.id);
                    $("div#section-edit-modal select#type").val(section.type);
                    switch(section.type) {
                	case "EDITABLE" :
                		$("div#section-edit-modal div#applications-list").hide();
                		$("div#section-edit-modal div#link-block").show();
                		break;
                	case "APPLICATION" :
                        if (section.application) {
                        	$("div#section-edit-modal input#application-id").val(section.application.id);
                        	$("div#section-edit-modal input#application-name").val(section.application.name).attr("readonly", "readonly");
                        	$("div#section-edit-modal a#clear-application-link").show();
                        } else {
                        	$("div#section-edit-modal input#application-id").val("");
                        	$("div#section-edit-modal input#application-name").val("").removeAttr("readonly");
                        	$("div#section-edit-modal a#clear-application-link").hide();
                        }
                		$("div#section-edit-modal div#applications-list").show();
                		$("div#section-edit-modal div#link-block").hide();
                		$("div#section-edit-modal div#link-block input#link").val("");
                		break;
                	case "SYSTEM" :
                		$("div#section-edit-modal div#applications-list").hide();
                		$("div#section-edit-modal div#link-block").show();
                		break;
                	default :
                		break;
                	}

					$("div#section-edit-modal span#span-icon").addClass($("div#section-edit-modal input#icon-class").val());

                    $("div#section-edit-modal a#clear-application-link").off("click");
                    $("div#section-edit-modal a#clear-application-link").on("click", function() {
                    	$("div#section-edit-modal input#application-id").val("");
                    	$("div#section-edit-modal input#application-name").val("").removeAttr("readonly").focus();
                    	$("div#section-edit-modal a#clear-application-link").hide();
                    	return false;
                    });
                    $("div#section-edit-modal select#type").change(function() {
                    	var type = $(this).val();
                    	switch(type) {
                    	case "EDITABLE" :
                    		$("div#section-edit-modal input#application-id").val("");
                    		$("div#section-edit-modal div#applications-list").slideUp();
                    		$("div#section-edit-modal div#link-block").slideDown();
                    		break;
                    	case "APPLICATION" :
                    		$("div#section-edit-modal input#application-id").val("");
                    		$("div#section-edit-modal input#application-name").val("").removeAttr("readonly");
                    		$("div#section-edit-modal a#clear-application-link").hide();	
                    		$("div#section-edit-modal div#applications-list").slideDown();
                    		$("div#section-edit-modal div#link-block").slideUp();
                    		$("div#section-edit-modal div#link-block input#link").val("");
                    		$("div#section-edit-modal div#link-block a#clear-application-link").val("");
                    		break;
                    	case "SYSTEM" :
                    		$("div#section-edit-modal input#application-id").val("");
                    		$("div#section-edit-modal div#applications-list").slideUp();
                    		$("div#section-edit-modal div#link-block").slideDown();
                    		break;
                    	default :
                    		break;
                    	}
                    });

					function iconInputChange() {
						var $iconInput = $("div#section-edit-modal input#icon-class");
						var $spanIcon = $("div#section-edit-modal span#span-icon");
						var $imageIcon = $("div#section-edit-modal img#image-icon");

						var inputValue = $iconInput.val();
						if(isURL(inputValue)) {
							$imageIcon.show();
							$spanIcon.hide();

							$imageIcon.attr('src',inputValue);
						} else {
							$imageIcon.hide();
							$spanIcon.show();

							$spanIcon.removeClass();
							$spanIcon.addClass(inputValue);
						}
					}

					$("div#section-edit-modal input#icon-class").on('input',iconInputChange);
					$("div#section-edit-modal input#icon-class").on('change',iconInputChange);

					if(section.icon) {
						$("div#section-edit-modal input#icon-class").val(section.icon).change();
					}

					$("div#section-edit-modal a#upload-icon-url").off('click').click(function() {
						IconUploadDialog.show();
						return false;
					});

                    $("div#section-edit-modal button#apply-button").off("click");
                    $("div#section-edit-modal button#apply-button").on("click", function() {
                        var link = $("div#section-edit-modal input#link").val();
                        if (link && rootSectionName == "blagosfera") {
                        	link = "/Благосфера/" + link;
                        }
                        $.radomJsonPost('/menu_edit/edit.json', {
                            section_id: $("div#section-edit-modal input#id").val(),
                            root_id : rootSectionId,
                            title: $("div#section-edit-modal input#title").val(),
                            link: link,
							icon: $("div#section-edit-modal input#icon-class").val(),
                            help_link: $("div#section-edit-modal input#help-link").val(),
                            type : $("div#section-edit-modal select#type").val(),
                            application_id : $("div#section-edit-modal input#application-id").val(),
                            published: true,//сейчас это свойство все равно не используется
							openInNewLink: $("div#section-edit-modal input#openInNewLink").prop("checked"),
							disabled: $("div#section-edit-modal input#disabled").prop("checked"),
							show_to_admin_users_only: $("div#section-edit-modal input#showToAdminUsersOnly").prop("checked"),
							show_to_verified_users_only: $("div#section-edit-modal input#showToVerifiedUsersOnly").prop("checked"),
							show_to_authorized_users_only: $("div#section-edit-modal input#showToAuthorizedUsersOnly").prop("checked"),
							min_registrator_level_to_show: $("div#section-edit-modal select#minRegistratorLevelToShow").val(),
                        }, function(section) {
                            oNode.name = section.title;
                            oNode.render();
                            $("div#section-edit-modal").modal("hide");

							var $sectionIcon = $("div[data-section-id="+section.id+"]").find("span#section-icon");
							$sectionIcon.empty();

							if(section.icon) {
								if(isURL(section.icon)) {
									$sectionIcon.html('<img id="image-icon" style="width: 19px; height: 19px;" src="'+ section.icon +'"/>');
								} else {
									$sectionIcon.html('<i class="' + section.icon + '"></i>');
								}
							} else {
								$sectionIcon.html('<i class="fa fa-question-circle"></i>');
							}
                        });
                    });
                    $("div#section-edit-modal").modal();
                });
            }
        }]
    });
}
	
</script>

<h1 id="menuEditHeader"></h1>
<hr />
<table class="table gtreetable" id="gtreetable">
	<thead>
		<tr>
			<th>Разделы</th>
		</tr>
	</thead>
</table>
<div class="modal fade" id="section-edit-modal" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Редактирование раздела</h4>
			</div>
			<div class="modal-body">
				<input type="hidden" value="" id="id" />
				<div class="form-group">
					<label>Название</label>
					<input type="text" class="form-control" id="title" placeholder="Название">
					<span class="help-block">Поле обязательно для заполнения</span>
				</div>				
				<div class="form-group" id="link-block">
					<label>Ссылка</label>
					<input type="text" class="form-control" id="link" placeholder="Ссылка" />
					<span class="help-block">Поле не обязательно для заполнения, оставьте пустым если раздел не должен иметь собственной отдельной страницы</span>
				</div>
				<div class="form-group">
					<label>Ссылка на справку</label>
					<div class="input-group">
						<div class="input-group-addon">http://ramera.ru/help/</div>					
						<input type="text" class="form-control" id="help-link" placeholder="Ссылка на справку">
					</div>
					<span class="help-block">Поле не обязательно для заполнения, оставьте пустым если раздел не должен ссылаться на справку</span>
				</div>
				<div class="form-group">
					<label>Тип раздела</label>
					<select class="form-control" id="type">
						<option value="EDITABLE">Раздел с редактируемым контентом</option>
						<option value="APPLICATION">Раздел приложения</option>
						<option value="SYSTEM">Системный раздел</option>
					</select>
					<span class="help-block">Выберите тип раздела</span>
				</div>
				<div class="form-group">
					<label>Иконка</label>
					<div class="input-group">
						<input type="text" class="form-control" id="icon-class" placeholder="Класс иконки" value=""/>
						<div class="input-group-addon"><a id="upload-icon-url" href="#">Загрузить</a></div>
						<div class="input-group-addon"><span id="span-icon" style="display:none;"></span><img id="image-icon" style="width: 15px; height: 15px; display:none;" src=""/></div>
					</div>
					<span id="help-block-icon" class="help-block">Введите класс иконки</span>
				</div>
				<div class="form-group" id="applications-list">
					<label>Приложение</label>
					<input type="text" class="form-control" id="application-name" />
					<input type="hidden" id="application-id" />
					<a href="#" class="pull-right" id="clear-application-link">Изменить</a>
					<span class="help-block">Выберите приложение</span>
				</div>
				<div class="checkbox">
					<label>
						<input type="checkbox" id="openInNewLink"> Открывать раздел в новом окне(для корневых разделов)
					</label>
				</div>
				<div class="checkbox">
					<label>
						<input type="checkbox" id="disabled"> Сделать неактивным
					</label>
				</div>
				<div class="checkbox">
					<label>
						<input type="checkbox" id="showToAdminUsersOnly"> Видимый только для администраторов системы
					</label>
				</div>
				<div class="checkbox">
					<label>
						<input type="checkbox" id="showToVerifiedUsersOnly"> Видимый только для идентифицированных пользователей
					</label>
				</div>
				<div class="checkbox">
					<label>
						<input type="checkbox" id="showToAuthorizedUsersOnly"> Видимый только для авторизованных пользователей
					</label>
				</div>
				<div class="form-group">
					<label>Минимальная категория регистратора, которому будет показываться данный раздел</label>
					<select class="form-control" id="minRegistratorLevelToShow">
						<option value="" selected="selected">Не важно</option>
						<option value="0">Регистратор высшей категории</option>
						<option value="1">Регистратор 1 категории</option>
						<option value="2">Регистратор 2 категории</option>
						<option value="3">Регистратор 3 категории</option>
					</select>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
				<button type="button" class="btn btn-primary" id="apply-button">Сохранить</button>
			</div>
		</div>
	</div>
</div>