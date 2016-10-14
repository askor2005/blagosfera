/**
 * Created by aotts on 26.10.2015.
 * утильный класс
 */
define([], function () {
    var utils = {};

    utils.clickEventWrapper = function (method, ctx) {
        var lastCtx;
        var lastMethod;
        return function (event) {
            if(event.which === 1){
                var nowCtx = ctx || this;
                if(lastCtx != nowCtx) {
                    lastCtx = nowCtx;
                    if(typeof method !== "function") {
                        lastMethod = lastCtx[method];
                    } else {
                        lastMethod = method;
                    }
                }
                if(lastMethod) {
                    lastMethod.call(lastCtx, event);
                }
            }
            if(event.which === 1 || event.which === 2) {
                event.preventDefault();
            }
        }
    };

    utils.findObjectInArray = function(arr, objectProperty, propertyValue) {
        var foundObj = null;
        for (var index in arr) {
            var obj = arr[index];
            if (obj[objectProperty] == propertyValue) {
                foundObj = obj;
                break;
            }
        }
        return foundObj;
    };

    utils.removeItemFromArray = function(arr, objectProperty, propertyValue) {
        var foundObj = null;
        for (var index in arr) {
            var obj = arr[index];
            if (obj[objectProperty] == propertyValue) {
                foundObj = obj;
                arr.splice(parseInt(index),1);
                break;
            }
        }
    };

    return utils;
});