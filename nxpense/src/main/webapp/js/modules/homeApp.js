(function(angular) {
	'use strict';
	
	var homeAppModule= angular.module('homeApp', []);
	
	homeAppModule.controller('userController', ['$scope', function($scope) {
		$scope.logout = function() {
			window.location.assign('/nxpense/logout');
		};
	}]);
	
})(window.angular);