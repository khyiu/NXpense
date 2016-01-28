(function () {
    'use strict';

    angular.module('homeApp').directive('nxSideMenu', SideMenu);

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

            this.viewStates = null;

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
                var elementSelector = '#' + menuEntry;
                var state = self.viewStates[menuEntry];
                var newState;

                if (!state) {
                    throw new Error('View state for menu entry named [' + menuEntry + '] does not exist!');
                }

                newState = (state === 'collapsed' ? 'open' : 'collapsed');

                if (state === 'collapsed') {
                    updateViewStateAfterAnimation(menuEntry, newState);
                    angular.element(elementSelector).slideDown();
                } else {
                    angular.element(elementSelector).slideUp(400, function animationEnded() {
                        updateViewStateAfterAnimation(menuEntry, newState);
                        $rootScope.$apply();
                    });
                }


                $event.preventDefault();
            }

            function updateViewStateAfterAnimation(menuEntry, newState) {
                self.viewStates[menuEntry] = newState;
            }
        }
    }
})();