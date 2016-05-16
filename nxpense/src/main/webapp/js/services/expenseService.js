(function () {
    'use strict';

    angular.module('homeApp').service('expenseService', ExpenseService);

    ExpenseService.$inject = ['$http'];

    function ExpenseService($http) {
        this.assignTag = assignTag;
        this.removeTag = removeTag;
        this.getBalance = getBalance;
        this.getExpensePage = getExpensePage;

        ///////////////////////////////////////

        function assignTag(expenseId, tagId) {
            var url = '/nxpense/expense/' + expenseId + '/tag?id=' + tagId;
            return $http.put(url);
        }

        function getBalance() {
            var url = '/nxpense/expense/balance';
            return $http.get(url);
        }

        function getExpensePage(parameters) {
            var url = '/nxpense/expense/page';
            return $http.get(url, {params: parameters});
        }

        function removeTag(expenseId, tagName) {
            var url = '/nxpense/expense/' + expenseId + '/tag/' + tagName;
            return $http.delete(url);
        }

    }
})();