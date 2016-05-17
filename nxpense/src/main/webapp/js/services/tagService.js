(function () {
    'use strict';

    angular.module('homeApp').service('TagService', TagService);

    TagService.$inject = ['$http'];

    function TagService($http) {
        this.createTag = createTag;
        this.deleteTag = deleteTag;
        this.updateTag = updateTag;

        ////////////////////////////////

        function createTag(tagData) {
            var url = '/nxpense/tag';
            return $http.post(url, tagData);
        }

        function deleteTag(tagId) {
            var url = '/nxpense/tag/' + tagId;
            return $http.delete(url);
        }

        function updateTag(tagId, tagData) {
            var url = '/nxpense/tag/' + tagId;
            return $http.put(url, tagData);
        }
    }
})();