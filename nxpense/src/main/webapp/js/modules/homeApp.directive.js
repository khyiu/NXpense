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

    homeAppDirectiveModule.directive('sideMenu', ['Restangular', '$rootScope', function (Restangular, $rootScope) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'navigation/side-menu.html',
            scope: {
            },
            controller: function ($scope, $rootScope) {
                $scope.logout = function () {
                    window.location.assign('/' + $rootScope.WEB_CONTEXT + '/logout');
                };
            }
        };
    }]);

    homeAppDirectiveModule.directive('nxExpenseTableFooter', ['Restangular', '$modal', 'notificationHelper', '$http', function (Restangular, $modal, notificationHelper, $http) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'pagination/expense-table-footer.html',
            controller: function ($scope, $rootScope) {
                // open modal for expense creation
                $scope.openNewExpenseModal = function () {
                    $modal.open({
                        templateUrl: 'modal/new-expense-modal.html',
                        controller: 'modalController',
                        resolve: {
                            selectedExpense: function () {
                                return null;
                            }
                        }
                    });
                };

                $scope.editSelectedExpense = function () {
                    $modal.open({
                        templateUrl: 'modal/new-expense-modal.html',
                        controller: 'modalController',
                        resolve: {
                            selectedExpense: function () {
                                return _.findWhere($scope.expenses, {selected: true});
                            }
                        },
                    }).opened.then(function () {
                            //  NOTE: setting a timeout to wait for the opened modal to be actually visible
                            //        before triggering the auto-resize on the text area
                            setTimeout(function () {
                                $('#description > textarea').trigger('autosize.resize');
                            }, 100);
                        });
                };

                $scope.disableEditButton = function () {
                    var hasOnlyOneSelectedExpense = $scope.expenses && _.where($scope.expenses, {selected: true}).length === 1;
                    return !hasOnlyOneSelectedExpense;
                };

                $scope.deleteSelected = function () {
                    var expensesToDelete = _.where($scope.expenses, {selected: true});
                    var deleteRequest = {
                        method: 'DELETE',
                        url: '/' + $scope.WEB_CONTEXT + '/expense',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        data: []
                    };

                    _.each(expensesToDelete, function(expenseToDelete) {
                        deleteRequest.data.push({
                            id: expenseToDelete.id,
                            version: expenseToDelete.version
                        });
                    });

                    $http(deleteRequest).then(
                        function () {
                            notificationHelper.showOperationSuccess("Expense(s) deleted.");

                            // send event to trigger reloading of current item page
                            $rootScope.$broadcast('expense:reloadPage');
                        },

                        function (error) {
                            var msgToDisplay = "Failed deleting expenses! Please retry later...";

                            if(error && error.status === 499 && error.data) {
                                msgToDisplay = error.data;
                            }

                            notificationHelper.hideServerInfo();
                            notificationHelper.showOperationFailure(msgToDisplay);
                        }
                    );
                };

                $scope.disableDeleteButton = function () {
                    var hasSelectedItems = $scope.expenses && _.findWhere($scope.expenses, {selected: true});
                    return !hasSelectedItems;
                };

                $scope.getPageFirstItem = function () {
                    return $rootScope.page + (($rootScope.page - 1) * ($rootScope.pageSize - 1));
                };

                $scope.getPageLastItem = function () {
                    return $rootScope.page * $rootScope.pageSize;
                };

                // bound to expense-table-footer.html with getterSetter option...
                $scope.page = function (newSelectedPage) {
                    var newSelectPageIndex;
                    if (newSelectedPage) {
                        newSelectPageIndex = parseInt(newSelectedPage, 10);

                        if (!isNaN(newSelectPageIndex) && newSelectedPage > 0 && newSelectedPage <= $rootScope.totalNumberOfPage) {
                            $rootScope.page = parseInt(newSelectedPage, 10);
                            $rootScope.$broadcast('expense:reloadPage');
                        }
                    }

                    return $rootScope.page;
                };

                $scope.goToNextPage = function ($event, step) {
                    $event.preventDefault();
                    $scope.page($rootScope.page + step);
                };
            }
        };
    }]);

})(window.angular);