<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>

<style type="text/css">

</style>

<script id="contacts-list-item-template" type="x-tmpl-mustache">
	<div class="moderation-news-list-item" data-news-id="{{news.id}}">
		<dl class="dl-horizontal">
			<dt>Заголовок</dt>
			<dd>{{news.title}}</dd>

			<dt>Автор</dt>
			<dd><a href="{{news.author.link}}">{{news.author.fullName}}</a></dd>

			<dt>Дата</dt>
			<dd>{{news.date}}</dd>

			<dt>Текст</dt>
			<dd>{{text}}</dd>

		</dl>	
		<div class="form-group">
			<a href="{{news.link}}" target="_blank" class="btn btn-info btn-xs">Открыть полный текст</a>
			<a href="#" class="btn btn-success btn-xs set-moderated">Отметить как проверенную</a>
			<a href="#" class="btn btn-danger btn-xs delete">Удалить</a>
			<label class="pull-right">
				<input type="checkbox" class="news-mark"/> Выбрать
			</label>
		</div>
		<hr/>
	</div>
</script>

<script type="text/javascript">

	var NewsModeration = {
		
		communityId : "${community.id}",
			
		lastLoadedId : null,
			
		template : $("#contacts-list-item-template").html(),
		templateParsed : false,
		getTemplate : function() {
			if (!NewsModeration.templateParsed) {
				Mustache.parse(NewsModeration.template);
				NewsModeration.templateParsed = true;
			}
			return NewsModeration.template;
		},
		
		deleteMarkup : function(news) {
			var $div = $("div.moderation-news-list-item[data-news-id=" + news.id + "]");
			$div.slideUp(function() {
				$div.remove();
				
				if ($("div.moderation-news-list-item").length == 0) {
					ScrollListener.load();
				}
				
			});
		},
		
		setModerated : function(newsId) {
			$.radomJsonPost("/news/set_moderated.json", {
				news_id : newsId
			}, function(news) {
				NewsModeration.deleteMarkup(news);
			});
		},

		deleteNews : function(newsId) {
			$.radomJsonPost("/news/delete/" + newsId + ".json", {
				
			}, function(news) {
				$(radomEventsManager).trigger("news.delete", news);
			});					
		},
		
		getMarkup : function(news) {
			var model = {};
			model.news = news;
			var text  = $("<div>" + news.text + "</div>").text();
			if (text == "") {
				text = "Данная новость не содержит текста, только медиа контент (изображения, видео и т.д.)";
			}
			if (text.length > 200) {
				text = text.substring(0,200) + " ...";
			}
			model.text = text;
			var markup = Mustache.render(NewsModeration.getTemplate(), model);
			var $markup = $(markup);
			
			$markup.find("a.set-moderated").click(function() {
				NewsModeration.setModerated(news.id);
				return false;
			});
			
			$markup.find("a.delete").click(function() {
				bootbox.confirm("Вы уверены?", function(result) {
					if (result) {
						NewsModeration.deleteNews(news.id);
						return false;
					}
				});
				return false;
			});
			return $markup;	
		},

		appendMarkup : function(news) {
			$("div.news-list").append(NewsModeration.getMarkup(news));
		},
		
		prependMarkup : function(news) {
			$("div.news-list").prepend(NewsModeration.getMarkup(news));
		}
		
	};

	$(document).ready(function() {
		ScrollListener.init("/news/community.json", "get", function() {
			var params = {};
			if (NewsModeration.lastLoadedId) {
				params.last_loaded_id = NewsModeration.lastLoadedId 
			}
			params.community_id = NewsModeration.communityId;
			params.exclude_moderated = true;
			//params.per_page = 1;
			return params;
		}, function() {
			
		}, function(response) {
			$.each(response, function(index, news) {
				if (news.id < NewsModeration.lastLoadedId || NewsModeration.lastLoadedId == null) {
					NewsModeration.lastLoadedId = news.id;
				}
				NewsModeration.appendMarkup(news);
			});
		});	
		
		$("a#set-selected-moderated").click(function() {
			
			var $inputs = $("input.news-mark:checked");
			
			if ($inputs.length == 0) {
				bootbox.alert("Не выбрана ни одна новость");				
			} else {
				$.each($inputs, function(index, input) {
					var $input = $(input);
					var $div = $input.parents("div.moderation-news-list-item");
					var newsId = $div.attr("data-news-id");
					NewsModeration.setModerated(newsId);
				});
			}
			return false;
		});

		$("a#delete-selected").click(function() {
			
			var $inputs = $("input.news-mark:checked");
			
			if ($inputs.length == 0) {
				bootbox.alert("Не выбрана ни одна новость");
			} else {
				bootbox.confirm("Вы уверены?", function(result) {
					if (result) {
						$.each($inputs, function(index, input) {
							var $input = $(input);
							var $div = $input.parents("div.moderation-news-list-item");
							var newsId = $div.attr("data-news-id");
							NewsModeration.deleteNews(newsId);
						});					
					}
				});
			}
			return false;
		});
		
		$(radomEventsManager).bind("news.create", function(event, news) {
			if (news.scopeType == "COMMUNITY" && news.scope.id == NewsModeration.communityId) {
				NewsModeration.prependMarkup(news);
			}
		});

		$(radomEventsManager).bind("news.delete", function(event, news) {
			NewsModeration.deleteMarkup(news);
		});
		
	});
	
</script>

<t:insertAttribute name="communityHeader" />
<hr/>
<t:insertAttribute name="menu" />
<div class="form-group">
	<a href="#" class="btn btn-success btn-sm" id="set-selected-moderated">Отметить выбранные как проверенные</a>
	<a href="#" class="btn btn-danger btn-sm" id="delete-selected">Удалить выбранные</a>
</div>
<hr/>

<div class="news-list">

</div>