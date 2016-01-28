(function () {
    'use strict';

    var notificationPosition = 'top right';

    angular.module('notificationHelperModule', [])
        .value('notificationHelper', {

            showServerInfo: function (message) {
                $.notify(message, {
                    autoHide: false,
                    clickToHide: false,
                    globalPosition: notificationPosition,
                    className: 'info'
                });
            },

            hideServerInfo: function () {
                $('.notifyjs-bootstrap-info').trigger('notify-hide');
            },

            showOperationSuccess: function (message) {
                $.notify(message, {
                    autoHide: true,
                    clickToHide: true,
                    globalPosition: notificationPosition,
                    className: 'success'
                });
            },

            showOperationFailure: function (message) {
                $.notify(message, {
                    autoHide: true,
                    clickToHide: true,
                    globalPosition: notificationPosition,
                    className: 'error'
                });
            }
        });
})();