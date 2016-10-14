$(document).ready(function () {
    UploadFromWebcamDialog.init();
});

var UploadFromWebcamDialog = new function () {

    var $dialog;
    var $snapshotButton;
    var $nextButton;
    var $cancelButton;
    var camera;
    var snapshot;

    var $imageSnapshot;

    var uploadType;
    var base64ImageData;
    var onSuccessCallBack;


    this.init = function() {
        var self = this;
        $dialog = $("div#upload-from-webcam-dialog");
        $snapshotButton = $("div#upload-from-webcam-dialog button#take-snapshot-button");
        $dialog.on('shown.bs.modal', function () {
            var options = {
                shutter_ogg_url: "/js/webcam/shutter.ogg",
                shutter_mp3_url: "/js/webcam/shutter.mp3",
                swf_url: "/js/webcam/jpeg_camera.swf"
            };
            camera = new JpegCamera("#webcam_stream", options).ready(function (info) {
                console.log(info);
                $nextButton.prop("disabled", false);
                $snapshotButton.prop("disabled", false);
            }).error(function (error) {
                $dialog.hide();
                bootbox.alert("Камера не обнаружена. Возможно камера используется другим приложением. Попробуйте закрыть его и перезагрузить страницу.");
            });
        });

        $dialog.on('hidden.bs.modal', function () {
            camera.stop();
        });

        $snapshotButton.click(function () {
            if (camera != null) {
                snapshot = camera.capture({
                    mirror: true
                });

                snapshot.get_blob(function(blob){
                    var reader = new window.FileReader();
                    reader.readAsDataURL(blob);
                    reader.onloadend = function() {
                        var base64data = reader.result;
                        $imageSnapshot.attr("src", base64data);
                        $imageSnapshot.show();
                        base64ImageData = base64data;
                    }
                });
            }
        });

        $cancelButton = $("div#upload-from-webcam-dialog button#cancel-button");
        $cancelButton.click(function () {
            $dialog.modal("hide")
        });

        $nextButton = $("div#upload-from-webcam-dialog button#next-button");
        $nextButton.click(function () {
            if (base64ImageData == null) {
                bootbox.alert("Необходимо сделать снимок");
            }
            uploadSnapshot(base64ImageData, function(response){
                onSuccessCallBack(self, response);
            });
        });

        $imageSnapshot = $("#canvas_snapshot");
    };

    this.show = function (params) {
        base64ImageData = null;
        $imageSnapshot.hide();
        if (params) {
            onSuccessCallBack = params.onSuccess;
            uploadType = params.uploadType;
        }
        if (uploadType == null) {
            uploadType = "PHOTO";
        }
        if (onSuccessCallBack == null) {
            onSuccessCallBack = function () {
                bootbox.alert("Не передан параметр - обработчик кнопки далее");
            }
        }

        $nextButton.prop("disabled", true);
        $snapshotButton.prop("disabled", true);
        $dialog.modal("show");
    };

    this.hide = function() {
        $dialog.modal("hide");
    };

    function uploadSnapshot(base64Image, callBack) {
        $.radomJsonPostWithWaiter(
            "/images/" + uploadType + "/upload_base64.json",
            base64Image,
            callBack,
            function (r) {
                if (r && r.message) {
                    bootbox.alert(r.message);
                } else {
                    bootbox.alert("Ошибка загрузки");
                }
            }, {
                contentType: 'application/octet-stream'
            }
        );
    }
};