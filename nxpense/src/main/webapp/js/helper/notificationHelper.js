(function (angular) {
    'use strict';

    var notificationHelperModule = angular.module('notificationHelperModule', []);

    var notificationPosition = 'top right';

    notificationHelperModule.value('notificationHelper', {

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

})(window.angular);