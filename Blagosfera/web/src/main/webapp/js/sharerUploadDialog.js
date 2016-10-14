$(document).ready(function () {
    SharerUploadDialog.init();
});

var SharerUploadDialog = new function () {
    var profileId = "";
    var dialog = this;
    var $dialog;

    function isPortableDevice() {
        //console.log(navigator.userAgent);
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    }

    function successCallback(dialog, resultUrl) {
        dialog.hide();
        //var urlOriginal = resultUrl;

        UploadEditImageDialog.show({
            imageUrl: resultUrl,
            onSuccess: function (dialog, resultUrl) {
                dialog.hide();

                var urlOriginal = resultUrl;
                UploadCropDialog.show({
                    imageUrl: resultUrl,
                    onSuccess: function (dialog, resultUrl) {
                        $.radomJsonPostWithWaiter("/images/avatar/sharer/" + profileId + ".json", {
                            url: resultUrl,
                            urlOriginal: urlOriginal
                        }, function (r) {
                            if (r.result == "error") {
                                if (r.message) {
                                    bootbox.alert(r.message);
                                } else {
                                    bootbox.alert("Ошибка загрузки");
                                }
                            } else {
                                dialog.hide();
                                $("a#avatar-original-link").attr("href", urlOriginal);
                                $("div#profile-avatar-wrapper img").attr("src", Images.getResizeUrl(resultUrl, "c250"));

                                //Иконку в header'e меняем только, если пользователь редактировал свой аватар (админ может редактировать чужие)
                                if (CurrentSharer.id == profileId) {
                                    $(".header-avatar").attr("src", Images.getResizeUrl(resultUrl, "c48"));
                                }
                            }
                        }, function () {
                            bootbox.alert("Ошибка загрузки");
                        });
                    }
                });
            }
        });
    }

    this.init = function () {
        $dialog = $("div#sharer-upload-dialog");
        profileId = $dialog.find("input#profile-id").val();

        var $cancelButton = $("div#sharer-upload-dialog button#cancel-button");
        var $showUploadFromComputerDialogButton = $("div#sharer-upload-dialog button#show-upload-from-computer-dialog-button");
        var $showUploadFromUrlDialogButton = $("div#sharer-upload-dialog button#show-upload-from-url-dialog-button");
        var $showUploadFromWebcamDialogButton = $("div#sharer-upload-dialog button#show-upload-from-webcam-dialog-button");

        $cancelButton.click(function () {
            SharerUploadDialog.hide();
        });

        $showUploadFromComputerDialogButton.click(function () {
            SharerUploadDialog.hide();
            UploadFromComputerDialog.show({onSuccess: successCallback});
        });

        $showUploadFromUrlDialogButton.click(function () {
            SharerUploadDialog.hide();
            UploadFromUrlDialog.show({onSuccess: successCallback});
        });

        $showUploadFromWebcamDialogButton.click(function () {
            SharerUploadDialog.hide();
            UploadFromWebcamDialog.show({onSuccess: successCallback});
        });

        if (isPortableDevice()) {
            $showUploadFromWebcamDialogButton.hide();
            $showUploadFromComputerDialogButton.html("Сделать снимок или выбрать из галереи");
        } else {
            $showUploadFromComputerDialogButton.html("Выбрать файл");
        }
    };

    this.show = function () {
        $dialog.modal("show");
    };

    this.hide = function () {
        $dialog.modal("hide");
    };
};