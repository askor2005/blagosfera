/**
 * Created by aotts on 27.10.2015.
 * Редактор голосований в собрании
 */
define([
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "utils/utils",
    "backbone/components/ListView",
    "./MulticomplexValueModalEditor",
    "backbone",
    "jquery",
    "text!./template/VotingsTemplateModal.html",
    "noty",
    "typeahead"
], function (CollectionBinder, TableRowView, utils, ListView, MulticomplexValueModalEditor, Backbone, $, modalTemplate, noty) {
    var votingsRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    function(data){
                        return "<a href='#' class='editVoting' subject='" + data.subject + "'>" + data.subject + "</a>";
                    },
                    function(data) {
                        return "<a href='#' class='btn btn-default btn-sm votingUpBtn' item_index='" + data.index + "'>" +
                            "       <span class='glyphicon glyphicon-chevron-up'></span>" +
                            "   </a>";
                    },
                    function(data) {
                        return "<a href='#' class='btn btn-default btn-sm votingDownBtn' item_index='" + data.index + "'>" +
                            "       <span class='glyphicon glyphicon-chevron-down'></span>" +
                            "   </a>";
                    },
                    function(data) {
                        return "<a href='#' class='btn btn-danger btn-sm minusBtn' subject='" + data.subject + "'>" +
                            "       <span class='glyphicon glyphicon-minus'></span>" +
                            "   </a>";
                    }
                ]
            });
        });
    };
    var votingItemsRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    function(data){
                        return "<a href='#' class='editVotingItem' item_value='" + data.value + "'>" + data.value + "</a>"+
                            "   <input type='text' class='form-control editVotingItemInput' style='display: none;' value='" + data.value + "' />"+
                            "   <a class='glyphicon glyphicon-ok acceptVotingItem' style='display: none;' href='#' title='Применить'></a>";
                    },
                    function(data) {
                        return "<a href='#' class='btn btn-danger btn-sm votingItemMinusBtn' item_value='" + data.value + "'>" +
                            "       <span class='glyphicon glyphicon-minus'></span>" +
                            "   </a>";
                    }
                ]
            });
        });
    };
    var votingButtonsRowFactory = function () {
        return new CollectionBinder.ViewManagerFactory(function (model) {
            return new TableRowView({
                model: model,
                testOnChange: false,
                rowConfig: [
                    function(data){
                        return "<a href='#' class='editVotingButton' button_text='" + data.buttonText + "'>" + data.buttonText + "</a>";
                    },
                    function(data) {
                        return "<a href='#' class='btn btn-danger btn-sm votingButtonMinusBtn' button_text='" + data.buttonText + "'>" +
                            "       <span class='glyphicon glyphicon-minus'></span>" +
                            "   </a>";
                    }
                ]
            });
        });
    };


    var ModalEditor = Backbone.View.extend({
        tagName: "div",
        className: "modal",
        template: _.template(modalTemplate, {variable: "data"}),
        model: null,
        votingList: null,
        votingItemsList: null,
        votingButtonsList : null,
        votingsModel : [],
        currentVotingItemsModel : [],
        currentVotingButtonsModel : [],
        editableVotingModel : null,
        editMode : false,

        editButtonContentModel : null, // Открытая для редактирования кнопка с контентом

        currentEditVotingItem : null, //

        // Элементы формы
        gui : {
            subject : null, // Наименование голосования (input)
            description : null, // Описание голосования (tinymce)
            votingCreateCondition : null, // Условие создания голосования (input)
            votingType : null, // Тип голосования (select)
            isVoteCancellable : null, // Возможность переголосовать (checkbox)
            isVoteCommentsAllowed : null, // Вомозность добавлять комменты (checkbox)
            isVisible : null, // Видимость голосования (checkbox)
            addAbstain : null, // Добавлять "Воздержаться" (checkbox)
            //addMine : null, // Добавлять варианты голосования вручную (radio)
            //votingItemsFromVar : null, // Брать варианты голосования из переменной (radio)
            votingItemsSource : null,

            votingItemsVar : null, // Наименование переменной вариантов голосования (input)
            votingItemName : null, // Наименование варианта голосования для добавления (input)
            addVotingItem : null, // Добавление варианта голосования (button)
            addVoting : null, // Добавление голосования
            otherVotingSource : null,
            chooseVotingItemsTypeBlock : null, // Блок с выбором вида вариантов голосования (переменная или вручную занести)
            votingItemsVarBlock : null, // Блок с переменной вариантов голосования
            addMineBlock : null, // Блок с доабвлением вариантов голосований вручную
            otherVotingSourceBlock : null,

            isFailOnContraResultBlock : null, // Блок для доп. параметра при голосовании ЗА Против Воздержался
            isFailOnContraResult : null, // Доп. параметр при голосовании ЗА Против Воздержался (checkbox)
            //isCandidateProposeBlock : null,
            //isCandidatePropose : null, // Параметр голосования ЗА\Против - для выдвижения текущего пользователя в кандидаты следующих выборов (checkbox)
            // или выборов для создания кандидатов для следующих выборов
            minSelectionCountBlock : null,
            maxSelectionCountBlock : null, // Блок для доп. параметра при голосовании множественным выбором
            minSelectionCount : null,
            maxSelectionCount : null, // Количество выбираемых вариантов

            minWinnersCountBlock : null,
            maxWinnersCountBlock : null,
            minWinnersCount : null,
            maxWinnersCount : null,

            isMultipleWinners : null, // Флаг - голосование может иметь несколько победителей
            percentForWin : null, // Минимальный процент для победы в голосовании с множественным выбором (input)

            //isVariantProposeBlock : null, // Блок с флагом - создавать интервью в котором будут предложены варианты для следующего голосования
            //isVariantPropose : null, // Флаг - создавать интервью в котором будут предложены варианты для следующего голосования

            votingButtonName : null, // Текст кнопки для модального окна
            votingButtonContent : null, // Контент модального окна (tinymce)
            votingButtonModal : null, // Модальное окно для редактирования или добавления кнопки с контентом

            votingWinnerText : null, // Текст, который отображается при победе варианта голосования

            sentence : null,
            successDecree : null,
            failDecree : null
        },

        events: {
            "click .accept": utils.clickEventWrapper("saveValue"),
            "click #addVoting": utils.clickEventWrapper("addVoting"),
            "click #addVotingItem": utils.clickEventWrapper("addVotingItem"),

            "click #addVotingButtonContentButton": utils.clickEventWrapper("addVotingButton"),
            "click #saveVotingButtonContentButton": utils.clickEventWrapper("saveVotingButton"),
            "click #cancelSaveVotingButtonContentButton": utils.clickEventWrapper("cancelSaveVotingButton")
        },

        // Запускается 1 раз при инициализации компонента
        initialize: function () {
            this.votingList = new ListView({
                childFactory: votingsRowFactory()
            });
            this.votingItemsList = new ListView({
                childFactory: votingItemsRowFactory()
            });
            this.votingButtonsList = new ListView({
                childFactory: votingButtonsRowFactory()
            });


            this.bindCurrentState();
        },

        // Отрабатывает каждый раз при отображении компонента
        render: function () {
            this.$el.html(this.template(this.model.omit("property", "shape")));
            this.initGuiControls();

            var $votingsTableBody = this.$("div#votings tbody");
            var $votingItemsTableBody = this.$("div#votingItems tbody");
            var $votingButtonsContentsTableBody = this.$("div#votingButtonsContents tbody");
            this.votingList.setElement($votingsTableBody).render();
            this.votingItemsList.setElement($votingItemsTableBody).render();
            this.votingButtonsList.setElement($votingButtonsContentsTableBody).render();
            this.applyModelValue();
            return this;
        },

        // ??
        remove: function () {
            this.votingList.remove();
            this.votingItemsList.remove();
            this.votingButtonsList.remove();
            Backbone.View.prototype.remove.call(this);
        },

        // Инициализация обработчиков
        bindCurrentState: function () {
            var self = this;
            this.listenTo(this.model, "change:value change:refresh", this.applyModelValue);
            this.applyModelValue();
            /*this.$el.on("show.bs.modal", function(){
                _.bind(self.applyModelValue, self)
            });*/
            this.bindEvents();
        },

        sortVotingsInModel: function(votingIndex, direction) {
            if (direction == "up") {
                if (votingIndex == 0) {
                    return;
                }
                var element1 = this.votingsModel[votingIndex - 1];
                var element2 = this.votingsModel[votingIndex];

                this.votingsModel[votingIndex - 1] = element2;
                this.votingsModel[votingIndex] = element1;
            } else if (direction == "down") {
                if (votingIndex == this.votingsModel.length -1) {
                    return;
                }
                var element1 = this.votingsModel[votingIndex + 1];
                var element2 = this.votingsModel[votingIndex];

                this.votingsModel[votingIndex + 1] = element2;
                this.votingsModel[votingIndex] = element1;
            }
            this.initIndexField();
            this.votingList.getCollection().reset(this.votingsModel);
        },

        initIndexField: function() {
            for (var i=0; i < this.votingsModel.length; i++) {
                var element = this.votingsModel[i];
                element["index"] = i;
            }
        },

        // Инициализация обработчиков в таблице голосований
        bindEvents: function(){
            var self = this;
            this.listenTo(this.votingList.collectionBinder, "elCreated", function (model, view) {
                view.$el.on("click", "a.minusBtn", utils.clickEventWrapper(function () {
                    if (self.editMode) {
                        bootbox.alert("Включен режим редактирования. Необходимо сначала сохранить изменения.");
                        return false;
                    }
                    utils.removeItemFromArray(self.votingsModel, "subject", model.get("subject"));
                    self.initIndexField();
                    self.votingList.getCollection().remove(model);
                }, self));

                view.$el.on("click", "a.votingUpBtn", utils.clickEventWrapper(function () {
                    if (self.editMode) {
                        bootbox.alert("Включен режим редактирования. Необходимо сначала сохранить изменения.");
                        return false;
                    }
                    self.sortVotingsInModel(model.get("index"), "up");
                }, self));
                view.$el.on("click", "a.votingDownBtn", utils.clickEventWrapper(function () {
                    if (self.editMode) {
                        bootbox.alert("Включен режим редактирования. Необходимо сначала сохранить изменения.");
                        return false;
                    }
                    self.sortVotingsInModel(model.get("index"), "down");
                }, self));


                view.$el.on("click", "a.editVoting", utils.clickEventWrapper(function () {
                    var votingModel = utils.findObjectInArray(self.votingsModel, "subject", model.get("subject"));
                    if (votingModel != null) {
                        self.initEditableMode(votingModel);
                    }
                }, self));
            });

            // События строки таблицы с элементами голосования
            this.listenTo(this.votingItemsList.collectionBinder, "elCreated", function (model, view) {
                view.$el.on("click", "a.votingItemMinusBtn", utils.clickEventWrapper(function () {
                    utils.removeItemFromArray(self.currentVotingItemsModel, "value", model.get("value"));
                    self.votingItemsList.getCollection().remove(model);
                }, self));
                view.$el.on("click", "a.editVotingItem", utils.clickEventWrapper(function () {
                    this.$el.find(".editVotingItem").hide();
                    this.$el.find(".editVotingItemInput").show();
                    this.$el.find(".acceptVotingItem").show();
                    self.currentEditVotingItem = utils.findObjectInArray(self.currentVotingItemsModel, "value", model.get("value"));
                }, view));
                view.$el.on("click", "a.acceptVotingItem", utils.clickEventWrapper(function () {
                    var value = this.$el.find(".editVotingItemInput").val();
                    if (value == null || value == "") {
                        bootbox.alert("Значение не может быть пустым");
                        return false;
                    }
                    self.currentEditVotingItem.value = value;
                    self.votingItemsList.getCollection().reset(self.currentVotingItemsModel);

                    this.$el.find(".editVotingItem").show();
                    this.$el.find(".editVotingItemInput").hide();
                    this.$el.find(".acceptVotingItem").hide();
                }, view));
            });

            // События кнопок с контентом
            this.listenTo(this.votingButtonsList.collectionBinder, "elCreated", function (model, view) {
                view.$el.on("click", "a.votingButtonMinusBtn", utils.clickEventWrapper(function () {
                    utils.removeItemFromArray(self.currentVotingButtonsModel, "value", model.get("value"));
                    self.votingButtonsList.getCollection().remove(model);
                }, self));
                view.$el.on("click", "a.editVotingButton", utils.clickEventWrapper(function () {
                    /*this.$el.find(".editVotingItem").hide();
                    this.$el.find(".editVotingItemInput").show();
                    this.$el.find(".acceptVotingItem").show();
                    self.currentEditVotingItem = utils.findObjectInArray(self.currentVotingItemsModel, "value", model.get("value"));*/
                    // Открыть модальное окно, заполнить его данными
                    //self.editButtonContentModel = {buttonText : model.get("buttonText"), content : model.get("content")};
                    for (var index in self.currentVotingButtonsModel) {
                        var buttonTextModel = self.currentVotingButtonsModel[index];
                        if (buttonTextModel.buttonText == model.get("buttonText")) {
                            self.editButtonContentModel = buttonTextModel;
                            break;
                        }
                    }

                    self.addVotingButton(null, self.editButtonContentModel);
                    /*self.gui.votingButtonModal.modal("show");

                    self.gui.votingButtonName.val("qwdqw");
                    self.gui.votingButtonContent.setContent("asd");*/

                }, view));
            });
        },

        // Применить модель к отображению
        applyModelValue: function () {
            var value = this.model.get("value");
            // Кастим value в список голосований
            this.editableVotingModel = null;
            this.votingsModel = [];
            if (value != null && value != "") {
                this.votingsModel = value.data;
                this.initIndexField();
            }
            this.votingList.getCollection().reset(this.votingsModel);
            this.votingItemsList.getCollection().reset([]);
            this.votingButtonsList.getCollection().reset([]);
        },

        saveValue: function () {
            this.model.set("value", {"data" : this.votingsModel});
        },

        // Отобразить блок с выбором вида вариантов голосования (статичные либо через переменную)
        showChooseVotingItemsTypeBlock: function() {
            this.gui.chooseVotingItemsTypeBlock.show();
            this.votingItemsList.getCollection().reset(this.currentVotingItemsModel);
            var selectFromType = this.editableVotingModel == null || this.editableVotingModel.selectFromType == null ? "addMine" : this.editableVotingModel.selectFromType;
            this.gui.votingItemsSource.filter("[id=" + selectFromType + "]").prop("checked", true).trigger("change");
        },

        // Скрыть блок с выбором вида вариантов голосования (статичные либо через переменную)
        hideChooseVotingItemsTypeBlock: function() {
            this.gui.chooseVotingItemsTypeBlock.hide();
            this.currentVotingItemsModel = [];
            this.votingItemsList.getCollection().reset(this.currentVotingItemsModel);
            this.gui.votingItemsVar.val("");

            this.gui.addMineBlock.hide();
            this.gui.otherVotingSourceBlock.hide();
            this.gui.votingItemsVarBlock.hide();
        },

        initVotingTypeBlock: function(votingType){
            this.gui.isFailOnContraResultBlock.hide();
            //this.gui.isCandidateProposeBlock.hide();
            this.gui.minSelectionCountBlock.hide();
            this.gui.maxSelectionCountBlock.hide();

            this.gui.minWinnersCountBlock.hide();
            this.gui.maxWinnersCountBlock.hide();

            this.gui.isFailOnContraResult.prop("checked", false);
            //this.gui.isCandidatePropose.prop("checked", false);
            this.gui.minSelectionCount.val("1");
            this.gui.maxSelectionCount.val("1");

            this.gui.minWinnersCount.val("1");
            this.gui.maxWinnersCount.val("1");

            //this.gui.percentForWin.closest(".row").hide();
            this.gui.percentForWin.val("51");
            this.gui.isMultipleWinners.prop("checked", false);
            var multipleWinnersBlock = this.gui.isMultipleWinners.closest(".row");
            multipleWinnersBlock.hide();

            //this.gui.isVariantProposeBlock.hide();
            //this.gui.isVariantPropose.prop("checked", false);

            switch (votingType) {
                case "PRO_CONTRA":
                    this.hideChooseVotingItemsTypeBlock();
                    this.gui.isFailOnContraResultBlock.show();
                    //this.gui.isCandidateProposeBlock.show();
                    break;
                case "CANDIDATE":
                    this.showChooseVotingItemsTypeBlock();
                    this.gui.minSelectionCountBlock.show();
                    this.gui.maxSelectionCountBlock.show();

                    this.gui.minWinnersCountBlock.show();
                    this.gui.maxWinnersCountBlock.show();
                    multipleWinnersBlock.show();
                    break;
                case "INTERVIEW":
                    this.hideChooseVotingItemsTypeBlock();
                    //this.gui.maxSelectionCountBlock.show();
                    //multipleWinnersBlock.show();
                    //this.gui.isVariantProposeBlock.show();
                    break;
                case "SINGLE_SELECTION":
                    this.showChooseVotingItemsTypeBlock();
                    multipleWinnersBlock.show();
                    break;
                case "MULTIPLE_SELECTION":
                    this.showChooseVotingItemsTypeBlock();
                    this.gui.minSelectionCountBlock.show();
                    this.gui.maxSelectionCountBlock.show();

                    this.gui.minWinnersCountBlock.show();
                    this.gui.maxWinnersCountBlock.show();
                    multipleWinnersBlock.show();
                    break;
                default:
                    this.hideChooseVotingItemsTypeBlock();
                    break;
            }
        },

        initOtherVotingSelect: function(votingTypes, multipleWinners, selectedIndex, currentVotingIndex) {
            this.gui.otherVotingSource.empty();
            for (var i in this.votingsModel) {
                var voting = this.votingsModel[i];
                if (votingTypes.indexOf(voting.votingType) > -1 && 
                    (voting.isMultipleWinners == true && multipleWinners == true || multipleWinners != true) &&
                    (currentVotingIndex > voting.index || currentVotingIndex == -1)
                ) {
                    this.gui.otherVotingSource.append("<option value='" + voting.index + "'>" + voting.subject + "</option>");
                }
            }
            this.gui.otherVotingSource.selectpicker("refresh");
            this.gui.otherVotingSource.selectpicker("val", selectedIndex);
        },

        // Инициализация ui элементов
        initGuiControls: function(){
            this.gui.subject = this.$("#votingSubject");
            this.gui.votingCreateCondition = this.$("#votingCreateCondition");
            this.gui.votingType = this.$("#votingType");
            this.gui.isVoteCancellable = this.$("#isVoteCancellable");
            this.gui.isVoteCommentsAllowed = this.$("#isVoteCommentsAllowed");
            this.gui.isVisible = this.$("#isVisible");
            this.gui.addAbstain = this.$("#addAbstain");
            //this.gui.addMine = this.$("#addMine");
            //this.gui.votingItemsFromVar = this.$("#votingItemsFromVar");
            this.gui.otherVotingSource = this.$("#otherVotingSource");
            this.gui.votingItemsSource = this.$("[name=votingItemsSource]");
            this.gui.votingItemsVar = this.$("#votingItemsVar");
            this.gui.votingItemName = this.$("#votingItemName");
            this.gui.addVotingItem = this.$("#addVotingItem");
            this.gui.addVoting = this.$("#addVoting");
            this.gui.chooseVotingItemsTypeBlock = this.$("#chooseVotingItemsTypeBlock");
            this.gui.votingItemsVarBlock = this.$("#votingItemsVarBlock");
            this.gui.addMineBlock = this.$("#addMineBlock");
            this.gui.otherVotingSourceBlock = this.$("#otherVotingSourceBlock");
            this.gui.isFailOnContraResultBlock = this.$("#isFailOnContraResultBlock");
            //this.gui.isCandidateProposeBlock = this.$("#isCandidateProposeBlock");
            this.gui.isFailOnContraResult = this.$("#isFailOnContraResult");
            //this.gui.isCandidatePropose = this.$("#isCandidatePropose");
            this.gui.minSelectionCountBlock = this.$("#minSelectionCountBlock");
            this.gui.minSelectionCount = this.$("#minSelectionCount");
            this.gui.maxSelectionCountBlock = this.$("#maxSelectionCountBlock");
            this.gui.maxSelectionCount = this.$("#maxSelectionCount");

            this.gui.minWinnersCountBlock = this.$("#minWinnersCountBlock");
            this.gui.maxWinnersCountBlock = this.$("#maxWinnersCountBlock");

            this.gui.minWinnersCount = this.$("#minWinnersCount");
            this.gui.maxWinnersCount = this.$("#maxWinnersCount");

            this.gui.isMultipleWinners = this.$("#isMultipleWinners");
            this.gui.percentForWin = this.$("#percentForWin");
            //this.gui.isVariantProposeBlock = this.$("#isVariantProposeBlock");
            //this.gui.isVariantPropose = this.$("#isVariantPropose");

            this.gui.votingButtonName = this.$("#votingButtonName");
            this.gui.votingButtonModal = this.$("#votingButtonModal");
            //this.gui.addVotingButtonContentButton = this.$("#addVotingButtonContentButton");
            //this.gui.saveVotingButtonContentButton = this.$("#saveVotingButtonContentButton");

            var self = this;

            this.$el.on("shown.bs.modal", function() {
                try {
                    self.$("#votingButtonContent").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#votingButtonContent").radomTinyMCE({
                    onCreate : function(editor){
                        self.gui.votingButtonContent = editor;
                    }
                });
                try {
                    self.$("#votingDescription").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#votingDescription").radomTinyMCE({
                    onCreate : function(editor){
                        self.gui.description = editor;
                    }
                });

                try {
                    self.$("#votingWinnerText").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#votingWinnerText").radomTinyMCE({
                    onCreate : function(editor){
                        self.gui.votingWinnerText = editor;
                    }
                });


                try {
                    self.$("#sentence").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#sentence").radomTinyMCE({
                    onCreate : function(editor){
                        self.gui.sentence = editor;
                    }
                });
                try {
                    self.$("#successDecree").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#successDecree").radomTinyMCE({
                    onCreate : function(editor){
                        self.gui.successDecree = editor;
                    }
                });
                try {
                    self.$("#failDecree").tinymce().remove();
                } catch (e) {
                    console.log(e);
                }
                self.$("#failDecree").radomTinyMCE({
                    onCreate : function(editor){
                        self.gui.failDecree = editor;
                    }
                });
            });

            this.gui.votingType.selectpicker("refresh");
            this.gui.votingType.selectpicker("val", "-1");

            // Изменение типа голосования
            this.gui.votingType.on("change", function(){
                var votingType = $(this).val();
                self.initVotingTypeBlock(votingType);
            });

            // Изменение вида вариантов голосований
            /*this.gui.addMine.on("click", function(){
                self.gui.addMineBlock.show();
                self.gui.votingItemsVarBlock.hide();
            });
            this.gui.votingItemsFromVar.on("click", function(){
                self.gui.addMineBlock.hide();
                self.gui.votingItemsVarBlock.show();
            });*/
            //debugger;
            this.gui.votingItemsSource.on("change", function(){
                var votingItemsSourceValue = $(this).attr("id");
                var votingModel = self.editableVotingModel;
                var sourceVotingIndex = -1;
                if (votingModel != null) {
                    sourceVotingIndex = votingModel.sourceVotingIndex;
                }

                var votingType = self.gui.votingType.val();
                var currentVotingIndex = votingModel == null ? -1 : votingModel.index;
                
                $(".votingSourceBlock").hide();
                switch(votingItemsSourceValue) {
                    case "addMine":
                        self.votingItemsList.getCollection().reset(self.currentVotingItemsModel);
                        self.gui.addMineBlock.show();
                        break;

                    case "votingItemsFromVar":
                        self.gui.votingItemsVarBlock.show();
                        break;

                    case "genFromInterview": // Заполнить список интервью которые по индексу меньше текущего голосования
                        self.gui.otherVotingSourceBlock.show();
                        self.initOtherVotingSelect(["INTERVIEW"], null, sourceVotingIndex, currentVotingIndex);
                        break;

                    case "genFromWinnersOtherVoting": // Заполнить список другими голосованиями которые по индексу меньше текущего голосования
                        self.gui.otherVotingSourceBlock.show();
                        if (votingType == "CANDIDATE") {
                            self.initOtherVotingSelect(["CANDIDATE"], true, sourceVotingIndex, currentVotingIndex);
                        } else if (votingType == "SINGLE_SELECTION" || votingType == "MULTIPLE_SELECTION") {
                            self.initOtherVotingSelect(["SINGLE_SELECTION", "MULTIPLE_SELECTION"], true, sourceVotingIndex, currentVotingIndex);
                        }
                        break;

                    case "genFromLosersOtherVoting": // Заполнить список другими голосованиями которые по индексу меньше текущего голосования
                        self.gui.otherVotingSourceBlock.show();
                        if (votingType == "CANDIDATE") {
                            self.initOtherVotingSelect(["CANDIDATE"], false, sourceVotingIndex, currentVotingIndex);
                        } else if (votingType == "SINGLE_SELECTION" || votingType == "MULTIPLE_SELECTION") {
                            self.initOtherVotingSelect(["SINGLE_SELECTION", "MULTIPLE_SELECTION"], false, sourceVotingIndex, currentVotingIndex);
                        }
                        break;
                }
            });
            
            this.gui.isMultipleWinners.on("change", function(){
                if ($(this).is(":checked")) {
                    
                } else {
                    
                }
            });
        },

        // Влючить режим редактирования голосования
        initEditableMode: function(votingModel){
            this.editableVotingModel = votingModel;
            this.currentVotingItemsModel = votingModel.votingItems;
            this.currentVotingButtonsModel = votingModel.votingButtons == null ? [] : votingModel.votingButtons;

            this.editMode = true;
            this.enableGuiEditableMode();
        },

        // Включить режим редактирования голосования
        enableGuiEditableMode: function(){
            this.initVotingTypeBlock(this.editableVotingModel.votingType);
            //this.currentVotingItemsModel = [];
            // TODO
            //this.editableVotingModel
            //this.gui.votingItemsSource
            /*if (this.editableVotingModel.votingItems != null && this.editableVotingModel.votingItems.length > 0) {
                this.gui.addMine.click();
                this.currentVotingItemsModel = this.editableVotingModel.votingItems;
                this.votingItemsList.getCollection().reset(this.currentVotingItemsModel);
            } else if (this.editableVotingModel.votingItemsVar != null && this.editableVotingModel.votingItemsVar != "") {
                this.gui.votingItemsFromVar.click();
            }*/

            this.votingButtonsList.getCollection().reset(this.currentVotingButtonsModel);

            this.gui.subject.val(this.editableVotingModel.subject);
            this.gui.description.setContent(this.editableVotingModel.description);
            this.gui.votingCreateCondition.val(this.editableVotingModel.votingCreateCondition);
            this.gui.votingType.selectpicker("val", this.editableVotingModel.votingType);
            this.gui.isVoteCancellable.prop("checked", this.editableVotingModel.isVoteCancellable); // checkbox
            this.gui.isVoteCommentsAllowed.prop("checked", this.editableVotingModel.isVoteCommentsAllowed); // checkbox
            this.gui.isVisible.prop("checked", this.editableVotingModel.isVisible); // checkbox
            this.gui.addAbstain.prop("checked", this.editableVotingModel.addAbstain); // checkbox
            this.gui.votingItemsVar.val(this.editableVotingModel.votingItemsVar); // input

            this.gui.minSelectionCount.val(this.editableVotingModel.minSelectionCount); // input
            this.gui.maxSelectionCount.val(this.editableVotingModel.maxSelectionCount); // input

            this.gui.minWinnersCount.val(this.editableVotingModel.minWinnersCount); // input
            this.gui.maxWinnersCount.val(this.editableVotingModel.maxWinnersCount); // input

            this.gui.isFailOnContraResult.prop("checked", this.editableVotingModel.isFailOnContraResult); // checkbox
            //this.gui.isCandidatePropose.prop("checked", this.editableVotingModel.isCandidatePropose); // checkbox
            this.gui.isMultipleWinners.prop("checked", this.editableVotingModel.isMultipleWinners); // checkbox
            this.gui.percentForWin.val(this.editableVotingModel.percentForWin); // input

            if (this.editableVotingModel.votingWinnerText != null) {
                this.gui.votingWinnerText.setContent(this.editableVotingModel.votingWinnerText);
            }
            if (this.editableVotingModel.sentence != null) {
                this.gui.sentence.setContent(this.editableVotingModel.sentence);
            }
            if (this.editableVotingModel.successDecree != null) {
                this.gui.successDecree.setContent(this.editableVotingModel.successDecree);
            }
            if (this.editableVotingModel.failDecree != null) {
                this.gui.failDecree.setContent(this.editableVotingModel.failDecree);
            }

            /*if (this.editableVotingModel.isMultipleWinners && this.editableVotingModel.votingType != "PRO_CONTRA") {
                this.gui.percentForWin.closest(".row").show();
            } else {
                this.gui.percentForWin.closest(".row").hide();
            }*/
            //this.gui.isVariantPropose.prop("checked",this.editableVotingModel.isVariantPropose);


            this.gui.addVoting.text("Сохранить голосование");
            this.$("#editModeText").show();
            this.$(".modal-content").css("background-color", "#FFFFAD");

            var selectFromType = this.editableVotingModel.selectFromType;
            selectFromType = selectFromType == null ? "addMine" : selectFromType;
            this.gui.votingItemsSource.filter("[id=" + selectFromType + "]").prop("checked", true).trigger("change");
        },

        // Выключить режим редактирования голосования
        disableGuiEditableMode: function(){
            this.gui.addVoting.text("Добавить голосование");
            this.$(".modal-content").css("background-color", "#FFF");
            this.$("#editModeText").hide();
        },

        // Добавить вариант голосования
        addVotingItem: function() {
            if (this.gui.votingItemName.val() == null || this.gui.votingItemName.val() == "") {
                bootbox.alert("Необходимо добавить значение элемента");
                return false;
            }
            var value = this.gui.votingItemName.val();
            for (var index in this.currentVotingItemsModel) {
                var item = this.currentVotingItemsModel[index];
                if (item.value == value) {
                    bootbox.alert("Необходимо добавить уникальное значение");
                    return false;
                }
            }
            this.currentVotingItemsModel.push({value : value});
            this.votingItemsList.getCollection().reset(this.currentVotingItemsModel);
            this.gui.votingItemName.val("");
        },

        // Добавить голосование
        addVoting: function() {
            var votingModel = null;
            if (this.editMode) {
                votingModel = this.editableVotingModel;
            } else {
                votingModel = {};
            }

            var selectFromType = this.gui.votingItemsSource.filter(":checked").attr("id");
            var sourceVotingIndex = null;
            var votingItems = [];
            var votingItemsVar = "";
            // Если выбран варинат - добавление вариантов для голосования
            //if (this.gui.addMine.prop("checked")) {
            if (selectFromType == "addMine") {
                votingItems = this.currentVotingItemsModel;
            } else if (selectFromType == "votingItemsFromVar") {
                votingItemsVar = this.gui.votingItemsVar.val();
            } else {
                sourceVotingIndex = this.gui.otherVotingSource.val();
            }
            this.currentVotingItemsModel = [];

            votingModel.subject = this.gui.subject.val();
            votingModel.description = this.gui.description.getContent();
            votingModel.votingCreateCondition = this.gui.votingCreateCondition.val();
            votingModel.votingType = this.gui.votingType.val();
            votingModel.isVoteCancellable = this.gui.isVoteCancellable.prop("checked");
            votingModel.isVoteCommentsAllowed = this.gui.isVoteCommentsAllowed.prop("checked");
            votingModel.isVisible = this.gui.isVisible.prop("checked");
            votingModel.addAbstain = this.gui.addAbstain.prop("checked");
            votingModel.votingItemsVar = votingItemsVar == "" ? null : votingItemsVar;
            votingModel.votingItems = votingItems;
            votingModel.isFailOnContraResult = this.gui.isFailOnContraResult.prop("checked");
            //votingModel.isCandidatePropose = this.gui.isCandidatePropose.prop("checked");
            votingModel.minSelectionCount = this.gui.minSelectionCount.val();
            votingModel.maxSelectionCount = this.gui.maxSelectionCount.val();

            votingModel.minWinnersCount = this.gui.minWinnersCount.val();
            votingModel.maxWinnersCount = this.gui.maxWinnersCount.val();

            votingModel.isMultipleWinners = this.gui.isMultipleWinners.prop("checked");
            votingModel.percentForWin = this.gui.percentForWin.val();
            //votingModel.isVariantPropose = this.gui.isVariantPropose.prop("checked");
            votingModel.votingButtons = this.currentVotingButtonsModel;
            votingModel.votingWinnerText = this.gui.votingWinnerText.getContent();
            votingModel.sentence = this.gui.sentence.getContent();
            votingModel.successDecree = this.gui.successDecree.getContent();
            votingModel.failDecree = this.gui.failDecree.getContent();
            votingModel.selectFromType = selectFromType;
            votingModel.sourceVotingIndex = sourceVotingIndex;

            this.currentVotingButtonsModel = [];

            this.initIndexField();

            if (!this.editMode) {
                this.votingsModel.push(votingModel);
            }

            this.editableVotingModel = null;
            this.editMode = false;
            this.disableGuiEditableMode();

            this.votingList.getCollection().reset(this.votingsModel);
            this.initVotingTypeBlock(null);

            this.votingButtonsList.getCollection().reset(this.currentVotingButtonsModel);

            this.gui.subject.val("");
            this.gui.description.setContent("");
            this.gui.votingCreateCondition.val("true");
            this.gui.votingType.selectpicker("val", "-1");
            this.gui.isVoteCancellable.prop("checked", true);
            this.gui.isVoteCommentsAllowed.prop("checked", false);
            this.gui.isVisible.prop("checked", true);
            this.gui.addAbstain.prop("checked", true);
            //this.gui.addMine; // TODO
            //this.gui.votingItemsFromVar; // TODO
            this.gui.votingItemsVar.val("");
            this.gui.votingItemName.val("");
            this.gui.isMultipleWinners.prop("checked", false);
            this.gui.percentForWin.val("51");

            this.gui.votingWinnerText.setContent("Победитель голосования");
            
            this.gui.sentence.setContent("");
            this.gui.successDecree.setContent("");
            this.gui.failDecree.setContent("");
        },

        editVoting: function(){
            console.log(this);
        },

        removeVoting: function(){

        },

        // Открыть модальное окно для добавления кнопки с контентом
        addVotingButton : function(event, model){
            if (model == null) {
                model = {
                    buttonText: "",
                    content: ""
                }
            }

            this.gui.votingButtonModal.modal("show");
            this.gui.votingButtonName.val(model.buttonText);
            this.gui.votingButtonContent.setContent(model.content);
            this.votingButtonsList.getCollection().reset(this.currentVotingButtonsModel);
        },

        // Сохранить кнопку с контентом
        saveVotingButton : function(){
            if (this.gui.votingButtonName.val() == null || this.gui.votingButtonName.val() == "") {
                bootbox.alert("Не заполнен текст кнопки.");
                return;
            }
            if (this.gui.votingButtonContent.getContent() == null || this.gui.votingButtonContent.getContent() == "") {
                bootbox.alert("Не заполнен контент.");
                return;
            }
            if (this.editButtonContentModel == null) {
                for (var index in this.currentVotingButtonsModel) {
                    var buttonContent = this.currentVotingButtonsModel[index];
                    if (buttonContent.buttonText == this.gui.votingButtonName.val()) {
                        bootbox.alert("Кнопка с таким текстом уже существует.");
                        return;
                    }
                }
            }

            this.gui.votingButtonModal.modal("hide");

            var model = {
                buttonText: this.gui.votingButtonName.val(),
                content: this.gui.votingButtonContent.getContent()
            };

            if (this.editButtonContentModel == null) {
                this.currentVotingButtonsModel.push(model);
            } else {
                this.editButtonContentModel.buttonText = model.buttonText;
                this.editButtonContentModel.content = model.content;
            }
            this.votingButtonsList.getCollection().reset(this.currentVotingButtonsModel);
            this.editButtonContentModel = null;
        },

        cancelSaveVotingButton : function(){
            this.gui.votingButtonModal.modal("hide");
        }
    });

    return MulticomplexValueModalEditor.extend({
        modalClass: ModalEditor
    });
});