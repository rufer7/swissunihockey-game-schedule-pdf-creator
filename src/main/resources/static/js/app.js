angular.module('gameSchedulePDFCreatorApp', ['ionic', 'gameSchedulePDFCreatorApp.services'])
    .config(
    ['$stateProvider', '$urlRouterProvider', '$httpProvider', function ($stateProvider, $urlRouterProvider, $httpProvider) {

        $stateProvider.state('index', {
                url: '/index',
                templateUrl: 'partials/index.html',
                controller: IndexController
            }
        );

        $urlRouterProvider.otherwise('/index');

        /* Intercept http errors */
        var interceptor = function ($rootScope, $q, $location) {

            function success(response) {
                return response;
            }

            function error(response) {

                var status = response.status;
                var config = response.config;
                var method = config.method;
                var url = config.url;


                $rootScope.error = method + " on " + url + " failed with status " + status;

                return $q.reject(response);
            }

            return function (promise) {
                return promise.then(success, error);
            };
        };
        $httpProvider.interceptors.push(interceptor);
        $httpProvider.defaults.headers.common['Content-Type'] = 'application/json';
    }]
).run(function ($rootScope, $http) {

        /* Reset error when a new view is loaded */
        $rootScope.$on('$viewContentLoaded', function () {
            delete $rootScope.error;
        });
    });


function IndexController($scope, SwissunihockeyAPIService) {

    $scope.clubEntries = [];
    SwissunihockeyAPIService.getClubs().success(function (data, status) {
        $scope.clubEntries = data.entries;
    });

    $scope.selectedClubId;
    $scope.teamEntries;
}

var services = angular.module('gameSchedulePDFCreatorApp.services', []);

services.factory('SwissunihockeyAPIService', function ($http) {
    return {
        getClubs: function () {
            return $http.get("/clubs").success(function (data) {
                return data.entries;
            });
        }
    }
});
