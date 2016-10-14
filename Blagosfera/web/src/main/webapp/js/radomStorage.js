// Возможно всётаки сделать альтернативу через куки
var notSupportedStorageContainer = {};
var notSupportedStorage = {
    setItem : function(key, value){
        notSupportedStorageContainer[key] = value;
    },
    getItem : function(key){
        return notSupportedStorageContainer[key];
    },
    removeItem : function(key){
        delete notSupportedStorageContainer[key];
    },
    clear : function(){
        notSupportedStorageContainer = {};
    }
}
var radomLocalStorage = null;
var radomSessionStorage = null;
if (localStorage != null) {
    radomLocalStorage = localStorage;
} else {
    radomLocalStorage = notSupportedStorage;
}
if (sessionStorage != null) {
    radomSessionStorage = sessionStorage;
} else {
    radomSessionStorage = notSupportedStorage;
}
