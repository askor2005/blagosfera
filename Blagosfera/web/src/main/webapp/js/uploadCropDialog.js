$(document).ready(function () {
    UploadCropDialog.init();
});

var UploadCropDialog = new function () {
    var dialog = this;
    var onSuccessCallback = null;
    var imageUrl = null;
    var cropBounds = null;
    var response = null;
    var aspect = null;
    var needCrop = null;

    var $dialog;
    var $cancelButton;
    var $saveButton;

    function onSuccess(resultUrl) {
        if (onSuccessCallback) {
            onSuccessCallback(dialog, resultUrl);
        }
    }

    function onChangeJcrop(bounds) {
        if (bounds.w > 0 && bounds.h > 0) {
            cropBounds = bounds;
            $('#crop').show();
        } else {
            cropBounds = null;
            $('#crop').hide();
        }

        refreshSaveButton();
    }

    function clearAndRefresh() {
        clear();
        refresh();
    }

    function clear() {
        response = null;
        cropBounds = null;
    }

    function refresh() {
        refreshSaveButton();
        refreshJcrop();
    }

    function refreshSaveButton() {
        if (cropBounds || !needCrop) {
            $saveButton.removeClass("disabled");
        } else {
            $saveButton.addClass("disabled");
        }
    }

    function refreshJcrop() {
        $('#crop-target').attr("src", imageUrl);
        $('#crop-img').show();
        jcrop_api.setImage(imageUrl);
        jcrop_api.setOptions({aspectRatio: aspect});
    }

    this.init = function () {
        $dialog = $("div#upload-crop-dialog");

        onChangeCallbackJcrop = onChangeJcrop;

        $cancelButton = $("div#upload-crop-dialog button#cancel-button");
        $saveButton = $("div#upload-crop-dialog button#save-button");

        $cancelButton.click(function () {
            UploadCropDialog.hide();
        });

        $saveButton.click(function () {
            if (needCrop) {
                var url = "/images/crop.json";
                $.radomJsonPostWithWaiter(url, {
                    src: imageUrl,
                    x1: cropBounds.x < 0 ? 0 : cropBounds.x,
                    x2: cropBounds.x2 < 0 ? 0 : cropBounds.x2,
                    y1: cropBounds.y < 0 ? 0 : cropBounds.y,
                    y2: cropBounds.y2 < 0 ? 0 : cropBounds.y2
                }, function (r) {
                    r = JSON.parse(r);
                    onSuccess(r.image);
                }, function (r) {
                    if (r && r.message) {
                        bootbox.alert(r.message);
                    } else {
                        bootbox.alert("Ошибка загрузки");
                    }
                });
            } else {
                onSuccess(imageUrl);
            }
        });
    };

    this.show = function (params) {
        onSuccessCallback = params.onSuccess;
        imageUrl = params.imageUrl;

        if (params.aspect != undefined) {
            aspect = params.aspect;
        } else {
            aspect = 1;
        }

        if (params.needCrop != undefined) {
            needCrop = params.needCrop;
        } else {
            needCrop = true;
        }

        clearAndRefresh();
        $dialog.modal("show");
    };

    this.hide = function () {
        $dialog.modal("hide");
    };
};