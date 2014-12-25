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

	loginAppModule.controller('registrationController', ['$scope', '$http', function($scope, $http) {
		$scope.email;
		$scope.password;
		$scope.passwordRepeat;
		
		$scope.createAccount = function () {
			console.log('>>> createAccount');
			
			var request = {
				method: 'POST',
				url: '/nxpense/account/new',
				params: {
					email: this.email,
					password: this.password,
					passwordRepeat: this.passwordRepeat
				}	
			};
			
			$http(request).success(function(){
				console.log('>>> request sent with success');
			});
		}
		
		// First argument could be either a function returning the value to watch, or the value's name	
		$scope.$watch('passwordRepeat', function(newValue, oldValue, scope){
			var validationRule = 'confirmationCheck';
			
			// Custom validation rule: comparison of repeated password with chosen password if the
			// latter is not 'undefined'
			if(scope.password && scope.password !== newValue) {
				scope.registrationForm.passwordRepeat.$setValidity(validationRule, false);
			} else {
				scope.registrationForm.passwordRepeat.$setValidity(validationRule, true);
			}		
		});
	}])
})(window.angular);