define('com.elevenpaths/latch/main', function () {

    "use strict";

    var app = ox.ui.createApp({ name: "com.elevenpaths/latch", title : "Latch"});

    return {
        getApp: app.getInstance
    };

});
