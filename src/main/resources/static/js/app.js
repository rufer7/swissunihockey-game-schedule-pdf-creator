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

    var date = new Date();
    var season = date.getFullYear();
    if (date.getMonth() < 5)
    {
        season -= 1;
    }
    $scope.season = season.toString() + "/" + (season + 1).toString();
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
            var date = new Date();
            var season = date.getFullYear();
            if (date.getMonth() < 5)
            {
                season -= 1;
            }
            console.log("/clubs?season=" + season);
            return $http.get("/clubs?season=" + season).success(function (data) {
                return data.entries;
            });
        },
        getTeams: function (clubId) {
            var date = new Date();
            var season = date.getFullYear();
            if (date.getMonth() < 5)
            {
                season -= 1;
            }
            console.log("/teams?club_id=" + clubId + "&season=" + season + "&mode=by_club");
            return $http.get("/teams?club_id=" + clubId + "&season=" + season + "&mode=by_club").success(function (data) {
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

                    var ie = navigator.userAgent.match(/MSIE\s([\d.]+)/),
                        ie11 = navigator.userAgent.match(/Trident\/7.0/) && navigator.userAgent.match(/rv:11/),
                        ieEDGE = navigator.userAgent.match(/Edge/g),
                        ieVer = (ie ? ie[1] : (ie11 ? 11 : (ieEDGE ? 12 : -1)));

                    if (ie && ieVer < 10) {
                        console.log("No blobs on IE ver<10");
                        return;
                    }

                    file = new Blob([data], {type: 'application/pdf'});

                    if (ieVer > -1) {
                        window.navigator.msSaveBlob(file, 'Spielplan.pdf');

                    } else {
                        var file = new Blob([data], {type: 'application/pdf'});
                        var fileURL = URL.createObjectURL(file);
                        window.open(fileURL);
                    }
                });
        }
    }
});
