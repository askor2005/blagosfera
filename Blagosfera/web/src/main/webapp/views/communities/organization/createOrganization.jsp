<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles"%>
<%@ taglib prefix="radom" uri="http://kabinet2.ra-dom.ru/radomTlds" %>
<jsp:include page="../../fields/addressFields.jsp" />
<jsp:include page="../../fields/fileField.jsp" />
<style>
    .infoBlock {
        padding: 10px 15px;
        border-radius: 3px;
        border: 1px solid #ddd;
        color: #333;
        background-color: #f5f5f5;
    }
    #organizationCommonInfoBlock {
        display: none;
    }
</style>

<script type="text/javascript">
    var currentOrganizationImpl = null;
    var createOrganizationTempData = null;
    var currentAssociationFormCode = null;
    function handleTemplateByCode(organizationFormCode, createOrganizationTempData, recursionCount) {console.log("asd");
        var formData = {};
        if (createOrganizationTempData != null && organizationFormCode != null && createOrganizationTempData.associationForm == organizationFormCode) {
            formData = createOrganizationTempData.data;
        }

        currentAssociationFormCode = null;
        if (recursionCount == null) {
            recursionCount = 0;
        }
        if (organizationFormCode == null || organizationFormCode == "null") {
            organizationFormCode = "default";
        }
        $("#createOrganizationButton").prop("disabled", true);

        $("#organizationFormFields").empty();
        $("#organizationCommonInfoBlock").hide();
        currentOrganizationImpl = null;

        require(
                [
                    "text!/templates/communities/formstemplates/" + organizationFormCode + ".html"
                ],
                function () {
                    require(
                            [
                                "text!/templates/communities/formstemplates/" + organizationFormCode + ".html",
                                "community/organizationCreate/" + organizationFormCode
                            ],
                            function (organizationFormTemplate, organizationImpl) {
                                if (organizationFormCode != "default") {
                                    $("#createOrganizationButton").prop("disabled", false);
                                }
                                currentAssociationFormCode = organizationFormCode;
                                currentOrganizationImpl = organizationImpl;
                                $("#organizationFormFields").append(organizationFormTemplate);
                                currentOrganizationImpl.init(formData);
                                $("#organizationCommonInfoBlock").fadeIn();
                            },
                            function(error){
                                console.log(error);
                            }
                    );
                },
                function(error){
                    console.log(error);
                    if (recursionCount == 0) {
                        // Загружаем шаблон для не реализованных
                        handleTemplateByCode("default", createOrganizationTempData, recursionCount + 1);
                    }
                }
        );
    }

    function getTempData(callBack, onErrorRequest) {
        $.radomJsonPost(
            "/organization/get_temp_data.json",
            {},
            callBack,
            onErrorRequest
        );
    }

    function saveTempData(requestData, callBack, onErrorRequest) {
        $.radomJsonPost(
            "/organization/save_temp_data.json",
            JSON.stringify(requestData),
            callBack,
            onErrorRequest,
            {
                contentType : 'application/json'
            }
        );
    }

    function clearTempData(callBack, onErrorRequest) {
        $.radomJsonPost(
                "/organization/clear_temp_data.json",
                {},
                callBack,
                onErrorRequest
        );
    }

    function showSaveTempDataWaiter() {
        $("#saveOrganizationDataInProcess").show();
        $("#onSuccessSaveTempData").hide();
        $("#onErrorSaveTempData").hide();
    }

    function onSaveTempData() {
        $("#saveOrganizationDataInProcess").hide();
        var date = new Date();
        var formattedDate = date.format("HH:MM:ss");
        $("#onSuccessSaveTempData").show();
        $("#onErrorSaveTempData").hide();
        $("#createOrganizationFormSaveTimeBlock").css("visibility", "visible");
        $("#createOrganizationFormSaveTime").html(formattedDate);
    }

    function onErrorSaveTempData() {
        $("#saveOrganizationDataInProcess").hide();
        $("#onSuccessSaveTempData").hide();
        $("#onErrorSaveTempData").show();
        $("#createOrganizationFormSaveTimeBlock").css("visibility", "visible");
    }

    function handleSaveTempData() {
        if (currentOrganizationImpl != null && currentOrganizationImpl.getTempData != null) {
            showSaveTempDataWaiter();
            setTimeout(function(){
                var createOrganizationFormData = {
                    associationForm : currentAssociationFormCode,
                    data : currentOrganizationImpl.getTempData()
                };
                saveTempData(createOrganizationFormData, onSaveTempData, onErrorSaveTempData);
                createOrganizationTempData = createOrganizationFormData;
            }, 10);
        }
    }

    function handleClearTempData() {
        if (currentOrganizationImpl != null) {
            showSaveTempDataWaiter();
            setTimeout(function(){
                var createOrganizationFormData = {
                    associationForm : currentAssociationFormCode,
                    data : null
                };
                currentOrganizationImpl.init(null);
                saveTempData(createOrganizationFormData, onSaveTempData, onErrorSaveTempData);
                createOrganizationTempData = createOrganizationFormData;
            }, 10);
        }
    }

    function initSaveTempData() {
        setInterval(function(){
            handleSaveTempData();
        }, 1000 * 60);
    }
    
    function initAssociationForm() {
        // Инициализация компонентов со списками
        // Форма объединения
        RameraListEditorModule.init(
                $("#associationForm"),
                {
                    labelClasses: ["checkbox-inline"],
                    labelStyle: "margin-left: 10px;",
                    selectClasses: ["form-control"]
                },
                function (event, data) {
                    if (event == RameraListEditorEvents.VALUE_CHANGED) {
                        handleTemplateByCode(data.code, createOrganizationTempData);
                    }
                }
        );
        //handleTemplateByCode("community_cooperative_society", createOrganizationTempData);
    }

	$(document).ready(function() {
        initSaveTempData();
        getTempData(function(response){
            createOrganizationTempData = response;
            initAssociationForm();
        }, function(){
            initAssociationForm();
        });

        // Создание запроса
        $("#createOrganizationButton").click(function(){
            if (currentOrganizationImpl == null) {
                bootbox.alert("Не выбрана организационно-правовая фома юр лица или нет реализации для данной организационно-правовой фомы!");
                return false;
            }
            currentOrganizationImpl.createRequest(function(){
                clearTempData();
            });
        });
        //
        $("#saveTempDataCreateOrganizationButton").click(function(){
            handleSaveTempData();
        });
        $("#clearTempDataCreateOrganizationButton").click(function(){
            handleClearTempData();
        });
    });
</script>

<h1>Создать юридическое лицо</h1>
<hr/>

<div>
    <div class="form-group" id="associationFormBlock">
        <label>Организационно-правовая форма</label>
        <div id="associationForm" rameraListEditorName="community_association_forms_groups"></div>
        <span class="help-block help-block-info">Укажите одну из доступных организационно-правовых форм юридического лица.</span>
        <span style="display: none;" class="help-block help-block-error"></span>
    </div>
    <div id="organizationFormFields"></div>

    <div id="organizationCommonInfoBlock">
        <div class="form-group infoBlock">
            Если Вы уже заполнили все поля данных, нажмите кнопку "Начать процедуру создания юридического лица".
            После нажатия на данную кнопку всем пользователям, выбранных Вами в качестве учредителей,
            будут отправлены информационные листы с данными о создаваемом юридическом лице.
            Если все предполагаемые учредители ответят согласием на создание данного юридического лица,
            Вы будете оповещены об этом и сможете приступить к организации Учредительного собрания и создания Учредительных документов для регистрации в Налоговой.<br/>
            После нажатия на кнопку "Начать процедуру создания юридического лица"
            Вам будет необходимо подтвердить ваше действие с помощью электронно-цифровой подписи,
            создаваемой сканированием отпечатка вашего пальца.
        </div>

        <div class="form-group" style="text-align: center;">
            <button id="createOrganizationButton" class="btn btn-primary" >Начать процедуру создания юридического лица</button>
        </div>
    </div>
</div>