define([], function () {
    var baseString = "unique-";

    var counters = {};

    return function (base) {
        if(typeof base !== "string") {
            base = baseString;
        }
        var c = counters[base];
        if(!c) {
            c = 1;
            counters[base] = c;
        } else {
            c += 1;
        }
        counters[base] = c;
        return base + c;
    }
});
