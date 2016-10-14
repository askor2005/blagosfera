<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<%@ taglib prefix="security"
           uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
  <title>Тестовая страница</title>
  <script type="text/javascript" src="/js/jquery.js" ></script>
  <script type="text/javascript" src="/js/rameraListEditor/rameraListEditor.js" ></script>
  <script type="text/javascript">
    $(document).ready(function() {
      RameraListEditorAdmin.init($("#admin_node"));

      RameraListEditorModule.init($(".group_type"), function(viewEvent, data){
        if (viewEvent == RameraListEditorEvents.VALUE_CHANGED) {
          $(".social_form_groups").html("");
          $(".social_form_groups").attr(RameraListEditorModule.ATTRIBUTE_NAME, "social_form_group_" + data.value);
          RameraListEditorModule.init($(".social_form_groups"), function(viewEvent, data){
            console.log(data);
          });
        }
      });

      RameraListEditorModule.init($(".listEditor"));
    });
  </script>
</head>

<body>
  <div id="admin_node" style="display: inline-block; vertical-align: top;"></div>

  <div style="display: inline-block;  vertical-align: top;">
    <div class="group_type" rameraListEditorName="group_type_soc_radio"></div>
    <div class="social_form_groups"></div>

    <div class="listEditor" rameraListEditorName="new_name"></div>
  </div>
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
