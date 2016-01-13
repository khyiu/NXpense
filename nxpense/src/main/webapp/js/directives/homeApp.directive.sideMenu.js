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
            var self = this;

            this.viewStates;

            this.logout = logout;
            this.toggle = toggle;

            initViewState();

            /////////////////////

            function initViewState() {
                self.viewStates = {
                    expenses: 'collapsed',
                    availableTags: 'collapsed',
                    tags: 'collapsed'
                }
            }

            function logout() {
                window.location.assign('/' + $rootScope.WEB_CONTEXT + '/logout');
            }

            function toggle($event, menuEntry) {
                var state = self.viewStates[menuEntry];

                if(!state) {
                    throw new Error('View state for menu entry named [' + menuEntry + '] does not exist!');
                }

                self.viewStates[menuEntry] = (state === 'collapsed' ? 'open' : 'collapsed');
                $event.preventDefault();
            }
        }
    }
})();