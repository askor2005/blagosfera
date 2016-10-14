$(document).ready(function () {
    UploadFromUrlDialog.init();
});

var UploadFromUrlDialog = new function() {
    var dialog = this;
    var onSuccessCallback = null;
    var response = null;

    var $dialog;
    var $form;
    var $cancelButton;
    var $nextButton;
    var $urlToUploadInput;
    var $slideImage;
    var $uploadProgress;

    var restrictions;
    var uploadType;

    function onSuccess(resultUrl) {
        if(onSuccessCallback) {
            onSuccessCallback(dialog,resultUrl);
        }
    }

    function clearAndRefresh() {
        clear();
        refresh();
    }

    function clear() {
        response = null;
        $urlToUploadInput.val("");
        $slideImage.css("display","none");
    }

    function refresh() {
        refreshNextButton();
        refreshSlideImage();
    }

    function refreshNextButton() {
        if(response) {
            $nextButton.removeClass("disabled");
        } else {
            $nextButton.addClass("disabled");
        }
    }

    function refreshSlideImage() {
        if(response) {
            $slideImage.slideDown();
        } else {
            $slideImage.slideUp();
        }
    }

    this.init = function () {
        $dialog = $("div#upload-from-url-dialog");

        $uploadProgress = $dialog.find("#upload-progress");
        $uploadProgress.hide()

        $urlToUploadInput = $("div#upload-from-url-dialog input#url-to-upload");

        $form = $("div#upload-from-url-dialog form#url-to-upload-form");
        $form.submit(function(e) {
            e.preventDefault();

            var path = $urlToUploadInput.val();
            if (path == "") {
                clearAndRefresh();
                return;
            }

            var extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            var allowedExtensions = restrictions.typesAllowed.split(",");
            var url = "/images/"+uploadType+"/upload_url.json";

            if (allowedExtensions && allowedExtensions.length > 0 && $.inArray(extension, allowedExtensions) < 0) {
                bootbox.alert("Разрешена загрузка файлов с расширениями " + allowedExtensions.join(", "));
                clearAndRefresh();
                return;
            }

            $uploadProgress.show();
            $.radomJsonPost(url, {
                url : path
            }, function (r) {
                $uploadProgress.hide();
                $slideImage.attr("src", r.image);
                response = r;
                refresh();
            },function (r) {
                $uploadProgress.hide();
                clearAndRefresh();
                if (r && r.message) {
                    bootbox.alert(r.message);
                } else {
                    bootbox.alert("Ошибка загрузки");
                }
            });
        });

        $cancelButton = $("div#upload-from-url-dialog button#cancel-button");
        $cancelButton.click(function() {
            UploadFromUrlDialog.hide();
        });

        $nextButton = $("div#upload-from-url-dialog button#next-button");
        $nextButton.click(function() {
            onSuccess(response.image);
        });

        $slideImage = $("div#upload-from-url-dialog img#slide-image");
    }

    function imageRestrictionsCallback(r) {
        restrictions = r;

        var $minPhotoSize = $dialog.find("#min-photo-size");
        var $maxPhotoSize = $dialog.find("#max-photo-size");
        var $maxFileSize = $dialog.find("#max-file-size");
        var $extensions = $dialog.find("#extensions");

        $minPhotoSize.html(restrictions.minWidth + "x" + restrictions.minHeight);
        $maxPhotoSize.html(restrictions.maxWidth + "x" + restrictions.maxHeight);
        $maxFileSize.html(restrictions.maxUploadsize);
        $extensions.html(restrictions.typesAllowed);
    }

    this.show = function (params) {
        if(params && params.restrictionsFunction) {
            params.restrictionsFunction(imageRestrictionsCallback);
        } else {
            getImageRestrictions(imageRestrictionsCallback);
        }

        if(params && params.uploadType) {
            uploadType = params.uploadType;
        } else {
            uploadType = "PHOTO";
        }

        onSuccessCallback = params.onSuccess;
        clearAndRefresh();
        $dialog.modal("show");
    }

    this.hide = function () {
        $dialog.modal("hide");
    }
};