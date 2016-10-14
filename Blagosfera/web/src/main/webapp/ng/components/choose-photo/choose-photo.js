'use strict';

define([
    'app'
], function (app) {

    app.directive('choosephoto', function($mdDialog, $parse, $translatePartialLoader, $rootScope) {
        return {
            restrict: 'E',
            transclude: true,
            scope: {
                sourceUrl : "=sourceUrl",
                resultUrl : "=resultUrl"/*,
                sourceUrl : "=sourceUrlValue",
                resultUrl : "=resultUrlValue"*/
            },
            controller: [ "$scope", function($scope) {
                $translatePartialLoader.addPart('choosephoto');
                $scope.file = {};
                $scope.fileUrl = $scope.sourceUrl;
                $scope.croppedImage = $scope.resultUrl;
                $scope.imageIsLoading = false;

                $scope.minPhotoWidth = 100;
                $scope.minPhotoHeight = 50;
                $scope.maxUploadSize = "8MB";

                $scope.webCamStream = null;

                $scope.$watch('fileUrl', function(newValue, oldValue) {
                    $scope.sourceUrl = $scope.fileUrl;
                });
                $scope.$watch('croppedImage', function(newValue, oldValue) {
                    $scope.resultUrl = $scope.croppedImage;
                });

                $scope.onChooseFile = function(){
                    if ($scope.file != null) {
                        $scope.imageIsLoading = true;
                        var fileReader = new FileReader();
                        console.log($scope.file);
                        fileReader.readAsDataURL($scope.file);
                        fileReader.onload = function (e) {
                            $scope.imageIsLoading = false;
                            $scope.fileUrl = e.target.result;
                        };
                    }
                };

                $scope.showWebCameraDialog = function() {
                    $mdDialog.show({
                        templateUrl: '/ng/components/choose-photo/webcam.html',
                        clickOutsideToClose:true,
                        controller: ['scope', function(webCamDialogScope) {

                            webCamDialogScope.createPhotoChannel = {
                                // the fields below are all optional
                                videoHeight: 200,
                                videoWidth: 300,
                                video: null // Will reference the video element on success
                            };

                            var _video = null;
                            var webcamFileUrl = "";

                            webCamDialogScope.patOpts = {x: 0, y: 0, w: 25, h: 25};

                            // Setup a channel to receive a video property
                            // with a reference to the video element
                            // See the HTML binding in main.html
                            webCamDialogScope.channel = {};

                            webCamDialogScope.webcamError = false;
                            webCamDialogScope.onError = function (err) {
                                webCamDialogScope.$apply(
                                    function() {
                                        webCamDialogScope.webcamError = err;
                                    }
                                );
                            };

                            webCamDialogScope.onSuccess = function () {
                                // The video element contains the captured camera data
                                _video = webCamDialogScope.channel.video;
                                webCamDialogScope.$apply(function() {
                                    webCamDialogScope.patOpts.w = _video.width;
                                    webCamDialogScope.patOpts.h = _video.height;
                                    //scope.showDemos = true;
                                });
                            };

                            webCamDialogScope.onStream = function (stream) {
                                $scope.webCamStream = stream;
                                // You could do something manually with the stream.
                            };

                            webCamDialogScope.makeSnapshot = function() {
                                if (_video) {
                                    var patCanvas = document.querySelector('#snapshot');
                                    if (!patCanvas) return;

                                    patCanvas.width = _video.width;
                                    patCanvas.height = _video.height;
                                    var ctxPat = patCanvas.getContext('2d');

                                    var idata = getVideoData(
                                        webCamDialogScope.patOpts.x, webCamDialogScope.patOpts.y,
                                        webCamDialogScope.patOpts.w, webCamDialogScope.patOpts.h);
                                    ctxPat.putImageData(idata, 0, 0);

                                    webcamFileUrl = patCanvas.toDataURL();
                                    //sendSnapshotToServer(patCanvas.toDataURL());
                                }
                            };

                            webCamDialogScope.doneCapture = function() {
                                $scope.fileUrl = webcamFileUrl;
                                $mdDialog.hide();
                            };

                            var getVideoData = function getVideoData(x, y, w, h) {
                                var hiddenCanvas = document.createElement('canvas');
                                hiddenCanvas.width = _video.width;
                                hiddenCanvas.height = _video.height;
                                var ctx = hiddenCanvas.getContext('2d');
                                ctx.drawImage(_video, 0, 0, _video.width, _video.height);
                                return ctx.getImageData(x, y, w, h);
                            };
                        }]
                    }).then(function(answer) {
                        $scope.stopStream();
                    }, function() {
                        $scope.stopStream();
                    });
                };

                $scope.stopStream = function () {
                    if ($scope.webCamStream != null) {
                        $scope.webCamStream.getAudioTracks().forEach(function(track) {
                            track.stop();
                        });
                        $scope.webCamStream.getVideoTracks().forEach(function(track) {
                            track.stop();
                        });
                    }
                };
            }],
            templateUrl:'/ng/components/choose-photo/choose-photo.html',
            replace: true
        };
    });

    return app;
});


