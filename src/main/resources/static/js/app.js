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


function IndexController($scope, SwissunihockeyAPIService, PDFGeneratorService) {

    $scope.clubEntries = [];
    $scope.teamEntries = [];
    $scope.selectedClub = {
        id: ""
    };
    $scope.selectedTeam = {
        id: ""
    }

    SwissunihockeyAPIService.getClubs().success(function (data) {
        $scope.clubEntries = data.entries;
    });

    $scope.$watch('selectedClub.id', function(newVal) {
        if (newVal !== "") {
            SwissunihockeyAPIService.getTeams(newVal).success(function (data) {
                $scope.teamEntries = data.entries;
            });
        }
    });

    $scope.generatePDF = function() {
        PDFGeneratorService.getPDF($scope.selectedClub.id, $scope.selectedTeam.id);
    }
}

var services = angular.module('gameSchedulePDFCreatorApp.services', []);

services.factory('SwissunihockeyAPIService', function ($http) {
    return {
        getClubs: function () {
            return $http.get("/clubs").success(function (data) {
                return data.entries;
            });
        },
        getTeams: function (clubId) {
            console.log("/teams?club_id=" + clubId + "&mode=by_club");
            return $http.get("/teams?club_id=" + clubId + "&mode=by_club").success(function (data) {
                return data.entries;
            })
        }
    }
});

services.factory('PDFGeneratorService', function ($http) {
    return {
        getPDF: function (clubId, teamId) {
            return $http.get('/api/clubs/' + clubId + '/teams/' + teamId + '/game-schedule', { responseType: 'arraybuffer' })
                .success(function (data) {
                    var file = new Blob([data], { type: 'application/pdf' });
                    var fileURL = URL.createObjectURL(file);
                    window.open(fileURL);
                });
        }
    }
});
