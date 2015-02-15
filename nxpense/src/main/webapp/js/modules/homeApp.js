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
		$scope.opened = false;

		$scope.ok = function() {
			$modalInstance.close();
		};

		$scope.cancel = function() {
			console.log('>>> clicked on modal CANCEL');
		};

		$scope.openCal = function($event) {
			$event.preventDefault();
			$event.stopPropagation();

			$scope.opened = true;
		};

		$scope.processKey = function($event) {
			var tabbed = $event.keyCode === 9 && !$event.shiftKey;
			var backTabbed = $event.keyCode === 9 && $event.shiftKey;
			var activeElement = $(document.activeElement)[0];
			var firstTabbableElement;
			var lastTabbableElement;
			
			if(tabbed && activeElement.hasAttribute('lastTab')) {
				$event.preventDefault();
				firstTabbableElement = $(document).find('*[firstTab]');
				firstTabbableElement.focus();
			} else if(backTabbed && activeElement.hasAttribute('firstTab')) {
				$event.preventDefault();
				lastTabbableElement = $(document).find('*[lastTab]');
				lastTabbableElement.focus();
			}
		};
	}]);

})(window.angular);