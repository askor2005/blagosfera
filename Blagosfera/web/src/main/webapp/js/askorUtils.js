function stringForms(intValue, value1, value2, value3) {
    var result = "";
    if (intValue > 10 && intValue < 20) {
        result = value3;
    } else {
        if (intValue % 10 == 1) {
            result = value1;
        } else if (intValue % 10 > 1 && intValue % 10 < 5) {
            result = value2;
        } else {
            result = value3;
        }
    }
    return result;
}

function removeItemFromArray(arr, objectProperty, propertyValue) {
    var foundObj = null;
    for (var index in arr) {
        var obj = arr[index];
        if (obj[objectProperty] == propertyValue) {
            foundObj = obj;
            arr.splice(parseInt(index),1);
            break;
        }
    }
}

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function getLastPartOfUrl() {
    var url = window.location.href;
    var parts = url.split("/");
    return parts[parts.length - 1];
}

function fromServerDateToLocalDate(date) {
    var localTimeZoneOffset = (clientTimeZoneOffset - serverTimeZoneOffset) * 60 * 60 * 1000;
    date.setTime(date.getTime() + localTimeZoneOffset);
    return date;
}

function createIsoDate(isoString) {
    return fromServerDateToLocalDate(moment(isoString).toDate());
}

function createTimestampDate(timestamp) {
    var date = new Date(parseFloat(timestamp));
    return fromServerDateToLocalDate(date);
}

function createDateFromDate(dateLocal) {
    var date = new Date(dateLocal);
    return fromServerDateToLocalDate(date);
}

function createFormattedDate(formattedString, format) {
    var date = parseFormattedDate(formattedString, format);
    return fromServerDateToLocalDate(date);
}

function parseFormattedDate(formattedString, format) {
    return moment(formattedString, format).toDate();
}

function createDate() {
    var date = new Date();
    var clientTimeZone = date.getTimezoneOffset() / 60 * -1;
    date.setTime(serverTime - clientTimeZone * 60 * 60 * 1000 + clientTimeZoneOffset * 60 * 60 * 1000);
    return date;
}

function toServerDate(date) {
    var timeStamp = null;
    if (date instanceof Date) {
        timeStamp = date.getTime();
    } else {
        timeStamp = date;
    }
    var serverDate = new Date(timeStamp);
    var localTimeZoneOffset = (clientTimeZoneOffset - serverTimeZoneOffset) * 60 * 60 * 1000;
    serverDate.setTime(serverDate.getTime() - localTimeZoneOffset);
    return serverDate;
}