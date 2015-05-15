(function(angular) {
    'use strict';

    var homeAppDirectiveModule = angular.module('homeApp.directive', ['ui.bootstrap', 'restangular', 'notificationHelperModule']);


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

    homeAppDirectiveModule.directive('nxExpenseTableFooter', ['Restangular', '$modal', 'notificationHelper', function (Restangular, $modal, notificationHelper) {
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
                        }
                    });
                };

                $scope.disableEditButton = function () {
                    var hasOnlyOneSelectedExpense = $scope.expenses && _.where($scope.expenses, {selected: true}).length === 1;
                    return !hasOnlyOneSelectedExpense;
                };

                $scope.deleteSelected = function () {
                    var idsToDelete = _.where($scope.expenses, {selected: true});
                    idsToDelete = _.pluck(idsToDelete, 'id');

                    var expenseDao = Restangular.all('expense');
                    var queryParameters = {
                        ids: idsToDelete
                    };

                    expenseDao.remove(queryParameters).then(
                        function () {
                            notificationHelper.showOperationSuccess("Expense(s) deleted.");

                            // send event to trigger reloading of current item page
                            $rootScope.$broadcast('expense:reloadPage');
                        },

                        function () {
                            notificationHelper.hideServerInfo();
                            notificationHelper.showOperationFailure("Failed deleting expenses! Please retry later...");
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