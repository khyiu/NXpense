(function(angular) {
  'use strict';

  var homeAppModule = angular.module('homeApp', ['ui.bootstrap', 'restangular', 'notificationHelperModule']);

  // Run block to initialize attributes to be used across controllers that are not necessarily nested in each other
  // --> cannot benefit from scope hierarchy...
  homeAppModule.run(function($rootScope, $location) {
    // Deducing the current application's web context from the path to access the current page
    $rootScope.WEB_CONTEXT = window.location.pathname.split('/')[1];

    // Page size selected by default when data is bound to view
    $rootScope.pageSize = 10;

    // Page number selected by default = 1
    $rootScope.page = 1;
  });

  homeAppModule.controller('userController', ['$scope', function($scope) {
    $scope.logout = function() {
      window.location.assign('/nxpense/logout');
    };
  }]);

  homeAppModule.controller('expenseController', ['$scope', '$modal', 'Restangular', function($scope, $modal, Restangular) {
    $scope.openNewExpenseModal = function() {
      $modal.open({
        templateUrl: 'modal/new-expense-modal.html',
        controller: 'modalController'
      });
    }

    // todo: trigger reloading of data to display
    $scope.$watch('pageSize', function(newValue, oldValue) {
      var expenseDAO = Restangular.all($scope.WEB_CONTEXT + '/expense/list');
      var queryParameters = {
        page: $scope.page,
        size: newValue,
        direction: 'ASC',
        properties: ['position', 'date']
      };

      expenseDAO.getList(queryParameters).then(
        function() {
          console.log('>>> get expense list');
        }
      );
    });
  }]);

  homeAppModule.controller('modalController', ['$scope', '$modalInstance', 'Restangular', '$filter', 'notificationHelper',
    function($scope, $modalInstance, Restangular, $filter, notificationHelper) {
      var expenseDAO = Restangular.one($scope.WEB_CONTEXT);

      $scope.newExpense = {
        source: 'DEBIT_CARD'
      };

      $scope.ok = function() {
        // NOTE: 'date' filter is applied on the input date with a format that will strip down the time part.
        //       As a result, we don't need to worry about timezone side-effects
        this.newExpense.date = $filter('date')($scope.newExpense.date, 'dd/MM/yyyy');

        $modalInstance.close();
        notificationHelper.showServerInfo('Saving...');

        expenseDAO.post('expense/new', this.newExpense).then(
          function() {
            notificationHelper.hideServerInfo();
            notificationHelper.showOperationSuccess("Expense saved.");
          },

          function() {
            notificationHelper.hideServerInfo();
            notificationHelper.showOperationFailure("Failed saving expense!");
          }
        );
      };

      $scope.cancel = function() {
        $modalInstance.close();
      };

      $scope.openCal = function($event) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.calOpened = true;
      };

      $scope.processKey = function($event) {
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