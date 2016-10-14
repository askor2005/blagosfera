<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <t:insertAttribute name="favicon"/>

    <title>БЛАГОСФЕРА</title>

    <link href="/css/bootstrap.css" rel="stylesheet">
    <link href="/css/signin.css" rel="stylesheet">

    <script type="text/javascript" src="/js/jquery.js"></script>
    <script type="text/javascript" src="/js/jquery.cookie.js"></script>
    <script type="text/javascript" src="/js/bootstrap.js"></script>

    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>

<body>
<div class="container" style="display : none;">
    <form class="form-signin" role="form">
        <img id="logo" src="/i/logo.png"/>
        <h2 class="form-signin-heading">Восстановление доступа</h2>

        <div class="alert alert-info" role="alert">
            Для того чтобы начать процедуру восстановления доступа, введите код с картинки и e-mail, на который
            зарегистрирован Ваш аккаунт в системе. После этого на Ваш e-mail будут высланы дальнейшие инструкции.
        </div>

        <input type="text" class="form-control text-input recovery-init-email" placeholder="E-mail"
               required autofocus name="email" id="email"/>

        <br>

        <div class="captcha-block">
            <center>
                <div class="g-recaptcha" data-sitekey="6Lf8GCITAAAAAAuST_60fckQPMdOgQIKa0djjONc"></div>
            </center>
        </div>

        <div class="alert alert-message" style="display: none;" role="alert"></div>

        <br>
        <button class="btn btn-lg btn-primary" type="submit">Продолжить</button>
        <hr>
        <a href="/register">Зарегистрироваться</a> | <a href="/">Войти</a>
        <hr>
    </form>
    <p class="text-muted login-footer">Система БЛАГОСФЕРА. Все права защищены 2014-2015 (c) ООО "НТЦ "Аскор". | <a
            href="/Благосфера/наши партнеры">Наши партнеры</a></p></p>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        var $container = $("div.container");
        var windowHeight = $(window).height();
        var containerHeight = $container.height();

        if (containerHeight < windowHeight) {
            $container.css("margin-top", ((windowHeight - containerHeight) / 2));
        }

        $container.fadeIn();

        $("form").submit(function (event) {
            event.preventDefault();

            var username = $('input#email').val();
            var captchaResponse = grecaptcha.getResponse();

            if (captchaResponse) {
                $("button[type=submit]").attr("disabled", "disabled");
                $("div.alert-message").slideUp();

                $.ajax({
                    url: '/api/user/restorePassword.json?' + $.param({u: username, c: captchaResponse}),
                    contentType: 'application/json; charset=utf-8',
                    processData: false,
                    type: 'post',
                    dataType: 'json',
                    data: '',
                    success: function (response) {
                        if (response === 'OK') {
                            $("div.alert-message").addClass("alert-success").removeClass("alert-danger").html("На указанный e-mail высланы инструкции по восстановлению доступа").slideDown();
                            $("input.recovery-init-email").slideUp();
                            $("div.captcha-block").slideUp();
                            $("button[type=submit]").slideUp();
                        } else {
                            $("div.alert-message").removeClass("alert-success").addClass("alert-danger").html("Неверно заполнен e-mail или капча").slideDown();
                        }

                        grecaptcha.reset();
                    },
                    error: function () {
                        console.log("ajax error");
                    },
                    complete: function () {
                        $("button[type=submit]").removeAttr("disabled");
                    }
                });
            } else {
                $("div.alert-message").removeClass("alert-success").addClass("alert-danger").html("капча не заполнена").slideDown();
            }
        });
    });
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