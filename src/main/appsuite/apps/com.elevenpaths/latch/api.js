define('com.elevenpaths/latch/api', ['io.ox/core/http', 'io.ox/core/api/factory'], function (http, apiFactory) {

    'use strict';

    var api;

    api = apiFactory({
    });

    api.pair = function (token) {

        var rv = http.PUT({
            module: 'latch',
            params: {
                action: 'pair',
                token: token
            }
        });

        return rv;

    };

    api.status = function () {

        var rv = http.GET({
            module: 'latch',
            params: {
                action: 'status'
            }
        });

        return rv;

    };

    api.unpair = function () {

        var rv = http.PUT({
            module: 'latch',
            params: {
                action: 'unpair'
            }
        });

        return rv;

    };

    return api;

});
