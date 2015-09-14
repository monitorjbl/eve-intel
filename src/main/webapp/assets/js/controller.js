app.controller('PilotStats', function ($scope, $routeParams, $http, $timeout, $log) {
  var MAX_ATTEMPTS = 1;
  var DELAY = 3000;
  var S3_URL = 'https://s3.amazonaws.com/eve-intel-stats/pilot/';
  var API_URL = 'https://rpbwclxcdk.execute-api.us-east-1.amazonaws.com/prod/eveintel/load';

  $scope.inputType = 'single';
  $scope.loading = false;

  $scope.search = function () {
    $log.info('Searching');
    $scope.requestLoad();
    $scope.loadPilots();
  };

  $scope.requestLoad = function () {
    var payload = {pilots: $scope.pilotName.split('\n')};
    $http.post(API_URL, payload);
  };

  $scope.loadPilots = function () {
    if (!$scope.pilotName) {
      return;
    }

    $scope.statList = [];
    delete $scope.error;
    delete $scope.loadComplete;
    $scope.loading = true;

    $scope.pilotNames = $scope.pilotName.toLowerCase().split('\n');
    $scope.pilotsLoaded = {};
    var updatePilot = function (name, loaded, message) {
      $scope.pilotsLoaded[name] = {loaded: loaded, message: message};
      if (!loaded) {
        $scope.loadingErrorsFound = true;
      }
      if (Object.keys($scope.pilotsLoaded).length == $scope.pilotNames.length) {
        $scope.loading = false;
      }
    };

    $.each($scope.pilotNames, function (i, n) {
      var name = n.toLowerCase();
      var attempts = 0;

      function attempt() {
        $log.info('Loading details for ' + name);
        $http.get(S3_URL + name).success(function (data) {
          $log.info('Found details for ' + name);
          updatePilot(name, true);
          if (data.killCount) {
            $scope.statList.push(assignData(data));
          } else {
            $scope.statList.push(data);
          }
        }).error(function () {
          if (attempts++ < MAX_ATTEMPTS) {
            $log.info('No details found for ' + name + ', retrying');
            $timeout(attempt, DELAY);
          } else {
            $log.error('Could not get details for ' + name + ', timed out');
            updatePilot(name, false, 'Pilot data could not be found');
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

  if ($routeParams.pilot) {
    $scope.pilotName = $routeParams.pilot;
    $scope.search();
  }
});

