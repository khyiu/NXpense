(function () {
    'use strict';

    angular.module('homeApp').directive('nxExpenseTableFooter', nxExpenseTableFooter);

    nxExpenseTableFooter.$inject = ['$modal', 'notificationHelper', '$http', '$timeout'];

    function nxExpenseTableFooter($modal, notificationHelper, $http, $timeout) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'pagination/expense-table-footer.html',

            controller: function ($scope, $rootScope) {

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

                    _.each(expensesToDelete, function (expenseToDelete) {
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

                            if (error && error.status === 499 && error.data) {
                                msgToDisplay = error.data;
                            }

                            notificationHelper.hideServerInfo();
                            notificationHelper.showOperationFailure(msgToDisplay);
                        }
                    );
                };

                $scope.disableDeleteButton = function () {
                    var hasSelectedItems = $scope.expenseController.expenses && _.findWhere($scope.expenseController.expenses, {selected: true});
                    return !hasSelectedItems;
                };

                $scope.disableEditButton = function () {
                    var hasOnlyOneSelectedExpense = $scope.expenseController.expenses &&
                        _.where($scope.expenseController.expenses, {selected: true}).length === 1;
                    return !hasOnlyOneSelectedExpense;
                };

                $scope.editSelectedExpense = function () {
                    $modal.open({
                        templateUrl: 'modal/expense-modal.html',
                        controller: 'modalController',
                        controllerAs: 'modalController',
                        resolve: {
                            selectedExpense: function () {
                                return _.findWhere($scope.expenses, {selected: true});
                            }
                        }
                    }).opened.then(function () {
                        $timeout(function dynamizeTextarea() {
                            $('#description').find('> textarea').trigger('autosize.resize');
                        }, 100);
                    });
                };

                $scope.getPageFirstItem = function () {
                    return $rootScope.page + (($rootScope.page - 1) * ($rootScope.pageSize - 1));
                };

                $scope.getPageLastItem = function () {
                    return $rootScope.page * $rootScope.pageSize;
                };

                $scope.goToNextPage = function ($event, step) {
                    $event.preventDefault();
                    $scope.page($rootScope.page + step);
                };

                $scope.openNewExpenseModal = function () {
                    $modal.open({
                        templateUrl: 'modal/expense-modal.html',
                        controller: 'modalController',
                        controllerAs: 'modalController',
                        resolve: {
                            selectedExpense: function () {
                                return null;
                            }
                        }
                    });
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
            }
        };
    }
})();