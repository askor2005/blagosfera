<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<style type="text/css">
	#filterCountry  div.dropdown-menu  {
	 	width: 100%;
	}
</style>

<script id="profile-filling-panel-template" type="x-tmpl-mustache">

<div id="profile-filling-panel" class="panel panel-{{state}}">
	<div class="panel-heading">
		<h3 class="panel-title text-center">
			{{#showDangerIcons}}
				<i class="fa fa-warning faa-flash animated pull-left"></i>
			{{/showDangerIcons}}
			{{#showInfoIcon}}
				<i class="fa fa-info pull-left"></i>
			{{/showInfoIcon}}
			{{#showSuccessIcon}}
				<i class="fa fa-check pull-left"></i>
			{{/showSuccessIcon}}
		    Заполнение профиля
			{{#showDangerIcons}}
		    	<i class="fa fa-warning faa-flash animated pull-right"></i>
			{{/showDangerIcons}}
		</h3>
	</div>
	<div class="panel-body">
		<label>Ваш профиль заполнен на {{profileFilling.percent}}%</label>
		<div class="progress">
  			<div class="progress-bar progress-bar-{{state}} progress-bar-striped active" role="progressbar" aria-valuenow="{{profileFilling.percent}}" aria-valuemin="0" aria-valuemax="100" style="width: {{profileFilling.percent}}%"></div>
		</div>
		{{#showTreshold}}
			<label class="text-center">Необходимо заполнить профиль минимум на {{profileFilling.treshold}}%</label>
		{{/showTreshold}}
		<hr/>
		{{^profileFilling.avatarLoaded}}
			<p class="text-left">
				Необходимо загрузить фото
			</p>
		{{/profileFilling.avatarLoaded}}
		{{^profileFilling.allRequiredFilled}}
			<p class="text-left">
				Необходимо заполнить поля:
				<ul>
					{{#profileFilling.notFilledFields}}
						{{#required}}
							<li>{{name}}</li>
						{{/required}}
					{{/profileFilling.notFilledFields}}
				</ul>
			</p>
		{{/profileFilling.allRequiredFilled}}

		{{#problemsExists}}
			<hr/>
		{{/problemsExists}}

		{{#showArchivation}}
			<p class="text-center">
				{{archivationText}}
			</p>
			<hr/>
		{{/showArchivation}}

		{{#showDeletion}}
			<p class="text-center">
				{{deletionText}}
			</p>
			<hr/>
		{{/showDeletion}}

		<div class="text-center">
			{{#gotoProfile}}
				<a href="/sharer" class="btn btn-xs btn-default">Перейти в профиль</a>
			{{/gotoProfile}}
			<a href="#" id="close" class="btn btn-xs btn-{{state}}">Закрыть</a>
		</div>

	</div>
</div>

</script>

<script id="community-filling-panel-template" type="x-tmpl-mustache">

	<div id="community-filling-panel" class="panel panel-{{state}} community-filling-panel">
		<div class="panel-heading">
			<h3 class="panel-title text-center">
				{{#showDangerIcons}}
					<i class="fa fa-warning faa-flash animated pull-left"></i>
				{{/showDangerIcons}}
				{{#showInfoIcon}}
					<i class="fa fa-info pull-left"></i>
				{{/showInfoIcon}}
				{{#showSuccessIcon}}
					<i class="fa fa-check pull-left"></i>
				{{/showSuccessIcon}}
				Заполнение данных
				{{#showDangerIcons}}
					<i class="fa fa-warning faa-flash animated pull-right"></i>
				{{/showDangerIcons}}
			</h3>
		</div>
		<div class="panel-body">
			<label>Данные заполнены на {{communityFilling.percent}}%</label>
			<div class="progress">
				<div class="progress-bar progress-bar-{{state}} progress-bar-striped active" role="progressbar" aria-valuenow="{{communityFilling.percent}}" aria-valuemin="0" aria-valuemax="100" style="width: {{communityFilling.percent}}%"></div>
			</div>
			{{#showThreshold}}
				<label class="text-center">Для идентификации надо заполнить данные минимум на {{communityFilling.threshold}}%</label>
			{{/showThreshold}}
			<hr/>
			{{^communityFilling.avatarLoaded}}
				<p class="text-left">
					Необходимо загрузить фото
				</p>
			{{/communityFilling.avatarLoaded}}
			{{^communityFilling.allRequiredFilled}}
				<p class="text-left">
					Необходимо заполнить поля:
					<ul>
						{{#communityFilling.requiredFields}}
							{{#required}}
								<li>{{name}}</li>
							{{/required}}
						{{/communityFilling.requiredFields}}
					</ul>
				</p>
			{{/communityFilling.allRequiredFilled}}
			{{#canVerified}}
				<div style="text-align: center;">
					<a type="button" class="btn btn-primary" href="{{selectRegistratorLink}}">Заявка на идентификацию</a>
				</div>
			{{/canVerified}}
		</div>
	</div>

</script>
<script type="text/javascript">

	var ProfileFillingPanel = {

			sharerLink : "${sharer.link}",

			template : $('#profile-filling-panel-template').html(),
			templateParsed : false,
			getTemplate : function() {
				if (!ProfileFillingPanel.templateParsed) {
					Mustache.parse(ProfileFillingPanel.template);
					ProfileFillingPanel.templateParsed = true;
				}
				return ProfileFillingPanel.template;
			},

			show : function(profileFilling) {
                var model = {};
                model.profileFilling = profileFilling;
                model.state = profileFilling.percent == 0 ? "danger" :profileFilling.percent < profileFilling.treshold ? "warning" : profileFilling.percent < 100 ? "info" : "success";
                model.showDangerIcons = (model.state == "danger") || (model.state == "warning");
                model.showInfoIcon = (model.state == "info");
                model.showSuccessIcon = (model.state == "success");

                model.showTreshold = profileFilling.percent < profileFilling.treshold;

                model.problemsExists = !profileFilling.allRequiredFilled || !profileFilling.avatarLoaded;

                if (profileFilling.hoursBeforeArchivation) {
                    model.showArchivation = true;
                    if (profileFilling.hoursBeforeArchivation > 0) {
                        model.archivationText = "Ваш профиль будет перенесен в архив через " + RadomUtils.getHumanReadableDatesDistanceAccusative(profileFilling.hoursBeforeArchivation, true);
                    } else {
                        if (profileFilling.archived) {
                            model.archivationText = "Ваш профиль был перенесен в архив";
                        } else {
                            model.archivationText = "Ваш профиль будет перенесен в архив менее чем через час";
                        }
                    }
                } else {
                    model.showArchivation = false;
                }

                if (profileFilling.hoursBeforeDeletion) {
                    model.showDeletion = true;
                    if (profileFilling.hoursBeforeDeletion > 0) {
                        model.deletionText = "Ваш профиль будет " + (profileFilling.hoursBeforeDeletion <= 72 ? "удален" : "заблокирован") + " через " + RadomUtils.getHumanReadableDatesDistanceAccusative(profileFilling.hoursBeforeDeletion, true);
                    } else {
                        model.deletionText = "Ваш профиль будет удален менее чем через час";
                    }
                } else {
                    model.showDeletion = false;
                }

                model.gotoProfile = window.location.pathname != ProfileFillingPanel.sharerLink;
                var $markup =  $(Mustache.render(ProfileFillingPanel.getTemplate(), model));
                $markup.find("a#close").click(function() {
                    ProfileFillingPanel.hide();
                    return false;
                });

                $markup.hide();

                $("#profile-filling-panel").replaceWith($markup);
                $markup.show();

                if (profileFilling.percent < profileFilling.treshold) {
                    $("div#profile-filling-info-panel").slideDown();
                }
			},

			hide : function() {
				$("#profile-filling-panel").slideUp();
			},

			refresh : function(profileFilling) {
				ProfileFillingPanel.show(profileFilling);
			},

			scrollTo : function(callback) {
				$.scrollTo({top:'0px', left:'0x'}, 500, callback);
			}

	};

	$(document).ready(function() {

		$(radomEventsManager).bind("profile.archived", function() {
			window.location = "/sharer";
		});

		$(radomEventsManager).bind("profile.deleted", function() {
			bootbox.alert("Ваш профиль был удален так как Вы не заполнили его за выделенное на это время", function() {
				window.location = "/security/logout";
			});
		});

	});

</script>

<c:if test="${sharer.id == profile.id}">

	<c:if test="${sharer.archived}">
		<script type="text/javascript">
			bootbox.alert('Ваш профиль был перенесен в архив так как заполнен менее чем на ${profileFilling.treshold}%. Необходимо заполнить профиль, иначе он будет ${profileFilling.hoursBeforeDeletion <= 72 ? "удален" : "заблокирован"}.');
		</script>
	</c:if>

	<c:choose>
	    <c:when test="${profileFilling.percent == 0}">
	       <c:set var="profileFillingState" value="danger" />
	    </c:when>
	    <c:when test="${profileFilling.percent > 0 and profileFilling.percent < profileFilling.treshold}">
	       <c:set var="profileFillingState" value="warning" />
	    </c:when>
	    <c:when test="${profileFilling.percent >= profileFilling.treshold and profileFilling.percent < 100}">
	       <c:set var="profileFillingState" value="info" />
	    </c:when>
	    <c:otherwise>
	        <c:set var="profileFillingState" value="success" />
	    </c:otherwise>
	</c:choose>

	<c:if test="${profileFilling.percent < profileFilling.treshold}">

		<div id="profile-filling-panel" class="panel panel-${profileFillingState}">
			<div class="panel-heading">
				<h3 class="panel-title text-center">
					<c:if test='${profileFillingState == "danger" or profileFillingState == "warning"}'>
						<i class="fa fa-warning faa-flash animated pull-left"></i>
					</c:if>
					<c:if test='${profileFillingState == "info"}'>
						<i class="fa fa-info pull-left"></i>
					</c:if>
					<c:if test='${profileFillingState == "success"}'>
						<i class="fa fa-check pull-left"></i>
					</c:if>
				    Заполнение профиля
					<c:if test='${profileFillingState == "danger" or profileFillingState == "warning"}'>
				    	<i class="fa fa-warning faa-flash animated pull-right"></i>
					</c:if>
				</h3>
			</div>
			<div class="panel-body">
				<label>Ваш профиль заполнен на ${profileFilling.percent}%</label>
				<div class="progress">
		  			<div class="progress-bar progress-bar-${profileFillingState} progress-bar-striped active" role="progressbar" aria-valuenow="${profileFilling.percent}" aria-valuemin="0" aria-valuemax="100" style="width: ${profileFilling.percent}%"></div>
				</div>
				<c:if test="${profileFilling.percent < profileFilling.treshold}">
					<label class="text-center">Необходимо заполнить профиль минимум на ${profileFilling.treshold}%</label>
				</c:if>

				<hr/>
				<c:if test="${not profileFilling.avatarLoaded}">
					<p class="text-left">
						Необходимо загрузить фото
					</p>
				</c:if>
				<c:if test="${not profileFilling.allReqiredFilled}">
					<p class="text-left">
						Необходимо заполнить поля:
						<ul>
							<c:forEach items="${profileFilling.notFilledFields}" var="f">
								<c:if test="${f.required}">
									<li>${f.name}</li>
								</c:if>
							</c:forEach>
						</ul>
					</p>
				</c:if>

				<c:if test="${(not profileFilling.avatarLoaded) or (not profileFilling.allReqiredFilled)}">
					<hr/>
				</c:if>

				<c:if test="${(not empty profileFilling.hoursBeforeDeletion)}">
					<c:if test="${(profileFilling.hoursBeforeDeletion > 0)}">
						<p class="text-center">Ваш профиль будет <c:if test="${profileFilling.hoursBeforeDeletion <= 72}">удален</c:if><c:if test="${profileFilling.hoursBeforeDeletion > 72}">заблокирован</c:if> через ${radom:getHumanReadableDatesDistanceAccusative(profileFilling.hoursBeforeDeletion)}</p>
					</c:if>
					<c:if test="${(profileFilling.hoursBeforeDeletion <= 0)}">
						<p class="text-center">Ваш профиль будет удален менее чем через час</p>
					</c:if>
					<hr/>
				</c:if>

				<div class="text-center">
					<c:if test="${requestScope['javax.servlet.forward.servlet_path'] != sharer.link}">
						<a href="/sharer" class="btn btn-xs btn-default">Перейти в профиль</a>
					</c:if>

					<a href="#" id="close" class="btn btn-xs btn-${profileFillingState}" onclick='$("#profile-filling-panel").slideUp(); return false;'>Закрыть</a>
				</div>

			</div>
		</div>

	</c:if>

	<c:if test="${profileFilling.percent >= profileFilling.treshold}">
		<div id="profile-filling-panel" style="display : none;"></div>
	</c:if>

</c:if>
<script id="helpChildrenTemplate" type="x-tmpl-mustache">
	<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Дочерние разделы справки</h3>
			</div>
			<div class="panel-body">
				<ul>
					{{#children}}
	                 <li>
						<a href="/help/{{name}}">{{title}}</a>
					</li>
					{{/children}}
				</ul>
			</div>
		</div>
</script>
<div id="helpChildren">
</div>
<style>
	.has-warning .form-control {
		border-color: #F9C53F;
	}

	.has-warning .control-label {
		color: #F9C53F;
	}

	.has-warning .form-control-feedback {
		color: #F9C53F;
	}

	.has-warning .help-block {
		color: #F9C53F;
	}

	.has-warning .form-control:focus {
		border-color: #FAC15C;
		-webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 6px #F4EA30;
		box-shadow: inset 0 1px 1px rgba(0,0,0,.075),0 0 6px #F4EA30;
	}
</style>
<c:choose>
	<c:when test="${communityId != null}">
		<script src="https://api-maps.yandex.ru/2.1/?lang=ru_RU" type="text/javascript"></script>
		<div id="communityPageMenu"></div>
		<script id="communityMenuTemplate" type="x-tmpl-mustache">
			{{#isHasRightToCommunity}}

				{{#isWithOrganization}}{{^community.verified}}{{#isCommunityDirector}}
					<div id="community-filling-panel-default" class="panel panel-info community-filling-panel">
						<div class="panel-heading">
							<h3 class="panel-title text-center">Заполнение данных</h3>
						</div>
						<div class="panel-body">
							<div class="list-loader-animation" style="display: block;"></div>
						</div>
					</div>
				{{/isCommunityDirector}}{{/community.verified}}{{/isWithOrganization}}

				<div class="panel-group" id="community-sidebar-accordion" role="tablist" aria-multiselectable="true">
					{{#menuData.communityRootSections}}
						<div class="panel panel-default" {{^visible}}style="display : none;"{{/visible}} >
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" data-parent="#community-sidebar-accordion"
										href="#community-section-collapse-{{id}}"
										aria-expanded='{{expanded}}'
										aria-controls="community-section-collapse-{{id}}">
										{{title}}
									</a>
								</h4>
							</div>
							<div id="community-section-collapse-{{id}}"
								 class="panel-collapse collapse{{#expanded}} in{{/expanded}}"
								 role="tabpanel"
								 aria-labelledby="community-section-heading-{{id}}">
								<div class="panel-body">
									<ul class="nav list-unstyled">
										{{#children}}
											<li {{^visible}}style="display : none;"{{/visible}} {{#active}}class="active"{{/active}} >
												<a href="{{link}}">{{title}}</a>
											</li>
										{{/children}}
									</ul>
								</div>
							</div>
						</div>
					{{/menuData.communityRootSections}}
				</div>
			{{/isHasRightToCommunity}}

			{{#isCreator}}
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a href="{{community.link}}/account">Баланс объединения</a>
						</h3>
					</div>
					<ul class="list-group">
						{{#menuData.communityAccounts}}
							<li class="list-group-item" data-account-type-id="{{typeId}}">
								{{typeName}}: ({{balance}} Ра)
							</li>
						{{/menuData.communityAccounts}}
						{{#menuData.consumerSociety}}
							<li class="list-group-item">
								Деньги пайщиков: ({{menuData.communityBookAccountsBalance}}) Ра)
							</li>
						{{/menuData.consumerSociety}}
					</ul>
				</div>
			{{/isCreator}}

			{{#menuData.sharerBookAccountBalance}}
				<div class="panel panel-primary">
					<div class="panel-heading">
						<h3 class="panel-title">
							<a>Счёт в ПО</a>
						</h3>
					</div>
					<ul class="list-group">
						<li class="list-group-item">
							({{menuData.sharerBookAccountBalance}} Ра)
						</li>
					</ul>
				</div>
			{{/menuData.sharerBookAccountBalance}}

			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title">Фото {{#community.root}}объединения{{/community.root}}{{^community.root}}группы{{/community.root}}</h3>
				</div>
				<div class="panel-body">
					<div id="community-avatar-panel-body">
						<img src="{{community.avatar}}" class="img-thumbnail" id="right-sidebar-community-photo" />
						{{#isCreator}}
							<div class="image-bottom-links-wrapper">
								<a href="#" id="community-photo-upload-link">
									<span class="glyphicon glyphicon-open"></span>&nbsp;&nbsp;Загрузить фото
								</a>
							</div>
						{{/isCreator}}
					</div>
				</div>
			</div>

			<div class="panel panel-warning">
				<div class="panel-heading">
					<h3 class="panel-title">
						{{#community.root}}Объединение{{/community.root}}{{^community.root}}Группа{{/community.root}} на карте
					</h3>
				</div>
				<div class="panel-body">
					<div id="sidebar-community-map" style="height:200px;"></div>
					<hr/>
					{{community.geoLocation}}
				</div>
			</div>
		</script>

		<script type="text/javascript">
			function initCommunityMenu(community) {console.log(community);
				loadCommunityMenuData(community.id, community.type, function(menuData){
					initCommunityMenuAfterLoadMenuData(community, menuData);
				});
			}

			function initCommunityMenuAfterLoadMenuData(community, menuData) {
				var geoPosition = "";
				var geoLocation = "";
				var sharerId = "${sharer.id}";
				if (community.menuData.fields.COMMUNITY_GEO_POSITION != null) {
					geoPosition = community.menuData.fields.COMMUNITY_GEO_POSITION;
					geoLocation = community.menuData.fields.COMMUNITY_GEO_LOCATION;
				} else if (community.menuData.fields.COMMUNITY_LEGAL_GEO_POSITION != null) {
					geoPosition = community.menuData.fields.COMMUNITY_LEGAL_GEO_POSITION;
					geoLocation = community.menuData.fields.COMMUNITY_LEGAL_GEO_LOCATION;
				}
				prepareSections(community, menuData.communityRootSections);

                var model = {
					community : community,
					isWithOrganization : community.type == "COMMUNITY_WITH_ORGANIZATION",
					isCommunityDirector : true, // TODO
					sharerId : sharerId,
					isCreator : sharerId == community.creatorId,
					isHasRightToCommunity : community.selfMember != null && community.selfMember.status == "MEMBER",
					menuData : menuData
				};

                model.community.geoPosition = geoPosition;
				model.community.geoLocation = geoLocation;
				var communityMenuTemplate = $("#communityMenuTemplate").html();
				Mustache.parse(communityMenuTemplate);
				var markup = Mustache.render(communityMenuTemplate, model);
				$("#communityPageMenu").append(markup);

				ymaps.ready(function () {
					var position = (!geoPosition) ? [55.76, 37.64] : geoPosition.split(",");
					var sidebarMap = new ymaps.Map('sidebar-community-map', {
						center: position,
						zoom: 12,
						controls: []
					});
					if (geoPosition) {
						placemark = new ymaps.Placemark(position, {}, {});
						sidebarMap.geoObjects.add(placemark);
					}
					sidebarMap.controls.add('zoomControl', {
						position: {
							right: 10,
							top: 10
						}
					});
					sidebarMap.events.add('click', function (e) {
						$("#modal-community-map-container").modal();
					});
				});


				$("#modal-community-map-container").on("shown.bs.modal", function() {
					if (!$("#modal-community-map").data("initialized")) {
						var position = (!geoPosition) ? [55.76, 37.64] : geoPosition.split(",");
						var modalMap = new ymaps.Map('modal-community-map', {
							center: position,
							zoom: 12,
							controls: []
						});
						if (geoPosition) {
							placemark = new ymaps.Placemark(position, {}, {});
							modalMap.geoObjects.add(placemark);
						}
						modalMap.controls.add('zoomControl', {
							position: {
								right: 10,
								top: 10
							}
						});
						$("#modal-community-map").data("initialized", true);
					}
				});

				$("a#community-photo-upload-link").click(function() {
					$.radomUploadDialog({
						inputName : "image",
						url : "/images/upload/community/" + community.id + ".json",
						extensions : ["bmp", "png", "gif", "jpeg", "jpg"],
						callback : function(response) {
							$("img#right-sidebar-community-photo").attr("src", Images.getResizeUrl(response.image, "c250"));
						},
						data : {
							min_width : 250,
							min_height : 250
						},
						title : "Загрузка фото",
						description : "Минимальный размер фотографии для загрузки 250 на 250 пикселей, максимальный объем 5 мегабайт",
						hideOnSuccess : true
					});
					return false;
				});

				var communityEventsManager = {};
				var communityId = community.id;
				var communityFillingPanelTemplate = null;
				var verifiedCommunity = community.verified == true;
				var communityLink = community.link;

				if ($("#community-filling-panel-default").length > 0) {
					communityFillingPanelTemplate = $("#community-filling-panel-template").html();
					Mustache.parse(communityFillingPanelTemplate);
					updateFillingData(communityId, verifiedCommunity, communityFillingPanelTemplate, communityLink);
					$(communityEventsManager).bind("community_saved", function () { // Данные объединения были сохранены
						updateFillingData(communityId, verifiedCommunity, communityFillingPanelTemplate, communityLink);
					});
				}

				$("div#community-sidebar-accordion li a[href=#]").click(function() {
					bootbox.alert("Раздел в разработке");
					return false;
				});

                $("div#community-sidebar-accordion li a[href=#settings-schema]").click(function() {
					editCommunitySchema();
                    //bootbox.alert("Данный инструмент пока находится в разработке.");
					return false;
				});
			}

			function prepareSections(community, sections) {
				var urlParams = document.URL.split("/");
				var sectionUrlParams = [];
				for (var i = 5; i < urlParams.length; i++) {
					sectionUrlParams.push(urlParams[i]);
				}
				var currentUrl = "/" + sectionUrlParams.join("/");
				if (currentUrl == "/") {
					currentUrl = "";
				}
				for (var i in sections) {
					var section = sections[i];
					section.link = community.link + section.link;
					var activeChild = false;
					if (section.children != null) {
						for (var j in section.children) {
							var child = section.children[j];
							if (child.link == "/") {
								child.link = "";
							}
							child.active = child.link == currentUrl;
							if (child.link == null) {
								child.link = "javascript:bootbox.alert('В разработке')";
							} else if (child.link.startsWith("#")) {
								// do nothing
							} else {
								child.link = community.link + child.link;
							}
							if (child.active) {
								activeChild = true;
							}
						}
					}
					section.expanded = activeChild;
				}
			}

			function loadCommunityMenuData(communityId, communityType, callBack) {
				$.radomJsonPost(
						"/communities/menu_data.json",
						{
							community_id : communityId,
							community_type : communityType
						},
						callBack
				);
			}

			function updateFillingData(communityId, verifiedCommunity, communityFillingPanelTemplate, communityLink) {
				$("#community-filling-panel").remove();
				$("#community-filling-panel-default").show();
				CommunityFunctions.getCommunityFilling(communityId, function(response){
					var model = {};
					response.allRequiredFilled = (response.requiredFields == null || response.requiredFields.length == 0);
					model.state = response.percent == 0 ? "danger" : response.percent < response.threshold ? "warning" : response.percent < 100 ? "info" : "success";
					model.showDangerIcons = (model.state == "danger") || (model.state == "warning");
					model.showInfoIcon = (model.state == "info");
					model.showSuccessIcon = (model.state == "success");
					model.communityFilling = response;
					model.showThreshold = parseInt(response.threshold) > parseInt(response.percent);
					model.canVerified = !verifiedCommunity && !model.showThreshold;
					model.selectRegistratorLink = communityLink + "/registrator/select";

					var jqCommunityFillingContent = $(Mustache.render(communityFillingPanelTemplate, model));
					$("#community-filling-panel-default").hide();
					$(jqCommunityFillingContent).insertBefore($("#community-filling-panel-default"));
				});
			}
		</script>

	</c:when>

	<c:otherwise>
		<!--div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Мои данные</h3>
			</div>
			<ul class="list-group">
				<li class="list-group-item"><label>Фамилия имя отчество</label> <br />
					${sharer.fullName}</li>
				<li class="list-group-item"><label>ИКП</label> <br />
					${sharer.ikp}</li>
				<li class="list-group-item"><label>Группа</label> <br />
					$ {sharer.group.name}</li>
			</ul>
		</div-->
	</c:otherwise>

</c:choose>

<c:choose>

	<c:when test="${community != null}">

		<script id="right-sidebar-community-member" type="x-tmpl-mustache">
			<div class="col-xs-4 right-sidebar-member-item" data-member-id="{{member.id}}">
				<a href="{{member.sharer.link}}">
					<img src='{{member.sharer.avatar}}' class="img-thumbnail tooltiped-avatar" data-placement="left" data-sharer-ikp="{{member.sharer.ikp}}" />
				</a>
				<br/>
				<br/>
			</div>
		</script>

		<script type="text/javascript">

			var RightSidebarCommunityMembers = {

				communityId : "${community.id}",

				template : $('#right-sidebar-community-member').html(),
				templateParsed : false,

				getMarkup : function(members) {
					if (!RightSidebarCommunityMembers.templateParsed) {
						Mustache.parse(RightSidebarCommunityMembers.template)
					}
					var markup = "";
					$.each(members, function(index, member) {
						member.sharer.avatar = Images.getResizeUrl(member.sharer.avatar, "c84")
						var model = {
							member : member
						};
						markup += Mustache.render(RightSidebarCommunityMembers.template, model);
					});
					return markup;
				},

				showPage : function(members, membersCount, pagesCount) {
					$("div.right-sidebar-members .list").empty();
					$("div.right-sidebar-members .list").append(RightSidebarCommunityMembers.getMarkup(members));
					$("#right-sidebar-members-panel h3").html("Участники: " + membersCount)
					if (pagesCount == 1) {
						$(".right-sidebar-members a.to-left").addClass("disabled");
						$(".right-sidebar-members a.to-right").addClass("disabled");
					} else {
						$(".right-sidebar-members a.to-left").removeClass("disabled");
						$(".right-sidebar-members a.to-right").removeClass("disabled");
					}
				},

				showNextPage : function() {
					var currentPage = $("div.right-sidebar-members").attr("data-page");
					$.radomJsonGet("/communities/members_next_page.json", {
						community_id : RightSidebarCommunityMembers.communityId,
						page : currentPage
					}, function(response) {
						currentPage = response.page;
						$("div.right-sidebar-members").attr("data-page", currentPage);
						RightSidebarCommunityMembers.showPage(response.members, response.membersCount, response.pagesCount);
					});
				},

				showPreviousPage : function() {
					var currentPage = $("div.right-sidebar-members").attr("data-page");
					$.radomJsonGet("/communities/members_previous_page.json", {
						community_id : RightSidebarCommunityMembers.communityId,
						page : currentPage
					}, function(response) {
						currentPage = response.page;
						$("div.right-sidebar-members").attr("data-page", currentPage);
						RightSidebarCommunityMembers.showPage(response.members, response.membersCount, response.pagesCount);
					});
				},

				showCurrentPage : function() {
					var currentPage = $("div.right-sidebar-members").attr("data-page");
					$.radomJsonGet("/communities/members_page.json", {
						community_id : RightSidebarCommunityMembers.communityId,
						page : currentPage
					}, function(response) {
						currentPage = response.page;
						$("div.right-sidebar-members").attr("data-page", currentPage);
						RightSidebarCommunityMembers.showPage(response.members, response.membersCount, response.pagesCount);
					});
				},

				showFirstPage : function() {
					$.radomJsonGet("/communities/members_page.json", {
						community_id : RightSidebarCommunityMembers.communityId,
						page : 1
					}, function(response) {
						currentPage = response.page;
						$("div.right-sidebar-members").attr("data-page", currentPage);
						RightSidebarCommunityMembers.showPage(response.members, response.membersCount, response.pagesCount);
					});
				}

			};

			$(document).ready(function() {
				var firstPage = ${rightSidebarMembersFirstPage};
				var membersCount = ${community.membersCount};
				var pagesCount = ${rightSidebarMembersPagesCount};

				RightSidebarCommunityMembers.showPage(firstPage, membersCount, pagesCount);

				$("div.right-sidebar-members a.to-left").click(function() {
					RightSidebarCommunityMembers.showPreviousPage();
					return false;
				});

				$("div.right-sidebar-members a.to-right").click(function() {
					RightSidebarCommunityMembers.showNextPage();
					return false;
				});

				radomStompClient.subscribeToTopic("community_" + RightSidebarCommunityMembers.communityId + "_community_exclude", function(data) {
					RightSidebarCommunityMembers.showFirstPage();
				});

				radomStompClient.subscribeToTopic("community_" + RightSidebarCommunityMembers.communityId + "_community_leave", function(data) {
					RightSidebarCommunityMembers.showFirstPage();
				});

				radomStompClient.subscribeToTopic("community_" + RightSidebarCommunityMembers.communityId + "_community_accept_request", function(data) {
					RightSidebarCommunityMembers.showFirstPage();
				});

				radomStompClient.subscribeToTopic("community_" + RightSidebarCommunityMembers.communityId + "_community_accept_invite", function(data) {
					RightSidebarCommunityMembers.showFirstPage();
				});

				radomStompClient.subscribeToTopic("community_" + RightSidebarCommunityMembers.communityId + "_community_join", function(data) {
					RightSidebarCommunityMembers.showFirstPage();
				});

			});

		</script>

		<div class="panel panel-warning" id="right-sidebar-members-panel">
			<div class="panel-heading">
				<h3 class="panel-title">
				    Участники: ${membersCount}
				</h3>
			</div>
			<div class="panel-body right-sidebar-members" data-page="1">
				<div class="row list" style="height : 152px;">

				</div>
				<a href="#" class='to-left glyphicon glyphicon glyphicon-chevron-left<c:if test="${rightSidebarMembersPagesCount == 1}"> disabled</c:if>'></a>
				<a href="#" class='to-right glyphicon glyphicon glyphicon-chevron-right<c:if test="${rightSidebarMembersPagesCount == 1}"> disabled</c:if>'></a>
				<hr style="margin-top : 0;" />
				<a class="btn btn-link btn-block" href="${community.link}/members">Все участники</a>
			</div>

		</div>
	</c:when>

    <c:when test="${chatPage}">
        <div class="panel panel-info" id="dialog-contacts-block" style="display:none;">
            <div class="panel-heading">
                <h3 class="panel-title"><span id="sidebar-dialog-contacts-count"></span></h3>
            </div>

            <div id="dialog-contacts-container">
                <select id="new-dialog-contacts-select"></select>
            </div>

            <ul class="list-group sidebar-dialog-contacts" id="sidebar-dialog-contacts">
            </ul>
        </div>
    </c:when>

</c:choose>

<div id="myCommunities" style="display: none;" class="panel panel-info">
	<div class="panel-heading">
		<h3 class="panel-title">Мои объединения</h3>
	</div>
	<div style="margin-top:10px;margin-bottom: 10px; margin-left: 15px;margin-right: 15px">
		<input type="text" class="form-control" id="groupName" placeholder="Начните ввод названия" data-toggle="tooltip" data-placement="top" title="Минимальная длина фильтра: 3 символа" />
	</div>
	<ul id="communitiesContainer" class="list-group sidebar-my-communities">
		<li style="display: none;" id="emptyCommunities" class="text-muted text-center">Вы не состоите в <a href="/groups/all">объединениях</a></li>
	</ul>
	<div class="col-xd-4 text-center">
	<btn  id="showMoreCommunities" style="display: none; margin-top: 10px;margin-bottom: 10px;" class="btn btn-info">Показать еще</btn>
	</div>
</div>

<div class="panel panel-info">
	<div class="panel-heading">
		<h3 class="panel-title">Контакты онлайн <span id="sidebar-online-contacts-count"></span></h3>
	</div>
	<ul class="list-group sidebar-online-contacts">
        <li id="empty" class="text-muted text-center" style="display: none;">Никого из Ваших контактов<br/>нет в сети</li>
	</ul>
</div>

<script id="contact-template" type="x-tmpl-mustache">
    <li class="list-group-item sidebar-online-contacts-item" id="sidebar-online-contacts-item-{{other.id}}">
        <a href="{{other.link}}" class="avatar-link"><img class="img-thumbnail tooltiped-avatar" data-sharer-ikp="{{other.ikp}}" src='{{other.avatar_c30}}'/></a>
        <a href="{{other.link}}" class="name-link">{{other.shortName}}</a>

        <a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{other.id}}');" class="glyphicon glyphicon-comment" title="Перейти к диалогу" data-toggle="tooltip" data-placement="top"></a>
    </li>
</script>

<script id="community-template" type="x-tmpl-mustache">
    <li class="list-group-item" >
       <div class="row">
       <div class="col-xs-3">
        <a href="{{link}}" class="avatar-link"><img class="img-thumbnail tooltiped-avatar"  src='{{avatar_c30}}'/></a>
       </div>
        <div style="float:left;width:75%">
        <a href="{{link}}" style="{{#bold}}font-weight:bold;{{/bold}}" class="name-link">{{name}}</a>
        </div>
        </div>
    </li>
</script>

<script type="application/javascript">
    var roster = [];
    var rosterLoading = false;

    var contactTemplate = $('#contact-template').html();
	var communityTemplate = $('#community-template').html();
    Mustache.parse(contactTemplate);
	Mustache.parse(communityTemplate);

    var rosterContainer = $('ul.sidebar-online-contacts');
    var emptyRosterMessage = $('<li id="empty" class="text-muted text-center" style="display: none;">Никого из Ваших контактов<br/>нет в сети</li>');

    function loadRoster() {
        rosterLoading = true;

        $.get('/contacts/online.json', {}, function (response) {
            roster = response;
            renderRoster();
        });
    }
	var communitiesPage = 0;
	var loadCommunitiesEnabled = true;
	function loadTopCommunities(first) {
		if (first === true) {
			communitiesPage = 0;
			$("#communitiesContainer").empty();
		}
		loadCommunitiesEnabled = true;
		$.post('/communities/top.json', {
			page : communitiesPage,
			name : $("#groupName").val()
		}, function (response) {
			renderCommunities(response,communitiesPage);
			if (response.length > 0) {
				++communitiesPage;
			}
			loadCommunitiesEnabled = true;
		});
	}
	function renderCommunities(communities,page) {
		if (communities.length === 0) {
			if (page == 0) {
				$("#emptyCommunities").fadeIn();
			}
			$("#showMoreCommunities").fadeOut();
		}
		else {
			$("#emptyCommunities").fadeOut();
			communities.forEach(function (item) {
				item.avatar_c30 = Images.getResizeUrl(item.avatar, "c30");
				if (page === 0) {
					item.bold = true;
				}
				var $li = $(Mustache.render(communityTemplate, item));
				$("#communitiesContainer").append($li);
			});
			$("#showMoreCommunities").fadeIn();
		}
	}

    function renderRoster() {
        rosterContainer.html('');

        if (roster.length === 0) {
            rosterContainer.append(emptyRosterMessage);
            emptyRosterMessage.fadeIn();
        } else {
            roster.forEach(function (item) {
                emptyRosterMessage.fadeOut();
				item.other.avatar_c30 = Images.getResizeUrl(item.other.avatar, "c30");
                $li = $(Mustache.render(contactTemplate, item));
                rosterContainer.append($li);
            });
        }

        $("span#sidebar-online-contacts-count").html("(" + roster.length + ")");
        rosterLoading = false;

        setTimeout(function () {
            loadRoster();
        }, 15 * 60000);
    }

    $(document).ready(function() {
        loadRoster();
		var sectionName = "${sectionName}";
		if (sectionName === "ramera") {
			$("#myCommunities").show();
			loadTopCommunities(true);
			$("#groupName").on("input",function(){
					loadTopCommunities(true);
			});
		}
		$("#showMoreCommunities").click(function(){
			if (!loadCommunitiesEnabled)
			 return;
			loadTopCommunities(false);
		});

        $(".sidebar-online-contacts a.glyphicon.glyphicon-comment").radomTooltip({container : "body"});

        radomStompClient.subscribeToUserQueue("contact_online", function(contact) {
            if (rosterLoading) return;
			contact.other.avatar_c30 = Images.getResizeUrl(contact.other.avatar, "c30");
            emptyRosterMessage.hide();

            var item = rosterContainer.find('li#sidebar-online-contacts-item-' + contact.other.id);

            if (item.length) {
            } else {
                roster.push(contact);

                $li = $(Mustache.render(contactTemplate, contact));
                $li.find("a.glyphicon-comment").radomTooltip({container : "body"});
                rosterContainer.prepend($li);
                $li.css("background-color", "#a94442");

                $li.animate({
                    backgroundColor: "#fff",
                }, 1000);
            }

            $("span#sidebar-online-contacts-count").html("(" + roster.length + ")");
        });
		radomStompClient.subscribeToUserQueue("contact_offline", function(contact) {
			if (rosterLoading) return;
			var item = rosterContainer.find('li#sidebar-online-contacts-item-' + contact.other.id);
			if (item.length) {
				var oldRoster = roster;
			    roster = [];
			    for (var i in oldRoster) {
					if (oldRoster[i].id != contact.id) {
						roster.push(oldRoster[i]);
					}
				}
				item.remove();
			} else {

			}
			$("span#sidebar-online-contacts-count").html("(" + roster.length + ")");
			if (roster.length == 0) {
				emptyRosterMessage.show();
			}
		});
    });
</script>

<c:if test="${isSelfProfile}">
	<style>
		.progress {
			position: relative;
		}

		.progress span {
			position: absolute;
			display: block;
			width: 100%;
			color: black;
		}
	</style>

	<c:if test="${registeredInvitedSharersCount > 0 and profile.verified}">
		<div class="panel panel-info">
			<div class="panel-heading">
				<h3 class="panel-title">Приглашённые мною</h3>
			</div>
			<ul class="list-group">
				<li class="text-center"><br/>Вы пригласили в Систему:<br/>
				${registeredInvitedSharersCount} ${radom:numberHumans(registeredInvitedSharersCount)}
				</li>
				<hr>
				<li class="text-center">
					<a id="invited-sharers-details" href="#"><strong>Подробнее</strong>&nbsp;<span class=""><span class="caret"></span></span></a>

					<div id="invited-sharers-details-block" class="panel panel-default" style="display: none; margin-left: 10px; margin-right: 10px;">
						<strong>Из них:</strong>
						<br/>
						Идентифицированно:<br/>${verifiedInvitedSharersCount} ${radom:numberHumans(verifiedInvitedSharersCount)}
						<div class="progress" style="margin-left: 10px;margin-right: 10px;">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${verifiedInvitedSharersCount}"
								 aria-valuemin="0" aria-valuemax="${registeredInvitedSharersCount}" style="width:${verifiedInvitedSharersCount/registeredInvitedSharersCount*100}%">
							</div>
							<span>${verifiedInvitedSharersCount} из ${registeredInvitedSharersCount}</span>
						</div>

						Стали Регистраторами 3-го Ранга:<br/>${registratorsLevel3InvitedSharersCount} ${radom:numberHumans(registratorsLevel3InvitedSharersCount)}
						<div class="progress" style="margin-left: 10px;margin-right: 10px;">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${registratorsLevel3InvitedSharersCount}"
								 aria-valuemin="0" aria-valuemax="${registeredInvitedSharersCount}" style="width:${registratorsLevel3InvitedSharersCount/registeredInvitedSharersCount*100}%">
							</div>
							<span>${registratorsLevel3InvitedSharersCount} из ${registeredInvitedSharersCount}</span>
						</div>

						Стали Регистраторами 2-го Ранга:<br/>${registratorsLevel2InvitedSharersCount} ${radom:numberHumans(registratorsLevel2InvitedSharersCount)}
						<div class="progress" style="margin-left: 10px;margin-right: 10px;">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${registratorsLevel2InvitedSharersCount}"
								 aria-valuemin="0" aria-valuemax="${registeredInvitedSharersCount}" style="width:${registratorsLevel2InvitedSharersCount/registeredInvitedSharersCount*100}%">
							</div>
							<span>${registratorsLevel2InvitedSharersCount} из ${registeredInvitedSharersCount}</span>
						</div>

						Стали Регистраторами 1-го Ранга:<br/>${registratorsLevel1InvitedSharersCount} ${radom:numberHumans(registratorsLevel1InvitedSharersCount)}
						<div class="progress" style="margin-left: 10px;margin-right: 10px;">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${registratorsLevel1InvitedSharersCount}"
								 aria-valuemin="0" aria-valuemax="${registeredInvitedSharersCount}" style="width:${registratorsLevel1InvitedSharersCount/registeredInvitedSharersCount*100}%">
							</div>
							<span>${registratorsLevel1InvitedSharersCount} из ${registeredInvitedSharersCount}</span>
						</div>

						Создали и сертифицировали Объединение:<br/>${createdAndVerifiedCommunitiesInvitedSharersCount} ${radom:numberHumans(createdAndVerifiedCommunitiesInvitedSharersCount)}
						<div class="progress" style="margin-left: 10px;margin-right: 10px;">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${createdAndVerifiedCommunitiesInvitedSharersCount}"
								 aria-valuemin="0" aria-valuemax="${registeredInvitedSharersCount}" style="width:${createdAndVerifiedCommunitiesInvitedSharersCount/registeredInvitedSharersCount*100}%">
							</div>
							<span>${createdAndVerifiedCommunitiesInvitedSharersCount} из ${registeredInvitedSharersCount}</span>
						</div>
					</div>
				</li>
				<hr>
				<li class="text-center"><a id="invited-sharers-list-button" href="/invites"><strong>Список приглашённых</strong></a><br/><br/></li>
			</ul>
		</div>

		<script type="text/javascript">
			$(function() {
				var $invitedSharerDetails = $("#invited-sharers-details");
				var $invitedSharerDetailsBlock = $("#invited-sharers-details-block");

				$invitedSharerDetails.click(function() {
					if($invitedSharerDetails.hasClass("active")) {
						$invitedSharerDetails.find("span").first().removeClass("dropup");
						$invitedSharerDetails.removeClass("active");
						$invitedSharerDetailsBlock.slideUp();
					} else {
						$invitedSharerDetails.find("span").first().addClass("dropup");
						$invitedSharerDetails.addClass("active");
						$invitedSharerDetailsBlock.slideDown();
					}
					return false;
				});
			});
		</script>

	</c:if>

	<c:if test="${not empty registratorLevelMnemo}">
		<div class="panel panel-info">
			<div class="panel-heading">
				<h3 class="panel-title">Идентифицированные мною</h3>
			</div>
			<ul class="list-group">
				<li class="text-center">
					<div class="form-group">
						<br/>Пользователи: ${verifiedByMeSharersCount}<br/>
						<a id="invited-verified-sharers-list-button" href="#">Смотреть список</a>
					</div>
				</li>
				<c:if test="${registratorLevelMnemo!='registrator.level3'}">
					<hr>
					<li class="text-center">
						<div class="form-group">
							Объединения: ${verifiedByMeCommunitiesCount}<br/>
							<a id="invited-verified-communities-list-button" href="#">Смотреть список</a>
						</div>
					</li>
					<c:if test="${registratorLevelMnemo=='registrator.level1' or registratorLevelMnemo=='registrator.level0'}">
						<hr>
						<li class="text-center">
							<div class="form-group">
								Регистраторы: ${verifiedByMeRegistratorsCount}<br/>
								<a id="invited-verified-registrators-list-button" href="#">Смотреть список</a>
							</div>
						</li>
					</c:if>
				</c:if>
			</ul>
		</div>

		<script id="invited-verified-sharers-table-row-template" type="x-tmpl-mustache">
			<tr class="{{trClass}}">
				<td>
					<div class="row">
						<div class="col-xs-3" style="min-height: 50px; padding-right: 4px;">
							<img class="header-avatar img-thumbnail" src="{{entry.avatarUrlSrc}}">
						</div>
						<div class="col-xs-9" style="min-height: 50px; padding-left: 4px;">
							<div>
								{{entry.email}}
								<br/>
								{{entry.name}}
							</div>
						</div>
					</div>
				</td>
				<td>
					{{entry.registrationDate}}
					<br/>
					<span style="font-size: 10px;"><b>Приглашён:</b></span>
					<br>
					{{#entry.inviterName}}
						{{entry.inviterName}}
					{{/entry.inviterName}}
					{{^entry.inviterName}}
						Регистрация без приглашения
					{{/entry.inviterName}}
				</td>
				<td>
					{{entry.verificationDate}}
					<br/>
					{{entry.verificationType}}
				</td>
				<td>
					В рамках юр. лица: {{entry.memberWithOrganizationCount}}
					<br/>
					Вне рамок юр. лица: {{entry.memberWithoutOrganizationCount}}
					<br/>
					<a name="communities-as-member-list" href="#">Смотреть список</a>
				</td>
				<td>
					В рамках юр. лица: {{entry.creatorWithOrganizationCount}}
					<br/>
					Вне рамок юр. лица: {{entry.creatorWithoutOrganizationCount}}
					<br/>
					<a name="communities-as-creator-list" href="#">Смотреть список</a>
				</td>
			</tr>
		</script>

		<script type="text/javascript">
			var rowTemplate = $("#invited-verified-sharers-table-row-template").html();

			function getRowMarkup(entry) {
				var model = {};
				model.entry = entry;
				model.entry.avatarUrlSrc = Images.getResizeUrl(entry.avatarUrlSrc, "c48");

				var markup = Mustache.render(rowTemplate, model);
				var $markup = $(markup);

				$markup.find("[name=communities-as-member-list]").click(function() {
					bootbox.alert("В разработке");
					return false;
				});

				$markup.find("[name=communities-as-creator-list]").click(function() {
					bootbox.alert("В разработке");
					return false;
				});

				return $markup;
			}

			$(function() {
				var $invitedVerifiedSharersListDialog = $("#invited-verified-sharers-list-dialog");

				var $invitedVerifiedSharersListButton = $("#invited-verified-sharers-list-button");
				$invitedVerifiedSharersListButton.click(function() {
					$.radomJsonPost("/verified_sharers_list.json", {
					}, function(entries) {
						if(entries && entries.length==0) {
							bootbox.alert("Вы не идентифицировали ни одного пользователя");
						} else {
							$invitedVerifiedSharersListDialog.find(".modal-dialog").css("width", "1000px");
							var $tbody = $invitedVerifiedSharersListDialog.find(".modal-body table tbody");
							$tbody.empty();
							$.each(entries, function (index, entry) {
								$tbody.append(getRowMarkup(entry));
							});
							$invitedVerifiedSharersListDialog.modal("show");
						}
					});
					return false;
				});

				var $invitedVerifiedCommunitiesListDialog = $("#invited-verified-communities-list-dialog");
				var $invitedVerifiedSharersListButton = $("#invited-verified-communities-list-button");
				$invitedVerifiedSharersListButton.click(function() {
					$.radomJsonPost("/verified_communities_list.json", {
					}, function(entries) {
						if(entries && entries.length==0) {
							bootbox.alert("Вы не сертифицировали ни одного объединения");
						} else {
							bootbox.alert("В разработке");
						}
					}, function() {
						bootbox.alert("В разработке");
					});
					return false;
				});

				var $invitedVerifiedRegistratorsListButton = $("#invited-verified-registrators-list-button");
				$invitedVerifiedRegistratorsListButton.click(function() {
					$.radomJsonPost("/registrator/verified_registrators_list.json", {
					}, function(entries) {
						if(entries && entries.length==0) {
							bootbox.alert("Вы не идентифицировали ни одного регистратора");
						} else {
							bootbox.alert("В разработке");
						}
					}, function() {
						bootbox.alert("В разработке");
					});
					return false;
				});
			});
		</script>

	</c:if>

	<c:if test="${sharer.verified and (empty registratorLevelMnemo or registratorLevelMnemo=='registrator.level3')}">
		<div class="panel panel-info">
			<div class="panel-heading">
				<h3 class="panel-title">Получение статуса Регистратора</h3>
			</div>
			<ul class="list-group">
				<br>
				<c:if test="${empty registratorLevelMnemo}">
						<li class="text-center">
							<div class="form-group">
								Идентифицированные пользователи
								<div class="progress" style="margin-left: 10px;margin-right: 10px;">
									<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${verifiedInvitedSharersCount}"
										 aria-valuemin="0" aria-valuemax="${needVerifiedSharersToRegistratorLevel3}" style="width:${verifiedInvitedSharersCount/needVerifiedSharersToRegistratorLevel3*100}%">
									</div>
									<span>${verifiedInvitedSharersCount} из ${needVerifiedSharersToRegistratorLevel3}</span>
								</div>
								<a id="nedd-verified-sharers-to-registrator-level3-details" href="#" onclick="return false;">Подробнее</a>
							</div>
						</li>
						<hr>
				</c:if>
				<li class="text-center">
					<div class="form-group">
						Сертифицированные объединения
						<div class="progress" style="margin-left: 10px;margin-right: 10px;">
							<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${createdAndVerifiedCommunitiesInvitedSharersCount}"
								 aria-valuemin="0" aria-valuemax="${needVerifiedCommunitiesToRegistratorLevel2}" style="width:${createdAndVerifiedCommunitiesInvitedSharersCount/needVerifiedCommunitiesToRegistratorLevel2*100}%">
							</div>
							<span>${createdAndVerifiedCommunitiesInvitedSharersCount} из ${needVerifiedCommunitiesToRegistratorLevel2}</span>
						</div>
						<a id="nedd-verified-communities-to-registrator-level2-details" href="#" onclick="return false;">Подробнее</a>
					</div>
				</li>
				<hr>
				<li class="text-center">
					<div class="form-group">
						<c:if test="${empty registratorLevelMnemo}">
							<a id="become-registrator-leve3" href="#" class="btn btn-primary">Стать Регистратором 3-го Ранга</a>
						</c:if>
						<c:if test="${registratorLevelMnemo=='registrator.level3'}">
							<a id="become-registrator-leve2"  href="#" class="btn btn-primary">Стать Регистратором 2-го Ранга</a>
						</c:if>
					</div>
				</li>
			</ul>
		</div>

		<script type="text/javascript">
			$(function() {
				$("#nedd-verified-sharers-to-registrator-level3-details").radomTooltip({
					delay : {
						show: 100,
						hide: 100
					},
					position : "top",
					container : "body",
					title : "Для получения Вами статуса Регистратора 3-го ранга необходимо, чтобы ${needVerifiedSharersToRegistratorLevel3} приглашённых Вами пользователей идентифицировались. В данный момент идентифицировано ${verifiedInvitedSharersCount} приглашённых Вами пользователей."
				});
				$("#nedd-verified-communities-to-registrator-level2-details").radomTooltip({
					delay : {
						show: 100,
						hide: 100
					},
					position : "top",
					container : "body",
					title : "Для получения Вами статуса Регистратора 2-го ранга прежде всего необходимо, чтобы Вы стали Регистратором 3-го Ранга, а затем помогли сертифицировать ${needVerifiedCommunitiesToRegistratorLevel2} Объединений. В данный момент Вы помогли сертифицировать ${createdAndVerifiedCommunitiesInvitedSharersCount} Объединений."
				});

				var becomeRegistratorLeve3 = $("a#become-registrator-leve3");
				becomeRegistratorLeve3.click(function() {
					bootbox.alert("В разработке");
					return false;
				});

				var becomeRegistratorLeve2 = $("a#become-registrator-leve2");
				becomeRegistratorLeve2.click(function() {
					bootbox.alert("В разработке");
					return false;
				});
			});
		</script>
	</c:if>

</c:if>

<security:authorize access="hasRole('ROLE_DEVELOPER')">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">Время формирования страницы</h3>
		</div>
		<div class="panel-body">
			<p class="text-center">${requestProcessingDuration} мс</p>
		</div>
	</div>

	<c:if test="${not empty profile}">
		<div class="panel panel-default" style="overflow-x: scroll;">
			<div class="panel-heading">
				<h3 class="panel-title">Роли</h3>
			</div>
			<div class="panel-body">
				<ul>
					<c:forEach items="${profileRoles}" var="pr">
						<li>${pr}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</c:if>
</security:authorize>
<c:if test="${contactsSearch}">
<script type="text/javascript">
	var maxAge = 80;
	var minAge = 14;
	function fullFillAgeFilter($selector,minAge,maxAge) {
		$selector.empty();
		$selector.append($('<option></option>'));
		for (var i = minAge;i <= maxAge;++i) {
			$selector.append($('<option>'+i+'</option>'));
		}
	}
	$(document).ready(function() {
		RameraListEditorModule.init(
				$("#filterCountry"),
				{
					selectId: 'countrySearch'
				},
				function (event, data) {
					if (event == RameraListEditorEvents.VALUE_CHANGED) {
						$("#filterCity").unbind();
						$("#filterCity").val("");
						if (!data.code) {
							$("#cityBlock").hide();
							initScrollListener();
							return;
						}
						initScrollListener();
						$("#cityBlock").show();
						var code = data.code.split("_")[0];
						var addressSystem = "";
						if (code == "ru") { // Для России кладр, для остального гугл
							addressSystem = "kladr";
						} else {
							addressSystem = "google";
						}
						if (code == "ru"){ // Для России кладр, для остального гугл
							$("#filterCity").destroyGoogleAddress();
							$("#filterCity").kladr({'type' : $.kladr.type.city, change : function(){
								initScrollListener();
							}});
						} else {
							$.destroyRadomKladr($("#filterCity"));
							$("#cityBlock").googleAddress(data, $("#zip"), $("#room"), $("#zip"),"room",true);
							$("#filterCity").change(function(){
								initScrollListener();
							});
						}

					}
				});
		$("#ageFrom").change(function(){
			var ageFrom = $("#ageFrom").val();
			if (ageFrom) {
				ageFrom = parseInt(ageFrom);
			}
			var ageTo = $("#ageTo").val();
			if (ageTo) {
				ageTo = parseInt(ageTo);
			}
			if (ageFrom > ageTo) {
				ageTo = ageFrom;
			}
			fullFillAgeFilter($("#ageTo"),ageFrom,maxAge);
			$("#ageTo").val(ageTo);
			initScrollListener();
		});
		$("#ageTo").change(function(){
			initScrollListener();
		});
		$("#sexFilter").change(function(){
			initScrollListener();
		});
		fullFillAgeFilter($("#ageFrom"),minAge,maxAge);
		fullFillAgeFilter($("#ageTo"),minAge,maxAge);
	});
</script>
<div class="panel panel-info">
	<div class="panel-heading" >
		<h3 class="panel-title">Фильтр поиска</h3>
	</div>
	<div class="panel-body">
		<div class="form-group">
			<label for="filterCountry">Страна</label>
		<div rameralisteditorname="country_id" id="filterCountry">
		</div>
			</div>
		<div class="form-group" id="cityBlock" style="display:none;">
			<label for="filterCity">Город</label>
			<input data-field-type="CITY" class="form-control"  id="filterCity" style="width: 220px;">
			<input data-field-type="DISTRICT" style="display: none;">
			<input data-field-type="REGION" style="display: none;">
			<input data-field-type="STREET" style="display: none;">
			<input data-field-type="BUILDING" style="display: none;">
			<input id="zip" style="display: none;">
			<input id="room" style="display: none;">
			</input>
		</div>
		<div class="form-group" id="ageBlock">
			<label style="display: block;">Возраст</label>
			<label for="ageFrom" style="margin-top: 5px">от</label>
			<select style="margin-right: 5px;margin-left: 5px;width: 80px; display: inline" class="form-control" id="ageFrom">

			</select>
			<label for="ageTo">до</label>
			<select  style="margin-left: 5px;width: 80px; display: inline" class="form-control" id="ageTo">

			</select>
			</input>
		</div>
		<div class="form-group" id="sexBlock">
			<label style="display: block;">Пол</label>

			<select style="width: 220px;" class="form-control" id="sexFilter">
				<option value="">Любой</option>
				<option value="true">Мужской</option>
				<option value="false">Женский</option>
			</select>
			</input>
		</div>
	</div>
</div>
</c:if>
