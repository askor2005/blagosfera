<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<script type="text/javascript">
    $(document).ready(function(){
        require(
                [
                    "text!/templates/emailsender/emailSenderTemplate.html",
                    "emailsender/emailSenderModule"
                ],
                function (emailSenderTemplate, emailSenderModule, utils) {
                    var jqEmailSenderTemplateNode = $(emailSenderTemplate);
                    $("#emailSenderRootNode").append(jqEmailSenderTemplateNode);
                    emailSenderModule.init(
                            jqEmailSenderTemplateNode,
                            {
                                jqMailSubject: $("#mailSubject"),
                                jqMailFrom: $("#mailFrom"),

                                jqSharerNameNode: $("#sharerName"),
                                jqSharersTable: $("#sharersTable"),
                                jqAddSharer: $("#addSharer"),
                                deleteSharerClass: "deleteSharer",
                                jqSharersTemplate: $("#sharersTemplate"),
                                jqTemplateNameNode: $("#templateName"),
                                jqSharersProviderAll: $("#sharersProviderAll"),
                                jqSharersProviderMan: $("#sharersProviderMan"),
                                jqSharersProviderWoman: $("#sharersProviderWoman"),
                                jqSharersProviderSelected: $("#sharersProviderSelected"),
                                jqSharersProviderSelectedBlock: $("#sharersProviderSelectedBlock"),
                                jqSendToEmailButton: $("#sendToEmailButton"),
                                jqSendMailResults: $("#sendMailResults")
                            }
                    );
                },
                function(error){
                    console.log(error);
                }
        );
    });
</script>
<div class="col-xs-12">
    <h2>${currentPageTitle}</h2>
</div>
<div id="emailSenderRootNode"></div>