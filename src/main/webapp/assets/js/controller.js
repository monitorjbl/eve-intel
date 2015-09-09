app.controller('pilotStats', function ($scope, $http, $timeout, $log) {
  $scope.inputType = 'single';
  $scope.loading = false;

  $scope.search = function () {
    $scope.requestLoad();
    $scope.loadPilots();
  };

  $scope.requestLoad = function () {
    var payload = {pilots: $scope.pilotName.split('\n')};
    console.log(payload);
    $http.post('https://rpbwclxcdk.execute-api.us-east-1.amazonaws.com/prod/eveintel/load', payload);
  };

  $scope.loadPilots = function () {
    if (!$scope.pilotName) {
      return;
    }

    $scope.statList = [];
    delete $scope.error;
    delete $scope.loadComplete;
    $scope.loading = true;

    var names = $scope.pilotName.split('\n');
    var loaded = [];
    $log.info(names);
    $.each(names, function (i, name) {
      name = name.toLowerCase();
      function attempt() {
        $log.info("Loading details for " + name);
        $http.get('https://s3.amazonaws.com/eve-intel-stats/pilot/' + name).success(function (data) {
          $log.info("Found details for " + name);
          loaded.push(name);
          if (data.killCount) {
            $scope.statList.push(assignData(data));
          }

          if (loaded.length == names.length) {
            $scope.loading = false;
          }
        }).error(function (data, code) {
          if (code != 403) {
            $scope.error = data;
          } else {
            $log.info("No details found for " + name + ", retrying");
            $timeout(attempt, 3000);
          }
        });
      }

      attempt();
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