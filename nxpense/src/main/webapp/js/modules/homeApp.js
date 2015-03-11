(function (angular) {
    'use strict';

    var homeAppModule = angular.module('homeApp', ['ui.bootstrap', 'restangular', 'notificationHelperModule']);

    homeAppModule.controller('userController', ['$scope', function ($scope) {
        $scope.logout = function () {
            window.location.assign('/nxpense/logout');
        };
    }]);

    homeAppModule.controller('expenseController', ['$scope', '$modal', function ($scope, $modal) {
        $scope.openNewExpenseModal = function () {
            $modal.open({
                templateUrl: 'modal/new-expense-modal.html',
                controller: 'modalController'
            });
        }
    }]);

    homeAppModule.controller('modalController', ['$scope', '$modalInstance', 'Restangular', '$filter', 'notificationHelper',
        function ($scope, $modalInstance, Restangular, $filter, notificationHelper) {
        // todo make root route 'nxpense' somehow global --> using Factory?
        var expenseDAO = Restangular.one('nxpense');

        $scope.newExpense = {
            source: 'DEBIT_CARD'
        };

        $scope.ok = function () {
            // NOTE: 'date' filter is applied on the input date with a format that will strip down the time part.
            //       As a result, we don't need to worry about timezone side-effects
            this.newExpense.date = $filter('date')($scope.newExpense.date, 'dd/MM/yyyy');

            $modalInstance.close();
            notificationHelper.showServerInfo('Saving...');

            expenseDAO.post('expense/new', this.newExpense).then(
                function(){
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationSuccess("Expense saved.");
                },

                function(){
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure("Failed saving expense!");
                }
            );
        };

        $scope.cancel = function () {
            $modalInstance.close();
        };

        $scope.openCal = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.calOpened = true;
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