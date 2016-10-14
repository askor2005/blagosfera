/**
 * Created by aotts on 26.10.2015.
 * Редактор входных параметров посылаемых в вызов процесса
 */
define([
    "Backbone.CollectionBinder",
    "./tableRow/TableRowView",
    "./SingleTableClassFactory"
], function (CollectionBinder, TableRowView, SingleTableClassFactory) {
    var eventOptions = [
        { value: "ENGINE_CREATED", text: "ENGINE_CREATED" },
        { value: "ENGINE_CLOSED", text: "ENGINE_CLOSED" },
        { value: "ENTITY_CREATED", text: "ENTITY_CREATED" },
        { value: "ENTITY_INITIALIZED", text: "ENTITY_INITIALIZED" },
        { value: "ENTITY_UPDATED", text: "ENTITY_UPDATED" },
        { value: "ENTITY_DELETED", text: "ENTITY_DELETED" },
        { value: "ENTITY_SUSPENDED", text: "ENTITY_SUSPENDED" },
        { value: "ENTITY_ACTIVATED", text: "ENTITY_ACTIVATED" },
        { value: "JOB_EXECUTION_SUCCESS", text: "JOB_EXECUTION_SUCCESS" },
        { value: "JOB_EXECUTION_FAILURE", text: "JOB_EXECUTION_FAILURE" },
        { value: "JOB_RETRIES_DECREMENTED", text: "JOB_RETRIES_DECREMENTED" },
        { value: "TIMER_FIRED", text: "TIMER_FIRED" },
        { value: "JOB_CANCELED", text: "JOB_CANCELED" },
        { value: "ACTIVITY_STARTED", text: "ACTIVITY_STARTED" },
        { value: "ACTIVITY_COMPLETED", text: "ACTIVITY_COMPLETED" },
        { value: "ACTIVITY_CANCELLED", text: "ACTIVITY_CANCELLED" },
        { value: "ACTIVITY_SIGNALED", text: "ACTIVITY_SIGNALED" },
        { value: "ACTIVITY_MESSAGE_RECEIVED", text: "ACTIVITY_MESSAGE_RECEIVED" },
        { value: "ACTIVITY_ERROR_RECEIVED", text: "ACTIVITY_ERROR_RECEIVED" },
        { value: "UNCAUGHT_BPMN_ERROR", text: "UNCAUGHT_BPMN_ERROR" },
        { value: "ACTIVITY_COMPENSATE", text: "ACTIVITY_COMPENSATE" },
        { value: "VARIABLE_CREATED", text: "VARIABLE_CREATED" },
        { value: "VARIABLE_UPDATED", text: "VARIABLE_UPDATED" },
        { value: "VARIABLE_DELETED", text: "VARIABLE_DELETED" },
        { value: "TASK_ASSIGNED", text: "TASK_ASSIGNED" },
        { value: "TASK_CREATED", text: "TASK_CREATED" },
        { value: "TASK_COMPLETED", text: "TASK_COMPLETED" },
        { value: "PROCESS_COMPLETED", text: "PROCESS_COMPLETED" },
        { value: "PROCESS_CANCELLED", text: "PROCESS_CANCELLED" },
        { value: "MEMBERSHIP_CREATED", text: "MEMBERSHIP_CREATED" },
        { value: "MEMBERSHIP_DELETED", text: "MEMBERSHIP_DELETED" },
        { value: "MEMBERSHIPS_DELETED", text: "MEMBERSHIPS_DELETED" }
    ];


    var isErrorType = function (type) {
        return type === "error" || type === "message" || type === "signal" || type === "globalSignal";
    };

    return SingleTableClassFactory({
        attributeName: "eventListeners",
        title: "Слушатели событий",
        header: ["Событие", "Значение", "Тип значения", "Тип сущности", ""],
        rowObjectFactory: function () {
            return {event: "ENGINE_CREATED", value: "", type: "className", entityType: ""}
        },
        rowFactoryProvider: function () {
            return new CollectionBinder.ViewManagerFactory(function (model) {
                return new TableRowView({
                    model: model,
                    rowConfig: [
                        TableRowView.templates.select("event", eventOptions),
                        TableRowView.templates.input("value"),
                        TableRowView.templates.select("type", [
                            {value: "className", text: "Название класса"},
                            {value: "delegateExpression", text: "Делегатное выражение"},
                            {value: "error", text: "Выбросить ошибку"},
                            {value: "message", text: "Выбросить сообщение"},
                            {value: "signal", text: "Выбросить сигнал"},
                            {value: "globalSignal", text: "Выбросить глобальный сигнал"}
                        ]),
                        function (data) {
                            if(isErrorType(data.type)) {
                                return "";
                            }
                            return TableRowView.templates.input("entityType");
                        },
                        TableRowView.templates.minusBtn()
                    ]
                });
            });
        },
        fromModelValueConverter: function (value) {
            var type;
            var val;
            if(value.rethrowEvent) {
                type = value.rethrowType || "error";
                if(type === "error") {
                    val = value.errorcode;
                } else if(type === "message") {
                    val = value.messagename;
                } else {
                    val = value.signalname;
                }
            } else {
                if (val = value.delegateExpression) {
                    type = "delegateExpression";
                } else {
                    val = value.className;
                    type = "className";
                }
            }
            return {
                event: value.events && value.events[0] && value.events[0].event || "",
                type: type,
                value: val || "",
                entityType: value.rethrowEvent ? "" : value.entityType || ""
            };
        },
        toModelValueConverter: function (model) {
            var type = model.get("type");
            var rethrowEvent = isErrorType(type);
            var event = model.get("event");
            var res = {
                events:  event && [{event: event}] || [],
                delegateExpression: "",
                className: "",
                entityType: "",
                rethrowEvent: rethrowEvent,
                rethrowType: "",
                errorcode: "",
                messagename: "",
                signalname: ""
            };
            var value = model.get("value") || "";
            if(rethrowEvent) {
                res.rethrowType = type;
                if(type === "error") {
                    res.errorcode = value;
                } else if(type === "message") {
                    res.messagename = value;
                } else {
                    res.signalname = value;
                }
            } else {
                res.entityType = model.get("entityType") || "";
                res[type] = value;
            }
            return res;
        }
    });
});