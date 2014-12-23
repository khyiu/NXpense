(function(angular) {
	'use strict';
	
	var loginAppModule = angular.module('loginApp', []);

	loginAppModule.controller('welcomeController', ['$scope', function($scope) {
		$scope.visiblePanel = 'login';
		
		$scope.goToRegistration = function() {
			$scope.visiblePanel = 'registration';
		};
		
		$scope.goToLogin = function() {
			$scope.visiblePanel = 'login';
		}
		
	}]);
	
	loginAppModule.controller('registrationController', ['$scope', function($scope) {
		$scope.email;
		$scope.password;
		$scope.passwordRepeat;
		
		$scope.createAccount = function () {
			console.log('>>> create account with ' + this.email);
		}
	}])
})(window.angular);