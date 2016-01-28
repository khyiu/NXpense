(function () {
    'use strict';

    angular.module('homeApp').directive('nxAttachmentItem', attachmentItem);

    function attachmentItem() {
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

                            idxToDelete = $scope.expense.attachments.map(function (attachment) {
                                return attachment.filename;
                            }).indexOf(attachmentToRemove.filename);

                            $scope.expense.attachments.splice(idxToDelete, 1);
                        }
                    }
                };
            }
        };
    }
})();