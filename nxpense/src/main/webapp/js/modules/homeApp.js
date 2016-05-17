(function (angular) {
    'use strict';

    var homeAppModule = angular.module('homeApp', ['ngRoute', 'ui.bootstrap', 'notificationHelperModule', 'ngDraggable']);

    // Run block to initialize attributes to be used across controllers that are not necessarily nested in each other
    // --> cannot benefit from scope hierarchy...
    homeAppModule.run(function ($rootScope) {
        // Deducing the current application's web context from the path to access the current page
        $rootScope.WEB_CONTEXT = window.location.pathname.split('/')[1];

        // Page size selected by default when data is bound to view
        $rootScope.pageSize = 10;

        // Page number selected by default = 1
        $rootScope.page = 1;
    });

    homeAppModule.config(function ($routeProvider) {
        // Routes configuration
        $routeProvider.when('/expense/details', {
            templateUrl: 'views/expense-details.html',
            controller: 'expenseController',
            controllerAs: 'expenseController'
        }).when('/tag/management', {
            templateUrl: 'views/tag-manage.html',
            controller: 'tagController',
            controllerAs: 'tagController'
        }).otherwise({
            redirectTo: '/expense/details'
        });
    });
})(window.angular);