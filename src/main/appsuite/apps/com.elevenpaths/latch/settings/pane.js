define('com.elevenpaths/latch/settings/pane',
    ['io.ox/core/extensions',
     'com.elevenpaths/latch/api',
     'gettext!com.elevenpaths/latch'
    ], function (ext, api, gt) {

    'use strict';

    function draw() {

        $('.io-ox-tasks-settings').empty();

        $('.io-ox-tasks-settings').append($('<h1>').text(gt('Latch')));
        $('.io-ox-tasks-settings').append($('<p>').text(gt('Latch works as a security lock that protects your Webmail through a mobile app when you are not connected.')));
        $('.io-ox-tasks-settings').append($('<br>'));

        api.status().done(function (data) {

            if (data.status === 'paired') {

                $('.io-ox-tasks-settings').append($('<form>').addClass('form-horizontal').append($('<div>').addClass('control-group')));

                var label = $('<label class="control-label" style="margin-right: 5px; text-align: left;">');

                label.text(gt('Your Webmail is now ')).append($('<strong>').text(gt('Paired')));

                $('.control-group').empty().append(label);

                var controls = $('<span class="controls">');
                var button = $('<button type="button" class="btn btn-primary">').text(gt('Unpair Latch'));

                button.on('click', function () {
                    api.unpair().done(function () {
                        draw();
                    }).fail(function () {
                        require(['io.ox/core/notifications'], function (notifications) {
                            notifications.yell('error', gt('Unable to unpair Latch account'));
                        });
                    });
                });

                controls.append(button);
                $('.control-group').append(controls);

            }
            else {

                $('.io-ox-tasks-settings').append($('<p>').text(gt('Your Webmail is now ')).append($('<strong>').text(gt('Unpaired'))));

                $('.io-ox-tasks-settings').append($('<form>').addClass('form-horizontal').append($('<div>').addClass('control-group')));

                var label = $('<label class="control-label" style="margin-right: 5px; text-align: left;">');

                label.text(gt('Type the pairing code that appears on your mobile\'s Latch app: '));
                label.append($('<input type="text" class="latch-token" maxlength="6" style="width: 60px">'));

                $('.control-group').empty().append(label);

                var controls = $('<span class="controls">');
                var button = $('<button type="button" class="btn btn-primary">').text(gt('Pair Latch'));

                button.on('click', function () {
                    if ($('.latch-token')[0].value.length === 6) {
                        api.pair($('.latch-token')[0].value).done(function () {
                            draw();
                        }).fail(function () {
                            require(['io.ox/core/notifications'], function (notifications) {
                                notifications.yell('error', gt('Unable to pair Latch account'));
                            });
                        });
                    }
                });

                controls.append(button);
                $('.control-group').append(controls);

            }

            $('.io-ox-tasks-settings').append($('<br>'));
			$('.io-ox-tasks-settings').append($('<p>').text(gt('Download the free Latch app and get a pairing code to protect your Webmail:')));
            $('.io-ox-tasks-settings').append($('<br>'));

			var section = $('<section>');

            var slc1 = $('<div>').addClass('shop-link-container');
            var slc2 = $('<div>').addClass('shop-link-container');
            var slc3 = $('<div>').addClass('shop-link-container');
            var slc4 = $('<div>').addClass('shop-link-container');
            var slc5 = $('<div>').addClass('shop-link-container');

            var sl1 = $('<a>').addClass('shoplink');
			var sl2 = $('<a>').addClass('shoplink');
            var sl3 = $('<a>').addClass('shoplink');
            var sl4 = $('<a>').addClass('shoplink');
            var sl5 = $('<a>').addClass('shoplink');
            
            sl1.attr('href', 'https://itunes.apple.com/us/app/latch-by-elevenpaths/id744999016');
            sl1.attr('target', '_blank');
            sl2.attr('href', 'https://play.google.com/store/apps/details?id=com.elevenpaths.android.latch');
            sl2.attr('target', '_blank');
            sl3.attr('href', 'https://www.windowsphone.com/es-es/store/app/latch/6054cd65-d9ce-49a5-9202-e2be77bdcd03');
            sl3.attr('target', '_blank');
            sl4.attr('href', 'https://marketplace.firefox.com/app/latch/');
            sl4.attr('target', '_blank');
            sl5.attr('href', 'https://appworld.blackberry.com/webstore/content/59940537');
            sl5.attr('target', '_blank');

            var img1 = $('<img>');
            var img2 = $('<img>');
            var img3 = $('<img>');
            var img4 = $('<img>');
            var img5 = $('<img>');
            
            img1.attr('src', 'data:image/png;base64,' + gt('iVBORw0KGgoAAAANSUhEUgAAAIcAAAAoCAMAAADjcxkDAAAAilBMVEUAAAD///8ODg4KCgrU1NQGBgbJycm/v7/39/cSEhLu7u7Pz8+mpqa0tLRaWlrh4eFAQEAcHByOjo7z8/Pi4uKamppkZGQ3Nzdzc3PZ2dnp6el+fn5ubm6bm5uFhYXBwcFISEgnJydTU1MYGBgwMDB5eXkiIiK2trY0NDRnZ2eSkpJOTk6jo6M9PT2cNlPGAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3wwWFDs3AB6ggwAAAqRJREFUWMPtmAuTojAMx/NHV5BVfHC6K4663Oq5qPf9v941ffAoCMp6gzdzGaeUtpRfkzQNEoSs+l0KE4DEz3npVBySHD3qXBzBsadnEDwFBfWEczwDh/MPcGxQNhrmtlXNGAy59IKal/lBGw4fZY6XAa45WBMHWnKIrYS+3RgKf6LJmBJPbnmtD1WV6hMcrsFXzSt1yzf+lusX4PMODqDKLDR55xKZUWQ1PBPeaQ3mMG2iCDeEWGuM9bGh8JC35Y0c1+Akh1h/ijL2E2kX5hiaBYhiG8nuYWaX10X1Aq9znCraooOcbzMfGxj+/RSvFxwnGkVaH6kPJ1UcN/rHioFdog95/IS2TyLQ17x/XNZiJ8mGQFgHK628SHV/qjsKl/S6oQTwb+CALd3EjxIGBl1w9EsYp070gW7M0sxhPzCL48ZJg527/B7HWyOHaPqon/LcRo8WR9DM0W94Raz6z2qrtuT41cQxQNww+wq7THeP8w/PNos4h0+6xv2/s6pKCXbp2+XzP3RFxkN4M8D03Llf9jYHpQco24iDpakeigdTKFbhTbjBC1QjPBWFMaaoCNLMoWdXcsQ2x8EnDsy5n6ZNX+lizcBIlhcz8Cg1V88xrQA5F9VBCs1DTkH7gjsMxEOjHAeX27wmVba3uC+uo6LX5tDVONMbLA7LovaBUeKYlzAuhYy1lqOXA17XcCylvLU+b/WKwWd/gWNmb9MSh2vbpTH/qAkg+k5q3UOWhWQuqwJY6kgJZQO/zPPH2/Kx6dXdss4yYDm94Swg67qr/VWqBb4KIMUhTXkhB9VYX6dFdRxM5cx2MWFOxzHzhcgiDcWsI53hJXmLLHjI8THfcx6oRfx+/Hflf46/nI89h4DcJ/hfqpeILed0DtJTW98lp1OR8eQP/Ggcpsl69JAAAAAASUVORK5CYII='));		
            img1.attr('width', '135');
            img1.attr('height', '40');
            img1.attr('alt', gt('Download Latch for iOS'));
            img2.attr('src', 'data:image/png;base64,' + gt('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'));
            img2.attr('width', '115');
            img2.attr('height', '40');
            img2.attr('alt', gt('Download Latch for Android'));
            img3.attr('src', 'data:image/png;base64,' + gt('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'));		
            img3.attr('width', '150');
            img3.attr('height', '49');
            img3.attr('alt', gt('Download Latch for Windows Phone'));
            img4.attr('src', 'data:image/png;base64,' + gt('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'));
            img4.attr('width', '150');
            img4.attr('height', '49');
            img4.attr('alt', gt('Download Latch for Firefox OS'));
            img5.attr('src', 'data:image/png;base64,' + gt('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'));
            img5.attr('width', '110');
            img5.attr('height', '47');
            img5.attr('alt', gt('Download Latch for Blackberry'));

            sl1.append(img1);
            sl2.append(img2);
            sl3.append(img3);
            sl4.append(img4);
            sl5.append(img5);

			slc1.append(sl1);
			slc2.append(sl2);
			slc3.append(sl3);
			slc4.append(sl4);
			slc5.append(sl5);

			section.append(slc1);
			section.append(slc2);
			section.append(slc3);
			section.append(slc4);
			section.append(slc5);

			$('.io-ox-tasks-settings').append(section);

        }).fail(function () {
            require(['io.ox/core/notifications'], function (notifications) {
                notifications.yell('error', gt('Unable to get Latch account status'));
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
            draw();
        }
    });

});
