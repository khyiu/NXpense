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

    homeAppModule.controller('userController', ['$scope', function ($scope) {
        $scope.logout = function () {
            window.location.assign('/' + $scope.WEB_CONTEXT + '/logout');
        };
    }]);

    homeAppModule.controller('expenseController', ['$rootScope', '$scope', '$modal', 'Restangular', 'notificationHelper', function ($rootScope, $scope, $modal, Restangular, notificationHelper) {
        $scope.changePageSizeCallback = function (newValue) {
            var expenseDAO = Restangular.one('expense');
            var queryParameters = {
                page: $scope.page,
                size: newValue || $scope.pageSize,
                direction: 'ASC',
                properties: ['position', 'date']
            };

            notificationHelper.showServerInfo("Fetching expenses...");
            expenseDAO.one('page').get(queryParameters).then(
                function (response) {
                    notificationHelper.hideServerInfo();

                    $scope.expenses = response.items;
                    $rootScope.numberOfExpense = response.numberOfItems;
                    $rootScope.pageSize = response.pageSize;
                    $rootScope.page = response.pageNumber;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure("Failed fetching expenses! Please retry later...");
                }
            );
        };

        $scope.openNewExpenseModal = function () {
            $modal.open({
                templateUrl: 'modal/new-expense-modal.html',
                controller: 'modalController'
            });
        };

        $scope.$watch('pageSize', $scope.changePageSizeCallback);
        $scope.$on('expense:created', function() {
            $scope.changePageSizeCallback();
        });
    }]);

    homeAppModule.controller('modalController', ['$rootScope', '$scope', '$modalInstance', 'Restangular', '$filter', 'notificationHelper',
        function ($rootScope, $scope, $modalInstance, Restangular, $filter, notificationHelper) {
            var expenseDAO = Restangular.one('expense');

            $scope.newExpense = {
                // default expense source when creating a new expense item
                source: 'DEBIT_CARD'
            };

            $scope.ok = function () {
                $modalInstance.close();
                notificationHelper.showServerInfo('Saving...');

                expenseDAO.all('new').post(this.newExpense).then(
                    function () {
                        notificationHelper.hideServerInfo();
                        notificationHelper.showOperationSuccess("Expense saved.");

                        // send event to trigger items fetching
                        $rootScope.$broadcast('expense:created');
                    },

                    function () {
                        notificationHelper.hideServerInfo();
                        notificationHelper.showOperationFailure("Failed saving expense!");
                    }
                );
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