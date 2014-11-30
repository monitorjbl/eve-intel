app.controller('RecentActivity', function ($scope, $http, $timeout) {
    $scope.loading = false;
    $scope.getSinglePilot = function () {
        delete $scope.recentActivity;
        delete $scope.error;
        delete $scope.loadComplete;
        $scope.loading = true;
        $http.get('api/pilotStatistics/' + $scope.pilot).success(function (data) {
            if (data != '') {
                $scope.recentActivity = data;
                //morris needs the div to be visible before it renders
                //need to delay this call
                $timeout(function () {
                    $scope.killedAlliances = convertToGraph(data.killedAlliances);
                    $scope.assistedAlliances = convertToGraph(data.assistedAlliances);
                    $scope.usedShips = convertToGraph(data.usedShips);
                    $scope.killedShips = convertToGraph(data.killedShips);
                    $scope.regions = convertToGraph(data.regions);
                    $scope.killsPerDay = data.killsPerDay;
                    $scope.killsPerHour = data.killsPerHour;
                    $scope.loadComplete = true;
                });
            } else {
                $scope.error = 'Pilot has no kill information for the past month.';
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