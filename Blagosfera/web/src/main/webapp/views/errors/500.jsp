<%@ page language="java" isErrorPage="true" import="java.io.*" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<style>
    .panel-heading a:after {
        font-family:'Glyphicons Halflings';
        content:"\e114";
        float: right;
        color: grey;
    }
    .panel-heading a.collapsed:after {
        content:"\e080";
    }
</style>

<div class="panel panel-default">
    <div class="panel-heading">Внутренняя ошибка приложения</div>
    <div class="panel-body">
        <p style="color: blue;">Сообщение об ошибке отправлено в службу технической поддержки.</p>
    </div>
</div>


<div class="panel panel-default" id="stackTracePanel">
    <div class="panel-heading">
        <h4 class="panel-title">
            <a data-toggle="collapse" data-target="#collapseDerails"
               href="#collapseTwo" class="collapsed">
                <i class="fa fa-info-circle"></i>    Посмотреть детали
            </a>
        </h4>

    </div>
    <div id="collapseDerails" class="panel-collapse collapse">
        <div class="panel-body" style="font-size: 12px;">
            <%
                if (exception != null) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    exception.printStackTrace(printWriter);
                    out.println(stringWriter);
                    printWriter.close();
                    stringWriter.close();
                }
            %>
        </div>
    </div>
</div>