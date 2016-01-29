(function (angular) {
    'use strict';

    var homeAppModule = angular.module('homeApp', ['ngRoute', 'ui.bootstrap', 'restangular', 'notificationHelperModule', 'ngDraggable']);

    // Run block to initialize attributes to be used across controllers that are not necessarily nested in each other
    // --> cannot benefit from scope hierarchy...
    homeAppModule.run(function ($rootScope, Restangular, notificationHelper) {
        // Deducing the current application's web context from the path to access the current page
        $rootScope.WEB_CONTEXT = window.location.pathname.split('/')[1];

        // Page size selected by default when data is bound to view
        $rootScope.pageSize = 10;

        // Page number selected by default = 1
        $rootScope.page = 1;

        $rootScope.loadTags = function () {
            notificationHelper.showServerInfo('Fetching tags...');
            Restangular.all('tag').customGET('user').then(
                function (tags) {
                    notificationHelper.hideServerInfo();
                    $rootScope.existingTags = tags;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure('Failed fetching tags! Please retry later...');
                }
            );
        };

        $rootScope.loadTags();
    });

    homeAppModule.config(function (RestangularProvider, $routeProvider) {
        // Deducing the current application's web context from the path to access the current page + use it as base url in Restangular
        var webContext = window.location.pathname.split('/')[1];
        RestangularProvider.setBaseUrl('/' + webContext);

        // Routes configuration
        $routeProvider.when('/expense/details', {
            templateUrl: 'views/expense-details.html',
            controller: 'expenseController'
        }).when('/tag/management', {
            templateUrl: 'views/tag-manage.html',
            controller: 'tagController',
            controllerAs: 'tagController'
        }).otherwise({
            redirectTo: '/expense/details'
        });
    });



    homeAppModule.controller('expenseController', ['$rootScope', '$scope', 'Restangular', 'notificationHelper', function ($rootScope, $scope, Restangular, notificationHelper) {
        var expenseDAO = Restangular.one('expense');
        $scope.sortProp = 'date';
        $scope.sortAsc = true;

        $scope.dropCompleted = function (expenseId, $tag) {
            expenseDAO.one(expenseId.toString()).one('tag').put({
                id: $tag.id
            }).then(
                function (targetExpenseTags) {
                    var updatedExpense = _.findWhere($scope.expenses, {id: expenseId});
                    updatedExpense.tags = targetExpenseTags;
                },

                function () {
                    notificationHelper.showOperationFailure('Failed adding tag! Please retry later...');
                }
            )
        };

        $scope.updatePageSize = function (newPageSize) {
            var newPageSizeInt;

            if (newPageSize) {
                newPageSizeInt = parseInt(newPageSize, 10);

                if (!isNaN(newPageSizeInt)) {
                    $rootScope.pageSize = newPageSize;
                    $rootScope.page = 1;
                    $scope.reloadPage();
                }
            }
        };

        $scope.reloadPage = function () {
            var queryParameters = {
                page: $rootScope.page,
                size: $rootScope.pageSize,
                direction: $scope.sortAsc ? 'ASC' : 'DESC',
                properties: [$scope.sortProp, 'position']
            };

            // load balance info
            expenseDAO.customGET('balance').then(
                function(balance) {
                    $scope.balance = balance;
                },

                function() {
                    notificationHelper.showOperationFailure('Failed fetching balance!');
                }
            );

            // load expense items
            notificationHelper.showServerInfo('Fetching expenses...');
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
                    notificationHelper.showOperationFailure('Failed fetching expenses! Please retry later...');
                }
            );
        };

        $scope.$watch('pageSize', $scope.updatePageSize);

        $scope.$on('expense:reloadPage', function () {
            $scope.reloadPage();
            $scope.selectedAll = false;
        });

        // Function called when the 'select all' checkbox value is changed --> based on the new value, all visible
        // expenses will be un-checked.
        // The $event parameter may be:
        // - undefined --> event initiated by mouse
        // - defined --> event initiated by keyboard
        $scope.toggleSelectAll = function ($event) {
            if ($event) {
                if ($event.keyCode === 13) {
                    $scope.selectedAll = !$scope.selectedAll;
                    $event.preventDefault();
                } else {
                    return;
                }
            }

            _.each($scope.expenses, function (expense) {
                expense.selected = $scope.selectedAll;
            });
        };

        $scope.toggleItemSelection = function($event, expense) {
            expense.selected = !expense.selected;
            this.updateGlobalItemSelection($event, expense);
        };

        $scope.updateGlobalItemSelection = function ($event) {
            var numberOfSelectedItem;

            if ($event.originalEvent instanceof MouseEvent || $event.keyCode === 13) {
                $event.stopPropagation();

                // Update 'selectedAll' attribute accordingly only if its value will change --> prevent $digest loop to run if not actually necessary
                numberOfSelectedItem = _.where($scope.expenses, {selected: true}).length;

                if (numberOfSelectedItem < $scope.expenses.length && $scope.selectedAll) {
                    $scope.selectedAll = false;
                } else if (numberOfSelectedItem === $scope.expenses.length && !$scope.selectedAll) {
                    $scope.selectedAll = true;
                }
            }
        };

        // Function to handle changing sort criteria (property/direction).
        // If $event is provided, the event is triggered by keyboard --> restrict on key = ENTER
        $scope.updateSort = function ($event, sortProp) {
            if (_.isUndefined($event) || $event.keyCode === 13) {
                if ($scope.sortProp === sortProp) {
                    $scope.sortAsc = !$scope.sortAsc;
                } else {
                    $scope.sortProp = sortProp;
                    $scope.sortAsc = true;
                }

                $scope.reloadPage();
            }
        };

        // Removal of tag from expense item
        $scope.removeTag = function($event, expense, tagName) {
            $event.preventDefault();
            notificationHelper.showServerInfo('Removing tag...');

            expenseDAO.one(expense.id.toString()).one('tag').customDELETE(tagName).then(
                function(updatedExpense) {
                    notificationHelper.hideServerInfo();
                    expense.tags = updatedExpense.tags;
                },

                function() {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure('Failed removing tag from expense! Please retry later...');
                }
            );

        };
    }]);



})(window.angular);