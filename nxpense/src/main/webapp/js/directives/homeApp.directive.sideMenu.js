(function () {
    'use strict';

    angular.module('homeApp.directive').directive('nxSideMenu', SideMenu);

    SideMenu.$inject = ['$rootScope'];

    function SideMenu($rootScope) {
        return {
            restrict: 'E',
            transclude: true,
            templateUrl: 'navigation/side-menu.html',
            controller: SideMenuController,
            controllerAs: 'sideMenuController'
        };

        function SideMenuController() {
            this.logout = logout;

            /////////////////////

            function logout() {
                window.location.assign('/' + $rootScope.WEB_CONTEXT + '/logout');
            }
        }
    }
})();