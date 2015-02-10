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
        controller: 'modalController',
        size: 'lg'
      });
    }
  }]);

  homeAppModule.controller('modalController', ['$scope', '$modalInstance', function($scope, $modalInstance){
    $scope.opened = false;

    $scope.ok = function() {
      console.log('>>> clicked on modal OK');
      $modalInstance.close();
    };

    $scope.cancel = function() {
      console.log('>>> clicked on modal CANCEL');
      $modalInstance.dismiss('cancel');
    };

    $scope.openCal = function($event) {
      $event.preventDefault();
      $event.stopPropagation();
      console.log('>>> open cal');

      $scope.opened = true;
    };
  }]);

})(window.angular);