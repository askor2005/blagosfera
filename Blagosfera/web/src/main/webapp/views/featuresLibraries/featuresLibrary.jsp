<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<style type="text/css">

	.features-library-item {
		text-align : center;	
		text-decoration : none !important;
		padding-top : 0;
		height : 220px;
		position : relative;
		margin-bottom : 10px;
	}

	.features-library-item .controls {
		position : absolute;
		top : 120px;
		left : auto;
		right : auto;
		width : 140px;
		background-color : #ccc;
		opacity : 0.6;
		text-align : right;
		padding : 0 2px;
		height : 21px;
	}

	.features-library-item .controls.top-controls {
		top : 0;
	}

	.features-library-item .label {
		position : absolute;
		top : 3px;
		left : 18px;
		z-index : 2;
	}

	.features-library-item .controls:hover {
		opacity : 0.9;
	}

	.features-library-item .controls a {
		margin : 2px 0;
	}

	.features-library-item:hover {
	}

	.features-library-item .features-library-item-icon {
		font-size : 92px;
		display : block;
	}
	
	.features-library-item .features-library-item-label {
		margin-top : 10px;
		cursor : pointer;
	}	
	
	div.applications-list-container {
		min-height : 400px;
	}

	div.features-library-child {
		height : 210px;
	}

</style>

<script id="application-item-template" type="x-tmpl-mustache">
	<div class="col-xs-3 features-library-item" data-application-id="{{application.id}}">
		{{#showFree}}
			<div class="label label-success free">Бесплатное</div>
		{{/showFree}}
		{{#showDownloaded}}
			<div class="label label-info downloaded">Загружено</div>
		{{/showDownloaded}}
		{{#showCost}}
			<div class="label label-danger cost">{{application.cost}} Ра</div>
		{{/showCost}}
		{{#showBought}}
			<div class="label label-info bought">Куплено</div>
		{{/showBought}}
		{{#showInstalled}}
			<div class="label label-info installed">Установлено</div>
		{{/showInstalled}}

		<div class="controls top-controls"></div>

		<div class="controls">
			<div class="pull-left text-left">
				{{#showStart}}
					<a href="{{application.startLink}}" class="glyphicon glyphicon-play start"></a>
				{{/showStart}}
				<a href="{{application.infoLink}}" class="glyphicon glyphicon-exclamation-sign info"></a>
				{{#showDownload}}
					<a href="#" class="glyphicon glyphicon-save download"></a>
				{{/showDownload}}
				{{#showBuy}}
					<a href="#" class="glyphicon glyphicon-tag buy"></a>
				{{/showBuy}}
				{{#showInstall}}
					<a href="#" class="glyphicon glyphicon-ok install"></a>
				{{/showInstall}}
				{{#showUninstall}}
					<a href="#" class="glyphicon glyphicon-remove uninstall"></a>
				{{/showUninstall}}
		
				{{#showShowInMenu}}
					<a href="#" class="glyphicon glyphicon-eye-close show-in-menu"></a>
				{{/showShowInMenu}}
				{{#showHideInMenu}}
					<a href="#" class="glyphicon glyphicon-eye-open hide-in-menu"></a>
				{{/showHideInMenu}}
			</div>
	
			{{#isAdmin}}
				<div class="pull-right text-right">
					<a href="{{application.editLink}}" class="glyphicon glyphicon-pencil"></a>				
				</div>
			{{/isAdmin}}
		
		</div>
		<a href='{{application.infoLink}}'>
			<img src='{{logoUrl}}' />
		</a>
		<label class="features-library-item-label">{{application.name}}</label>

	</div>	
</script>

<script id="child-item-template" type="x-tmpl-mustache">
	<div class="col-xs-3 features-library-child text-center" data-section-id="{{section.id}}">
		<a href="{{section.link}}">
			<img src='{{imageUrl}}' />
			<label class="features-library-child-label">{{section.title}}</label>
		</a>
	</div>	
</script>

<script type="text/javascript">

	var FeaturesLibrary = {
			
			purchasedApplicationId : null,
			
			sectionId : '${currentSection.id}',
			isAdmin : '${isAdmin}',
			template : $("script#application-item-template").html(),
			templateParsed : false,
			
			getTemplate : function() {
				if (!FeaturesLibrary.templateParsed) {
					FeaturesLibrary.templateParsed = true;
					Mustache.parse(FeaturesLibrary.template);
				}
				return FeaturesLibrary.template;
			},
			
			getMarkup : function(application) {
				var model = {};
				model.application = application;
				model.showFree = !application.sharerApplication && application.free;
				model.showDownloaded = application.free && !!application.sharerApplication && !application.sharerApplication.installed;
				model.showCost = !application.sharerApplication && !application.free;
				model.showBought = !application.free && !!application.sharerApplication && !application.sharerApplication.installed;
				model.showInstalled = !!application.sharerApplication && application.sharerApplication.installed;
				
				model.showStart = !!application.sharerApplication && application.sharerApplication.installed;
				model.showDownload = !application.sharerApplication && application.free;
				model.showBuy = !application.sharerApplication && !application.free;
				model.showInstall = !!application.sharerApplication && !application.sharerApplication.installed;
				model.showUninstall = !!application.sharerApplication && application.sharerApplication.installed;
				
				model.showShowInMenu = application.hasSection && !!application.sharerApplication && application.sharerApplication.installed && !application.sharerApplication.showInMenu;
				model.showHideInMenu = application.hasSection && !!application.sharerApplication && application.sharerApplication.installed && application.sharerApplication.showInMenu;
				
				model.isAdmin = FeaturesLibrary.isAdmin;
				model.logoUrl = Images.getResizeUrl(application.logoUrl, "c140");
				
				var $markup = $(Mustache.render(FeaturesLibrary.getTemplate(), model));
				
		 		$markup.find("a.start").radomTooltip({
		 			title : "Запустить приложение",
		 			container : "body",
		 			placement : "top"
		 		});

		 		$markup.find("a.info").radomTooltip({
		 			title : "Информация о приложении",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.download").radomTooltip({
		 			title : "Загрузить приложение",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.download").click(function() {
		 			$.radomJsonPost("/apps/download.json", {
		 				application_id : application.id
		 			}, function(application) {
		 				FeaturesLibrary.refreshMarkup(application);
		 			});
		 			return false;
		 		});
		 		
		 		$markup.find("a.buy").radomTooltip({
		 			title : "Купить приложение",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.buy").click(function() {
		 			DebitDialog.show("Покупка приложения [" + application.name + "]", application.cost, "Купить", "Отмена", function(accountTypeId) {
		 				DebitDialog.hide();
		 	 			$.radomJsonPost("/apps/buy.json", {
		 	 				application_id : application.id,
		 	 				account_type_id : accountTypeId
		 	 			}, function(application) {
		 					FeaturesLibrary.refreshMarkup(application);
		 					Accounts.refresh();
		 	 			});
		 			});
		 			return false;
		 		});
		 		
		 		
		 		$markup.find("a.install").radomTooltip({
		 			title : "Установить приложение",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.install").click(function() {
		 			$.radomJsonPost("/apps/install.json", {
		 				application_id : application.id
		 			}, function(application) {
		 				FeaturesLibrary.refreshMarkup(application);
		 				FeaturesLibrary.refreshVisible(application);
		 			});
		 			return false;
		 		});
		 		
		 		$markup.find("a.uninstall").radomTooltip({
		 			title : "Удалить приложение",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.uninstall").click(function() {
		 			$.radomJsonPost("/apps/uninstall.json", {
		 				application_id : application.id
		 			}, function(application) {
		 				FeaturesLibrary.refreshMarkup(application);
		 				FeaturesLibrary.refreshVisible(application);
		 			});
		 			return false;
		 		});
		 		
		 		$markup.find("a.show-in-menu").radomTooltip({
		 			title : "Отображать в меню",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.show-in-menu").click(function() {
		 			$.radomJsonPost("/apps/show_in_menu.json", {
		 				application_id : application.id
		 			}, function(application) {
		 				FeaturesLibrary.refreshMarkup(application);
		 				FeaturesLibrary.refreshVisible(application);
		 			});
		 			return false;
		 		}); 
		 		
		 		$markup.find("a.hide-in-menu").radomTooltip({
		 			title : "Не отображать в меню",
		 			container : "body",
		 			placement : "top"
		 		});
		 		
		 		$markup.find("a.hide-in-menu").click(function() {
		 			$.radomJsonPost("/apps/hide_in_menu.json", {
		 				application_id : application.id
		 			}, function(application) {
		 				FeaturesLibrary.refreshMarkup(application);
		 				FeaturesLibrary.refreshVisible(application);
		 			});
		 			return false;
		 		}); 
		 		
				return $markup;
			},
			
			refreshVisible : function(application) {
	 			if (application.sharerApplication && application.sharerApplication.installed && application.sharerApplication.showInMenu) {
	 				$(radomEventsManager).trigger("section.show", application.section);
	 			} else {
	 				$(radomEventsManager).trigger("section.hide", application.section);
	 			}
			},
		
			appendMarkup : function(application) {
				$("div#applications-list").append(FeaturesLibrary.getMarkup(application));
			},
			
			refreshMarkup : function(application) {
				$("div[data-application-id=" + application.id + "]").replaceWith(FeaturesLibrary.getMarkup(application));
			},
			
			initScrollListener : function() {
				$("div#applications-list").empty();
				ScrollListener.init("/apps/list.json", "get", function() {
					// get params
					var params = {};
					params.section_id = FeaturesLibrary.sectionId;
					params.query = $("input#query").val();
					 
					var onlyMy = $("ul#applications-nav li.active a").attr("data-only-my");
		 			
		 			if (onlyMy == "true") {
		 				params.include_not_sharers = false;
		 				switch ($("div#my-applications-filter-group select").val()) {
		 				case "all":
		 					break;
		 				case "downloaded-only":
		 					params.include_bought = false;
		 					params.include_installed = false;
		 					break;
		 				case "bought-only":
		 					params.include_downloaded = false;
		 					params.include_installed = false;		 					
		 					break;
		 				case "downloaded-and-bought-only":
		 					params.include_installed = false;
		 					break;
		 				case "installed-only":
		 					params.include_downloaded = false;
		 					params.include_bought = false;
		 					break;		 					
		 				}
		 			} else {
		 				params.include_not_sharers = true;
		 				switch ($("div#all-applications-filter-group select").val()) {
		 				case "all":
		 					
		 					break;
		 				case "all-exclude-my":
		 					params.include_downloaded = false;
		 					params.include_bought = false;
		 					params.include_installed = false;
		 					break;
		 				}
		 			}
					
					return params;
				}, function() {
					// before callback
					$("div#applications-loader-animation").show();
					$("div#applications-not-found").hide();
				}, function(response) {
					// after callback
					$("div#applications-loader-animation").fadeOut(function() {
						$.each(response, function(index, application) {
							FeaturesLibrary.appendMarkup(application);
						});
						if ($(".features-library-item").length == 0) {
							$("div#applications-not-found").show();
						}						
					});
				}, null, null, null);
			},
			
			childTemplate : $("#child-item-template").html(),
			childTemplateParsed : false,
			getChildTemplate : function() {
				if (!FeaturesLibrary.childTemplateParsed) {
					FeaturesLibrary.childTemplateParsed = true;
					Mustache.parse(FeaturesLibrary.childTemplate);
				}
				return FeaturesLibrary.childTemplate;
			},
			appendChildMarkup : function(child) {
				var model = {};
				if (!child.link) {
					child.link = "#";
				}
				model.section = child;
				model.imageUrl = Images.getResizeUrl(child.imageUrl, "c140");
				var $markup = $(Mustache.render(FeaturesLibrary.getChildTemplate(), model));
				$("div#children-list").append($markup);
			},
			showChildren : function(children) {
				$("div#children-list").empty();
				$.each(children, function(index, child) {
					FeaturesLibrary.appendChildMarkup(child);
				});
			},
			loadChildren : function() {
				$("div#children-loader-animation").show();
				$.radomJsonGet("/sections/list.json", {
					parent_id : FeaturesLibrary.sectionId,
					query : $("input#query").val()
				}, function(sections) {
					$("div#children-loader-animation").fadeOut(function() {
						$("div#children-not-found").hide();
						FeaturesLibrary.showChildren(sections);
						if ($("div.features-library-child").length == 0) {
							$("div#children-not-found").show();
						}						
					});
				});
			}
			
	};

 	$(document).ready(function() {
 		
 		$("ul#applications-nav li a").click(function() {
 			var $this = $(this);
 			var onlyMy = $this.attr("data-only-my");
 			
 			if (onlyMy == "true") {
 				$("div#all-applications-filter-group").hide();
 				$("div#my-applications-filter-group").show();
 				$("div#all-applications-filter-group select option").removeAttr("selected");
 				$("div#my-applications-filter-group select option").removeAttr("selected");
 			} else {
 				$("div#all-applications-filter-group").show();
 				$("div#my-applications-filter-group").hide();
 				$("div#all-applications-filter-group select option").removeAttr("selected");
 				$("div#my-applications-filter-group select option").removeAttr("selected"); 				
 			}
 			
 			$("ul#applications-nav li").removeClass("active");
 			$this.parents("li").addClass("active");
 			$("input#query").val("");
 			
 			FeaturesLibrary.initScrollListener();
 			return false;
 		});
 			
 		$("div#all-applications-filter-group select").change(function() {
 			FeaturesLibrary.initScrollListener();
 		});
 		
		$("div#my-applications-filter-group select").change(function() {
 			FeaturesLibrary.initScrollListener();
 		});
 		
 		$("input#query").callbackInput(100, 3, function() {
 			FeaturesLibrary.initScrollListener();
 			FeaturesLibrary.loadChildren();
 		});
 		
 		$("body").on("click", ".features-library-item [href=#]", function() {
 			bootbox.alert("Инструмент в разработке");
 			return false;
 		});

 		$("body").on("click", ".features-library-child [href=#]", function() {
 			bootbox.alert("Инструмент в разработке");
 			return false;
 		});
 		
 		FeaturesLibrary.initScrollListener();
 		FeaturesLibrary.loadChildren();
 		
 	});
</script>

<h2>
	${currentSection.title}
</h2>

<hr />
	
	<div class="row">
		<security:authorize access="hasRole('ROLE_ADMIN')">
			<div class="col-xs-8">
				<input type="text" id="query" class="form-control" placeholder="Фильтр по названиям. Минимальная длина 3 символа." />
			</div>
			<div class="col-xs-4">
				<a href="/admin/apps/create?features_library_section_id=${currentSection.id}" class="btn btn-primary btn-block">Создать приложение</a>
			</div>
		</security:authorize>
		<security:authorize access="!hasRole('ROLE_ADMIN')">
			<div class="col-xs-12">
				<input type="text" id="query" class="form-control" placeholder="Фильтр по названиям. Минимальная длина 3 символа." />
			</div>
		</security:authorize>		
	</div>

	
<hr />

<c:if test="${not empty currentSection.children}">
	<h4>Подразделы</h4>
	<hr/>
	<div class="row" id="children-list"></div>
	<div class="row list-not-found" id="children-not-found">
		<div class="panel panel-default">
			<div class="panel-body">Подразделы не найдены</div>
		</div>
	</div>	
	<div class="row list-loader-animation" id="children-loader-animation"></div>
	<hr/>
</c:if>

<c:if test="${applicationCount > 0}">

	<ul class="nav nav-tabs" id="applications-nav">
		<li role="presentation" id="all-applications-tab" class="active"><a href="#" data-only-my="false">Все приложения</a></li>
		<li role="presentation" id="my-applications-tab"><a href="#" data-only-my="true">Мои приложения</a></li>
	</ul>
	
	<br/>
	
	<div class="form-group" id="all-applications-filter-group">
		<label>Фильтр приложений</label>
		<select id="all-applications-filter-select" class="form-control">
			<option value="all">Все приложения</option>
			<option value="all-exclude-my">Все приложения кроме моих</option>
		</select>
	</div>
	
	<div class="form-group" id="my-applications-filter-group" style="display : none;">
		<label>Фильтр приложений</label>
		<select id="my-applications-filter-select" class="form-control">
			<option value="all">Все мои приложения</option>
			<option value="downloaded-only">Только загруженные, но не установленные приложения</option>
			<option value="bought-only">Только купленные, но не установленные приложения</option>
			<option value="downloaded-and-bought-only">Загруженные или купленные, но не установленные приложения</option>
			<option value="installed-only">Только установленные приложения</option>
		</select>
	</div>
	
	<div class="applications-list-container">
		<div class="row" id="applications-list"></div>
		<div class="row list-not-found" id="applications-not-found">
			<div class="panel panel-default">
				<div class="panel-body">Приложения не найдены</div>
			</div>
		</div>
		<div class="row list-loader-animation" id="applications-loader-animation"></div>
	</div>
	<hr/>

</c:if>

<c:if test="${empty currentSection.children and applicationCount == 0}">
	<div class="row list-not-found" id="children-not-found" style="display : block !important;">
		<div class="panel panel-default">
			<div class="panel-body">В настоящее врем в данном разделе отсутствуют подраздели и приложения</div>
		</div>
	</div>
</c:if>


<div class="modal fade" id="buy-modal" role="dialog" aria-labelledby="buy-modal-label" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="buy-modal-label">Покупка приложения</h4>
      </div>
      <div class="modal-body">

			<input type="hidden" id="dialog-buy-application-id" value="" />

      		<div class="form-group">
      			<label>Выберите счёт для оплаты</label>
				<select id="dialog-buy-account-type-id" class="form-control">
					<c:forEach items="${accountTypes}" var="at">
						<option value="${at.id}">${at.name} <c:if test="${not empty accountsMap[at]}">(${radom:formatMoney(accountsMap[at].balance)} Ра)</c:if> </option>
					</c:forEach>
				</select>
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
        <button type="button" class="btn btn-primary" id="dialog-buy-button">Купить</button>
      </div>
    </div>
  </div>
</div>
