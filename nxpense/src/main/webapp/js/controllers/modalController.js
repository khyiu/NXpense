(function () {
    'use strict';

    angular.module('homeApp').controller('modalController', ModalController);

    ModalController.$inject = [
        '$rootScope', '$scope', '$modalInstance',
        'notificationHelper', 'selectedExpense', '$http',
        '$timeout', 'underscore'
    ];

    function ModalController($rootScope, $scope, $modalInstance,
                             notificationHelper, selectedExpense, $http,
                             $timeout, _) {
        var self = this;

        this.attachmentsToDisplay = [];
        this.newAttachments = [];
        this.newAttachmentsToDisplay = [];

        this.addAttachments = addAttachments;
        this.cancel = cancel;
        this.ok = ok;
        this.openFileSelector = openFileSelector;
        this.processKey = processKey;

        init();

        ///////////////////////////////

        function addAttachments(selectedFiles) {
            _.each(selectedFiles, function (selectedFile) {
                selectedFile.tempRandomId = Math.random();
                self.newAttachments.push(selectedFile);
                self.newAttachmentsToDisplay.push({
                    filename: selectedFile.name,
                    fileSize: selectedFile.size,
                    tempRandomId: selectedFile.tempRandomId
                });

                // This explicit call to $apply() was necessary, otherwise, the $scope.newAttachments wouldn't be
                // updated until the next action on the current scope...
                $scope.$apply();
            });
        }

        function buildRequest(formData) {
            var requestUrl;

            if (self.expense.id) {
                requestUrl = '/' + $scope.WEB_CONTEXT + '/expense/' + self.expense.id;
            } else {
                requestUrl = '/' + $scope.WEB_CONTEXT + '/expense';
            }

            return {
                method: 'POST',
                url: requestUrl,
                headers: {
                    'Content-Type': undefined
                },
                data: formData
            };
        }

        function cancel() {
            $modalInstance.dismiss();
        }

        function init() {
            if (selectedExpense) {
                self.expense = _.extend({}, selectedExpense);
            } else {
                self.expense = {
                    source: 'DEBIT_CARD',
                    attachments: []
                };
            }

            self.attachmentsToDisplay = [].concat(self.expense.attachments);
        }

        function ok() {
            var formData = new FormData();
            $modalInstance.close();
            notificationHelper.showServerInfo('Saving...');

            _.each($scope.newAttachments, function (attachment) {
                formData.append('attachments', attachment);
            });

            formData.append('expense', new Blob([JSON.stringify(self.expense)], {type: 'application/json'}));

            saveExpense(buildRequest(formData));
        }

        function openFileSelector() {
            $timeout(function triggerClickEvent() {
                angular.element('#attachment-selector').click();
            });
        }

        function processKey($event) {
            var tabbed = $event.keyCode === 9 && !$event.shiftKey;
            var backTabbed = $event.keyCode === 9 && $event.shiftKey;
            var activeElement = $(document.activeElement)[0];
            var firstTabbableElement;
            var lastTabbableElement;

            if (tabbed && activeElement.hasAttribute('lastTab')) {
                $event.preventDefault();
                firstTabbableElement = $(document).find('*[firstTab]');
                firstTabbableElement.focus();
            } else if (backTabbed && activeElement.hasAttribute('firstTab')) {
                $event.preventDefault();
                lastTabbableElement = $(document).find('*[lastTab]');
                lastTabbableElement.focus();
            }
        }

        function saveExpense(request) {
            $http(request).then(saveExpenseOK, saveExpenseKO);
        }

        function saveExpenseKO(data, status) {
            var msgToDisplay = 'Failed saving expense!';

            if (status === 499 && data) {
                msgToDisplay = data;
            }

            notificationHelper.hideServerInfo();
            notificationHelper.showOperationFailure(msgToDisplay);
        }

        function saveExpenseOK() {
            notificationHelper.hideServerInfo();
            notificationHelper.showOperationSuccess('Expense saved.');

            // send event to trigger reloading of current item page
            $rootScope.$broadcast('expense:reloadPage');
        }
    }
})();