(function () {
    'use strict';

    angular.module('homeApp').controller('expenseController', ExpenseController);

    ExpenseController.$inject = [
        '$rootScope', '$scope', 'notificationHelper',
        'underscore', 'expenseService'
    ];

    function ExpenseController($rootScope, $scope, notificationHelper,
                               _, expenseService) {
        var self = this;

        this.pageSize = 10;
        this.sortAsc = true;
        this.sortProp = 'date';

        this.dropCompleted = dropCompleted;
        this.reloadPage = reloadPage;
        this.removeTag = removeTag;
        this.toggleItemSelection = toggleItemSelection;
        this.toggleSelectAll = toggleSelectAll;
        this.updateGlobalItemSelection = updateGlobalItemSelection;
        this.updatePageSize = updatePageSize;
        this.updateSort = updateSort;

        $scope.$on('expense:reloadPage', function () {
            self.reloadPage();
            self.selectedAll = false;
        });

        init();

        /////////////////////////////////

        function dropCompleted(expenseId, $tag) {
            expenseService.assignTag(expenseId.toString(), $tag.id).then(
                function (response) {
                    var updatedExpense = _.findWhere(self.expenses, {id: expenseId});
                    updatedExpense.tags = response.data;
                },

                function () {
                    notificationHelper.showOperationFailure('Failed adding tag! Please retry later...');
                }
            );
        }

        function init() {
            self.updatePageSize(self.pageSize);
        }

        function reloadPage() {
            var queryParameters = {
                page: $rootScope.page,
                size: $rootScope.pageSize,
                direction: self.sortAsc ? 'ASC' : 'DESC',
                properties: [self.sortProp, 'position']
            };

            expenseService.getBalance().then(
                function getBalanceOK(response) {
                    self.balance = response.data;
                },

                function getBalanceKO() {
                    notificationHelper.showOperationFailure('Failed fetching balance!');
                }
            );

            // load expense items
            notificationHelper.showServerInfo('Fetching expenses...');
            expenseService.getExpensePage(queryParameters).then(
                function (response) {
                    notificationHelper.hideServerInfo();
                    self.expenses = response.data.items;
                    $rootScope.numberOfExpense = response.data.numberOfItems;
                    $rootScope.pageSize = response.data.pageSize;
                    self.pageSize = response.data.pageSize;
                    $rootScope.page = response.data.pageNumber;
                    $rootScope.totalNumberOfExpense = response.data.totalNumberOfItems;
                    $rootScope.totalNumberOfPage = response.data.totalNumberOfPages;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure('Failed fetching expenses! Please retry later...');
                }
            );
        }

        // Removal of tag from expense item
        function removeTag($event, expense, tagName) {
            $event.preventDefault();
            notificationHelper.showServerInfo('Removing tag...');

            expenseService.removeTag(expense.id, tagName).then(
                function (response) {
                    notificationHelper.hideServerInfo();
                    expense.tags = response.data.tags;
                },

                function () {
                    notificationHelper.hideServerInfo();
                    notificationHelper.showOperationFailure('Failed removing tag from expense! Please retry later...');
                }
            );
        }

        function toggleItemSelection($event, expense) {
            expense.selected = !expense.selected;
            self.updateGlobalItemSelection($event, expense);
        }

        // Function called when the 'select all' checkbox value is changed --> based on the new value, all visible
        // expenses will be un-checked.
        // The $event parameter may be:
        // - undefined --> event initiated by mouse
        // - defined --> event initiated by keyboard
        function toggleSelectAll($event) {
            if ($event && ( ($event.type === 'keydown' && $event.keyCode === 13) || $event.type === 'click')) {
                self.selectedAll = !self.selectedAll;

                _.each(self.expenses, function (expense) {
                    expense.selected = self.selectedAll;
                });

                $event.preventDefault();
            }
        }

        function updateGlobalItemSelection($event) {
            var numberOfSelectedItem;

            if ($event.originalEvent instanceof MouseEvent || $event.keyCode === 13) {
                $event.stopPropagation();

                // Update 'selectedAll' attribute accordingly only if its value will change 
                // --> prevent $digest loop to run if not actually necessary
                numberOfSelectedItem = _.where(self.expenses, {selected: true}).length;

                if (numberOfSelectedItem < self.expenses.length && self.selectedAll) {
                    self.selectedAll = false;
                } else if (numberOfSelectedItem === self.expenses.length && !self.selectedAll) {
                    self.selectedAll = true;
                }
            }
        }

        function updatePageSize(newPageSize) {
            var newPageSizeInt;

            if (newPageSize) {
                newPageSizeInt = parseInt(newPageSize, 10);

                if (!isNaN(newPageSizeInt)) {
                    $rootScope.pageSize = newPageSize;
                    $rootScope.page = 1;
                    self.reloadPage();
                }
            }
        }

        // Function to handle changing sort criteria (property/direction).
        // If $event is provided, the event is triggered by keyboard --> restrict on key = ENTER
        function updateSort($event, sortProp) {
            if (_.isUndefined($event) || $event.keyCode === 13) {
                if (self.sortProp === sortProp) {
                    self.sortAsc = !self.sortAsc;
                } else {
                    self.sortProp = sortProp;
                    self.sortAsc = true;
                }

                self.reloadPage();
            }
        }
    }
})();