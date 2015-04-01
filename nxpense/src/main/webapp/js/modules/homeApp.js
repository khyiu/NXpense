(function(angular) {
  'use strict';

  var homeAppModule = angular.module('homeApp', ['ui.bootstrap', 'restangular', 'notificationHelperModule']);

  // Run block to initialize attributes to be used across controllers that are not necessarily nested in each other
  // --> cannot benefit from scope hierarchy...
  homeAppModule.run(function($rootScope) {
    // Deducing the current application's web context from the path to access the current page
    $rootScope.WEB_CONTEXT = window.location.pathname.split('/')[1];

    // Page size selected by default when data is bound to view
    $rootScope.pageSize = 10;

    // Page number selected by default = 1
    $rootScope.page = 1;
  });

  homeAppModule.config(function(RestangularProvider) {
    // Deducing the current application's web context from the path to access the current page + use it as base url in Restangular
    var webContext = window.location.pathname.split('/')[1];
    RestangularProvider.setBaseUrl('/' + webContext);
  });

  homeAppModule.controller('userController', ['$scope', function($scope) {
    $scope.logout = function() {
      window.location.assign('/' + $scope.WEB_CONTEXT + '/logout');
    };
  }]);

  homeAppModule.controller('expenseController', ['$rootScope', '$scope', '$modal', 'Restangular', 'notificationHelper', function($rootScope, $scope, $modal, Restangular, notificationHelper) {
    $scope.bypassCallbackOnce = false;
    $scope.changePageSizeCallback = function(newValue) {
      var expenseDAO = Restangular.one('expense');
      var queryParameters = {
        page: $scope.page,
        size: newValue,
        direction: 'ASC',
        properties: ['position', 'date']
      };

      notificationHelper.showServerInfo("Fetching expenses...");
      expenseDAO.one('page').get(queryParameters).then(
        function(response) {
          notificationHelper.hideServerInfo();
          $scope.bypassCallbackOnce = true;

          $scope.expenses = response.items;
          $rootScope.numberOfExpense = response.numberOfItems;
          $rootScope.pageSize = response.pageSize;
          $rootScope.page = response.pageNumber + 1;
        },

        function() {
          notificationHelper.hideServerInfo();
          notificationHelper.showOperationFailure("Failed fetching expenses! Please retry later...");
        }
      );
    };

    $scope.openNewExpenseModal = function() {
      $modal.open({
        templateUrl: 'modal/new-expense-modal.html',
        controller: 'modalController'
      });
    };

    $scope.$watch('pageSize', function(newValue) {
      if($scope.bypassCallbackOnce) {
        $scope.bypassCallbackOnce = false;
      } else {
        $scope.changePageSizeCallback(newValue);
      }
    });
  }]);

  homeAppModule.controller('modalController', ['$scope', '$modalInstance', 'Restangular', '$filter', 'notificationHelper',
    function($scope, $modalInstance, Restangular, $filter, notificationHelper) {
      var expenseDAO = Restangular.one('expense');

      $scope.newExpense = {
        source: 'DEBIT_CARD'
      };

      $scope.ok = function() {
        // NOTE: 'date' filter is applied on the input date with a format that will strip down the time part.
        //       As a result, we don't need to worry about timezone side-effects
        this.newExpense.date = $filter('date')($scope.newExpense.date, 'dd/MM/yyyy');

        $modalInstance.close();
        notificationHelper.showServerInfo('Saving...');

        expenseDAO.all('new').post(this.newExpense).then(
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