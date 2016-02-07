(function (angular) {
    'use strict';

    var homeAppModule = angular.module('homeApp', ['ngRoute', 'ui.bootstrap', 'restangular', 'notificationHelperModule', 'ngDraggable']);

    // Run block to initialize attributes to be used across controllers that are not necessarily nested in each other
    // --> cannot benefit from scope hierarchy...
    homeAppModule.run(function ($rootScope, Restangular, notificationHelper) {
        // Deducing the current application's web context from the path to access the current page
        $rootScope.WEB_CONTEXT = window.location.pathname.split('/')[1];

        // Page size selected by default when data is bound to view
        $rootScope.pageSize = 10;

        // Page number selected by default = 1
        $rootScope.page = 1;

        $rootScope.loadTags = function () {
            notificationHelper.showServerInfo('Fetching tags...');
            Restangular.all('tag').customGET('user').then(
                function (tags) {
                    notificationHelper.hideServerInfo();
                    $rootScope.existingTags = tags;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure('Failed fetching tags! Please retry later...');
                }
            );
        };

        $rootScope.loadTags();
    });

    homeAppModule.config(function (RestangularProvider, $routeProvider) {
        // Deducing the current application's web context from the path to access the current page + use it as base url in Restangular
        var webContext = window.location.pathname.split('/')[1];
        RestangularProvider.setBaseUrl('/' + webContext);

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