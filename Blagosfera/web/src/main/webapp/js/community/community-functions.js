// Функции для работы с объединением
CommunityFunctions = {
    // Сообщение вступления в ПО
    successRequestMessage :
        "Ув, {sharerName}, для Вас создан документ <a href='{link}' target='_blank'>\"{documentName}\"</a>. " +
        "Чтобы завершить процедуру подачи заявки на вступление в Потребительское Общество <b>\"{communityName}\"</b>, " +
        "Вам необходимо подписать заявление. Это можно сделать пройдя в Ваш личный портал РаДОМ в список " +
        "Документов на подпись или же просто нажав на кнопку \"Перейти к подписанию документа\". " +
        "После того, как Вы подпишите заявление, на Вашем личном счету в системе Благосфера будет заморожена " +
        "сумма {amount}, которая, после принятия Вас в пайщики <b>\"{communityName}\"</b>, будет передана в <b>\"{communityName}\"</b> " +
        "в качестве вступительного и минимального паевого взноса.<br/>" +
        "<a href='{link}' class='btn btn-primary' target='_blank'>{linkDescription}</a>",

    // Сообщение выхода из ПО
    successRequestLeaveFromCommunityMessage :
        "Ув, {sharerName}, для Вас создан документ <a href='{link}' target='_blank'>\"{documentName}\"</a>. " +
        "Чтобы завершить процедуру подачи заявки на выход из Потребительского Общества <b>\"{communityName}\"</b>, " +
        "Вам необходимо подписать заявление. Это можно сделать пройдя в Ваш личный портал РаДОМ в список " +
        "Документов на подпись или же просто нажав на кнопку \"Перейти к подписанию документа\". " +
        "После того, как Вы подпишите заявление и его рассмотрит Совет Потребительского Общества " +
        //"сумма {amount}, которая, после принятия Вас в пайщики <b>\"{communityName}\"</b>, будет передана в <b>\"{communityName}\"</b> " +
        //"в качестве вступительного и минимального паевого взноса.<br/>" +
        "Здесь должна быть текстовка о том, что средства с паевой книжки вернутся к Вам :)" +
        "<a href='{link}' class='btn btn-primary' target='_blank'>{linkDescription}</a>",
    
    // Сообщение при подаче заявки на вступление в объединение в котором необходимо подписать документы для вступления
    successRequestDocumentCommunityMessage :
        "Ув, {sharerName}, для Вас созданы документы, которые необходимо подписать для вступления в объединение.<br/>" +
        "Ссылка для перехода к пакету документов:<hr/>" +
        "<a href='{link}' style='display: block;' class='btn btn-primary' target='_blank'>{linkDescription}</a>",

    successRequestExistsDocumentCommunityMessage :
        "Ув, {sharerName}, документы, которые необходимо подписать для вступления в объединение уже созданы.<br/>" +
        "Ссылка для перехода к пакету документов:<hr/>" +
        "<a href='{link}' style='display: block;' class='btn btn-primary' target='_blank'>{linkDescription}</a>",

    // Сделать запрос на членство в объединении
    createRequestToJoinInCommunity : function(communityId) {
        var self = this;
        $.radomJsonPostWithWaiter("/communities/request.json", {
            community_id : communityId
        }, function(response) {
            //response.eventType = "request";
            //$(radomEventsManager).trigger("community-member.event", response);
        }, null, {
            responseParameters : {
                "documentRequest" : {
                    "neededParameters" : ["sharerName", "link", "linkDescription"],
                    "content" : self.successRequestDocumentCommunityMessage
                },
                "existsDocumentRequest" : {
                    "neededParameters" : ["sharerName", "link", "linkDescription"],
                    "content" : self.successRequestExistsDocumentCommunityMessage
                },
                "kuchPoName" : {
                    "neededParameters" : ["sharerName", "link", "documentName", "linkDescription", "communityName", "amount", "member"],
                    "content" : self.successRequestMessage
                }
            }
        });
    },

    acceptToJoinToCommunity : function(member_id) {
        var self = this;
        //var successMessage = this.successRequestMessage;
        // Если у сообщества есть условия на вступление, то по сути этот запрос выполнит запрос на вступление
        $.radomJsonPostWithWaiter("/communities/accept_invite.json", {
            member_id : member_id
        }, function(response) {
            // Если это объединение, в которое нужно вступать с условиями
            if (response.sharerName != null) {
                //response.eventType = "request";
                //$(radomEventsManager).trigger("community-member.event", response);
            } else { // Если это просто подтверждение запроса на вступление
                //response.eventType = "accept_invite";
                //$(radomEventsManager).trigger("community-member.event", response);
                // Делаем редирект в объединение
                //window.location.href = response.member.community.link;
            }
        }, null, {
            responseParameters : {
                "documentRequest" : {
                    "neededParameters" : ["sharerName", "link", "linkDescription"],
                    "content" : self.successRequestDocumentCommunityMessage
                },
                "existsDocumentRequest" : {
                    "neededParameters" : ["sharerName", "link", "linkDescription"],
                    "content" : self.successRequestExistsDocumentCommunityMessage
                },
                "kuchPoName" : {
                    "neededParameters" : ["sharerName", "link", "documentName", "linkDescription", "communityName", "amount", "member"],
                    "content" : self.successRequestMessage
                }
            }
        });

        /*CommunityRequiredConditionsDialog.loadConditions({
            communityId : communityId,
            callback : function(communityId) {
                $.radomJsonPost("/communities/accept_invite.json", {
                    member_id : memberId
                }, function(response) {
                    $(radomEventsManager).trigger("community-member.accept-invite", response);
                });
            }
        });*/
    },

    // Запрос выхода из объединения
    leaveFromCommunity : function(memberId) {
        var successMessage = this.successRequestLeaveFromCommunityMessage;
        $.radomJsonPostWithWaiter("/communities/leave.json", {
            member_id : memberId
        }, function(response) {
            //response.eventType = "leave";
            //$(radomEventsManager).trigger("community-member.event", response);
        }, null, {
            "neededParameters" : ["sharerName", "link", "documentName", "linkDescription", "communityName"/*, "amount", "member"*/],
            "content" : successMessage
        });
    },

    // Отменить запрос на выход из объединения
    cancelRequestToLeaveFromCommunity: function(memberId) {
        $.radomJsonPostWithWaiter("/communities/cancel_request_to_leave.json", {
            member_id : memberId
        });
    },

    // Вступить в открытое объединение
    joinToOpenCommunity: function(communityId) {
        var self = this;
        $.radomJsonPostWithWaiter("/communities/join.json", {
            community_id : communityId
        }, function(response) {
            //response.eventType = "join";
            //$(radomEventsManager).trigger("community-member.event", response);
        }, null, {
            responseParameters : {
                "documentRequest" : {
                    "neededParameters" : ["sharerName", "link", "linkDescription"],
                    "content" : self.successRequestDocumentCommunityMessage
                },
                "existsDocumentRequest" : {
                    "neededParameters" : ["sharerName", "link", "linkDescription"],
                    "content" : self.successRequestExistsDocumentCommunityMessage
                }
            }
        });
    },

    // Отменить заявку на вступление в объединение
    cancelRequest: function(memberId) {
        $.radomJsonPostWithWaiter("/communities/cancel_request.json", {
            member_id : memberId
        }, function(response) {
            //response.eventType = "cancel_request";
            //$(radomEventsManager).trigger("community-member.event", response);
        });
    },

    // Отклонить приглашение в объединени
    rejectInvite: function(memberId) {
        $.radomJsonPostWithWaiter("/communities/reject_invite.json", {
            member_id : memberId
        }, function(response) {
            //response.eventType = "reject_invite";
            //$(radomEventsManager).trigger("community-member.event", response);
        });
    },

    //-------
    // Запрос на вступление в объединение от организации
    requestToJoinCommunity: function(candidateCommunityId, callBack, errorCallBack) {
        $.radomJsonPostWithWaiter(
            "/communities/createRequestToOrganizationMember.json",
            {
                communityId : communityId,
                candidateCommunityId : candidateCommunityId
            },
            callBack,
            errorCallBack
        );
    },
    // Запрос от руководителя организации на выход организации из объединения
    requestFromOrganizationToExcludeCommunity: function(memberId, callBack, errorCallBack) {
        $.radomJsonPostWithWaiter(
            "/communities/request_from_organization_to_exclude_organization_member.json",
            {
                member_id : memberId
            },
            callBack,
            errorCallBack
        );
    },

    // Запрос от полномочного объединения на выход организации из объединения
    requestFromCommunityToExcludeCommunity: function(memberId, callBack, errorCallBack) {
        $.radomJsonPostWithWaiter(
            "/communities/request_from_community_to_exclude_organization_member.json",
            {
                member_id : memberId
            },
            callBack,
            errorCallBack
        );
    },

    // Принятие запроса на вступление в объединение
    acceptOrganizationRequest: function(memberId, callBack, errorCallBack){
        $.radomJsonPostWithWaiter(
            "/communities/accept_organization_request.json",
            {
                member_id : memberId
            },
            callBack,
            errorCallBack
        );
    },

    // Принятие запроса на вступление в объединение нескольких организаций
    acceptOrganizationRequests: function(communityId, memberIds, callBack, errorCallBack, additionalParameters){
        var parameters = {
            contentType: "application/json"
        };
        if (additionalParameters != null) {
            parameters =  $.extend(parameters, additionalParameters);
        }
        $.radomJsonPostWithWaiter(
            "/group/" + communityId + "/accept_organization_requests.json",
            JSON.stringify(memberIds),
            callBack,
            errorCallBack,
            parameters
        );
    },

    // Отклонение запроса на вступление в объединение полномочным объединения
    rejectRequestToJoinCommunity: function(memberId, callBack, errorCallBack) {
        $.radomJsonPostWithWaiter(
            "/communities/reject_organization_request.json",
            {
                member_id : memberId
            },
            callBack,
            errorCallBack
        );
    },

    // Отмена запроса на вступление организации в объединение
    cancelRequestCommunity: function(memberId, callBack, errorCallBack) {
        $.radomJsonPostWithWaiter(
            "/communities/cancel_organization_request.json",
            {
                member_id : memberId
            },
            callBack,
            errorCallBack
        );
    },
    // Отмена запроса на выход из объединения организации
    cancelRequestToLeaveCommunity: function(memberId, callBack, errorCallBack) {
        $.radomJsonPostWithWaiter(
            "/communities/cancel_exclude_organization_request.json",
            {
                member_id : memberId
            },
            callBack,
            errorCallBack
        );
    },

    // Принять выход из объединения от нескольких организаций
    acceptExcludeOrganizationMembers: function(communityId, memberIds, callBack, errorCallBack, additionalParameters) {
        var parameters = {
            contentType: "application/json"
        };
        if (additionalParameters != null) {
            parameters =  $.extend(parameters, additionalParameters);
        }
        $.radomJsonPostWithWaiter(
            "/group/" + communityId + "/accept_exclude_organization_requests.json",
            JSON.stringify(memberIds),
            callBack,
            errorCallBack,
            parameters
        );
    },

    // Получить данные по заполненности юр лица
    getCommunityFilling: function (communityId, callBack, errorCallBack){
        $.radomJsonPost(
            "/communities/get_community_filling.json",
            {community_id : communityId},
            callBack,
            errorCallBack
        );
    }

}