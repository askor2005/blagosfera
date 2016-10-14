<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <t:insertAttribute name="favicon"/>

    <title>БЛАГОСФЕРА</title>

    <link href="/css/bootstrap.css?v=${buildNumber}" rel="stylesheet">
    <link href="/css/signin.css?v=${buildNumber}" rel="stylesheet">

    <script type="text/javascript" src="/js/jquery.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/jquery.cookie.js?v=${buildNumber}"></script>
    <script type="text/javascript" src="/js/bootstrap.js?v=${buildNumber}"></script>

    <script type="text/javascript">
        var loginInProcess = false;

        var emailRegexp = /^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;

        function checkEmail(usernameInput) {
            if (!emailRegexp.test(usernameInput.val())) {
                usernameInput.parent().removeClass("has-success").addClass("has-error");
                usernameInput.parent().find(".form-control-feedback").removeClass("glyphicon-ok").addClass("glyphicon-remove");
            } else {
                usernameInput.parent().removeClass("has-error").addClass("has-success");
                usernameInput.parent().find(".form-control-feedback").addClass("glyphicon-ok").removeClass("glyphicon-remove");
            }
        }

        $(document).ready(function () {
            var $container = $("div.container");
            var windowHeight = $(window).height();
            var containerHeight = $container.height();

            if (containerHeight < windowHeight) {
                $container.css("margin-top", ((windowHeight - containerHeight) / 2));
            }
            $container.fadeIn();

            var usernameInput = $("#username");
            var passwordInput = $("#password");

            usernameInput.on("keypress", function (event) {
                if (navigator.userAgent.indexOf("Firefox") != -1) {
                    var code = event.which || event.keyCode;
                    if ((code == 8 || code == 9 || code == 46 || code == 37 || code == 39)) {
                        return;
                    }
                }

                if (event.which == 13) {
                    $("form.form-signin").submit();
                    return false;

                }

                if ((event.which != 8) || (event.which != 9) || (event.which != 13)) {
                    return RegExp('[a-zA-Z0-9_.+-@\.]').test(String.fromCharCode(event.charCode));
                } else {
                    return false;
                }
            });

            usernameInput.on("input", function () {
                checkEmail(usernameInput);
            });

            usernameInput.bind('paste', function (e) {
                setTimeout(function () {
                    usernameInput.val(usernameInput.val().split(" ").join("").toLowerCase());
                    checkEmail(usernameInput);
                }, 100);
            });

            passwordInput.bind("keypress", function (event) {
                if (event.which == 13) {
                    $("form.form-signin").submit();
                    return false;
                }

                return RegExp('^[\\S]{1}$').test(String.fromCharCode(event.charCode));
            });

            passwordInput.bind('paste', function (e) {
                setTimeout(function () {
                    passwordInput.val(passwordInput.val().split(" ").join(""));
                }, 100);
            });

            var countDots = 1;
        });

    </script>

    <style type="text/css">
        .form-control-feedback {
            top: 5px;
        }

        .form-group {
            margin-bottom: 0;
            z-index: 2;
        }
    </style>

    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body>
<div class="container" style="display : none;">
    <form class="form-signin" role="form" method="post" action="security/login" onsubmit="return doLogin()">
        <img id="logo" src="/i/logo.png"/>
        <h1 class="form-signin-heading">БЛАГОСФЕРА</h1>
        <h4 class="form-signin-heading">Личный Информационный Кабинет (ЛИК)</h4>
        <div id="loginBlock">
            <div class="form-group has-feedback">
                <input type="text" class="form-control text-input login-input" placeholder="E-mail" required autofocus
                       name="u" id="username" value="${param.email}"/>
                <span class="glyphicon form-control-feedback"></span>
            </div>
            <input type="password" class="form-control text-input" placeholder="Пароль" required name="p"
                   id="password"/>

            <input type="checkbox" name="r" id="rememberme" value="true"> Запомнить меня на этом компьютере

            <br><br>

            <center>
                <div class="g-recaptcha" data-sitekey="6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc"></div>
            </center>

            <br>
            <input type="hidden" value="${header['referer']}" name="referer">

            <div class="alert alert-danger" role="alert" id="email-error" style="display: none;">
                Введен некорректный e-mail
            </div>
            <div class="alert alert-danger" role="alert" id="password-error" style="display: none;">
                Пароль не введен
            </div>
            <div class="alert alert-danger" role="alert" id="captcha-error" style="display: none;">
                Капча не заполнена
            </div>
            <div class="alert alert-danger" role="alert" id="password-check-error" style="display: none;">
                Неверный email или пароль
            </div>
            <div class="alert alert-danger" role="alert" id="captcha-check-error" style="display: none;">
                Капча заполнена неверно
            </div>
            <div class="alert alert-danger" role="alert" id="request-error" style="display: none;">
                Ошибка отправки запроса. Повторите попытку позже
            </div>

            <c:if test="${param.sessions_exceed != null}">
                <div class="alert alert-danger" role="alert">
                    Параллельные сессии запрещены. <a href="/help/parallel_sessions" class="alert-link">Подробнее</a>
                </div>
            </c:if>
            <c:if test="${param.expired != null}">
                <div class="alert alert-info" role="alert">
                    Ваша сессия истекла. Авторизуйтеся заново чтобы продолжить.
                </div>
            </c:if>
            <c:if test="${param.force_closed != null}">
                <div class="alert alert-danger" role="alert">
                    Ваша сессия была принудительно прекращена с другого компьютера
                </div>
            </c:if>
            <c:if test="${param.activate != null}">
                <div class="alert alert-success" role="alert">
                    Аккаунт активирован, войдите в систему
                </div>
            </c:if>
            <c:if test="${param.redirected != null}">
                <div class="alert alert-info" role="alert">
                    Авторизуйтесь чтобы продолжить
                </div>
            </c:if>
            <button class="btn btn-lg btn-primary" id="loginButton" type="submit" onclick="return doLogin()">Вход
            </button>
        </div>
        <div id="loginInProcess" style="display: none;">
            <h4 class="form-signin-heading" id="waitLoginMessage">Подождите, происходит вход в систему...</h4>
            <div class="row list-loader-animation" id="loginAnimation"></div>
        </div>
        <hr/>
        <a href="/register">Зарегистрироваться</a> | <a href="/recovery/init">Восстановить доступ</a>
        <hr/>
    </form>
    <p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a
            href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
</div>

<script type="application/javascript">
    function doLogin(e) {
        if (loginInProcess) return false;

        var username = $('input#username').val();
        var password = $('input#password').val();
        var rememberme = $('input#rememberme').is(':checked') ? true : false;
        var captcha = grecaptcha.getResponse();

        if (!emailRegexp.test(username)) {
            $("div#email-error").slideDown();
            loginInProcess = false;
        } else if (!password) {
            $("div#email-error").slideUp();
            $("div#password-error").slideDown();
            loginInProcess = false;
        } else if (!captcha) {
            $("div#email-error").slideUp();
            $("div#password-error").slideUp();
            $("div#captcha-error").slideDown();
            loginInProcess = false;
        } else {
            $("div#email-error").slideUp();
            $("div#password-error").slideUp();
            $("div#captcha-error").slideUp();
            $("div#request-error").slideUp();

            $("div#password-login-error").slideUp();
            $("div#captcha-check-error").slideUp();

            $("#loginInProcess").slideDown();
            $("#loginBlock").slideUp();

            loginInProcess = true;

            $.get("/api/user/login.json", {
                "u": username,
                "p": password,
                "r": rememberme,
                "c": captcha
            }).done(function (data) {
                if (data === 'OK') {
                    window.location = '/';
                } else {
                    $("#loginInProcess").slideUp();
                    $("#loginBlock").slideDown();

                    grecaptcha.reset();

                    if (data === 'P') {
                        $("div#password-check-error").slideDown();
                    } else if (data === 'C') {
                        $("div#captcha-check-error").slideDown();
                    }
                }
            }).fail(function (data) {
                $("div#request-error").slideDown();
            }).always(function () {
                $("div#email-error").slideUp();
                $("div#password-error").slideUp();
                $("div#captcha-error").slideUp();
                loginInProcess = false;
            });
        }

        return false;
    }
</script>
<!-- BEGIN JIVOSITE CODE {literal} -->
<script type='text/javascript'>
    var jivositeKey = "${jivositeKey}";
    function jivo_onLoadCallback() {
        $.radomJsonGet(
                "/jivosite/info.json",
                null,
                function (response) {
                    clearJivositeInfo();
                    if (response.userInfo) {
                        var date = new Date(0);
                        jivo_api.setContactInfo(
                                {
                                    name: response.userInfo.name,
                                    email: response.userInfo.email,
                                    phone: response.userInfo.phone,
                                    description : ""

                                }
                        );
                        //jivo_api.setUserToken(response.userToken);
                    }
                });
    }
    function clearJivositeInfo() {
        delete_cookie('jv_client_name_'+jivositeKey);
        delete_cookie('jv_email_'+jivositeKey);
        delete_cookie('jv_phone_'+jivositeKey);
    }
    function delete_cookie ( cookie_name )

    {
        var cookie_date = new Date ( );
        cookie_date.setTime ( 0 );
        document.cookie = cookie_name += "=; path=/ ; expires=" +
                cookie_date.toUTCString();

    }

    (function(){ var widget_id = jivositeKey;var d=document;var w=window;function l(){
        var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0]; ss.parentNode.insertBefore(s, ss);}if(d.readyState=='complete'){l();}else{if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})();
</script>
<!-- {/literal} END JIVOSITE CODE -->
</body>
</html>