(function(angular) {
    'use strict';

    var homeAppDirectiveModule = angular.module('homeApp.directive', ['ui.bootstrap', 'restangular', 'notificationHelperModule', 'ngDraggable']);



    // Attribute type directive to set files chosen through file-type input element, into the modal controller's scope
    homeAppDirectiveModule.directive('attachmentModel', ['$parse', function ($parse) {
        return {
            restrict: 'A',
            link: function(scope, element, attributes) {
                var attachmentModel = $parse(attributes.attachmentModel);
                var attachmentModelSetter = attachmentModel.assign;

                element.bind('change', function(){
                    // if directive has access to a 'addAttachments' function, invoke it with the fileList object
                    if(scope.addAttachments) {
                        scope.addAttachments(element[0].files);
                    } else {
                        // otherwise, fallback to a generic setter that is built based on the 'attachment-model' attribute values passed to the directive
                        scope.$apply(function(){
                            attachmentModelSetter(scope, element[0].files);
                        });
                    }
                });
            }
        };
    }]);



})(window.angular);