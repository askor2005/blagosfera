<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style type="text/css">

	ul#participants-list {
		padding-left : 0;
	}

	ul#participants-list li{
		display : inline-block;
		list-style : none;
		margin-right : 5px;
	}
</style>

<script id="create-phase-template" type="x-tmpl-mustache">
<div class="panel panel-default" id="create-phase-panel">
	<div class="panel-heading">
		<h3 class="panel-title">Настройки голосования</h3>
	</div>
	<div class="panel-body">
		<form role="form" id="create-voting-form">
			<div class="form-group">
				<label for="topic">Тематика голосования</label>
				{{#voting}}
		    		<input type="text" class="form-control" value="{{voting.topic.name}}" readonly="readonly" />
				{{/voting}}
				{{^voting}}
			    	<select name="topic_id" class="form-control" id="topic">
			    		<option value="">Выберите тематику голосования из списка</option>
			    	</select>
		    		<span class="help-block">Из данного списка необходимо выбрать тематику, которая в наибольшей степени соответствует голосованию, которое Вы создаете.</span>
				{{/voting}}
			</div>
			<div class="form-group">
				<label for="topic">Вопрос, вынесенный на голосование</label>
				{{#voting}}
					<input class="form-control" value="{{voting.question}}" readonly="readonly" />
				{{/voting}}
				{{^voting}}
		    		<input name="question" class="form-control" id="question" placeholder="Введите текст вопроса" />
			    	<span class="help-block">В данном поле необходимо указать краткую формулировку вопроса, который Вы выносите не голосование.</span>
				{{/voting}}
			</div>
			<div class="form-group">
				<label for="description">Описание голосования</label>
				{{#voting}}
					<textarea class="form-control" readonly="readonly">{{voting.description}}</textarea>
				{{/voting}}
				{{^voting}}
		    		<textarea name="description" class="form-control" id="description"></textarea>
		    		<span class="help-block">В данном поле можно дать подробное описание голосованию, которое Вы создаете, привести необходимые коментарии. Поле не обязательно для заполнения.</span>
				{{/voting}}
			</div>
			<div class="form-group">
				<label>Предмет голосования</label>
				{{#voting}}
					<fieldset disabled>
						<div class="radio">
							<label>
								<input type="radio" {{^voting.answerIsSharer}} checked="checked" {{/voting.answerIsSharer}} />
								Принятие решения
							</label>
						</div>
						<div class="radio">
							<label>
								<input type="radio" {{#voting.answerIsSharer}} checked="checked" {{/voting.answerIsSharer}} />
								Выборы
							</label>					
						</div>
					</fieldset>
				{{/voting}}
				{{^voting}}
					<div class="radio">
						<label>
							<input type="radio" name="answer_is_sharer" value="false" checked="checked" />
							Принятие решения
						</label>
					</div>					
					<div class="radio">
						<label>
							<input type="radio" name="answer_is_sharer" value="true" />
							Выборы
						</label>					
					</div>
					<span class="help-block">Необходимо выбрать предмет голосования, которое Вы создаете. Если голосование связано с выбором из списка кандидатур участников, следует отметить вариант "Выборы", в противном случает - вариарнт "Принятие решения"</span>
				{{/voting}}
			</div>
			{{#voting.answerIsSharer}}
				<div class="form-group" id="self-promotion">
					<label>Самовыдвижение</label>
					<fieldset disabled>
						<div class="radio">
							<label>
								<input type="radio" {{^voting.allowSelfPromotion}} checked="checked" {{/voting.allowSelfPromotion}} />
								Запретить
							</label>
						</div>
						<div class="radio">									
							<label>
								<input type="radio" {{#voting.allowSelfPromotion}} checked="checked" {{/voting.allowSelfPromotion}} />
								Разрешить
							</label>
						</div>
					</fieldset>
				</div>
			{{/voting.answerIsSharer}}
			{{^voting}}
				<div class="form-group" id="self-promotion" style="display : none;">
					<label>Самовыдвижение</label>
					<div class="radio">
						<label>
							<input type="radio" name="allow_self_promotion" value="false"/>
							Запретить
						</label>
					</div>
					<div class="radio">									
						<label>
							<input type="radio" name="allow_self_promotion" value="true" />
							Разрешить
						</label>
					</div>
					<span class="help-block">Для голосования, предметом которого являются Выборы, необходимо определить, разрешено ли участникам выдвигать свои кандидатуоы самостоятельно или это будет разрешено только организатору голосования.</span>
				</div>
			{{/voting}}

			<div class="form-group">
				<label for="description">Дата и время окончания голосования</label>
				{{#voting}}
					<input type="text" class="form-control" value="{{voting.deadlineDate}}" readonly="readonly" />
				{{/voting}}
				{{^voting}}
					<div class="row">
						<div class="form-group col-xs-6">
							<div id="deadline-date" class="form-control"></div>
						</div>
						<div class="form-group col-xs-6">
							<div id="deadline-time" class="form-control"></div>
						</div>
					</div>
		    		<span class="help-block">В данном поле необходимо указать дату и время окончания голосования, которое Вы создаете. По истечении указанного времени голосование будет автоматически завершено.</span>
				{{/voting}}
			</div>
			{{^voting}}
				<div class="form-group">
					<a href="#" class="btn btn-primary" id="create-phase-next-button">Сохранить и продолжить</a>
				</div>
			{{/voting}}
		</form>
	</div>
</div>
</script>

<script id="system-phase-template" type="x-tmpl-mustache">

<div class="panel panel-default" id="system-phase-panel">
	<div class="panel-heading">
		<h3 class="panel-title">Выбор системы голосования</h3>
	</div>
	<div class="panel-body">
		<form role="form" id="system-voting-form">
			<input type="hidden" name="voting_id", value="{{voting.id}}" />
			<div class="form-group">
				<label for="description">Система голосования</label>
				{{#activePhase}}
					<select name="system_id" class="form-control" id="system">
						<option value="">Выберите систему голосования</option>
					</select>
					<span class="help-block">Необходимо выбрать одну из представленных систем проведения голосования. Для получения подробной информации о системе, выберите ее в списке.</span>
					<br/>
					<span id="voting-system-description"></span>
				{{/activePhase}}

				{{^activePhase}}
					<input type="text" class="form-control" value="{{voting.system.name}}" readonly="readonly" />				
				{{/activePhase}}
			</div>
			<div class="form-group">
				{{#activePhase}}
					<a href="#" class="btn btn-primary" id="system-phase-next-button">Сохранить и продолжить</a>
				{{/activePhase}}
			</div>
		</form>
	</div>
</div>
</script>

<script id="settings-phase-template" type="x-tmpl-mustache">

<div class="panel panel-default" id="settings-phase-panel">
	{{#hasSettings}}
		<div class="panel-heading">
			<h3 class="panel-title">Настройки системы голосования</h3>
		</div>
		<div class="panel-body">
			<form role="form" id="settings-voting-form">
				<input type="hidden" name="voting_id", value="{{voting.id}}" />
				{{#activePhase}}
					{{#voting.system.settings}}
						<div class="form-group">
							<label>{{name}}</label>
							<input type="text" class="form-control" name="s:{{id}}" />
							<span class="help-block">{{description}}</span>
						</div>
					{{/voting.system.settings}}
					<div class="form-group">
						{{#activePhase}}
							<a href="#" class="btn btn-primary" id="settings-phase-next-button">Сохранить и продолжить</a>
						{{/activePhase}}
					</div>
				{{/activePhase}}
				{{^activePhase}}
					{{#voting.settingValues}}
						<div class="form-group">
							<label>{{setting.name}}</label>
							<input type="text" class="form-control" readonly="readonly" value="{{value}}" />
						</div>
					{{/voting.settingValues}}				
				{{/activePhase}}
			</form>
		</div>
	{{/hasSettings}}
</div>

</script>

<script id="participants-phase-template" type="x-tmpl-mustache">

<div class="panel panel-default" id="participants-phase-panel">
	<div class="panel-heading">
		<h3 class="panel-title">Список участников</h3>
	</div>
	<div class="panel-body">
		{{#activePhase}}
			<ul id="participants-list">
				{{#voting.sharers}}
					<li>
						{{fullName}}
						<a href="#" data-sharer-id="{{id}}" class="sharer-delete-link glyphicon glyphicon-remove"></a>
					</li>
				{{/voting.sharers}}
				{{#voting.communities}}
					<li>
						{{name}}
						<a href="#" data-community-id="{{id}}" class="community-delete-link glyphicon glyphicon-remove"></a>
					</li>
				{{/voting.communities}}
			</ul>
			<hr/>
			<div class="form-group">
				<label>Добавить участника</label>
				<input id="sharer-add-input" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя участника" />
			</div>
			<div class="form-group">
				<label>Добавить объединение</label>
				<input id="community-add-input" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить название объединения" />
			</div>
			<div class="form-group">
				<a href="#" class="btn btn-primary" id="participants-phase-next-button">Сохранить и продолжить</a>
			</div>
		{{/activePhase}}
		{{^activePhase}}
			<ul id="participants-list">
				{{#voting.sharers}}
					<li>{{fullName}}; </li>
				{{/voting.sharers}}
				{{#voting.communities}}
					<li>{{name}}; </li>
				{{/voting.communities}}
			</ul>
		{{/activePhase}}
	</div>
</div>

</script>

<script id="answers-phase-template" type="x-tmpl-mustache">

<div class="panel panel-default" id="answers-phase-panel">
	<div class="panel-heading">
		<h3 class="panel-title">Список ответов (кандидатур)</h3>
	</div>
	<div class="panel-body">
		{{#activePhase}}
			<ul id="answers-list">
				{{#voting.answers}}
					<li>
						{{text}}
						<a href="#" class='answer-delete-link pull-right' data-answer-id='{{id}}'>Удалить</a>
					</li>
				{{/voting.answers}}
			</ul>
			<hr/>

			{{#voting.answerIsSharer}}
				<div class="form-group">
					<input id="answer-sharer-add-input" type="text" autocomplete="off" class="form-control" placeholder="Начните вводить имя участника" />
				</div>
			{{/voting.answerIsSharer}}

			{{^voting.answerIsSharer}}
				<div class="form-group">
					<input id="answer-text-add-input" type="text" autocomplete="off" class="form-control" placeholder="Введите вариант ответа" />
				</div>
				<div class="form-group">
					<a href="#" id="answer-text-add-link">Добавить вариант ответа</a>
				</div>
			{{/voting.answerIsSharer}}

			<hr />

			<div class="form-group">
				<a class="btn btn-primary" href="#" id="answers-phase-next-button">Сохранить и начать голосование</a>
			</div>

		{{/activePhase}}
		{{^activePhase}}
			<ul id="answers-list">
				{{#voting.answers}}
					<li>{{text}}</li>
				{{/voting.answers}}
			</ul>
		{{/activePhase}}
	</div>
</div>

</script>

<script id="votes-phase-template" type="x-tmpl-mustache">

<div class="panel panel-default" id="votes-phase-panel">
	<div class="panel-heading">
		<h3 class="panel-title">Создание голосования завершено</h3>
	</div>
	<div class="panel-body">
		Создание голосования завершено, голосование начато. За ходом голосования можно следить на <a href="/voting/control/{{voting.id}}">его странице</a>. Там же можно будет ознакомиться с результатами по завершению голосования. 
	</div>
</div>

</script>

<script type="text/javascript">

	var createPhaseTemplate = $("#create-phase-template").html();
	Mustache.parse(createPhaseTemplate);
	var systemPhaseTemplate = $("#system-phase-template").html();	
	Mustache.parse(systemPhaseTemplate);

	var settingsPhaseTemplate = $("#settings-phase-template").html();	
	Mustache.parse(settingsPhaseTemplate);
	
	var participantsPhaseTemplate = $("#participants-phase-template").html();	
	Mustache.parse(participantsPhaseTemplate);
	
	var answersPhaseTemplate = $("#answers-phase-template").html();	
	Mustache.parse(answersPhaseTemplate);
	
	var votesPhaseTemplate = $("#votes-phase-template").html();	
	Mustache.parse(votesPhaseTemplate);
	
	function animateReplaceWith($old, $new) {
		$new.hide();
		$old.after($new);
		$old.slideUp(function(){
			$old.remove();
		});
		$new.slideDown();
	}

	var voting = null;
	
	function getVoting(callback) {
		var votingId = window.location.hash.substr(1);
		if (votingId) {
			$.ajax({
				type : "get",
				dataType : "json",
				data : {
					voting_id : votingId
				},
				url : "/voting/get.json",
				success : function(response) {
					if (response.result == "error") {
						bootbox.alert(response.message);
					} else {
						voting = response;
						callback();
					}
				},
				error : function() {
					console.log("ajax error");
				}
			});
		} else {
			callback();
		}
	}
	
	function getTopicMarkup(topic, level) {
		var indent = "";
		for (i = 0; i < level; i++) {
			indent += "&nbsp;&nbsp;&nbsp;";
		}
		var markup = "<option value='" + topic.id + "'>" + indent + topic.name + "</option>";
		if (topic.children.length > 0) {
			$.each(topic.children, function(index, child){
				markup += getTopicMarkup(child, level + 1);
			});
		}
		return markup;
	}
	
	function createVoting() {
		$.radomJsonPost("/voting/create.json", $("form#create-voting-form").serialize(), function(response) {
			window.location.hash = "#" + response.id
			voting = response;
			showCreatePhase();
		});
		return false;
	}

	function showCreatePhase() {
		var model = {
				voting : voting
		};
		$createPhasePanel = $(Mustache.render(createPhaseTemplate, model));
		if (!voting) {
			$createPhasePanel.find("input[name=answer_is_sharer][value=false]").click(function(){
				var $selfPromotion = $("div#self-promotion");
				$selfPromotion.find("input").removeAttr("checked");
				$selfPromotion.slideUp();
			});

			$createPhasePanel.find("input[name=answer_is_sharer][value=true]").click(function(){
				var $selfPromotion = $("div#self-promotion");
				$selfPromotion.find("input[name=allow_self_promotion][value=false]").click();
				$selfPromotion.slideDown();
			});
			
			$.ajax({
				type : "get",
				dataType : "json",
				url : "/voting/topic/list.json",
				success : function(response) {
					$.each(response, function(index, topic){
						$createPhasePanel.find("select#topic").append(getTopicMarkup(topic, 0));
					});
				},
				error : function() {
					console.log("ajax error");
				}
			});
			
			Ext.onReady(function() {
				Ext.create('Ext.form.field.Date', {
					renderTo : 'deadline-date',
					xtype : 'datefield',
					name : 'deadline_date',
					format : 'd.m.Y',
					width : '100%'
				});
				Ext.create('Ext.form.field.Time', {
					renderTo : 'deadline-time',
					xtype : 'timefield',
					name : 'deadline_time',
					format : 'H:i',
					width : '100%'
				});
			});	
			$createPhasePanel.find("a#create-phase-next-button").click(function(){
				return createVoting();
			});
		} else {
			showSystemPhase();
		}
		animateReplaceWith($("#create-phase-panel"), $createPhasePanel);
	}
	
	function showSystemPhase() {
		var model = {
				voting : voting,
				activePhase : voting.phase == "SYSTEM"
		};
		$systemPhasePanel = $(Mustache.render(systemPhaseTemplate, model));
		if (voting.phase == "SYSTEM") {
			$.ajax({
				type : "get",
				dataType : "json",
				url : "/voting/systems_list.json",
				success : function(response) {
					var $select = $systemPhasePanel.find("select#system");
					$.each(response, function(index, system){
						var $option = $("<option value='" + system.id + "'>" + system.name + "</option>");
						$option.data("description", system.description);
						$select.append($option);
					});
					$select.change(function(){
						var $this = $(this);
						var description = $this.find("option:selected").data("description");
						var $description = $this.parents(".form-group").find("span#voting-system-description")
						$description.slideUp(200, function(){
							if (description) {
								$description.html(description);
							} else {
								$description.html("");
							}
							$description.slideDown(200);
						});
						
					});
				},
				error : function() {
					console.log("ajax error")
				}
			});
			$systemPhasePanel.find("a#system-phase-next-button").click(function(){
				$.radomJsonPost("/voting/control/set_system.json", $("form#system-voting-form").serialize(), function(response) {
					$.radomJsonPost("/voting/control/next_phase.json", {
						voting_id : voting.id
					}, function(response) {
						voting = response;
						showSystemPhase();
					});
				});
				return false;
			});
		} else {
			if (voting.phase == "SETTINGS") {
				showSettingsPhase();
			} else {
				showParticipantsPhase();
			}
		}
		animateReplaceWith($("#system-phase-panel"), $systemPhasePanel);
	}
	
	function showSettingsPhase() {
		var model = {
				voting : voting,
				hasSettings : voting.system.settings.length > 0,
				activePhase : voting.phase == "SETTINGS"
		};
		$settingsPhasePanel = $(Mustache.render(settingsPhaseTemplate, model));
		if (voting.phase == "SETTINGS") {
			$settingsPhasePanel.find("a#settings-phase-next-button").click(function() {
				
				$.radomJsonPost("/voting/control/set_settings.json", $("form#settings-voting-form").serialize(), function(response) {
					$.radomJsonPost("/voting/control/next_phase.json", {
						voting_id : voting.id
					}, function(response) {
						voting = response;
						showSettingsPhase();
					});
				});
				return false;
			});
		} else {
			showParticipantsPhase();
		}
		animateReplaceWith($("#settings-phase-panel"), $settingsPhasePanel);
	}
	
	function addSharerMarkup(sharer) {
		$("ul#participants-list").append("<li>" + sharer.fullName + " <a data-sharer-id='" + sharer.id + "' class='sharer-delete-link glyphicon glyphicon-remove' href='#'></a></li>");
	}
	
	function addSharer(sharerId) {
		$.radomJsonPost("/voting/control/add_sharer.json", {
			voting_id : voting.id,
			sharer_id : sharerId
		}, function(response){
			addSharerMarkup(response);
			$("input#sharer-add-input").val("");
		});
		$("input#sharer-add-input").val("");
	}
	
	function deleteSharer(sharerId) {
		$.radomJsonPost("/voting/control/delete_sharer.json", {
			voting_id : voting.id,
			sharer_id : sharerId
		}, function(response){
			$participantsPhasePanel.find("a[data-sharer-id=" + sharerId + "]").parent().remove();
		});		
	}
	
	function addCommunityMarkup(community) {
		$("ul#participants-list").append("<li>" + community.name + " <a data-community-id='" + community.id + "' class='community-delete-link glyphicon glyphicon-remove' href='#'></a></li>");
	}
	
	function addCommunity(communityId) {
		$.radomJsonPost("/voting/control/add_community.json", {
			voting_id : voting.id,
			community_id : communityId 
		}, function(response){
			addCommunityMarkup(response);
			$("input#community-add-input").val("");
		});
		$("input#community-add-input").val("");
	}
	
	function deleteCommunity(communityId) {
		$.radomJsonPost("/voting/control/delete_community.json", {
			voting_id : voting.id,
			community_id : communityId
		}, function(response){
			$participantsPhasePanel.find("a[data-community-id=" + communityId + "]").parent().remove();
		});		
	}
	
	function showParticipantsPhase() {
		var model = {
				voting : voting,
				activePhase : voting.phase == "PARTICIPANTS"
		};
		$participantsPhasePanel = $(Mustache.render(participantsPhaseTemplate, model));
		if (voting.phase == "PARTICIPANTS") {
			
			$participantsPhasePanel.on( "click", "a.sharer-delete-link", function() {
				deleteSharer($(this).attr("data-sharer-id"));
				return false;
			});

			$participantsPhasePanel.on( "click", "a.community-delete-link", function() {
				deleteCommunity($(this).attr("data-community-id"));
				return false;
			});
			
			var $input = $participantsPhasePanel.find("input#sharer-add-input"); 
			$input.typeahead({
			    onSelect: function(item) {
			        addSharer(item.value);			
			    },
			    ajax: {
			        url: "/contacts/search.json",
			        timeout: 500,
			        displayField: "fullName",
			        triggerLength: 1,
			        method: "post",
			        loadingClass: "loading-circle",
			        preDispatch: function (query) {
			            return {
			                query : query,
			                include_context_sharer : true
			            }
			        },
			        preProcess: function (response) {
			            if (response.result == "error") {
			                console.log("ajax error")
			                return false;
			            }
			            return response;
			        }
			    }
			});
			
			var $communityInput = $participantsPhasePanel.find("#community-add-input");
			$communityInput.typeahead({
			    onSelect: function(item) {
			        addCommunity(item.value);			
			    },
			    ajax: {
			        url: "/communities/search.json",
			        timeout: 500,
			        displayField: "name",
			        triggerLength: 1,
			        method: "post",
			        loadingClass: "loading-circle",
			        preDispatch: function (query) {
			            return {
			                query : query
			            }
			        },
			        preProcess: function (response) {
			            if (response.result == "error") {
			                console.log("ajax error")
			                return false;
			            }
			            return response;
			        }
			    }
			});
			
			$participantsPhasePanel.find("a#participants-phase-next-button").click(function() {
				$.radomJsonPost("/voting/control/next_phase.json", {
					voting_id : voting.id
				}, function(response) {
					voting = response;
					showParticipantsPhase();
				});
				return false;
			});
		} else {
			showAnswersPhase();
		}	
		animateReplaceWith($("#participants-phase-panel"), $participantsPhasePanel);
	}
	
	function addAnswerMarkup(answer) {
		$("ul#answers-list").append("<li>" + answer.text + "<a href='#' class='answer-delete-link pull-right' data-answer-id=" + answer.id + ">Удалить</a></li>");
	}
	
	function addAnswerSharer(sharerId) {
		$.radomJsonPost("/voting/control/add_answer.json", {
			voting_id : voting.id,
			sharer_id : sharerId
		}, function(response) {
			addAnswerMarkup(response);
			$("input#answer-sharer-add-input").val("");
		});
		$("input#answer-sharer-add-input").val("");
	}
	
	function deleteAnswer(answerId) {
		$.radomJsonPost("/voting/control/delete_answer.json", {
			answer_id : answerId
		}, function(response) {
			$("a[data-answer-id=" + answerId + "]").parent().remove();
		});		
	}
	
	function showAnswersPhase() {
		var model = {
			voting : voting,
			activePhase : voting.phase == "ANSWERS"
		};
		$answersPhasePanel = $(Mustache.render(answersPhaseTemplate, model));
		
		if (voting.phase == "ANSWERS") {
			$answersPhasePanel.on( "click", "a.answer-delete-link", function() {
				deleteAnswer($(this).attr("data-answer-id"));
				return false;
			});
			$answersPhasePanel.find("input#answer-sharer-add-input").typeahead({
			    onSelect: function(item) {
			        addAnswerSharer(item.value);			
			    },
			    ajax: {
			        url: "/voting/control/search_participants.json",
			        timeout: 500,
			        displayField: "fullName",
			        triggerLength: 1,
			        method: "post",
			        loadingClass: "loading-circle",
			        preDispatch: function (query) {
			            return {
			                query : query,
			                voting_id : voting.id
			            }
			        },
			        preProcess: function (response) {
			            if (response.result == "error") {
			                console.log("ajax error")
			                return false;
			            }
			            return response;
			        }
			    }
			});
			
			$answersPhasePanel.find("a#answer-text-add-link").click(function() {
				$.radomJsonPost("/voting/control/add_answer.json", {
					voting_id : voting.id,
					text : $("input#answer-text-add-input").val()
				}, function(response) {
					addAnswerMarkup(response);
					$("input#answer-text-add-input").val("");
				});
				return false;
			});
			
			$answersPhasePanel.find("a#answers-phase-next-button").click(function() {
				$.radomJsonPost("/voting/control/next_phase.json", {
					voting_id : voting.id
				}, function(response) {
					voting = response;
					showAnswersPhase();
				});
				return false;
			});
		} else {
			showVotesPhase();
		}	
		animateReplaceWith($("#answers-phase-panel"), $answersPhasePanel);
	}

	function showVotesPhase() {
		var model = {
				voting : voting,
				activePhase : voting.phase == "VOTES"
		};
		$votesPhasePanel = $(Mustache.render(votesPhaseTemplate, model));
		animateReplaceWith($("#votes-phase-panel"), $votesPhasePanel);
	}
	
	$(document).ready(function(){
		getVoting(showCreatePhase);
		radomStompClient.subscribeToUserQueue("new_self_promotion", function(messageBody) {
			addAnswerMarkup(messageBody);
		});
	});

</script>

<h1>Создание голосования</h1>

<hr/>

<div id="create-phase-panel"></div>
<div id="system-phase-panel"></div>
<div id="settings-phase-panel"></div>
<div id="participants-phase-panel"></div>
<div id="answers-phase-panel"></div>
<div id="votes-phase-panel"></div>