<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="news-list-item-template" type="x-tmpl-mustache">
<div class="row news-item" id="news-item-{{news.id}}">
	<div class="col-xs-2 avatar-wrapper">
		{{#showScope}}
			<a class="sharer-item-avatar-link" href="{{news.scope.link}}">
				<img src="{{news.scope.avatar}}" class="img-thumbnail" />
			</a>
		{{/showScope}}
		{{^showScope}}
			<a class="sharer-item-avatar-link" href="{{news.author.link}}">
				<img src="{{news.author.avatar}}" class="img-thumbnail tooltiped-avatar" data-sharer-ikp="{{news.author.ikp}}" data-placement="right" />
			</a>
		{{/showScope}}
	</div>
	<div class="col-xs-8">
		<h4 style="margin-top : 0; margin-bottom: 0;">
			<a href="{{news.link}}" style="display : block;">{{news.title}}</a>
			<small style="display : block; margin-top : 10px; padding-bottom :5px; font-size : 12px;">
				{{#news.editCount}}
					<i class="glyphicon glyphicon-pencil edited-glyphicon"></i>&nbsp;
				{{/news.editCount}}
				{{news.date}}
			</small>
			<small class="news_list_item_category">Категория: </small>
			<div class="news_tags">
				<small style="display: inline-block;">Теги: </small>
			</div>
		</h4>
	</div>
	<div class="col-xs-2 avatar-wrapper">
		{{#showScope}}
			<a class="sharer-item-avatar-link" href="{{news.author.link}}">
				<img src="{{news.author.avatar}}" class="img-thumbnail tooltiped-avatar" data-sharer-ikp="{{news.author.ikp}}" data-placement="left" />
			</a>
		{{/showScope}}
	</div>

	<div class="col-xs-12" style="margin-top : 20px;">
		<div class="news-text" style="max-height : 500px; overflow : hidden; width : 100%;">
			{{{news.text}}}
		</div>
	</div>

	<div class="col-xs-12">
		<div class="form-group shadow" style="background-image : url(/i/bottom-shadow.png); background-repeat : repeat-x; background-color : transparent; height : 50px; position: relative; top: -50px; margin-bottom: -55px;"></div>

		<div class="row" style="padding-top : 20px;">

			<div class="col-xs-2">
				<div class="rating" data-type="NEWS" data-id="{{news.id}}" data-title="{{news.title}}" data-all="{{news.ratingSum}}" data-weight="{{news.ratingWeight}}"></div>
			</div>

        	<div class="form-group col-xs-7" style="font-style:italic;">
            	{{#news.discussion.commentsCount}}
					Всего {{news.discussion.commentsCount}} {{comments}}.
					<br/>
					Последний {{news.discussion.lastCommentDate}} от <a href="{{news.discussion.lastCommentAuthorLink}}">{{news.discussion.lastCommentAuthor}}</a>
            	{{/news.discussion.commentsCount}}
            	{{^news.discussion.commentsCount}}
            		Нет ответов
            	{{/news.discussion.commentsCount}}
        	</div>

			<div class="form-group col-xs-3 text-right">
				<div class="btn-group invisible-buttons-group" role="group" style="display : none;">
					<a href="#" class="btn btn-default btn-sm show-full-news-link"><i class="glyphicon glyphicon-arrow-down"></i></a>
					<a href="#" class="btn btn-default btn-sm hide-full-news-link"><i class="glyphicon glyphicon-arrow-up"></i></a>
				</div>
				<div class="btn-group visible-buttons-group" role="group">
					<a href="{{news.link}}" class="discuss-news-link btn btn-default btn-sm"><i class="glyphicon glyphicon-comment"></i></a>
					{{#allowEdit}}
						<a href="#" class="edit-news-link btn btn-default btn-sm" data-news-id="{{news.id}}"><i class="glyphicon glyphicon-pencil"></i></a>
					{{/allowEdit}}
					{{#allowDelete}}
						<a href="#" class="delete-news-link btn btn-default btn-sm" data-news-id="{{news.id}}"><i class="glyphicon glyphicon-remove"></i></a>
					{{/allowDelete}}
				</div>
			</div>

		</div>
	</div>
</div>
<hr/>
</script>

<script type="text/javascript">
	//Максимальное число картинок и видео в новости
	var maxCountOfAttachments = parseInt('${radom:systemParameter("news.max-count-of-attachments", "10")}');
	//Число картинок в одной строчке коллажа новости
	var imagesPerRowInCollage = parseInt('${radom:systemParameter("news.collage.images-per-row", "3")}');
	//Максимальная высота, при которой новость не скрывается
	var newsMaxHeight = parseInt('${radom:systemParameter("news.max-height", "800")}');
	//Url сервера картинок
	var imgRepositoryUrl = '${radom:systemParameter("img.repository", "error")}';
	//Максимальное число тегов в новости
	var tagsMaxCount = parseInt('${radom:systemParameter("news.tags.max-count", "10")}');

	if (imgRepositoryUrl == 'error') {
		imgRepositoryUrl = null;
	}

	var commentInflector = new Inflector(['ответ', 'ответа', 'ответов']);
	var newsListItemTemplate = $('#news-list-item-template').html();
	Mustache.parse(newsListItemTemplate);
	function checNewsListItemkHeight($markup) {

		var $visibleButtonsGroup = $markup.find("div.visible-buttons-group");
		var $invisibleButtonsGroup = $markup.find("div.invisible-buttons-group");

		var $showLink = $markup.find(".show-full-news-link");
		var $text = $markup.find(".news-text");
		var $shadow = $markup.find(".shadow");

		var maxHeight = $text.css("max-height");
		var height = maxHeight == "400px" ? $text[0].offsetHeight + 99 : $text[0].offsetHeight;

		if (height < $text[0].scrollHeight) {

			var $collage = $text.find(".radom_collage");

			if ($collage.length) {
				if ($collage[0].offsetHeight < newsMaxHeight) {
					$text.css("max-height", 200 + $collage[0].offsetHeight + "px");
				} else {
					$text.css("max-height", newsMaxHeight + "px");
				}
			} else {
				$text.css("max-height", "400px");
			}

			$showLink.detach();
			$visibleButtonsGroup.prepend($showLink);
			$shadow.show();
		} else {
			$showLink.detach();
			$invisibleButtonsGroup.prepend($showLink);
			$shadow.hide();
		}
	}

	var sharerId = "${sharer.id}";


	function fillNewsCategoryInfo($markup, news) {

		var $categoriesHolder = $markup.find('.news_list_item_category');

		if (!news.category) {
			$categoriesHolder.text($categoriesHolder.text() + "Без категории");
			return;
		}

		var categories = [];

		var category = news.category;

		while (category) {
			categories.unshift(category);
			category = category.parent;
		}

		for (var i = 0; i < categories.length; ++i) {
			(function () {
				var $categoryA = $('<a></a>').text(categories[i].text);

				$categoriesHolder.append($categoryA);

				var categoryId = categories[i].id;

				$categoryA.on('click', function () {
					$(document).trigger("clickNewsCategory", categoryId);
					console.log(categoryId);
				});

				if (i != categories.length - 1) {
					$categoriesHolder.append('->');
				}
			})();
		}

	}

	function createAttachmentsCollage(news) {
		var attachments = news.attachments;

		if (!attachments || !attachments.length) {
			return '';
		}

		var $collage = $('<section class="radom_collage"></section>');
		for (var i = 0; i < attachments.length; ++i) {
			var $img;
			if (attachments[i].type == "IMAGE") {
				$img = $(new Image());
				$img.attr('orig-width', attachments[i].width);
				$img.attr('orig-height', attachments[i].height);
				$img.attr("src", attachments[i].src);
				$collage.append($img);
			} else if (attachments[i].type == "VIDEO") {
				var $thumbVideoContainer = $('<div class="news_thumb_video"></div>');
				$thumbVideoContainer.css("width", 480);
				$thumbVideoContainer.css("height", (attachments[i].height / attachments[i].width) * 480 );

				$img = $(new Image());
				$img.css("cursor", "pointer");
				$img.css("display", "block");
				$img.css("margin", "0 auto");
				$img.attr("class", "video-link-substitute");
				$img.attr("src", RadomUtils.getYoutubePreview(attachments[i].src, "hqdefault"));

				var youtubeId = RadomUtils.getYoutubeId(attachments[i].src);
				var $wrapperA = $('<a data-toggle="lightbox"></a>')
						.attr('href', 'https://www.youtube.com/watch?v=' + youtubeId)
						.attr('data-gallery', 'news-' + news.id)
						.attr('data-title', news.title);


				$thumbVideoContainer.append($wrapperA);
				$wrapperA.append($img);

				var $div = $('<div class="news_thumb_play"></div>');
				$thumbVideoContainer.append($div);
				$collage.append($thumbVideoContainer);
			}
		}

		return $collage;
	}

	function getNewsListItemMarkup(news, showScope,isPermittedEdit) {

		var $attachmentsCollage = createAttachmentsCollage(news);
        news.author.avatar = Images.getResizeUrl(news.author.avatar, "c84");

        if (!showScope) showScope = false;
        var model = {
			news : news,
			allowDelete : ((news.authorType == "SHARER") && (news.author.id == sharerId)) || isPermittedEdit,
			allowEdit : ((news.authorType == "SHARER") && (news.author.id == sharerId)) || isPermittedEdit,
			showScope : showScope && (news.authorType == "SHARER"),
			comments: commentInflector.inflect(news.discussion && news.discussion.commentsCount || 0)
		};

		var markup = Mustache.render(newsListItemTemplate, model);
		var $markup = $(markup);

		fillNewsCategoryInfo($markup, news);
		fillNewsTags($markup, news);

		$markup.find('.news-text').prepend($attachmentsCollage);

		$markup.find("a.discuss-news-link").radomTooltip({
			position : "top",
			container : "body",
			title : "Обсудить тему"
		});

		$markup.find("a.edit-news-link").click(function() {

			if (newsEditor) {
				newsEditor.destroy();
			}
			var $newsEditorContainer = $('<div style="display: none"></div>');
			$newsEditorContainer.insertBefore($($markup[0]));

			newsEditor = new NewsEditor(
                $newsEditorContainer,
                news,
                {
                    attachmentsEditor: {
                        maxCountOfAttachments: maxCountOfAttachments
                    },
                    categoryEditor: true,
                    textEditor: {
                        options: {
                            approvedElements: ['p', 'span', 'strong', 'em', 'li', 'ul', 'ol', 'i', 'b', 'a'],
                            toolbar: 'fontsizeselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist'
                        }
                    },
                    tagEditor: {
                        options: {
                            url: '/news/tags.json',
                            maxTags: tagsMaxCount
                        }
                    },
                    submitCallback: function (editor, news) {
                        $.radomJsonPost(
                                "/news/edit.json",
                                JSON.stringify(news),
                                function (response) {
                                    if (response.result && response.result == "error") {
                                        if (response.message) {
                                            bootbox.alert(response.message);
                                        } else {
                                            bootbox.alert("Произошла ошибка!");
                                        }

                                        editor.setDisabled(false);
                                        return;
                                    }

                                    replaceNewsListItemMarkup(response);
                                    editor.destroy();
                                },
                                //errorCallback
                                function (response) {
                                    if (response.message) {
                                        bootbox.alert(response.message);
                                    } else {
                                        bootbox.alert("Произошла ошибка!");
                                    }
                                    editor.setDisabled(false);
                                }, {
                                    contentType: "application/json"
                                }
                        );
                    },
                    cancelCallback: function () {
                        $markup.css('display', '');
                    }
                }
			);

			newsEditor.$container.append('<hr />');
			$markup.css('display', 'none');
			newsEditor.$container.css('display', '');
			$.scrollTo(newsEditor.$container, 300, {offset: -55});

			return false;
		}).radomTooltip({
			position : "top",
			container : "body",
			title : "Редактировать новость"
		});

		$markup.find("a.delete-news-link").click(function(){
			var $link = $(this);
			bootbox.confirm("Подтвердите удаление новости", function(result) {
				if (result) {
					$.radomJsonPost("/news/delete/" + $link.attr("data-news-id") + ".json", {}, function() {
						deleteNewsListItemMarkup(news);
						$(radomEventsManager).trigger("news.delete", news);
					});
				}
			});
			return false;
		}).radomTooltip({
			position : "top",
			container : "body",
			title : "Удалить новость"
		});

		$.each($markup.find("iframe"), function(index, iframe) {
			var $iframe = $(iframe);
			var width = parseInt($iframe.attr("width"));
			var height = parseInt($iframe.attr("height"));
			$iframe.attr("width", 480);
			$iframe.attr("height", 270);
			$iframe.css("display", "block");
			$iframe.css("margin", "0 auto");

            var linkUrl = RadomUtils.getYoutubePreview($iframe.attr("src"));
            if (linkUrl) {
				var $thumbVideoContainer = $('<div class="news_thumb_video"></div>');
				$thumbVideoContainer.css("width", 480);
				$thumbVideoContainer.css("height", 270);

				var $img = $(new Image());
				$img.css("cursor", "pointer");
				$img.css("display", "block");
				$img.css("margin", "0 auto");
				$img.css("position", "absolute");
				$img.attr("data-iframe-src", $iframe.attr("src"));
				$img.attr("class", "video-link-substitute");
				$img.attr("src", linkUrl);
				$thumbVideoContainer.append($img);

				var $div = $('<div class="news_thumb_play"></div>');
				$thumbVideoContainer.append($div);

				$iframe.replaceWith($thumbVideoContainer);
            }
		});


		var $visibleButtonsGroup = $markup.find("div.visible-buttons-group");
		var $invisibleButtonsGroup = $markup.find("div.invisible-buttons-group");

		var $showLink = $markup.find(".show-full-news-link");
		var $hideLink = $markup.find(".hide-full-news-link");
		var $text = $markup.find(".news-text");

		$text.html(RadomUtils.replaceLinks($text.html()));


		var $shadow = $markup.find(".shadow");

		$showLink.click(function() {
			$text.css("height", $text.height() + "px");
			$text.css("max-height", "none");
			$text.animateAuto("height", 300);

			$showLink.detach();
			$invisibleButtonsGroup.prepend($showLink);

			$hideLink.detach();
			$visibleButtonsGroup.prepend($hideLink);

			$shadow.hide();

			return false;
		}).radomTooltip({
			position : "top",
			container : "body",
			title : "Развернуть"
		});

		$hideLink.click(function() {
			var $collage = $text.find(".radom_collage");

			if ($collage.length) {
				if ($collage[0].offsetHeight < newsMaxHeight) {
					$text.animate({height: 200 + $collage[0].offsetHeight});
				} else {
					$text.animate({height: newsMaxHeight});
				}
			} else {
				$text.animate({height : 400});
			}


			$showLink.detach();
			$visibleButtonsGroup.prepend($showLink);

			$hideLink.detach();
			$invisibleButtonsGroup.prepend($hideLink);

			$shadow.show();
			return false;
		}).radomTooltip({
			position : "top",
			container : "body",
			title : "Свернуть"
		});

		$shadow.hide();

		$markup.find("img").load(function() {
			//$markup.find('.radom_collage').radomCollage(3);
			checNewsListItemkHeight($markup);
		});

		$markup.attr("data-show-scope", showScope)

		var $editedIcon = $markup.find("i.edited-glyphicon");
		if ($editedIcon.length > 0) {
			$editedIcon.radomTooltip({
				placement : "top",
				title : "Редактировалось " + news.editCount + " " + RadomUtils.getDeclension(news.editCount, "раз", "раза", "раз") + "<br/>" + (news.editCount > 1 ? "Последний " : "") + news.editDate,
				html : true
			});
		}

		$.each($markup.find("div.news-text").find("img"), function(index, img) {
			var $img = $(img);
			if ($img.hasClass("video-link-substitute")) {
				return;
			}

			$img.attr("data-original", $img.attr("src"));
			$img.attr("src", "/i/transparent-pixel.png");
			$img.addClass("loading-image");

			$img.bind("load", function() {
				$img.unbind("load");

				checNewsListItemkHeight($markup);
			});
		});
		return $markup;
	}

	function showNewsListItemMarkup(news, $list, prepend, showScope,isPermittedEdit) {
		if($("div#news-item-" + news.id).length == 0) {

			var $markup = getNewsListItemMarkup(news, showScope,isPermittedEdit);

			if (prepend) {
				$list.prepend($markup);
			} else {
				$list.append($markup);
			}

			//Вешаем на превью видео обработчики кликов для показа видео в lightbox'е
			bindVideoClicksForListItemMarkup($markup);

			$markup.find(".radom_collage").radomCollage(imagesPerRowInCollage);
			checNewsListItemkHeight($markup);

			//Заворачиваем картинки новости в anchor'ы, позволяющие показывать сгруппированные lightbox'ы
			createLightBoxWrappersForListItemMarkupImages($markup, news);

            $('.rating').rating();
		}
	}

	function replaceNewsListItemMarkup(news) {
		var $item = $("div#news-item-" + news.id);
		if ($item.length > 0) {
			$item.next("hr").remove();
			var showScope = ($item.attr("data-show-scope") == "true");
			var $markup = getNewsListItemMarkup(news, showScope);
			$item.replaceWith($markup);

			//Вешаем на превью видео обработчики кликов для показа видео в lightbox'е
			bindVideoClicksForListItemMarkup($markup);

			$markup.find(".radom_collage").radomCollage(imagesPerRowInCollage);
			checNewsListItemkHeight($markup);

			//Заворачиваем картинки новости в anchor'ы, позволяющие показывать сгруппированные lightbox'ы
			createLightBoxWrappersForListItemMarkupImages($markup, news);
			$('.rating').rating();

			$(window).trigger("scroll");
		}
	};

	function bindVideoClicksForListItemMarkup($markup) {
		$markup.find('.news_thumb_play').bind('click', function () {
			var $img = $($($(this).parents()[0]).find('img')[0]);
			$img.click();
		});
	};

	function createLightBoxWrappersForListItemMarkupImages($markup, news) {
		$.each($markup.find("div.news-text .radom_collage img"), function(index, img) {
			var $img = $(img);
			if ($img.hasClass("video-link-substitute")) {
				return;
			}

			var $imgWrapper = $('<a href="' + $img.attr('data-original') + '"></a>')
					.attr('data-toggle', 'lightbox')
					.attr("data-gallery",  'news-' + news.id)
					.attr('data-title', news.title);
			$imgWrapper.addClass('collage_img_wrapper');

			$img.replaceWith($imgWrapper);
			$imgWrapper.append($img);

			$img.lazyload();
			$img.removeClass("loading-image");
		});
	};


	/**
	 * Генерирует теги в выводе новости
	 * @param $markup
	 * @param news
	 */
	function fillNewsTags($markup, news) {
		var $tagHolder = $markup.find('.news_tags');

		for (var i = 0; i < news.tags.length; ++i) {
			(function () {
				var $tagDiv = $('<div>' + news.tags[i] + '</div>')
						.css('cursor', 'pointer');
				$tagHolder.append($tagDiv);

				$tagDiv.bind('click', function () {
					$(document).trigger("clickNewsTag", $tagDiv.text());
					console.log($tagDiv.text());
				});
			})();
		}
	};

	function deleteNewsListItemMarkup(news) {
		var $item = $("div#news-item-" + news.id);
		$item.fadeOut(function() {
			$item.next("hr").remove();
			$item.remove();
		});
	}

	//Слушаем событие показа/скрытия боковых меню для управления масштабом контента и отображением тумана
	$(document).on('sideMenuResizeEvent', function() {
		//обработка коллажей
		$.each($('.radom_collage'), function() {
			$.each($(this).find('.collage_img_wrapper'), function() {
				$(this).attr('orig-width', $($(this).find('img')[0]).attr('orig-width'));
				$(this).attr('orig-height', $($(this).find('img')[0]).attr('orig-height'));
			});
			$(this).radomCollage(imagesPerRowInCollage);
			$.each($(this).find('[data-toggle="lightbox"]'), function() {
				var $this = $(this);
				$this.css('width', '');
				$this.css('height', '');
			});
		});

		//обработка тумана в новостных лентах
			$.each($('.news-item'), function() {
				checNewsListItemkHeight($(this));
			});
	});

</script>
