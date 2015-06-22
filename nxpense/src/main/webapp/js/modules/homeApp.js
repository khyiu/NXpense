(function (angular) {
    'use strict';

    var homeAppModule = angular.module('homeApp', ['ngRoute', 'ui.bootstrap', 'restangular', 'notificationHelperModule', 'homeApp.directive']);

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
            notificationHelper.showServerInfo("Fetching tags...");
            Restangular.all('tag').customGET('user').then(
                function (tags) {
                    notificationHelper.hideServerInfo();
                    $rootScope.existingTags = tags;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure("Failed fetching tags! Please retry later...");
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
            controller: 'expenseController'
        }).when('/tag/management', {
            templateUrl: 'views/tag-manage.html',
            controller: 'tagController'
        }).otherwise({
            redirectTo: '/expense/details'
        });
    });

    homeAppModule.controller('tagController', ['$rootScope', '$scope', 'Restangular', 'notificationHelper', function ($rootScope, $scope, Restangular, notificationHelper) {
        var tagDAO = Restangular.all('tag');
        var defaultMode = 'Create';
        var existingTagPreviousState;
        var defaultTag = {
            backgroundColor: '#8546EB',
            foregroundColor: '#FFFF00',
            name: null
        };

        // reverting the existing tag whose edition was in progress to its initial state
        var discardEditionInProgress = function() {
            if(existingTagPreviousState) {
                angular.copy(existingTagPreviousState, $scope.currentTag);
                existingTagPreviousState = null;
            }
        };

        $scope.mode = defaultMode;

        // DISPLAY TAGS
        $scope.loadTags();

        // TAG DELETION
        $scope.remove = function (tag, $event) {
            if (tag && (_.isUndefined($event.keyCode) || $event.keyCode === 13)) {
                notificationHelper.showServerInfo("Deleting tag...");

                tagDAO.customDELETE(tag.id).then(
                    function () {
                        notificationHelper.hideServerInfo();
                        $scope.loadTags();
                    },

                    function () {
                        notificationHelper.hideServerInfo();
                        notificationHelper.showOperationFailure("Failed deleting tag! Please retry later...");
                    }
                );
            }

            $event.stopPropagation();
        };

        // TAG CREATION
        $scope.currentTag = angular.copy(defaultTag, {});

        $scope.reset = function () {
            $scope.mode = defaultMode;

            discardEditionInProgress();
            $scope.currentTag = angular.copy(defaultTag, {});
            $scope.tagForm.$setPristine(true);
        };

        $scope.$watch('currentTag.name', function () {
            $scope.tagForm.tagName.$setValidity('alreadyExists', true);
        });

        // TAG EDITION
        $scope.editTag = function (tag, $event) {
            if (tag && (_.isUndefined($event.keyCode) || $event.keyCode === 13)) {
                discardEditionInProgress();
                $scope.mode = 'Edit';
                existingTagPreviousState = angular.copy(tag, {});
                $scope.currentTag = tag;
                $event.preventDefault();
            }
        };

        // SAVING TAG CREATION/MODIFICATION
        $scope.saveTag = function () {
            var successCallback = function () {
                notificationHelper.hideServerInfo();
                notificationHelper.showOperationSuccess("Tag saved.");
                $scope.reset();
                $scope.loadTags();
            };

            var failureCallback = function (response) {
                var msgToDisplay = 'Failed saving tag!';
                var serverSideValidationMsg = response.headers('SERVERSIDE_VALIDATION_ERROR_MSG');

                if(response && response.status === 499 && response.data) {
                    msgToDisplay = response.data;
                }

                notificationHelper.hideServerInfo();
                notificationHelper.showOperationFailure(msgToDisplay);

                if (serverSideValidationMsg) {
                    $scope.tagForm.tagName.$setValidity('alreadyExists', false);
                }
            };

            if (this.currentTag.id) {
                tagDAO.one(this.currentTag.id.toString()).customPUT(this.currentTag).then(successCallback, failureCallback);
            } else {
                tagDAO.customPOST(this.currentTag).then(successCallback, failureCallback);
            }
        };
    }]);

    homeAppModule.controller('expenseController', ['$rootScope', '$scope', 'Restangular', 'notificationHelper', function ($rootScope, $scope, Restangular, notificationHelper) {
        var expenseDAO = Restangular.one('expense');
        $scope.sortProp = 'date';
        $scope.sortAsc = true;

        $scope.dropCompleted = function (expenseId, $tag) {
            expenseDAO.one(expenseId.toString()).one('tag').put({
                id: $tag.id
            }).then(
                function (targetExpenseTags) {
                    var updatedExpense = _.findWhere($scope.expenses, {id: expenseId});
                    updatedExpense.tags = targetExpenseTags;
                },

                function () {
                    notificationHelper.showOperationFailure("Failed adding tag! Please retry later...");
                }
            )
        };

        $scope.updatePageSize = function (newPageSize) {
            var newPageSizeInt;

            if (newPageSize) {
                newPageSizeInt = parseInt(newPageSize, 10);

                if (!isNaN(newPageSizeInt)) {
                    $rootScope.pageSize = newPageSize;
                    $rootScope.page = 1;
                    $scope.reloadPage();
                }
            }
        };

        $scope.reloadPage = function () {
            var queryParameters = {
                page: $rootScope.page,
                size: $rootScope.pageSize,
                direction: $scope.sortAsc ? 'ASC' : 'DESC',
                properties: [$scope.sortProp, 'position']
            };

            // load balance info
            expenseDAO.customGET('balance').then(
                function(balance) {
                    $scope.balance = balance;
                },

                function() {
                    notificationHelper.showOperationFailure("Failed fetching balance!");
                }
            );

            // load expense items
            notificationHelper.showServerInfo("Fetching expenses...");
            expenseDAO.one('page').get(queryParameters).then(
                function (response) {
                    notificationHelper.hideServerInfo();

                    $scope.expenses = response.items;
                    $rootScope.numberOfExpense = response.numberOfItems;
                    $rootScope.pageSize = response.pageSize;
                    $scope.pageSize = response.pageSize;
                    $rootScope.page = response.pageNumber;
                    $rootScope.totalNumberOfExpense = response.totalNumberOfItems;
                    $rootScope.totalNumberOfPage = response.totalNumberOfPages;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure("Failed fetching expenses! Please retry later...");
                }
            );
        };

        $scope.$watch('pageSize', $scope.updatePageSize);

        $scope.$on('expense:reloadPage', function () {
            $scope.reloadPage();
            $scope.selectedAll = false;
        });

        // Function called when the 'select all' checkbox value is changed --> based on the new value, all visible
        // expenses will be un-checked.
        // The $event parameter may be:
        // - undefined --> event initiated by mouse
        // - defined --> event initiated by keyboard
        $scope.toggleSelectAll = function ($event) {
            if ($event) {
                if ($event.keyCode === 13) {
                    $scope.selectedAll = !$scope.selectedAll;
                    $event.preventDefault();
                } else {
                    return;
                }
            }

            _.each($scope.expenses, function (expense) {
                expense.selected = $scope.selectedAll;
            });
        };

        $scope.toggleItemSelection = function ($event, expense) {
            var numberOfSelectedItem;

            // if $event provided -> event triggered by keyboard --> check that pressed key = ENTER
            // if $event == undefined -> event triggered by mouse
            if (_.isUndefined($event) || $event.keyCode === 13) {
                expense.selected = !expense.selected;

                // Update 'selectedAll' attribute accordingly only if its value will change --> prevent $digest loop to run if not actually necessary
                numberOfSelectedItem = _.where($scope.expenses, {selected: true}).length;

                if (numberOfSelectedItem < $scope.expenses.length && $scope.selectedAll) {
                    $scope.selectedAll = false;
                } else if (numberOfSelectedItem === $scope.expenses.length && !$scope.selectedAll) {
                    $scope.selectedAll = true;
                }
            }
        };

        // Function to handle changing sort criteria (property/direction).
        // If $event is provided, the event is triggered by keyboard --> restrict on key = ENTER
        $scope.updateSort = function ($event, sortProp) {
            if (_.isUndefined($event) || $event.keyCode === 13) {
                if ($scope.sortProp === sortProp) {
                    $scope.sortAsc = !$scope.sortAsc;
                } else {
                    $scope.sortProp = sortProp;
                    $scope.sortAsc = true;
                }

                $scope.reloadPage();
            }
        };
    }]);

    homeAppModule.controller('modalController', ['$rootScope', '$scope', '$modalInstance', 'Restangular', '$filter', 'notificationHelper', 'selectedExpense', '$http',
        function ($rootScope, $scope, $modalInstance, Restangular, $filter, notificationHelper, selectedExpense, $http) {

            if (selectedExpense) {
                $scope.expense = _.extend({}, selectedExpense);
            } else {
                $scope.expense = {source: 'DEBIT_CARD'};
            }

            $scope.ok = function () {
                var expenseAttachments = [];
                var formData = new FormData();
                var request;
                var requestUrl;

                $modalInstance.close();
                notificationHelper.showServerInfo('Saving...');

                _.each(this.attachments, function (attachment) {
                    expenseAttachments.push(attachment);
                    formData.append('attachments', attachment);
                });

                //formData.append('attachments', expenseAttachments);
                formData.append('expense', new Blob([JSON.stringify(this.expense)], {type: 'application/json'}));

                if (this.expense.id) {
                    requestUrl = '/' + $scope.WEB_CONTEXT + '/expense/' + this.expense.id;
                } else {
                    requestUrl = '/' + $scope.WEB_CONTEXT + '/expense';
                }

                request = {
                    method: 'POST',
                    url: requestUrl,
                    headers: {
                        'Content-Type': undefined
                    },
                    data: formData
                };

                $http(request)
                    .success(function(data, status, headers, config) {
                        notificationHelper.hideServerInfo();
                        notificationHelper.showOperationSuccess("Expense saved.");

                        // send event to trigger reloading of current item page
                        $rootScope.$broadcast('expense:reloadPage');
                    }).error(function(data, status, headers, config) {
                        var msgToDisplay = "Failed saving expense!";

                        if (status === 499 && data) {
                            msgToDisplay = data;
                        }

                        notificationHelper.hideServerInfo();
                        notificationHelper.showOperationFailure(msgToDisplay);
                    });
            };

            $scope.cancel = function () {
                $modalInstance.close();
            };

            $scope.processKey = function ($event) {
                var tabbed = $event.keyCode === 9 && !$event.shiftKey;
                var backTabbed = $event.keyCode === 9 && $event.shiftKey;
                var activeElement = $(document.activeElement)[0];
                var firstTabbableElement;
                var lastTabbableElement;

                if (tabbed && activeElement.hasAttribute('lastTab')) {
                    $event.preventDefault();
                    firstTabbableElement = $(document).find('*[firstTab]');
                    firstTabbableElement.focus();
                } else if (backTabbed && activeElement.hasAttribute('firstTab')) {
                    $event.preventDefault();
                    lastTabbableElement = $(document).find('*[lastTab]');
                    lastTabbableElement.focus();
                }
            };
        }]);

})(window.angular);