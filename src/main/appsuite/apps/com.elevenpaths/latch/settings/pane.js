define('com.elevenpaths/latch/settings/pane',
    ['io.ox/core/extensions',
     'com.elevenpaths/latch/api',
     'gettext!com.elevenpaths/latch'
    ], function (ext, api, gt) {

    'use strict';

    function draw() {

        api.status().done(function (data) {

            if (data.status === 'paired') {

                var label = $('<label class="control-label" style="margin-right: 5px; text-align: left;">');

                label.text('Account paired');

                $('.control-group').empty().append(label);

                var controls = $('<span class="controls">');
                var button = $('<button type="button" class="btn btn-primary">').text(gt('Unpair'));

                button.on('click', function () {
                    api.unpair().done(function () {
                        draw();
                    }).fail(function () {
                        require(['io.ox/core/notifications'], function (notifications) {
                            notifications.yell('error', 'Unable to unpair Latch account');
                        });
                    });
                });

                controls.append(button);
                $('.control-group').append(controls);

            }
            else {

                var label = $('<label class="control-label" style="margin-right: 5px; text-align: left;">');

                label.text('Account unpaired. Pairing token: ');
                label.append($('<input type="text" class="latch-token" maxlength="6" style="width: 60px">'));

                $('.control-group').empty().append(label);

                var controls = $('<span class="controls">');
                var button = $('<button type="button" class="btn btn-primary">').text(gt('Pair'));

                button.on('click', function () {
                    if ($('.latch-token')[0].value.length === 6) {
                        api.pair($('.latch-token')[0].value).done(function () {
                            draw();
                        }).fail(function () {
                            require(['io.ox/core/notifications'], function (notifications) {
                                notifications.yell('error', 'Unable to pair Latch account');
                            });
                        });
                    }
                });

                controls.append(button);
                $('.control-group').append(controls);

            }

        }).fail(function () {
            require(['io.ox/core/notifications'], function (notifications) {
                notifications.yell('error', 'Unable to get Latch account status');
            });
        });

    }

    ext.point('com.elevenpaths/latch/settings/detail').extend({
        draw: function () {
            var self = this;
            var pane = $('<div class="io-ox-tasks-settings">');
            self.append($('<div>').addClass('section').append(pane));
            ext.point('com.elevenpaths/latch/settings/detail' + '/pane').invoke('draw', pane);
        }
    });

    ext.point('com.elevenpaths/latch/settings/detail' + '/pane').extend({
        draw: function () {
            this.append($('<h1>').text('Latch'));
            this.append($('<form>').addClass('form-horizontal').append($('<div>').addClass('control-group')));
            draw();
        }
    });

});
