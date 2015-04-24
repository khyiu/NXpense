(function (angular) {
    'use strict';

    var homeAppModule = angular.module('homeApp', ['ui.bootstrap', 'restangular', 'notificationHelperModule']);

    // Run block to initialize attributes to be used across controllers that are not necessarily nested in each other
    // --> cannot benefit from scope hierarchy...
    homeAppModule.run(function ($rootScope) {
        // Deducing the current application's web context from the path to access the current page
        $rootScope.WEB_CONTEXT = window.location.pathname.split('/')[1];

        // Page size selected by default when data is bound to view
        $rootScope.pageSize = 10;

        // Page number selected by default = 1
        $rootScope.page = 1;
    });

    homeAppModule.config(function (RestangularProvider) {
        // Deducing the current application's web context from the path to access the current page + use it as base url in Restangular
        var webContext = window.location.pathname.split('/')[1];
        RestangularProvider.setBaseUrl('/' + webContext);
    });

    homeAppModule.directive('nxExpenseTableFooter', ['Restangular', '$modal', 'notificationHelper', function (Restangular, $modal, notificationHelper) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'pagination/expense-table-footer.html',
            controller: function($scope, $rootScope) {
                // open modal for expense creation
                $scope.openNewExpenseModal = function () {
                    $modal.open({
                        templateUrl: 'modal/new-expense-modal.html',
                        controller: 'modalController',
                        resolve: {
                            selectedExpense: function() {
                                return null;
                            }
                        }
                    });
                };

                $scope.editSelectedExpense = function() {
                    $modal.open({
                        templateUrl: 'modal/new-expense-modal.html',
                        controller: 'modalController',
                        resolve: {
                            selectedExpense: function() {
                                return _.findWhere($scope.expenses, {selected: true});
                            }
                        }
                    });
                };

                $scope.disableEditButton = function() {
                  var hasOnlyOneSelectedExpense = $scope.expenses && _.where($scope.expenses, {selected: true}).length === 1;
                  return !hasOnlyOneSelectedExpense;
                };

                $scope.deleteSelected = function() {
                    var idsToDelete = _.where($scope.expenses, {selected: true});
                    idsToDelete = _.pluck(idsToDelete, 'id');

                    var expenseDao = Restangular.all('expense');
                    var queryParameters = {
                        ids: idsToDelete
                    };

                    expenseDao.remove(queryParameters).then(
                      function() {
                          notificationHelper.showOperationSuccess("Expense(s) deleted.");

                          // send event to trigger reloading of current item page
                          $rootScope.$broadcast('expense:reloadPage');
                      },

                      function() {
                          notificationHelper.hideServerInfo();
                          notificationHelper.showOperationFailure("Failed deleting expenses! Please retry later...");
                      }
                    );
                };

                $scope.disableDeleteButton = function() {
                  var hasSelectedItems = $scope.expenses && _.findWhere($scope.expenses, {selected: true});
                  return !hasSelectedItems;
                };

                $scope.getPageFirstItem = function() {
                    return $rootScope.page + (($rootScope.page - 1) * ($rootScope.pageSize - 1));
                };

                $scope.getPageLastItem = function() {
                    return $rootScope.page * $rootScope.pageSize;
                };

                // bound to expense-table-footer.html with getterSetter option...
                $scope.page = function(newSelectedPage) {
                    var newSelectPageIndex;
                    if (newSelectedPage) {
                        newSelectPageIndex = parseInt(newSelectedPage, 10);

                        if(!isNaN(newSelectPageIndex) && newSelectedPage > 0 && newSelectedPage <= $rootScope.totalNumberOfPage) {
                            $rootScope.page = parseInt(newSelectedPage, 10);
                            $rootScope.$broadcast('expense:reloadPage');
                        }
                    }

                    return $rootScope.page;
                };

                $scope.goToNextPage = function($event, step) {
                    $event.preventDefault();
                    $scope.page($rootScope.page + step);
                };
            }
        };
    }]);

    homeAppModule.controller('userController', ['$scope', function ($scope) {
        $scope.logout = function () {
            window.location.assign('/' + $scope.WEB_CONTEXT + '/logout');
        };
    }]);

    homeAppModule.controller('expenseController', ['$rootScope', '$scope', 'Restangular', 'notificationHelper', function ($rootScope, $scope, Restangular, notificationHelper) {
        $scope.updatePageSize = function(newPageSize) {
            var newPageSizeInt;

            if(newPageSize) {
                newPageSizeInt = parseInt(newPageSize, 10);

                if(!isNaN(newPageSizeInt)) {
                    $rootScope.pageSize = newPageSize;
                    $rootScope.page = 1;
                    $scope.reloadPage();
                }
            }
        };

        $scope.reloadPage = function () {
            var expenseDAO = Restangular.one('expense');
            var queryParameters = {
                page: $rootScope.page,
                size: $rootScope.pageSize,
                direction: 'ASC',
                properties: ['date', 'position']
            };

            notificationHelper.showServerInfo("Fetching expenses...");
            expenseDAO.one('page').get(queryParameters).then(
                function (response) {
                    notificationHelper.hideServerInfo();

                    $scope.expenses = response.items;
                    $rootScope.numberOfExpense = response.numberOfItems;
                    $rootScope.pageSize = response.pageSize;
                    $scope.pageSize = response.pageSize;
                    $rootScope.page = response.pageNumber;
                    $rootScope.totalNumberOfExpense = response.totalNumberOfItems;
                    $rootScope.totalNumberOfPage = response.totalNumberOfPages;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure("Failed fetching expenses! Please retry later...");
                }
            );
        };

        $scope.$watch('pageSize', $scope.updatePageSize);

        $scope.$on('expense:reloadPage', function() {
            $scope.reloadPage();
            $scope.selectedAll = false;
        });

        // Function called when the 'select all' checkbox value is changed --> based on the new value, all visible
        // expenses will be un-checked.
        // The $event parameter may be:
        // - undefined --> event initiated by mouse
        // - defined --> event initiated by keyboard
        $scope.toggleSelectAll = function($event) {
            if($event) {
                if($event.keyCode === 13) {
                    $scope.selectedAll = !$scope.selectedAll;
                    $event.preventDefault();
                } else {
                    return;
                }
            }

            _.each($scope.expenses, function(expense) {
                expense.selected = $scope.selectedAll;
            });
        };

        $scope.toggleItemSelection = function($event, expense) {
            var numberOfSelectedItem;

            // if $event provided -> event triggered by keyboard --> check that pressed key = space bar
            // if $event == undefined -> event triggered by mouse
            if(_.isUndefined($event) || $event.keyCode === 13) {
                expense.selected = !expense.selected;

                // Update 'selectedAll' attribute accordingly only if its value will change --> prevent $digest loop to run if not actually necessary
                numberOfSelectedItem = _.where($scope.expenses, {selected: true}).length;

                if(numberOfSelectedItem < $scope.expenses.length && $scope.selectedAll) {
                    $scope.selectedAll = false;
                } else if (numberOfSelectedItem === $scope.expenses.length && !$scope.selectedAll) {
                    $scope.selectedAll = true;
                }
            }
        };
    }]);

    homeAppModule.controller('modalController', ['$rootScope', '$scope', '$modalInstance', 'Restangular', '$filter', 'notificationHelper', 'selectedExpense',
        function ($rootScope, $scope, $modalInstance, Restangular, $filter, notificationHelper, selectedExpense) {
            var expenseDAO = Restangular.one('expense');

            if(selectedExpense) {
                $scope.expense = selectedExpense;
            } else {
                $scope.expense = {source: 'DEBIT_CARD'};
            }

            $scope.ok = function () {
                var successCallback = function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationSuccess("Expense saved.");

                    // send event to trigger reloading of current item page
                    $rootScope.$broadcast('expense:reloadPage');
                };

                var failureCallback = function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure("Failed saving expense!");
                };

                $modalInstance.close();
                notificationHelper.showServerInfo('Saving...');

                if(this.expense.id) {
                    expenseDAO.customPUT(this.expense, this.expense.id).then(successCallback, failureCallback);
                }else {
                    expenseDAO.customPOST(this.expense).then(successCallback, failureCallback);
                }
            };

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.processKey = function ($event) {
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
            };
        }]);

})(window.angular);