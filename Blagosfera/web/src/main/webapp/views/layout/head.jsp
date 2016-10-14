<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>

    <t:insertAttribute name="favicon"/>

    <title>
        <c:if test="${not empty currentPageTitle}">${currentPageTitle}</c:if>
        <c:if test="${empty currentPageTitle}">Личный кабинет участника ${sharer.shortName}</c:if>
    </title>

    <meta name="description" content="${currentPageDescription}"/>
    <meta name="keywords" content="${currentPageKeywords}"/>

    <script>
        window._app_version = '${buildNumber}';
        //TODO сделать более адекватный идентификатор
        window._clientWindowId = "" + new Date().getTime();
    </script>

    <link rel="stylesheet" type="text/css" href="/css/ext-theme-crisp-all.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css?v=${buildNumber}">
    <link rel="stylesheet" type="text/css" href="/css/layout.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.kladr.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/font-awesome.css?v=${buildNumber}">
    <link rel="stylesheet" type="text/css" href="/css/font-awesome-animation.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/summernote.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-slider.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-select.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/daterangepicker.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-datetimepicker.css?v=${buildNumber}"/>

    <link rel="stylesheet" type="text/css" href="/css/bootstrap-select.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.check-list-box.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-tagsinput.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-gtreetable.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/datepicker3.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-progressbar-3.3.0.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-lightbox.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-table.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/rating.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/registration.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.Jcrop.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/intlTelInput.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/standard-table.css?v=${buildNumber}"/>

    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.min.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.radom.combobox.css?v=${buildNumber}"/>

    <link rel="stylesheet" type="text/css" href="/css/jquery.cssemoticons.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.fancybox.css?v=${buildNumber}"/>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap-plugins/ekko-leghtbox/ekko-lightbox.css?v=${buildNumber}" />

    <link rel="stylesheet" type="text/css" href="/js/dropzone/dropzone.min.css?v=${buildNumber}"/>


    <script type="text/javascript" src="/js/jquery.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery-ui.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.color.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.hashchange.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.query-object.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.kladr.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/accounts.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.animateAuto.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.scrollTo.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.uaparser.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.cookie.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.lazyload.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.okved-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.email-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.callback-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.money-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-slider.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/easing.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/sockjs-1.0.3.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/stomp.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/mustache.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-maxlength.min.js?v=${buildNumber}"></script>
    <!--<script type="text/javascript" src="/js/bootstrap-typeahead.js?v=${buildNumber}"></script>-->
    <script type="text/javascript" src="/js/bootstrap3-typeahead.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-gtreetable.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-gtreetable.ru.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-filestyle.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.bootstrap.wizard.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/bootstrap-select/bootstrap-select.min.js?v=${buildNumber}"></script>
    <script type="text/javascript"
            src="/js/bootstrap-select/i18n/bootstrap-select-defaults-ru_RU.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.radom-date-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/radom-ajax.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/rating.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/audiojs/audio.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/radom-sound.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.radom-tooltip.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.radom-collage.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/layout.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootbox.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.filtered-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.maskedinput.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.caret.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.changes-checker.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.capitalize-input.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/scroll-listener.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/notifications.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.radom-kladr.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.google.address.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.cascadingdropdown.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/inflector.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/ext-all.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/TransformGrid.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/ext-locale-ru.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/ext-theme-crisp.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/ext-date-picker-fix.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-tagsinput.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.jeditable.mini.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/tinymce/tinymce-dist/tinymce.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/tinymce-dist/jquery.tinymce.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/copypastefield/plugin.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/radomgroupfields/plugin.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/radomparticipantcustomfields/plugin.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/radomparticipantcustomtext/plugin.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/radomparticipantfilter/plugin.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/radomplaceholder/plugin.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/plugins/radomsystemfields/plugin.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.radom-tinymce.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/tinymce/askortinymce.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/bootstrap-datepicker.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-datepicker.ru.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/date.format.js?v=${buildNumber}"></script>
    <!--script type="text/javascript" src="/js/moment.min.js?v=${buildNumber}"></script-->
    <script type="text/javascript" src="/js/moment-with-locales.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/daterangepicker.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-datetimepicker.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/bootstrap-progressbar.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap-radom-progressbar.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.radom-lightbox.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.radom-sharer-certification.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.browser.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.visible.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.validate.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.checkListBox.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/bootstrap-table.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.Jcrop.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/crop.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/intlTelInput.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/fixTableHead.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/treepicker.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/accountsMoveDialog.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/radomStorage.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/declension-names.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/community/community-functions.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.radom.combobox.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.cssemoticons.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.fitvids.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.waitforimages.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.prettyembed.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jquery.fancybox.pack.js?v=${buildNumber}"></script>

    <c:if test="${radom:isSharer()}">
        <script type="text/javascript" src="/js/stomp-client.js?v=${buildNumber}"></script>
    </c:if>
    <script type="text/javascript" src="/js/askorUtils.js?v=${buildNumber}"></script>

    <%--<script type="text/javascript" src="/js/webcam/jquery.webcam.min.js?v=${buildNumber}"></script>--%>

    <script src="/js/webcam/swfobject.js?v=${buildNumber}" type="text/javascript"></script>
    <script src="/js/webcam/canvas-to-blob.js?v=${buildNumber}" type="text/javascript"></script>
    <script src="/js/webcam/jpeg_camera.js?v=${buildNumber}" type="text/javascript"></script>

    <script type="text/javascript" src="/js/sharerUploadDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/uploadCropDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/uploadFromComputerDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/uploadFromUrlDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/uploadFromWebcamDialog.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/radom-dialogs-utils.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/sharerToCommunityAccountsMoveDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/sharerToBookAccountsMoveDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bookToSharerAccountsMoveDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/communityToSharerAccountsMoveDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/communityToCommunityAccountsMoveDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/communityToBookAccountsMoveDialog.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/incomingPaymentDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/outgoingPaymentDialog.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/swfobject.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/signUploadDialog.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/jsgrid.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/uploadEditImageDialog.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/iconUploadDialog.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/noty/jquery.noty.packaged.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/noty/themes/radomTheme.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/require/require.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/require/requireConfig.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/timer.jquery.min.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/bootstrap-plugins/ekko-lightbox/ekko-lightbox.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/rameraListEditor/rameraListEditor.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.spinner.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/dropzone/dropzone.min.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/documentTemplateSettings.js?v=${buildNumber}"></script>

    <script type="text/javascript" src="/js/userMessage.js?v=${buildNumber}"></script>

    <script>
        $.extend($.noty.defaults, {
            layout: 'topRight',
            theme: 'radomTheme',
            type: 'information',
            maxVisible: 5,
            template: '<div class="noty_message"><div class="noty_close sharer_after"></div><span class="noty_text"></span></div>'
        });
        //из-за того что все библиотеки подключаются просто вставкой script, то пришлось объявлять вот так
        define("jquery", [], function () {
            return jQuery;
        });
        define("noty", [], function () {
            return noty;
        });
        define("typeahead", [], function () {});
    </script>

    <script type="text/javascript" src="/js/jquery.dropupload.js"></script>


    <style type="text/css">
        .tooltip-inner {
            max-width: 400px;
        }

        a#collector-dialog-trigger {
            position: fixed;
            left: 0;
            top: 60px;
            height: 300px;
            line-height: 30px;
            width: 30px;
            color: #fff;
            background-color: #d9534f;
            background-image: url("/i/questions.png");
            text-align: center;

            border: 1px solid #d43f3a;

            -webkit-border-bottom-right-radius: 4px;
            -webkit-border-top-right-radius: 4px;
            -moz-border-radius-bottomright: 4px;
            -moz-border-radius-topright: 4px;
            border-bottom-right-radius: 4px;
            border-top-right-radius: 4px;
            text-decoration: none !important;

            -webkit-transition: opacity 0.3s linear 0s;
            -moz-transition: opacity 0.3s linear 0s;
            -o-transition: opacity 0.3s linear 0s;
            transition: opacity 0.3s linear 0s;
            opacity: 0.9;
        }

        a#collector-dialog-trigger:hover {
            opacity: 1;
            -webkit-transition: opacity 0.3s linear 0s;
            -moz-transition: opacity 0.3s linear 0s;
            -o-transition: opacity 0.3s linear 0s;
            transition: opacity 0.3s linear 0s;
        }

        .breadcrumb {
            border: 1px solid #ddd;
        }
    </style>

<script id="okved-modal-template" type="x-tmpl-mustache">
    <div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="okved-modal-label" aria-hidden="true">
        <div class="modal-dialog" style="width: 830px;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span><span class="sr-only">Закрыть</span>
                    </button>
                    <h4 class="modal-title okved-modal-label"></h4>
                    <hr/>
                    <form-group>
                        <input class="form-control okved-query" type="text" placeholder="Начните ввод чтобы активировать фильтр" data-toggle="tooltip" data-placement="top" title="Минимальная длина фильтра: 3 символа" />
                    </form-group>
                </div>
                <div class="modal-body" style="padding: 10px 5px;">
                    <div class="tree-div"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Закрыть</button>
                    <button type="button" class="btn btn-primary save" data-dismiss="modal">Сохранить изменения</button>
                </div>
            </div>
        </div>
    </div>
</script>

    <script id="avatar-tooltip-template" type="x-tmpl-mustache">
	<div class='avatar-tooltip-wrapper' id='avatar-tooltip-wrapper-{{sharer.ikp}}'>
		<a class="avatar-wrapper" href='{{sharer.link}}'>
			<img class="avatar" src='{{resizedAvatar}}' />
			<img src="{{onlineIconSrc}}" class="online-icon" />
		</a>
		<div class="right-column">
			<a href='{{sharer.link}}' class="name">{{sharer.mediumName}}</a>

			{{#self}}
				<span class="contacts-count" style="display : block;"><i class="fa fa-chevron-right"></i> Это Вы</span>
			{{/self}}

			{{^self}}
				<a href="javascript:void(0);" onclick="ChatView.showDialogWithSharer('{{sharer.id}}');"><i class="glyphicon glyphicon-envelope"></i> Написать сообщение</a>
			{{/self}}

			{{^self}}
				<a href='#' class="do-accounts-move" onclick="accountsMove(event, this)"><i class="fa fa-money"></i> Перевести средства</a>
				<!--<a href='#' class="do-accounts-move" onclick="accountsMove(event, this)"><i class="fa fa-money"></i> Перевести средства</a>-->
			{{/self}}

			{{#showAdd}}
				{{#showAddDropdown}}
					<div class="dropdown">
  						<a class="btn btn-link btn-xs" id="add-label-{{sharer.id}}" role="button" data-toggle="dropdown" data-target="#" href="#">
   							<i class="fa fa-user"></i> Добавить в список контактов&nbsp;<span class="caret"></span>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="add-label-{{sharer.id}}">
							<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="0">Список по умолчанию</a></li>
							{{#groups}}
								<li role="presentation"><a role="menuitem" href="#" class="add-group-select-link" data-group-id="{{id}}">{{name}}</a></li>
							{{/groups}}
						</ul>
					</div>
				{{/showAddDropdown}}
				{{#showAddButton}}
					<a class="btn btn-link btn-xs add-group-select-link" role="button" href="#">Добавить в список контактов</a>
				{{/showAddButton}}
			{{/showAdd}}
		</div>
	</div>

    </script>

    <script type="text/javascript">
        var CurrentUser = {};
        var eventManager = {};

        function loadUserData(callBack) {
            $.radomJsonPost(
                    "/sharer/me.json",
                    {},
                    callBack
            );
        }

        var SHARER_ROLES = {
            <c:if test="${radom:hasRole('ROLE_SUPERADMIN')}">SUPERADMIN: true</c:if>
        };

        //fix for bootstrap tooltip. not to do through JQeury
        function accountsMove(event, target) {
            var $target = $(target);
            var sharer = {};
            event.preventDefault();
            event.stopPropagation();
            sharer.fullName = $target.attr('data-sharer-full-name');
            sharer.id = $target.attr('data-sharer-id');
            sharer.noClick = true;
            $("[aria-describedby=" + $target.closest('div.tooltip').attr('id') + "]").tooltip("hide");
            $target.accountsMoveDialog(sharer);
        }
        var AvatarTooltiper = {
            sharerId: "${sharer.id}",
            template: $('#avatar-tooltip-template').html(),
            templateParsed: false,
            getTemplate: function () {
                if (!AvatarTooltiper.templateParsed) {
                    Mustache.parse(AvatarTooltiper.template);
                    AvatarTooltiper.templateParsed = true;
                }
                return AvatarTooltiper.template;
            },

            groups: null,
            groupsLoaded: false,

            getGroups: function () {
                if (!AvatarTooltiper.groupsLoaded) {
                    $.ajax({
                        async: false,
                        type: "get",
                        dataType: "json",
                        url: "/contacts/contacts/lists.json",
                        success: function (response) {
                            AvatarTooltiper.groups = response;
                            AvatarTooltiper.groupsLoaded = true;
                        },
                        error: function () {
                            console.log("ajax error");
                        }
                    });
                }
                return AvatarTooltiper.groups;
            },

            getTitle: function (sharer) {
                var model = {};
                model.sharer = sharer;
                model.self = sharer.id == AvatarTooltiper.sharerId;
                model.resizedAvatar = Images.getResizeUrl(sharer.avatar, "c254");

                model.showAddDropdown = AvatarTooltiper.getGroups().length > 0;
                model.showAddButton = (AvatarTooltiper.getGroups().length == 0);
                model.groups = AvatarTooltiper.getGroups();

                model.onlineIconSrc = sharer.online ? "/i/icon-online.png" : "/i/icon-offline.png";

                model.showAdd = (!sharer.contact) && (sharer.id != AvatarTooltiper.sharerId);

                title = Mustache.render(AvatarTooltiper.getTemplate(), model);
                var $title = $(title);
                $title.find("a.do-accounts-move").attr('data-sharer-full-name', sharer.fullName);
                $title.find("a.do-accounts-move").attr('data-sharer-id', sharer.id);
                /*$title.find("a.do-accounts-move").click(function() {
                 $("[aria-describedby=" + $(this).closest("div.tooltip")[0].id + "]").tooltip("hide");
                 AccountsMoveDialog.show(sharer);
                 return false;
                 });*/

                $title.find("a.add-group-select-link").click(function () {
                    var groupId = $(this).attr("data-group-id");
                    $.radomJsonPost("/contacts/add.json", {
                        other_id: sharer.id,
                        group_id: groupId
                    }, function (response) {
                        $title.find("a.add-group-select-link").remove();
                        $title.find("div.dropdown").remove();
                        $title.find("span.contacts-count").show();
                    });
                    return false;
                });

                return $title;
            },

            processMouseEnter: function ($img) {
                if (!$img.attr("data-tooltip-initialized")) {
                    var title = ""
                    $.ajax({
                        type: "get",
                        async: false,
                        dataType: "json",
                        url: "/sharer/" + $img.attr("data-sharer-ikp") + ".json",
                        success: function (response) {
                            if (response.result == "error") {
                                console.log("sharer json loading error");
                            } else {
                                if (typeof response === 'string') response = JSON.parse(response);

                                title = AvatarTooltiper.getTitle(response);
                            }
                        },
                        error: function () {
                            console.log("ajax error");
                        }
                    });
                    $img.tooltip({
                        container: "body",
                        trigger: "manual",
                        html: true,
                        title: title
                    });
                    $img.attr("data-tooltip-initialized", "true");
                }

                var timeout = $img.data("timeout");
                if (timeout) {
                    clearTimeout(timeout);
                }

                timeout = setTimeout(function () {
                    $img.tooltip("show");

                    $("div#avatar-tooltip-wrapper-" + $img.attr("data-sharer-ikp")).parents(".tooltip").on("mouseenter", function () {
                        var timeout = $img.data("timeout");
                        if (timeout) {
                            clearTimeout(timeout);
                        }
                    }).on("mouseleave", function () {
                        var timeout = $img.data("timeout");
                        if (timeout) {
                            clearTimeout(timeout);
                        }
                        $img.data("timeout", setTimeout(function () {
                            $img.tooltip("hide");
                        }, RadomTooltipSettings.hideDelay > 0 ? RadomTooltipSettings.hideDelay : 100));
                    }).css("cursor", "default");


                }, RadomTooltipSettings.showDelay);

                $img.data("timeout", timeout);

            },

            processMouseLeave: function ($img) {
                var timeout = $img.data("timeout");
                if (timeout) {
                    clearTimeout(timeout);
                }
                $img.data("timeout", setTimeout(function () {
                    $img.tooltip("hide");
                }, RadomTooltipSettings.hideDelay > 0 ? RadomTooltipSettings.hideDelay : 100));
            }

        }

        // Сгенерировать раздел справки для раздела портала
        function generateHelpSection(sectionId) {
            $.ajax({
                type: "post",
                async: true,
                dataType: "json",
                url: "/admin/help/generate.json",
                data: {
                    sectionId: sectionId
                },
                success: function (response) {
                    if (response.result == "error") {
                        bootbox.alert("При генерации раздела справки произошла ошибка: " + response.message);
                    } else {
                        // Редирект на страницу редактирования справки
                        window.location.href = "/admin/help/edit/" + response.id;
                    }
                },
                error: function () {
                    bootbox.alert("ajax error on method generateHelpSection");
                }
            });
        }
        ;

        //
        function showEmptyHelpSectionMessage() {
            bootbox.alert("Раздел справки находится в разработке.");
        }
        //
        function showNotPublishedHelpSectionMessage() {
            bootbox.alert("Раздел справки не опубликован.");
        }
        //
        function showPublishedHelpSectionMessage(url) {
            var h = '${radom:getSetting("help.window.height", "50")}'; // высота в процентах
            var w = '${radom:getSetting("help.window.width", "30")}'; // ширина в процентах
            if (window.screen) {
                h = window.screen.availHeight * h / 100; // высота в пикслах
                w = window.screen.availWidth * w / 100; // ширина в пикслах
            } else {
                // если вдруг отсутствует window.screen, то выставляем относительно величины экрана 1024х768
                h = 768 * h / 100; // высота в пикслах
                w = 1024 * w / 100; // ширина в пикслах
            }
            var win = window.open(url, "blagosfera_help_window", "scrollbars=1,height="+h+",width="+w);
            win.focus();
        }

        $(document).ready(function () {
            $("#back-to-top").hide();
            $(function () {
                $(window).scroll(function () {
                    if ($(window).scrollTop() > 200) {
                        $("#back-to-top").fadeIn(1500);
                    } else {
                        $("#back-to-top").fadeOut(1500);
                    }
                });
                $("#back-to-top").click(function () {
                    $('body,html').animate({
                        scrollTop: 0
                    }, 1000);
                    return false;
                });
            });

            $("body").on("mouseenter", ".tooltiped-avatar", function () {
                var $img = $(this);
                AvatarTooltiper.processMouseEnter($img);
            }).on("mouseleave", ".tooltiped-avatar", function () {
                var $img = $(this);
                AvatarTooltiper.processMouseLeave($img);
            });

            $("body").on("mouseenter", "[data-help-section]", function () {
                var $element = $(this);
                var section = $element.attr("data-help-section");
                $element.css("position", "relative");
                var size = "20";
                // Если справки нет, и пользователь - суперадмин, то сделать запрос на её создание
                var linkHtml = "";

                var helpExists = $element.closest("[data-section-id]").attr("data-help-exists");
                helpExists = helpExists == "true" || helpExists == true ? true : false;
                var helpPublished = $element.closest("[data-section-id]").attr("data-help-published");
                helpPublished = helpPublished == "true" || helpPublished == true ? true : false;

                var userIsSuperAdmin = SHARER_ROLES.SUPERADMIN ? true : false;

                if (userIsSuperAdmin && (section == null || section == '' || !helpExists)) {
                    var sectionId = $element.closest("[data-section-id]").attr("data-section-id");
                    if (sectionId != null && sectionId != '') {
                        linkHtml = "<a class='question-link' href='javascript:void(0)' onclick='generateHelpSection(" + sectionId + ")'></a>";
                    }
                } else if (section == null || section == '' || !helpExists) {
                    linkHtml = "<a class='question-link' href='javascript:void(0)' onclick='showEmptyHelpSectionMessage()'></a>";
                } else if (!helpPublished && !userIsSuperAdmin) {
                    linkHtml = "<a class='question-link' href='javascript:void(0)' onclick='showNotPublishedHelpSectionMessage()'></a>";
                } else {
                    //linkHtml = "<a class='question-link' href='/help/" + section + "'></a>";
                    linkHtml = "<a class='question-link' href='/help/" + section + "' onclick='showPublishedHelpSectionMessage(\"" + "/help/popup/" + section  + "\"); return false;'></a>";
                }

                var $question = $("<img src='/i/q.png' />").css("width", size).css("height", size);
                var $link = $(linkHtml).css("position", "absolute").css("top", "0").css("right", "0").css("padding", "0").css("margin", "0").css("background-color", "transparent");
                $link.append($question);
                $element.append($link);
            }).on("mouseleave", "[data-help-section]", function () {
                var $element = $(this);
                $element.find("a.question-link").remove();
            });

            window.radomStompClient && radomStompClient.subscribeToUserQueue("show_popup", function (data) {
                if(data.clientWindowId && data.clientWindowId !== window._clientWindowId ) {
                    return; //хотят показать popup в другом окне
                }
                var options = {
                    data: data,
                    text: data.text,
                    force: !!data.force,
                    callback: {
                        onContentClick: function () {
                            var scriptData = data.clickScript;
                            if(scriptData) {
                                var ctx = scriptData.context || {};
                                ctx.notificationContext = {
                                    sharer: "${sharer.ikp}",
                                    clientWindowId: window._clientWindowId,
                                    notyId: this.options.id,
                                    callTime: new Date().getTime()
                                };
                                scriptData.context = ctx;
                                $.ajax({
                                    url: "/api/scripting/call",
                                    method: "POST",
                                    contentType: "application/json; charset=UTF-8",
                                    data: JSON.stringify(scriptData)
                                });
                            }
                        }
                    }
                };
                if(data.type) {
                    options.type = data.type;
                }
                if(data.timeout) {
                    options.timeout = data.timeout
                }
                if(data.closeWith) {
                    options.closeWith = data.closeWith.split(new RegExp(",\\s+"));
                }
                noty(options);
            });

            window.radomStompClient && radomStompClient.subscribeToUserQueue("execute_client_script", function (data) {
                if(data.clientWindowId && data.clientWindowId !== window._clientWindowId ) {
                    return; //хотят запустить скрипт в другом окне
                }
                var script = data.script;
                if(!script) {
                    return;
                }
                if(new RegExp("^\\s*function\\s*\\(").test(script)) {
                    script = "(" + script + ")()";
                } else {
                    script = "(function() { " + script + "})()";
                }
                var context = "";
                if(data.context) {
                    for (var key in data.context) {
                        context += "var " + key + " = " + JSON.stringify(data.context[key]) + "; ";
                    }
                }
                (function () {
                    eval(context + script)
                })();
            });

            //Включаем поддержку lightbox'ов
            $(document).delegate('*[data-toggle="lightbox"]', 'click', function(event) {
                event.preventDefault();
                $(this).ekkoLightbox();
            });

            <c:if test="${radom:isSharer()}">

            loadUserData(function(userData){
                CurrentUser = userData;
                $(eventManager).trigger("inited", CurrentUser);
            });

            $.radomJsonGet("/account/list.json", {}, function (response) {
                var accountsSelect = $('select#from-account-type-id');
                var accountsSelectTo = $('select#to-account-type-id');
                var accountsSelect2 = $('select#account_type_id');
                accountsSelect.empty();
                accountsSelectTo.empty();
                accountsSelect2.empty();

                $.each(response.accounts, function (index, account) {
                    accountsSelect.append('<option value="' + account.type.id + '">' + account.type.name + '</option>');
                    accountsSelectTo.append('<option value="' + account.type.id + '">' + account.type.name + '</option>');
                    accountsSelect2.append('<option value="' + account.type.id + '">' + account.type.name + '</option>');
                });
            });

            $.radomJsonGet("/account/paymentsystems.json", {}, function (response) {
                var select = $('select#payment_system_id');
                select.empty();

                $.each(response, function (index, paymentSystem) {
                    select.append('<option value="' + paymentSystem.id + '" data-ramera-comission="' + paymentSystem.rameraIncomingComission + '" data-bean-name="' + paymentSystem.beanName + '">' + paymentSystem.name + '</option>');
                });
            });

            </c:if>
        });

        var RadomTooltipSettings = {
            showDelay: parseInt('${radom:getSetting("interface.tooltip.delay.show", "2")}') * 1000,
            hideDelay: parseInt('${radom:getSetting("interface.tooltip.delay.hide", "0")}') * 1000,
            enable: '${radom:getSetting("interface.tooltip.enable", "true")}' == 'true'
        };

        var offsetDate = new Date();
        var clientTimeZoneOffset = offsetDate.getTimezoneOffset() / 60 * -1;
        <c:if test="${not empty radom:getTimeZone()}">
        clientTimeZoneOffset = ${radom:getTimeZone()};
        </c:if>
        var serverTimeZoneOffset = ${radom:getServerTimeZone()};
        var serverTime = ${radom:now().time};
        setInterval(function(){
            serverTime += 1000;
        }, 1000);
    </script>

    <link rel="manifest" href="/push/gcm/manifest.json?v=${buildNumber}"/>
</head>
