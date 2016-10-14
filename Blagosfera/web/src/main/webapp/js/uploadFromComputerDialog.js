$(document).ready(function () {
    UploadFromComputerDialog.init();
});

var UploadFromComputerDialog = new function() {
    var dialog = this;
    var onSuccessCallback = null;
    var response = null;

    var $dialog;
    var $form;
    var $cancelButton;
    var $nextButton;
    var $fileToUploadInput;
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
        $fileToUploadInput.val("");
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
        $dialog = $("div#upload-from-computer-dialog");

        $uploadProgress = $dialog.find("#upload-progress"); // $("div#upload-from-computer-dialog #upload-progress")
        $uploadProgress.hide();

        $fileToUploadInput = $("div#upload-from-computer-dialog input#file-to-upload");
        $fileToUploadInput.change(function() {
            $form.submit()
        });

        $form = $("div#upload-from-computer-dialog form#file-to-upload-form");
        $form.submit(function(e) {
            e.preventDefault();

            var path = $fileToUploadInput.val();
            if (path == "") {
                clearAndRefresh();
                return;
            }

            var extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
            var allowedExtensions = restrictions.typesAllowed.split(",");
            var url = "/images/"+uploadType+"/upload.json";

            if (allowedExtensions && allowedExtensions.length > 0 && $.inArray(extension, allowedExtensions) < 0) {
                bootbox.alert("Разрешена загрузка файлов с расширениями " + allowedExtensions.join(", "));
                clearAndRefresh();
                return;
            }

            var formData = new FormData($(this)[0]);
            var data = {
                min_width : 200,
                min_height : 200
            };
            if (data) {
                $.each(data, function(key, value) {
                    formData.append(key, value);
                });
            }

            $uploadProgress.show();
            $.ajax({
                url: url,
                type: 'POST',
                data: formData,
                cache: false,
                contentType: false,
                processData: false,
                success: function (r) {
                    $uploadProgress.hide();
                    if (r.result == "error") {
                        clearAndRefresh();
                        if (r.message) {
                            bootbox.alert(r.message);
                        } else {
                            bootbox.alert("Ошибка загрузки");
                        }
                    } else {
                        $slideImage.attr("src", r.image);
                        response = r;
                        refresh();
                    }
                },
                error: function() {
                    $uploadProgress.hide();
                    clearAndRefresh();
                    bootbox.alert("Ошибка загрузки");
                }
            });
        });

        $cancelButton = $("div#upload-from-computer-dialog button#cancel-button");
        $cancelButton.click(function() {
            UploadFromComputerDialog.hide();
        });

        $nextButton = $("div#upload-from-computer-dialog button#next-button");
        $nextButton.click(function() {
            onSuccess(response.image);
        });

        $slideImage = $("div#upload-from-computer-dialog img#slide-image");
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