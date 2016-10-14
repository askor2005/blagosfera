$(document).ready(function () {
    IconUploadDialog.init();
});

var IconUploadDialog = new function(){
    var dialog = this;
    var $dialog;

    function successCallback(dialog, resultUrl) {
        dialog.hide();
        UploadEditImageDialog.show({
            imageUrl: resultUrl,
            onSuccess: function(dialog, resultUrl) {
                dialog.hide();
                UploadCropDialog.show({
                    imageUrl: resultUrl,
                    onSuccess: function(dialog, resultUrl) {
                        //dialog.hide();
                        //$("input#icon-class").val(resultUrl).change();

                        $.radomJsonPost("/images/upload_section_icon.json", {
                            url : resultUrl
                        }, function (r) {
                            dialog.hide();
                            $("input#icon-class").val(r.image).change();
                        },function (r) {
                            if(r && r.message) {
                                bootbox.alert(r.message);
                            } else {
                                bootbox.alert("Ошибка загрузки");
                            }
                        });
                    }
                });
            }
        });
    }

    this.init = function () {
        $dialog = $("div#icon-upload-dialog");

        $dialog.find("button#cancel-button");

        var $cancelButton = $dialog.find("button#cancel-button");
        $cancelButton.click(function() {
            dialog.hide();
        });

        var $showUploadFromComputerDialogButton = $dialog.find("button#show-upload-from-computer-dialog-button");
        $showUploadFromComputerDialogButton.click(function() {
            dialog.hide();
            UploadFromComputerDialog.show({onSuccess:successCallback, restrictionsFunction:getImageIconRestrictions, uploadType:"ICON"});
        });

        var $showUploadFromUrlDialogButton = $dialog.find("button#show-upload-from-url-dialog-button");
        $showUploadFromUrlDialogButton.click(function() {
            dialog.hide();
            UploadFromUrlDialog.show({onSuccess:successCallback, restrictionsFunction:getImageIconRestrictions, uploadType:"ICON"});
        });
    }

    this.show = function () {
        $dialog.modal("show");
    }

    this.hide = function () {
        $dialog.modal("hide");
    }
};