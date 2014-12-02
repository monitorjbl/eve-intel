app.controller('pilotStats', function ($scope, $http, $timeout) {
    $scope.inputType = 'single';
    $scope.loading = false;

    $scope.search = function(){
        if($scope.inputType == 'single'){
            $scope.getSinglePilot();
        } else if($scope.inputType == 'multi'){
            $scope.getMultiplePilots();
        }
    };

    $scope.getMultiplePilots = function(){
        console.log($scope.pilotNames);
    }

    $scope.getSinglePilot = function () {
        delete $scope.pilotStats;
        delete $scope.error;
        delete $scope.loadComplete;
        $scope.loading = true;
        $http.get('api/pilotStatistics/' + $scope.pilotName).success(function (data) {
            if (data != '') {
                $scope.pilotStats = data;
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