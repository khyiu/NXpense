(function(angular) {
	'use strict';

	var loginAppModule = angular.module('loginApp', []);

	// Controller to manage flippable containers
	loginAppModule.controller('welcomeController', ['$scope', function($scope) {
		$scope.visiblePanel = 'login';

		$scope.goToRegistration = function() {
			$scope.visiblePanel = 'registration';
		};

		$scope.goToLogin = function() {
			$scope.visiblePanel = 'login';
		}

	}]);
	
	loginAppModule.controller('loginController', ['$scope', '$http', function($scope, $http) {
		$scope.email;
		$scope.password;
		$scope.rememberMe;
		
		$scope.login = function () {
			var rememberMe = this.rememberMe || false;
			var request = {
					method: 'POST',
					url: '/nxpense/login',
					params: {
						email: this.email,
						password: this.password,
						remember_me: rememberMe 
					}
			};
			
			// Angular does not implicitly follow redirection sent by the server 
			// --> user $location service to handle it client-side
			var followRedirection = function() {
				alert('>>> follow redirection');
			};
			var response = $http(request);
			
			// TODO handle login response
			response.success(function(data, status, headers, config) {
				console.log('>>> handle login callback');
			});
		};
	}]);

	// Controller to manage registration process
	loginAppModule.controller('registrationController', ['$scope', '$http', function($scope, $http) {
		var emailTakenRule = 'emailTaken';
		$scope.email;
		$scope.password;
		$scope.passwordRepeat;
		
		$scope.createAccount = function () {
			var request = {
				method: 'POST',
				url: '/nxpense/account/new',
				params: {
					email: this.email,
					password: this.password,
					passwordRepeat: this.passwordRepeat
				}	
			};
			
			var response = $http(request);
			
			response.success(function(){
				console.log('>>> account creation request with success');
			});

			response.error(function(data, status) {
				if(status === 409) {
					console.log($scope.email);
					$scope.registrationForm.email.$setValidity(emailTakenRule, false);
				}	
			});
		}
		
		// When passwordRepeat field changes, check if it matches password
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
		
		// When email field changes, if there is a previous "email taken" message, cleanse it
		$scope.$watch('email', function(newValue, oldValue, scope){
			var isEmailTaken = scope.registrationForm.email.$error[emailTakenRule];
			
			if(isEmailTaken) {
				scope.registrationForm.email.$setValidity(emailTakenRule, true);
			}
		});
	}]);
})(window.angular);