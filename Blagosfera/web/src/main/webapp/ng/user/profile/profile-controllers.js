'use strict';

define([
    'app',
    'tinymce'
], function (app, tinymce) {

    app.controller('ProfileCtrl', function ($scope, $routeParams, userProfileService, petrovichService, addressService, $timeout,broadcastService,countryCodesMapping,timeTableService,$mdDialog) {
        var scope = this;
        scope.showAllMemberCommunities = function() {

        };
        scope.showAllCreatedCommunities = function() {
        };
        scope.dataToSend = {};
        scope.mask = "99999999999";
        scope.limitCreatedCommunities = 5;
        scope.limitMemberCommunities = 5;
        scope.registrator = false;
        scope.dataToSend.basicInformation = {};
        scope.dataToSend.factAddress = {};
        scope.dataToSend.regAddress = {};
        scope.dataToSend.registratorOfficeAddress = {};
        scope.dataToSend.registratorData = {};
        scope.profile = {};
        scope.profile.ikp = $routeParams.ikp;
        scope.profile.data = undefined;
        scope.profile.invitation = undefined;
        scope.profile.exists = undefined;

        scope.profile.registrationAddress = addressService.createEmptyAddress();
        scope.profile.actualAddress = addressService.createEmptyAddress();
        scope.profile.registratorOfficeAddress = addressService.createEmptyAddress();
        scope.cancelRegistrationRequest = function(){
            userProfileService.deleteRequest(scope.profile.data.registrationRequest.id).then(
                function(response){
                    scope.profile.data.registrationRequest = undefined;
                }
            );
        }
        scope.setDataToSend = function(field,value,translateCountryCode) {
            if (!value) {
                value = "";
            }
            if (translateCountryCode) {
                if (countryCodesMapping.countryToIdMapping[value]) {
                    if (countryCodesMapping.countryToIdMapping[value]) {
                        scope.dataToSend.basicInformation[field] = countryCodesMapping.countryToIdMapping[value];
                    }
                    else {
                        scope.dataToSend.basicInformation[field]="";
                    }
                }
            }
            else {
                scope.dataToSend.basicInformation[field] = value;
            }
        };
        scope.showUploadAvatarDialog = function() {
            $mdDialog.show({
                preserveScope: true,
                controller: 'uploadAvatarDialogController',
                controllerAs: 'dialog',
                templateUrl: '/ng/components/dialogs/uploadUserAvatarDialog.html',
                parent: angular.element(document.body),
                targetEvent: false, clickOutsideToClose: false, fullscreen: true, disableParentScroll: false,
                onComplete: function () {
                }
            }).then(function (result) {

            });
        };
        $scope.$on('avatarChanged', function (avatarCropped,data) {
            scope.profile.data.avatar = data.croppedAvatar;
        });
        scope.setDataToSendDate = function(field,value){
            if (!value) {
                scope.dataToSend.basicInformation[field] = "";
                return;
            }
            scope.dataToSend.basicInformation[field] = moment(new Date(value)).format('DD.MM.YYYY');

        };
        scope.setDataToSendRegistrator = function(field,value) {
            if (!value) {
                value = "";
            }
                scope.dataToSend.registratorData[field] = value;
        };
        scope.timeTableChange = function(timetable) {
            scope.setDataToSendRegistrator('timetable',timeTableService.convertToOldDbFieldFormat(timetable));
        };
        scope.onFactAddressChange = function(field,value,translateCountryCode){
            if (!value) {
                value = "";
            }
            if (translateCountryCode) {

                if (countryCodesMapping.countryToIdMapping[value]) {
                    scope.dataToSend.factAddress[field] = countryCodesMapping.countryToIdMapping[value];
                }
            }
            else {
                scope.dataToSend.factAddress[field] = value;
            }
        };
        scope.onRegAddressChange = function(field,value,translateCountryCode){
            if (!value) {
                value = "";
            }
            if (translateCountryCode) {
                if (countryCodesMapping.countryToIdMapping[value]) {
                    scope.dataToSend.regAddress[field] = countryCodesMapping.countryToIdMapping[value];
                }
            }
            else {
                scope.dataToSend.regAddress[field] = value;
            }
        };
        scope.onRegistratorOfficeAddressChange = function (field,value,translateCountryCode) {
            if (!value) {
                value = "";
            }
            if (translateCountryCode) {
                if (countryCodesMapping.countryToIdMapping[value]) {
                    scope.dataToSend.registratorOfficeAddress[field] = countryCodesMapping.countryToIdMapping[value];
                }
            }
            else {
                scope.dataToSend.registratorOfficeAddress[field] = value;
            }
        };
        scope.saveProfile = function() {
            userProfileService.saveProfile(scope.dataToSend).then(function (response) {
                scope.dataToSend = {};
                scope.dataToSend.basicInformation = {};
                scope.dataToSend.factAddress = {};
                scope.dataToSend.regAddress = {};
                scope.dataToSend.registratorOfficeAddress = {};
                scope.dataToSend.registratorData = {};
                broadcastService.send('dialog', {
                    type: 'info',
                    message: 'changes_applied',
                });
                scope.loadProfile();
            });
        };
        /*scope.profile.actualAddress = {
         countryName: 'Россия',
         zipCode: '410065',

         regionLabel: 'Область',
         regionShortLabel: 'обл.',
         regionName: 'Саратовская',

         districtLabel: 'Район',
         districtShortLabel: 'р-он.',
         districtName: undefined,

         cityLabel: 'Город',
         cityShortLabel: 'г.',
         cityName: 'Саратов',

         streetLabel: 'Улица',
         streetShortLabel: 'ул.',
         streetName: 'Тверская',

         buildingLabel: 'Дом/корпус',
         buildingShortLabel: 'д.',
         buildingName: '39',

         appartmentLabel: 'Квартира/офис',
         appartmentShortLabel: 'кв./оф.',
         appartmentName: '88',

         location: {
         lat: undefined,
         lng: undefined
         }
         };*/

        scope.getVerifierName = function () {
            if (scope.profile.data.verified) {
                return petrovichService.convertNameAndJoin(
                    scope.profile.data.verifierGender,
                    scope.profile.data.verifierFirstName,
                    scope.profile.data.verifierMiddleName,
                    scope.profile.data.verifierLastName,
                    'instrumental'
                );
            }

            return '';
        };

        scope.getInviterName = function () {
            if (scope.profile.invitation) {
                return petrovichService.convertNameAndJoin(
                    scope.profile.invitation.gender,
                    scope.profile.invitation.firstName,
                    scope.profile.invitation.middleName,
                    scope.profile.invitation.lastName,
                    'instrumental'
                );
            }

            return '';
        };
        scope.loadProfile = function() {
            userProfileService.loadProfile(scope.profile.ikp).then(function (response) {
                scope.profile.exists = true;
                scope.profile.data = response.data;
                if (!scope.profile.data.contact) {
                    scope.profile.data.contact =  {};
                    scope.profile.data.contact.contactGroups =  [];
                    scope.profile.data.contact.sharerStatus = "NEW";
                    scope.profile.data.contact.otherStatus = "NEW";
                }
                else {
                    if ((scope.profile.data.contact.contactGroups.length == 0) && (scope.profile.data.contact.sharerStatus == "ACCEPTED")) {
                        scope.profile.data.contact.contactGroups = [{id: 0, name: 'Список по умолчанию'}];
                    }
                }
                scope.deleteContact = function() {
                    userProfileService.deleteContact(scope.profile.data.id).then(function(response){
                        scope.profile.data.contact =  {};
                        scope.profile.data.contact.contactGroups =  [];
                        scope.profile.data.contact.sharerStatus = "NEW";
                        scope.profile.data.contact.otherStatus = "NEW";
                    });
                };
                scope.onContactGroupsChange = function() {

                };
                $timeout(function() {
                    scope.onContactGroupsChange = function() {
                        if (scope.profile.data.contact.sharerStatus == "ACCEPTED") {
                            scope.profile.data.contact.contactGroups.push({id: 0, name: 'Список по умолчанию'});
                        }
                        var groupIds = [];
                        for (var i in scope.profile.data.contact.contactGroups)  {
                            if (scope.profile.data.contact.contactGroups[i].id != 0) {
                                groupIds.push(scope.profile.data.contact.contactGroups[i].id);
                            }
                        }
                        userProfileService.addContact(scope.profile.data.id,groupIds).then(function(response){
                            scope.profile.data.contact = response.data;
                            if (scope.profile.data.contact.sharerStatus == "ACCEPTED") {
                                scope.profile.data.contact.contactGroups.push({id: 0, name: 'Список по умолчанию'});
                            }
                        });
                        /*var clear = false;
                         for (var i in scope.profile.data.contact.contactGroups)  {
                         if (scope.profile.data.contact.contactGroups[i].id === 0) {
                         clear = true;
                         }
                         }
                         if (clear) {
                         scope.profile.data.contact.contactGroups = [{id:0,name:'Список по умолчанию'}];
                         }*/
                    };
                }, 0);
                scope.showAllMemberCommunities = function() {
                    scope.limitMemberCommunities = scope.profile.data.communitiesMember.length;
                };
                scope.showAllCreatedCommunities = function() {
                    scope.limitCreatedCommunities = scope.profile.data.communitiesCreated.length;
                };
                scope.passportCitizenshipSettings = response.data.passportCitizenshipSettings;
                if (scope.profile.data.citizenship){
                    if (countryCodesMapping.idToCountryMapping[scope.profile.data.citizenship]) {
                        scope.profile.data.citizenship = countryCodesMapping.idToCountryMapping[scope.profile.data.citizenship];
                    }
                    else {
                        scope.profile.data.citizenship = "";
                    }
                }
                if (scope.profile.data.registratorLevel != null){
                    scope.registrator = true;
                }
                if (scope.profile.data.dateOfBirth) {
                    scope.profile.data.dateOfBirth = moment(scope.profile.data.dateOfBirth, "DD.MM.YYYY").toDate();
                }
                if (scope.profile.data.passportIssueDate) {
                    scope.profile.data.passportIssueDate = moment(scope.profile.data.passportIssueDate, "DD.MM.YYYY").toDate();
                }
                if (scope.profile.data.passportExpirationDate) {
                    scope.profile.data.passportExpirationDate = moment(scope.profile.data.passportExpirationDate,"DD.MM.YYYY").toDate();
                }
                if (scope.profile.data.passportExpiredDate) {
                    scope.profile.data.passportExpiredDate = moment(scope.profile.data.passportExpiredDate,"DD.MM.YYYY").toDate();
                }
                if (scope.profile.data.timetable) {
                    scope.profile.data.timetable = timeTableService.convertFromOldDbFieldFormat(scope.profile.data.timetable);
                }
                else {
                    scope.profile.data.timetable = [[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]];
                }
                for (var i = 0; i < scope.profile.data.fields.length; i++) {
                    //console.log(scope.profile.data.fields[i].name, scope.profile.data.fields[i].internalName, scope.profile.data.fields[i].value);
                    if (scope.profile.data.fields[i].value) switch (scope.profile.data.fields[i].internalName) {
                        case 'FPOSTAL_CODE':
                            scope.profile.actualAddress.zipCode = scope.profile.data.fields[i].value;
                            break;
                        case 'FCOUNTRY_CL':
                            if (countryCodesMapping.idToCountryMapping[scope.profile.data.fields[i].value]) {
                                scope.profile.actualAddress.countryName = countryCodesMapping.idToCountryMapping[scope.profile.data.fields[i].value];
                            }
                            break;
                        case 'FREGION_RL':
                            scope.profile.actualAddress.regionName = scope.profile.data.fields[i].value;
                            break;
                        case 'FREGION_RL_DESCRIPTION':
                            scope.profile.actualAddress.regionLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FAREA_AL':
                            scope.profile.actualAddress.districtName = scope.profile.data.fields[i].value;
                            break;
                        case 'FAREA_AL_DESCRIPTION':
                            scope.profile.actualAddress.districtLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FDISTRICT_DESCRIPTION_SHORT':
                            scope.profile.actualAddress.districtShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FCITY_TL':
                            scope.profile.actualAddress.cityName = scope.profile.data.fields[i].value;
                            break;
                        case 'FCITY_TL_DESCRIPTION':
                            scope.profile.actualAddress.cityLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FCITY_DESCRIPTION_SHORT':
                            scope.profile.actualAddress.cityShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FSTREET':
                            scope.profile.actualAddress.streetName = scope.profile.data.fields[i].value;
                            break;
                        case 'FSTREET_DESCRIPTION':
                            scope.profile.actualAddress.streetLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FSTREET_DESCRIPTION_SHORT':
                            scope.profile.actualAddress.streetShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FHOUSE_DESCRIPTION':
                            scope.profile.actualAddress.buildingLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'FHOUSE':
                            scope.profile.actualAddress.buildingName = scope.profile.data.fields[i].value;
                            break;
                        case 'FROOM_DESCRIPTION':
                            scope.profile.actualAddress.appartmentLabel = scope.profile.data.fields[i].value;

                            if (scope.profile.actualAddress.appartmentLabel.toLowerCase() === 'квартира') {
                                scope.profile.actualAddress.appartmentShortLabel = 'кв.'
                            } else {
                                scope.profile.actualAddress.appartmentShortLabel = 'оф.'
                            }
                            break;
                        case 'FROOM':
                            scope.profile.actualAddress.appartmentName = scope.profile.data.fields[i].value;
                            break;
                        case 'F_GEO_POSITION':
                            var location = scope.profile.data.fields[i].value.split(',');
                            scope.profile.actualAddress.location.lat = location[0];
                            scope.profile.actualAddress.location.lng = location[1];
                            break;


                        case 'POSTAL_CODE':
                            scope.profile.registrationAddress.zipCode = scope.profile.data.fields[i].value;
                            break;
                        case 'COUNTRY_CL':
                            if (countryCodesMapping.idToCountryMapping[scope.profile.data.fields[i].value]) {
                                scope.profile.registrationAddress.countryName = countryCodesMapping.idToCountryMapping[scope.profile.data.fields[i].value];
                            }
                            break;
                        case 'REGION_RL':
                            scope.profile.registrationAddress.regionName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGION_RL_DESCRIPTION':
                            scope.profile.registrationAddress.regionLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'AREA_AL':
                            scope.profile.registrationAddress.districtName = scope.profile.data.fields[i].value;
                            break;
                        case 'AREA_AL_DESCRIPTION':
                            scope.profile.registrationAddress.districtLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'DISTRICT_DESCRIPTION_SHORT':
                            scope.profile.registrationAddress.districtShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'CITY_TL':
                            scope.profile.registrationAddress.cityName = scope.profile.data.fields[i].value;
                            break;
                        case 'CITY_TL_DESCRIPTION':
                            scope.profile.registrationAddress.cityLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'CITY_DESCRIPTION_SHORT':
                            scope.profile.registrationAddress.cityShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'STREET':
                            scope.profile.registrationAddress.streetName = scope.profile.data.fields[i].value;
                            break;
                        case 'STREET_DESCRIPTION':
                            scope.profile.registrationAddress.streetLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'STREET_DESCRIPTION_SHORT':
                            scope.profile.registrationAddress.streetShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'HOUSE_DESCRIPTION':
                            scope.profile.registrationAddress.buildingLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'HOUSE':
                            scope.profile.registrationAddress.buildingName = scope.profile.data.fields[i].value;
                            break;
                        case 'ROOM_DESCRIPTION':
                            scope.profile.registrationAddress.appartmentLabel = scope.profile.data.fields[i].value;

                            if (scope.profile.registrationAddress.appartmentLabel.toLowerCase() === 'квартира') {
                                scope.profile.registrationAddress.appartmentShortLabel = 'кв.'
                            } else {
                                scope.profile.registrationAddress.appartmentShortLabel = 'оф.'
                            }
                            break;
                        case 'ROOM':
                            scope.profile.registrationAddress.appartmentName = scope.profile.data.fields[i].value;
                            break;
                        case 'GEO_POSITION':
                            var location = scope.profile.data.fields[i].value.split(',');
                            scope.profile.registrationAddress.location.lat = location[0];
                            scope.profile.registrationAddress.location.lng = location[1];
                            break;


                        case 'REGISTRATOR_OFFICE_POSTAL_CODE':
                            scope.profile.registratorOfficeAddress.zipCode = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_COUNTRY':
                            if (countryCodesMapping.idToCountryMapping[scope.profile.data.fields[i].value]) {
                                scope.profile.registratorOfficeAddress.countryName = countryCodesMapping.idToCountryMapping[scope.profile.data.fields[i].value];
                            }
                            break;
                        case 'REGISTRATOR_OFFICE_REGION':
                            scope.profile.registratorOfficeAddress.regionName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_REGION_DESCRIPTION':
                            scope.profile.registratorOfficeAddress.regionLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_DISTRICT':
                            scope.profile.registratorOfficeAddress.districtName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION':
                            scope.profile.registratorOfficeAddress.districtLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_DISTRICT_DESCRIPTION_SHORT':
                            scope.profile.registratorOfficeAddress.districtShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_CITY':
                            scope.profile.registratorOfficeAddress.cityName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_CITY_DESCRIPTION':
                            scope.profile.registratorOfficeAddress.cityLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_CITY_DESCRIPTION_SHORT':
                            scope.profile.registratorOfficeAddress.cityShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_STREET':
                            scope.profile.registratorOfficeAddress.streetName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_STREET_DESCRIPTION':
                            scope.profile.registratorOfficeAddress.streetLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_STREET_DESCRIPTION_SHORT':
                            scope.profile.registratorOfficeAddress.streetShortLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_BUILDING_DESCRIPTION':
                            scope.profile.registratorOfficeAddress.buildingLabel = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_BUILDING':
                            scope.profile.registratorOfficeAddress.buildingName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_ROOM_DESCRIPTION':
                            scope.profile.registratorOfficeAddress.appartmentLabel = scope.profile.data.fields[i].value;

                            if (scope.profile.registratorOfficeAddress.appartmentLabel.toLowerCase() === 'квартира') {
                                scope.profile.registratorOfficeAddress.appartmentShortLabel = 'кв.'
                            } else {
                                scope.profile.registratorOfficeAddress.appartmentShortLabel = 'оф.'
                            }
                            break;
                        case 'REGISTRATOR_OFFICE_ROOM':
                            scope.profile.registratorOfficeAddress.appartmentName = scope.profile.data.fields[i].value;
                            break;
                        case 'REGISTRATOR_OFFICE_GEO_POSITION':
                            var location = scope.profile.data.fields[i].value.split(',');
                            scope.profile.registratorOfficeAddress.location.lat = location[0];
                            scope.profile.registratorOfficeAddress.location.lng = location[1];
                            break;
                    }
                }

                userProfileService.loadInvitation(scope.profile.data.id).then(function (response) {
                    scope.profile.invitation = response.data;
                });

                /*$timeout(function () {
                 tinymce.init({
                 skin: '../../../../lib/tinymce/custom',
                 selector: '#mytextarea',
                 inline: true,

                 plugins: [
                 "image imagetools",
                 "advlist autolink lists link charmap print preview anchor save",
                 "searchreplace visualblocks code fullscreen",
                 "insertdatetime media table contextmenu paste"
                 ],

                 automatic_uploads: false,

                 image_advtab: true,

                 file_browser_callback: function (field_name, url, type, win) {
                 if (type == 'image') {
                 $('#myimage').change(function () {
                 var file = this.files[0];

                 if (file) {
                 var reader = new FileReader();

                 reader.addEventListener("load", function () {
                 win.document.getElementById(field_name).value = reader.result;
                 }, false);

                 reader.readAsDataURL(file);
                 }
                 });

                 $('#myimage').click();
                 }
                 },

                 file_browser_callback_types: 'image', // 'file image media'

                 elementpath: true,

                 menu: {
                 file: {title: 'File', items: 'newdocument | print'},
                 edit: {title: 'Edit', items: 'undo redo | cut copy paste pastetext | selectall'},
                 insert: {title: 'Insert', items: 'link media | template hr'},
                 view: {title: 'View', items: 'visualaid'},
                 format: {
                 title: 'Format',
                 items: 'bold italic underline strikethrough superscript subscript | formats | removeformat'
                 },
                 table: {title: 'Table', items: 'inserttable tableprops deletetable | cell row column'},
                 tools: {title: 'Tools', items: 'spellchecker code'}
                 },

                 menubar: 'file edit insert view format table tools',

                 toolbar: 'save | insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',

                 save_onsavecallback: function () {
                 var editor = tinymce.get($('#mytextarea').attr('id'));

                 editor.uploadImages(function (success) {
                 // TODO save to server

                 var content = editor.getContent();
                 console.log(content);
                 });
                 },

                 images_upload_handler: function (blobInfo, success, failure) {
                 alert('ща загрузим картинку на сервер');
                 success('https://images.blagosfera.su//images/VGHF3HUFH5J/MRMOHVTOAD.jpg');
                 }
                 });
                 });*/

            }, function (response) {
                scope.profile.exists = false;
            });
        };
        scope.loadProfile();

    });

    return app;
});