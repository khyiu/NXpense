(function(angular) {
	'use strict';
	
	var loginAppModule = angular.module('loginApp', []);

	loginAppModule.controller('loginController', ['$scope', function($scope) {
		$scope.visiblePanel = 'login';
		
		$scope.goToRegistration = function() {
			$scope.visiblePanel = 'registration';
		};
		
		$scope.goToLogin = function() {
			$scope.visiblePanel = 'login';
		}
		
	}]);
})(window.angular);