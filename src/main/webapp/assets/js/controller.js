app.controller('pilotStats', function ($scope, $http, $timeout) {
    $scope.inputType = 'single';
    $scope.loading = false;

    $scope.search = function () {
        if ($scope.inputType == 'single') {
            $scope.getSinglePilot();
        } else if ($scope.inputType == 'multi') {
            $scope.getMultiplePilots();
        }
    };

    $scope.getMultiplePilots = function () {
        if (!$scope.pilotNames) {
            return;
        }

        $http.get('api/pilotStatistics/multi/' + $scope.pilotNames.split('\n')).success(function (data) {
            $scope.statList = [];
            $.each(data, function (i, v) {
                if (v.killCount) {
                    v = assignData(v);
                }
                $scope.statList.push(v);
            });
            $scope.loading = false;
        });
    }

    $scope.getSinglePilot = function () {
        delete $scope.pilotStats;
        delete $scope.error;
        delete $scope.loadComplete;
        $scope.loading = true;
        $http.get('api/pilotStatistics/single/' + $scope.pilotName).success(function (data) {
            if (data.killCount) {
                assignData(data);
            }
            $scope.statList = [data];
            $scope.loading = false;
        }).error(function (data) {
            $scope.error = data;
            $scope.loading = false;
        });
    };

    var assignData = function (data) {
        data.killedAlliancesGraph = convertToGraph(data.killedAlliances);
        data.assistedAlliancesGraph = convertToGraph(data.assistedAlliances);
        data.usedShipsGraph = convertToGraph(data.usedShips);
        data.killedShipsGraph = convertToGraph(data.killedShips);
        data.regionsGraph = convertToGraph(data.regions);
        data.killsPerDayGraph = data.killsPerDay;
        data.killsPerHourGraph = data.killsPerHour;
        data.loadComplete = true;
        return data;
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