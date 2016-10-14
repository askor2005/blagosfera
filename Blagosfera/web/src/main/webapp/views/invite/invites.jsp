<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<script id="invites-table-row-template" type="x-tmpl-mustache">
	<tr class="{{trClass}}">
	    <td>
	        {{entry.creationDate}}
            {{#hasPreviousInvites}}
                <br><span style="font-size: 10px;"><b>Предыдущие приглашения:</b></span><br>

                <div class="previous-invites-list">
                </div>

                <a href="#" class="btn btn-default btn-block previous-invites-button">
                    <span class="dropup previous-invites-caret">
                        <span class="caret"></span>
                    </span>
                </a>
            {{/hasPreviousInvites}}
	    </td>
		<td>
			{{entry.email}}
            <br/>{{entry.invitedLastName}} {{entry.invitedFirstName}} {{entry.invitedFatherName}}
		</td>
		<td>{{entry.invitedGender}}</td>
		<td>
		    {{#entry.guarantee}}
                <i class="glyphicon glyphicon-ok"></i>
		    {{/entry.guarantee}}
		    {{^entry.guarantee}}
                <i class="glyphicon glyphicon-remove"></i>
		    {{/entry.guarantee}}
		</td>
		<td>{{entry.howLongFamiliar}}</td>
		<td>
            {{#statusIsNotAccept}}
                <a href="#" name="send-to-email" data-object-id="{{entry.id}}"><i class="glyphicon glyphicon-send"></i></a>
		    {{/statusIsNotAccept}}
		    &nbsp;{{status}}
		    {{#statusIsNotAccept}}
                <br><span style="font-size: 10px;"><b>Отправлено приглашений за всё время: {{entry.invitesCount}}</b></span>
		    {{/statusIsNotAccept}}
		</td>
		<td>
		    {{#isRegistered}}
		        {{entry.invitedSharer.registrationDate}}
                <br/><a href="/sharer/{{entry.invitedSharer.ikp}}" class="btn btn-default btn-xs">Профиль пользователя</a>
            {{/isRegistered}}
		</td>
		<td>
		    {{#isRegistered}}
                {{#isVerified}}
                    {{entry.invitedSharer.verificationDate}}
                    <br/>Регистратор:
                    <br/>{{entry.invitedSharer.verifier}}
                {{/isVerified}}
            {{/isRegistered}}
		</td>
		<td>
		    {{#isVerified}}
		        {{registrator}}
		    {{/isVerified}}
		</td>
		<td>
		    {{#isVerified}}
                Физ. лиц: {{entry.invitedSharer.streamSharers}}<br/>
                Юр. лиц: {{entry.invitedSharer.streamOrganizations}}
		    {{/isVerified}}
		</td>
	</tr>
</script>

<style>
    .container {
        width: 1500px!important;
    }

    #invites-table th {
        text-align: center;
    }

    #invites-table td {
        text-align: center;
    }

    #advanced-search-block label {
        font-size: 13px;
    }
</style>

<script type="text/javascript">
    var invitesList = {
        rowTemplate : $("#invites-table-row-template").html(),
        rowTemplateParsed : false,

        getRowTemplate : function() {
            if (!invitesList.rowTemplateParsed) {
                Mustache.parse(invitesList.rowTemplate);
                invitesList.rowTemplateParsed = true;
            }
            return invitesList.rowTemplate;
        },

        getRowMarkup : function(entry) {
            var model = {};
            model.entry = entry;
            model.isRegistered = false;
            model.isVerified = false;
            model.registrator = "Нет";
            model.hasPreviousInvites = false;

            var invitedSharer = model.entry.invitedSharer;

            if (invitedSharer && invitedSharer.registratorLevel == 0) {
                model.registrator = "Высшего Ранга";
            } else if(invitedSharer && invitedSharer.registratorLevel == 1) {
                model.registrator = "1-го Ранга";
            }else if(invitedSharer && invitedSharer.registratorLevel == 2) {
                model.registrator = "2-го Ранга";
            }else if(invitedSharer && invitedSharer.registratorLevel == 3) {
                model.registrator = "3-го Ранга";
            }

            if (model.entry.inviteStatus == 0) {
                model.trClass = "success";
                model.status = "Принято";
                if (model.entry.invitedSharer) {
                    model.isRegistered = true;
                    if(model.entry.invitedSharer.verified) {
                        model.isVerified=true;
                    }
                }
            } else if (model.entry.inviteStatus == 1) {
                model.trClass = "info";
                model.status = "В ожидании";
                model.statusIsNotAccept = true;
            } else if (model.entry.inviteStatus == 2) {
                 model.trClass = "info";
                 model.status = "Просрочено";
                 model.statusIsNotAccept = true;
             } else if (model.entry.inviteStatus == 3) {
                model.trClass = "danger";
                model.status = "Отклонено";
            }  else if (model.entry.inviteStatus == 4) {
                model.trClass = "danger";
                model.status = "Профиль перенесён в архив";
            } else if (model.entry.inviteStatus == 5) {
                model.trClass = "danger";
                model.status = "Профиль перенесён в архив";
            }

            if(model.entry.previousInvites && model.entry.previousInvites.length > 0) {
                model.hasPreviousInvites = true && model.statusIsNotAccept;
            }

            var markup = Mustache.render(invitesList.getRowTemplate(), model);
            var $markup = $(markup);

            var $previousInvitesList = $markup.find(".previous-invites-list");

            if(model.hasPreviousInvites) {
                for(var i=0; i<model.entry.previousInvites.length; i++) {
                    $previousInvitesList.append("<span>" + model.entry.previousInvites[i].creationDate + "</span>");
                    if(i<model.entry.previousInvites.length-1) {
                        $previousInvitesList.append("<br>");
                    }
                }
            }

            var $previousInvitesCaret = $markup.find(".previous-invites-caret");
            var $previousInvitesButton = $markup.find(".previous-invites-button");
            $previousInvitesButton.click(function() {
                if($previousInvitesCaret.hasClass("dropup")) {
                    $previousInvitesCaret.removeClass("dropup");
                    $previousInvitesList.slideUp();
                } else {
                    $previousInvitesCaret.addClass("dropup");
                    $previousInvitesList.slideDown();
                }
                return false;
            });

            return $markup;
        },

        showRowMarkup : function(entry, prepend) {
            var $markup = invitesList.getRowMarkup(entry);
            if (prepend) {
                $("table#invites-table").prepend(invitesList.getRowMarkup(entry));
            } else {
                $("table#invites-table").append(invitesList.getRowMarkup(entry));
            }
        },

        lastLoadedId : null,

        initScrollListener : function() {
            $("table#invites-table tbody").empty();
            invitesList.lastLoadedId = null;
            ScrollListener.init("/invites/invites.json", "get", function() {
                var params = {};
                if (invitesList.lastLoadedId) {
                    params.last_loaded_id = invitesList.lastLoadedId;
                }
                var filter = $("select[name=filter]").val();
                if (filter) {
                    params.filter = filter;
                }
                var fromDate = $("input[name=from_date]").val();
                if (fromDate) {
                    params.from_date = fromDate;
                }
                var toDate = $("input[name=to_date]").val();
                if (toDate) {
                    params.to_date = toDate;
                }
                var email = $("input[name=email]").val();
                if (email) {
                    params.email = email;
                }

                var inviteStatus = $('#invite-status input:radio:checked').val();
                if (inviteStatus) {
                    params.invite_status = inviteStatus;
                }

                var registerFromDate = $("input[name=register_from_date]").val();
                if (registerFromDate) {
                    params.register_from_date = registerFromDate;
                }
                var registerToDate = $("input[name=register_to_date]").val();
                if (registerToDate) {
                    params.register_to_date = registerToDate;
                }

                var verifiedFilter = $("select#verified-filter").val();
                if (verifiedFilter) {
                    params.verified_filter = verifiedFilter;
                }

                var verifiedFromDate = $("input[name=verified_from_date]").val();
                if (verifiedFromDate) {
                    params.verified_from_date = verifiedFromDate;
                }
                var verifiedToDate = $("input[name=verified_to_date]").val();
                if (verifiedToDate) {
                    params.verified_to_date = verifiedToDate;
                }

                var verifierName = $("input#verifier-name").val();
                if (verifierName) {
                    params.verifier_name = verifierName;
                }

                var fromSharersCount = $("input#from-sharers-count").val();
                if (fromSharersCount) {
                    params.from_sharers_count = fromSharersCount;
                }
                var toSharersCount = $("input#to-sharers-count").val();
                if (toSharersCount) {
                    params.to_sharers_count = toSharersCount;
                }

                var fromOrganizationsCount = $("input#from-organizations-count").val();
                if (fromOrganizationsCount) {
                    params.from_organizations_count = fromOrganizationsCount;
                }
                var toOrganizationsCount = $("input#to-organizations-count").val();
                if (toOrganizationsCount) {
                    params.to_organizations_count = toOrganizationsCount;
                }

                var guaranteeFilter = $("select#guarantee-filter").val();
                if (guaranteeFilter) {
                    params.guarantee_filter = guaranteeFilter;
                }
                var sexFilter = $("select#sex-filter").val();
                if (sexFilter) {
                    params.sex_filter = sexFilter;
                }

                var fromInvitesCount = $("input#from-invites-count").val();
                if (fromInvitesCount) {
                    params.from_invites_count = fromInvitesCount;
                }
                var toInvitesCount = $("input#to-invites-count").val();
                if (toInvitesCount) {
                    params.to_invites_count = toInvitesCount;
                }

                var fromFamiliarYears = $("input#from-familiar-years").val();
                if (fromFamiliarYears) {
                    params.from_familiar_years = fromFamiliarYears;
                }
                var toFamiliarYears = $("input#to-familiar-years").val();
                if (toFamiliarYears) {
                    params.to_familiar_years = toFamiliarYears;
                }

                var registratorLevel = $('#registrator-level input:radio:checked').val();
                if (registratorLevel) {
                    params.registrator_level = registratorLevel;
                }

                return params;
            }, function() {
                $("div.list-loader-animation").show();
            }, function(entries, page) {
                var $tbody = $("table#invites-table tbody");
                $.each(entries, function(index, entry) {
                    invitesList.showRowMarkup(entry);
                    if (!invitesList.lastLoadedId || invitesList.lastLoadedId > entry.id) {
                        invitesList.lastLoadedId = entry.id;
                    }
                });
                $("div.list-loader-animation").hide();
                updateEvents();
                $("table#invites-table").fixMe();
            });
        }
    };

    function updateEvents() {
        $("a[name=send-to-email]").radomTooltip({
            container : "body",
            title : "Повторно отправить приглашение на почту",
            placement : "bottom",
            trigger : "manual"
        });
        $("a[name=send-to-email]").off("mouseenter").on("mouseenter", function() {
            var $this = $(this);
            var timeout = $this.data("timeout");
            if (timeout) {
                clearTimeout(timeout);
            }
            timeout = setTimeout(function() {
                $this.tooltip("show");
            }, RadomTooltipSettings.showDelay);
            $this.data("timeout", timeout);
        }).off("mouseleave").on("mouseleave", function() {
            var $this = $(this);
            var timeout = $this.data("timeout");
            if (timeout) {
                clearTimeout(timeout);
            }
            timeout = setTimeout(function() {
                $this.tooltip("hide");
            }, RadomTooltipSettings.hideDelay);
            $this.data("timeout", timeout);
        }).off("click").on("click", function() {
            var $this = $(this);
            var timeout = $this.data("timeout");
            if (timeout) {
                clearTimeout(timeout);
            }
            $this.tooltip("hide");

            var value = $this.attr("data-object-id");
            $.ajax({
                type: "post",
                dataType: "json",
                url: "/invite/sendToEmail.json?invite_id=" + value,
                success: function (response) {
                    if (response.result == "error") {
                        bootbox.alert(response.message);
                    } else {
                        bootbox.alert("Приглашение успешно отправлено");
                        invitesList.initScrollListener();
                    }
                },
                error: function () {
                    console.log("ajax error");
                }
            });
            return false;
        });
    }

    $(document).ready(function() {
        $("table#invites-table").fixMe();

        Ext.onReady(function() {
            Ext.create('Ext.form.field.Date', {
                renderTo : 'from-date',
                xtype : 'datefield',
                name : 'from_date',
                format : 'd.m.Y',
                labelWidth: 0,
                width : 105,
                value : '',
                listeners : {
                    change: function (t,n,o) {
                    },
                    select: function (t,n,o) {
                        invitesList.initScrollListener();
                    }
                }
            });
            Ext.create('Ext.form.field.Date', {
                renderTo : 'to-date',
                xtype : 'datefield',
                name : 'to_date',
                format : 'd.m.Y',
                labelWidth: 0,
                width : 105,
                value : '',
                listeners : {
                    change: function (t,n,o) {
                    },
                    select: function (t,n,o) {
                        invitesList.initScrollListener();
                    }
                }
            });

            Ext.create('Ext.form.field.Date', {
                renderTo : 'register-from-date',
                xtype : 'datefield',
                name : 'register_from_date',
                format : 'd.m.Y',
                labelWidth: 0,
                width : 105,
                value : '',
                listeners : {
                    change: function (t,n,o) {
                    },
                    select: function (t,n,o) {
                        invitesList.initScrollListener();
                    }
                }
            });
            Ext.create('Ext.form.field.Date', {
                renderTo : 'register-to-date',
                xtype : 'datefield',
                name : 'register_to_date',
                format : 'd.m.Y',
                labelWidth: 0,
                width : 105,
                value : '',
                listeners : {
                    change: function (t,n,o) {
                    },
                    select: function (t,n,o) {
                        invitesList.initScrollListener();
                    }
                }
            });
            Ext.create('Ext.form.field.Date', {
                renderTo : 'verified-from-date',
                xtype : 'datefield',
                name : 'verified_from_date',
                format : 'd.m.Y',
                labelWidth: 0,
                width : 105,
                value : '',
                listeners : {
                    change: function (t,n,o) {
                    },
                    select: function (t,n,o) {
                        invitesList.initScrollListener();
                    }
                }
            });
            Ext.create('Ext.form.field.Date', {
                renderTo : 'verified-to-date',
                xtype : 'datefield',
                name : 'verified_to_date',
                format : 'd.m.Y',
                labelWidth: 0,
                width : 105,
                value : '',
                listeners : {
                    change: function (t,n,o) {
                    },
                    select: function (t,n,o) {
                        invitesList.initScrollListener();
                    }
                }
            });

            $("input[type=hidden][name=from_date]").remove();
            $("input[type=hidden][name=to_date]").remove();
            $("input[type=hidden][name=register_from_date]").remove();
            $("input[type=hidden][name=register_to_date]").remove();
            $("input[type=hidden][name=verified_from_date]").remove();
            $("input[type=hidden][name=verified_to_date]").remove();
        });

        invitesList.initScrollListener();
        $("select[name=filter]").change(function() {
            invitesList.initScrollListener();
        });
        $("input[name=email]").callbackInput(100, 3, function() {
            invitesList.initScrollListener();
        });
        $("a#refresh-button").click(function() {
            invitesList.initScrollListener();
            return false;
        });

        $("input[name=optradio-status]").change(function() {
            invitesList.initScrollListener();
        });

        $("select[name=verified-filter]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=verifier-name]").callbackInput(100, 3, function() {
            invitesList.initScrollListener();
        });

        $("input[name=from-sharers-count]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=to-sharers-count]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=from-organizations-count]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=to-organizations-count]").change(function() {
            invitesList.initScrollListener();
        });

        $("select[name=guarantee-filter]").change(function() {
            invitesList.initScrollListener();
        });

        $("select[name=sex-filter]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=from-invites-count]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=to-invites-count]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=from-familiar-years]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=to-familiar-years]").change(function() {
            invitesList.initScrollListener();
        });

        $("input[name=optradio-rang]").change(function() {
            invitesList.initScrollListener();
        });
    });

    $(document).ready(function() {
        var $advancedSearchButton = $("#advanced-search-button");
        var $advancedSearchBlock = $("#advanced-search-block");

        $advancedSearchButton.click(function() {
            if($advancedSearchButton.hasClass("active")) {
                $advancedSearchButton.find("span").first().removeClass("dropup");
                $advancedSearchButton.removeClass("active");
                $advancedSearchBlock.slideUp();
            } else {
                $advancedSearchButton.find("span").first().addClass("dropup");
                $advancedSearchButton.addClass("active");
                $advancedSearchBlock.slideDown();
            }
            return false;
        });
    });
</script>

<h1>Список приглашений</h1>

<hr/>

<a href="/invite" class="btn btn-default btn-primary" id="goto-invite-button">Пригласить пользователя</a>

<hr/>

<div class="row">
    <div class="col-xs-9">
        <div class="form-group">
            <label>Поиск по имени или e-mail</label>
            <input type="text" autocomplete="off" class="form-control" name="email" placeholder="c 3 символов" />
        </div>
    </div>

    <div class="col-xs-3">
        <div class="form-group">
            <label>&nbsp;</label>
            <a href="#" class="btn btn-default btn-block" id="refresh-button">Обновить</a>
        </div>
    </div>

    <div class="col-xs-12">
        <div class="form-group">
            <a href="#" class="btn btn-default btn-block" id="advanced-search-button">
                Расширенный фильтр
                <span class="">
                    <span class="caret"></span>
                </span>
            </a>
        </div>
    </div>

</div>
<div id="advanced-search-block" style="display: none;">
    <div class="row" >
        <div class="col-xs-2">
            <div class="form-group" id="invite-status">
                <label>Статус</label>
                <div class="radio" style="margin-top: 0px;">
                    <label><input value="0" type="radio" name="optradio-status">Принято</label>
                </div>
                <div class="radio">
                    <label><input value="1" type="radio" name="optradio-status">В ожидании</label>
                </div>
                <div class="radio">
                    <label><input value="2" type="radio" name="optradio-status">Просрочено</label>
                </div>
                <div class="radio">
                    <label><input value="3" type="radio" name="optradio-status">Отклонено</label>
                </div>
                <div class="radio">
                    <label><input value="4" type="radio" name="optradio-status">Профиль перенесён в архив</label>
                </div>
                <div class="radio">
                    <label><input value="5" type="radio" name="optradio-status">Профиль удалён</label>
                </div>
            </div>
        </div>
        <div class="col-xs-3">
            <div class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Дата приглашения</label>
                <div class="form-inline">
                    <div class="form-control" id="from-date" style="padding-left: 0; width: 110px;"></div>
                    <input type="hidden" name="from_date" value='' />
                    <label style="width: 15px;">-</label>
                    <div class="form-control" id="to-date" style="padding-left: 0; width: 110px;"></div>
                    <input type="hidden" name="to_date" value='' />
                </div>
            </div>
            <div class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Дата регистрации</label>
                <div class="form-inline">
                    <div class="form-control" id="register-from-date" style="padding-left: 0; width: 110px;"></div>
                    <input type="hidden" name="register_from_date" value='' />
                    <label style="width: 15px;">-</label>
                    <div class="form-control" id="register-to-date" style="padding-left: 0; width: 110px;"></div>
                    <input type="hidden" name="register_to_date" value='' />
                </div>
            </div>
        </div>

        <div class="col-xs-4">
            <div class="form-group">
                <div class="form-inline">
                    <div class="form-group">
                        <label style="margin-bottom: 10px; ">Идентифицирован</label>
                        <div class="form-inline" style="text-align: left;">
                            <select id="verified-filter" class="form-control" name="verified-filter" style="width: 130px;">
                                <option value="-1">Не выбрано</option>
                                <option value="1">Идентифицирован</option>
                                <option value="0">Не идентифицирован</option>
                            </select>
                        </div>
                    </div>
                    &nbsp;
                    <div class="form-group" style="text-align: center;">
                        <label style="margin-bottom: 10px; ">Дата идентификации</label>
                        <div class="form-inline">
                            <div class="form-control" id="verified-from-date" style="padding-left: 0; width: 110px;"></div>
                            <input type="hidden" name="verified_from_date" value='' />
                            <label style="width: 15px;">-</label>
                            <div class="form-control" id="verified-to-date" style="padding-left: 0; width: 110px;"></div>
                            <input type="hidden" name="verified_to_date" value='' />
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label style="margin-bottom: 10px; ">Имя Регистратора</label>
                <input id="verifier-name" style="width: 385px;" type="text" autocomplete="off" class="form-control" name="verifier-name" placeholder="c 3 символов" />
            </div>
        </div>

        <div class="col-xs-3">
            <div class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Созданный поток физических лиц</label>
                <div class="form-inline">
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="from-sharers-count" name="from-sharers-count" placeholder="От"/>
                    <label style="width: 15px;">-</label>
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="to-sharers-count" name="to-sharers-count" placeholder="До"/>
                </div>
            </div>
            <div class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Созданный поток юридических лиц</label>
                <div class="form-inline">
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="from-organizations-count" name="from-organizations-count" placeholder="От"/>
                    <label style="width: 15px;">-</label>
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="to-organizations-count" name="to-organizations-count" placeholder="До"/>
                </div>
            </div>
        </div>
    </div>

    <hr/>

    <div class="row" >

        <div class="col-xs-2">
            <div class="form-group">
                <div class="form-inline">
                    <label style="margin-bottom: 10px; width: 69px;">Я ручаюсь</label>
                    <select id="guarantee-filter" class="form-control" name="guarantee-filter" style="width: 100px; padding-left: 0; padding-right: 0;">
                        <option value="-1">Не выбрано</option>
                        <option value="0">Нет</option>
                        <option value="1">Да</option>
                    </select>
                </div>
                <div class="form-inline">
                    <label style="margin-bottom: 10px; width: 69px;">Пол</label>
                    <select id="sex-filter" class="form-control" name="sex-filter" style="width: 100px; padding-left: 0; padding-right: 0;">
                        <option value="-1">Не выбрано</option>
                        <option value="1">Мужчина</option>
                        <option value="0">Женщина</option>
                    </select>
                </div>
            </div>
        </div>

        <div class="col-xs-3">
            <div class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Количество отправленных приглашений</label>
                <div class="form-inline">
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="from-invites-count" name="from-invites-count" placeholder="От"/>
                    <label style="width: 15px;">-</label>
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="to-invites-count" name="to-invites-count" placeholder="До"/>
                </div>
            </div>
        </div>

        <div class="col-xs-4">
            <div class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Сколько лет знакомы</label>
                <div class="form-inline">
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="from-familiar-years" name="from-familiar-years" placeholder="От"/>
                    <label style="width: 15px;">-</label>
                    <input style="width: 110px;" type="text" autocomplete="off" class="form-control" id="to-familiar-years" name="to-familiar-years" placeholder="До"/>
                </div>
            </div>
        </div>

        <div class="col-xs-3">
            <div id="registrator-level" class="form-group" style="text-align: center;">
                <label style="margin-bottom: 10px; ">Является Регистратором</label>
                <div class="form-inline">
                    <div class="radio" style="margin-top: 0px; width: 100px; text-align: left;">
                        <label><input type="radio" name="optradio-rang" value="1">1-го Ранга</label>
                    </div>
                    <div class="radio" style="margin-top: 0px; width: 100px; text-align: left;">
                        <label><input type="radio" name="optradio-rang" value="3">3-го Ранга</label>
                    </div>
                </div>
                <div class="form-inline">
                    <div class="radio" style="width: 100px; text-align: left;">
                        <label><input type="radio" name="optradio-rang" value="2">2-го Ранга</label>
                    </div>
                    <div class="radio" style="width: 100px; text-align: left;">
                        <label><input type="radio" name="optradio-rang" value="-1">Нет</label>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<hr/>

<table class="table" id="invites-table">
    <thead>
    <tr>
        <th>Дата приглашения</th>
        <th>Пользователь</th>
        <th>Пол</th>
        <th>Я ручаюсь</th>
        <th>Знакомы (лет)</th>
        <th>Статус</th>
        <th>Дата регистрации</th>
        <th>Дата идентификации</th>
        <th>Является Регистратором</th>
        <th>Созданный поток</th>
    </tr>
    </thead>
    <tbody style="font-size : 12px;">

    </tbody>
</table>

<div class="row list-loader-animation"></div>