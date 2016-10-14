//----------------------------------------------------------------------------------------------------------------------
// Смысл обработки следующих shown.bs.modal и hidden.bs.modal ивентов в том, чтобы правильно выставлять класс для body:
// - если хоть какой диалог открыт, то у body должен быть класс modal-open
// - иначе такого класса у него быть не должно
// [Проверка того, что на странице открыт диалог осуществляется условием ($('.modal .in').length > 0)]
// Если этого не сделать, то при отображении диалога может забагать скролл в следующих случаях:
// - если modal диалог запустить из другого modal диалога со скрытием первого
// - если modal диалог запустить из другого modal диалога без скрытия первого, а потом закрыть второй
// - ...
$(document).on('shown.bs.modal', dialogModalScrollFix);
$(document).on('hidden.bs.modal', dialogModalScrollFix);
function dialogModalScrollFix(event) {
    if ($('.modal.in').length > 0) {
        if (!$('body').hasClass('modal-open')) {
            $('body').addClass('modal-open');
        }
    } else {
        $('body').removeClass('modal-open');
    }
}
//----------------------------------------------------------------------------------------------------------------------

function getSharers(successCallback) {
    $.radomJsonGet("/not_deleted_sharers.json", {},
        function(response) {
            if(successCallback) {
                successCallback(response);
            }
        }, function() {
            bootbox.alert("Ошибка загрузки списка участников");
        });
}

function getCommunities(successCallback) {
    $.radomJsonGet("/communities.json", {},
        function(response) {
            if(successCallback) {
                successCallback(response);
            }
        }, function() {
            bootbox.alert("Ошибка загрузки списка объединений");
        });
}

function getCommunityMembers(id,successCallback) {
    $.radomJsonGet("/members.json", {id:id},
        function(response) {
            if(successCallback) {
                successCallback(response);
            }
        }, function() {
            bootbox.alert("Ошибка загрузки списка членов объединения");
        });
}

function getImageRestrictions(successCallback) {
    $.radomJsonGet("/images/PHOTO/restrictions.json", {},
        function(response) {
            if(successCallback) {
                successCallback(response);
            }
        }, function() {
            bootbox.alert("Ошибка загрузки");
        });
}

function getImageIconRestrictions(successCallback) {
    $.radomJsonGet("/images/ICON/restrictions.json", {id:id},
        function(response) {
            if(successCallback) {
                successCallback(response);
            }
        }, function() {
            bootbox.alert("Ошибка загрузки");
        });
}