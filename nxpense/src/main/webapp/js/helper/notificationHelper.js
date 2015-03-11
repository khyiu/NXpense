(function (angular) {
    'use strict';

    var notificationHelperModule = angular.module('notificationHelperModule', []);

    notificationHelperModule.value('notificationHelper', {
        showServerInfo: function(message) {
            $.notify(message, {
                autoHide: false,
                clickToHide: false,
                globalPosition: 'top center',
                className: 'info'
            });
        }
    });

})(window.angular);