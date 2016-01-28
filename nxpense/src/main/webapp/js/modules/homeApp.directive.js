(function(angular) {
    'use strict';

    var homeAppDirectiveModule = angular.module('homeApp.directive', ['ui.bootstrap', 'restangular', 'notificationHelperModule', 'ngDraggable']);

    homeAppDirectiveModule.directive('attachmentItem', [function () {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'modal/attachment-item.html',
            controller: function ($scope) {

                $scope.removeAttachment = function (attachmentToRemove, $event) {
                    var idxToDelete;

                    if (attachmentToRemove && (_.isUndefined($event.keyCode) || $event.keyCode === 13)) {
                        if (attachmentToRemove.hasOwnProperty('tempRandomId')) {
                            idxToDelete = $scope.newAttachments.map(function (attachment) {
                                return attachment.tempRandomId;
                            }).indexOf(attachmentToRemove.tempRandomId);

                            $scope.newAttachments.splice(idxToDelete, 1);
                            $scope.newAttachmentsToDisplay.splice(idxToDelete, 1);
                        } else {
                            idxToDelete = $scope.attachmentsToDisplay.map(function (attachment) {
                                return attachment.filename;
                            }).indexOf(attachmentToRemove.filename);

                            $scope.attachmentsToDisplay.splice(idxToDelete, 1);

                            idxToDelete = $scope.expense.attachments.map(function(attachment) {
                                return attachment.filename;
                            }).indexOf(attachmentToRemove.filename);

                            $scope.expense.attachments.splice(idxToDelete, 1);
                        }
                    }
                };
            }
        };
    }]);

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