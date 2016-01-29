(function () {
    'use strict';

    angular.module('homeApp').controller('tagController', TagController);

    TagController.$inject = ['$scope', 'Restangular', 'notificationHelper', 'underscore'];

    function TagController($scope, Restangular, notificationHelper, _) {
        var self = this;

        var defaultTag = {
            backgroundColor: '#8546EB',
            foregroundColor: '#FFFF00',
            name: null
        };
        var existingTagPreviousState;
        var tagDAO = Restangular.all('tag');

        this.currentTag = angular.copy(defaultTag, {});
        this.mode = 'Create';

        this.editTag = editTag;
        this.remove = remove;
        this.reset = reset;
        this.saveTag = saveTag;

        $scope.loadTags();

        /////////////////////////////

        function discardEditionInProgress() {
            if (existingTagPreviousState) {
                angular.copy(existingTagPreviousState, $scope.currentTag);
                existingTagPreviousState = null;
            }
        }

        function editTag(tag, $event) {
            if (tag && (_.isUndefined($event.keyCode) || $event.keyCode === 13)) {
                discardEditionInProgress();
                self.mode = 'Edit';
                self.existingTagPreviousState = angular.copy(tag, {});
                self.currentTag = tag;
                $event.preventDefault();
            }
        }

        function remove(tag, $event) {
            if (tag && (_.isUndefined($event.keyCode) || $event.keyCode === 13)) {
                notificationHelper.showServerInfo('Deleting tag...');

                tagDAO.customDELETE(tag.id).then(
                    function () {
                        notificationHelper.hideServerInfo();
                        $scope.loadTags();
                    },

                    function () {
                        notificationHelper.hideServerInfo();
                        notificationHelper.showOperationFailure('Failed deleting tag! Please retry later...');
                    }
                );
            }

            $event.stopPropagation();
        }

        function reset() {
            self.mode = 'Create';

            discardEditionInProgress();
            self.currentTag = angular.copy(defaultTag, {});
            $scope.tagForm.$setPristine();
        }

        function saveTag() {
            if (self.currentTag.id) {
                tagDAO.one(self.currentTag.id.toString()).customPUT(self.currentTag).then(saveTagOK, saveTagKO);
            } else {
                tagDAO.customPOST(self.currentTag).then(saveTagOK, saveTagKO);
            }
        }

        function saveTagKO(response) {
            var msgToDisplay = 'Failed saving tag!';
            var serverSideValidationMsg = response.headers('SERVERSIDE_VALIDATION_ERROR_MSG');
            var valid = serverSideValidationMsg ? false : true;

            if (response && response.status === 499 && response.data) {
                msgToDisplay = response.data;
            }

            notificationHelper.hideServerInfo();
            notificationHelper.showOperationFailure(msgToDisplay);

            $scope.tagForm.tagName.$setValidity('alreadyExists', valid);
        }

        function saveTagOK() {
            notificationHelper.hideServerInfo();
            notificationHelper.showOperationSuccess('Tag saved.');
            self.reset();
            $scope.loadTags();
        }
    }
})();