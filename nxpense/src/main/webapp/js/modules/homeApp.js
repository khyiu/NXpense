(function(angular) {
  'use strict';

  var homeAppModule = angular.module('homeApp', ['ui.bootstrap']);

  homeAppModule.controller('userController', ['$scope', function($scope) {
    $scope.logout = function() {
      window.location.assign('/nxpense/logout');
    };
  }]);

  homeAppModule.controller('expenseController', ['$scope', '$modal', function($scope, $modal) {
    $scope.openNewExpenseModal = function() {
      var modalInstance = $modal.open({
        templateUrl: 'modal/new-expense-modal.html',
        controller: 'modalController'
      });
    }
  }]);

  homeAppModule.controller('modalController', ['$scope', '$modalInstance', function($scope, $modalInstance){
    $scope.ok = function() {
      $modalInstance.close();
    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    }
  }]);

})(window.angular);