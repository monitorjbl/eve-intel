var app = angular.module('eveintel', ['ngRoute', 'ui.bootstrap']).config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'root.html'
    }).otherwise({
        redirectTo: '/'
    });
});

app.controller('RecentActivity', function ($scope, $http, $timeout) {
    $scope.getSinglePilot = function () {
        delete $scope.recentActivity;
        delete $scope.error;
        $scope.loading = true;
        $http.get('api/activity/' + $scope.pilot).success(function (data) {
            if (data != '') {
                $scope.recentActivity = data;
                //morris needs the div to be visible before it renders
                $timeout(function () {
                    $scope.killedAlliances = convertToGraph(data.killedAlliances);
                    $scope.assistedAlliances = convertToGraph(data.assistedAlliances);
                    $scope.usedShips = convertToGraph(data.usedShips);
                    $scope.killedShips = convertToGraph(data.killedShips);
                    $scope.regions = convertToGraph(data.regions);
                }, 100);
            } else {
                $scope.recentActivity={};
            }
            $scope.loading = false;
        }).error(function (data) {
            $scope.error = data;
            $scope.loading = false;
        });
    };

    var convertToGraph = function (data) {
        return $.map(data, function (v) {
            return {
                value: v.count,
                label: v.value.name
            }
        });
    };
});

app.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

app.directive('graphDoughnut', function () {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        link: function (scope, element, attrs) {
            var chart = $('<div class="graph graph-doughnut"></div>').appendTo(element);
            $.each(attrs.$attr, function (i, v) {
                if (v != 'data') {
                    $(chart).attr(v, attrs[v]);
                }
            });
            //$(element).remove();
            scope.$watch('data', function (newValue, oldValue) {
                if (newValue) {
                    $(chart).children().remove();
                    Morris.Donut({
                        element: chart,
                        data: scope.data,
                        resize: true
                    });
                }
            }, false);

        }
    }

});