define("com.elevenpaths/latch/settings/pane",
    ["dot!com.elevenpaths/latch/settings/form.html",
     "io.ox/core/extensions",
     "com.elevenpaths/latch/api",
    'gettext!com.example'
    ], function (tmpl, ext, api, gt) {

    'use strict';

    var staticStrings =  {
        TITLE: gt('Latch')
    };

    function draw() {

        $('.settings-container').empty().append(tmpl.render('com.elevenpaths/latch/settings', { strings: staticStrings }));
        
        api.status().done(function (data) {

            if (data.status === 'paired') {

                var label = $('<label class="control-label" style="text-align: left; width: 100px">');

                label.text('Account paired');

                $('.control-group').empty().append(label);

                var controls = $('<div class="controls" style="margin-left: 110px">');
                var button = $('<button type="button" class="btn btn-primary">').text(gt('Unpair'));

                button.on('click', function () {
                    api.unpair().done(function (data) {
                        draw();
                    }).fail(function (data) {
                        require(['io.ox/core/notifications'], function (notifications) {
                            notifications.yell('error', 'Unable to unpair Latch account');
                        });
                    });
                });

                controls.append(button);
                $('.control-group').append(controls);

            }
            else {

                var label = $('<label class="control-label" style="text-align: left; width: 290px">');

                label.text('Account unpaired. Pairing token: ');
                label.append($('<input type="text" class="latch-token" maxlength="6" style="width: 60px">'));

                $('.control-group').empty().append(label);

                var controls = $('<div class="controls" style="margin-left: 300px; padding-top: 5px">');
                var button = $('<button type="button" class="btn btn-primary">').text(gt('Pair'));

                button.on('click', function () {
                    if ($('.latch-token')[0].value.length === 6) {
                        api.pair($('.latch-token')[0].value).done(function (data) {
                            draw();
                        }).fail(function (data) {
                            require(['io.ox/core/notifications'], function (notifications) {
                                notifications.yell('error', 'Unable to pair Latch account');
                            });
                        });
                    }
                });

                controls.append(button);
                $('.control-group').append(controls);

            }

        }).fail(function (data) {
            require(['io.ox/core/notifications'], function (notifications) {
                notifications.yell('error', 'Unable to get Latch account status');
            });
        });

    }

    ext.point("com.elevenpaths/latch/settings/detail").extend({

        draw: function () {
            draw();
        }

    });

});
