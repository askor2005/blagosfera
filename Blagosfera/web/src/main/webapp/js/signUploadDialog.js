$(document).ready(function () {
    SignUploadDialog.init();
});

var SignUploadDialog = new function(){
    var profileId="";
    var dialog = this;
    var $dialog;

    function successCallback(dialog, resultUrl) {
        dialog.hide();
        UploadCropDialog.show({
            imageUrl: resultUrl,
            aspect : 100/30,
            needCrop: false,
            onSuccess: function(dialog, resultUrl) {
                $.radomJsonPost("/images/sign.json", {
                    url : resultUrl,
                    sharer_id : profileId
                }, function (r) {
                    dialog.hide();
                    bootbox.alert(r.image);
                },function (r) {
                    if(r && r.message) {
                        bootbox.alert(r.message);
                    }
                    bootbox.alert("Ошибка загрузки");
                });
            }
        });
    }

    this.init = function () {
        $dialog = $("div#sign-upload-dialog");
        profileId = $dialog.find("input#profile-id").val();

        var $cancelButton = $dialog.find("button#cancel-button");
        $cancelButton.click(function() {
            SignUploadDialog.hide();
        });

        var $showUploadFromComputerDialogButton = $dialog.find("button#show-upload-from-computer-dialog-button");
        $showUploadFromComputerDialogButton.click(function() {
            SignUploadDialog.hide();
            UploadFromComputerDialog.show({onSuccess:successCallback});
        });

        var $showUploadFromUrlDialogButton = $dialog.find("button#show-upload-from-url-dialog-button");
        $showUploadFromUrlDialogButton.click(function() {
            SignUploadDialog.hide();
            UploadFromUrlDialog.show({onSuccess:successCallback});
        });
    }

    this.show = function () {
        $dialog.modal("show");
    }

    this.hide = function () {
        $dialog.modal("hide");
    }
};