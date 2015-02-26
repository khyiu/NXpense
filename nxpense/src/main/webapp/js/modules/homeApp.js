(function(angular) {
  'use strict';

  var homeAppModule = angular.module('homeApp', ['ui.bootstrap', 'restangular']);

  homeAppModule.controller('userController', ['$scope', function($scope) {
    $scope.logout = function() {
      window.location.assign('/nxpense/logout');
    };
  }]);

  homeAppModule.controller('expenseController', ['$scope', '$modal', function($scope, $modal) {
    $scope.openNewExpenseModal = function() {
      $modal.open({
        templateUrl: 'modal/new-expense-modal.html',
        controller: 'modalController'
      });
    }
  }]);

  homeAppModule.controller('modalController', ['$scope', '$modalInstance', 'Restangular', '$filter', function($scope, $modalInstance, Restangular, $filter) {
    // todo make root route 'nxpense' somehow global --> using Factory?
    var expenseDAO = Restangular.one('nxpense');

    $scope.ok = function() {
      // NOTE: 'date' filter is applied on the input date with a format that will strip down the time part.
      //       As a result, we don't need to worry about timezone side-effects
      var newExpense = {
        // todo stop using hard-coded value once the modal form has been tweaked to check credit card as an expense source
        source: 'DEBIT_CARD',
        date: $filter('date')($scope.date, 'dd/MM/yyyy'),
        amount: $scope.amount,
        description: $scope.description
      };

      expenseDAO.post('expense/new', newExpense);
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