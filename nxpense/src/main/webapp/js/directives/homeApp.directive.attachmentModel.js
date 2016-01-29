(function () {
    'use strict';

    angular.module('homeApp').directive('nxAttachmentModel', attachmentModel);

    attachmentModel.$inject = ['$parse'];

    function attachmentModel($parse) {
        return {
            restrict: 'A',
            link: function (scope, element, attributes) {
                var targetController = attributes.nxAmController;
                var targetAction = attributes.nxAmAction;

                element.bind('change', function () {
                    scope[targetController][targetAction](element[0].files);
                });
            }
        };
    }
})();