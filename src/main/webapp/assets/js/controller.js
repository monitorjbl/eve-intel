app.controller('PilotStats', function ($scope, $routeParams, $http, $timeout, $log) {
  var MAX_ATTEMPTS = 24,
      DELAY = 5000,
      BACKOFF_MIN = 250,
      BACKOFF_MAX = 1000,
      S3_URL = 'https://s3.amazonaws.com/eve-intel-stats/pilot/',
      API_URL = 'https://rpbwclxcdk.execute-api.us-east-1.amazonaws.com/prod/eveintel/load';

  $scope.inputType = 'single';
  $scope.loading = false;
  $scope.filterOpts = {};

  $scope.search = function () {
    $log.debug('Searching');
    trackRequest('UserInitiated', 'Search', 'Start', $scope.pilotName);
    $scope.requestLoad();
    $scope.loadPilots();
    $scope.link = 'http://' + getCurrentPath() + '#/' + encodeURIComponent($scope.pilotName);
  };

  $scope.requestLoad = function () {
    var payload = {pilots: $scope.pilotName.split('\n')};
    $http.post(API_URL, payload).success(function () {
      trackRequest('UserInitiated', 'RequestLoad', 'Success');
    }).error(function () {
      trackRequest('UserInitiated', 'RequestLoad', 'Failure');
    });
  };

  $scope.sizeof = function (obj) {
    if (!obj) {
      return 0;
    } else if ($.type(obj) == 'object') {
      return Object.keys(obj).length;
    } else if ($.type(obj) == 'array') {
      return obj.length;
    }
  };

  $scope.loadPilots = function () {
    if (!$scope.pilotName) {
      return;
    }

    // prepare scope vars
    $scope.statList = [];
    delete $scope.error;
    delete $scope.loadComplete;
    delete $scope.loadingErrorsFound;
    $scope.loading = true;

    var names = $.unique($scope.pilotName.toLowerCase().split('\n'));
    $scope.pilotName = names.join('\n');

    $scope.loadRequest = {
      pilotNames: names,
      pilotsLoaded: {},
      total: names.length,
      current: 0
    };

    var addPilot = function (pilot) {
      $scope.statList.push(pilot);
      $scope.statList.sort(function (a, b) {
        var aName = a.pilot.name.toLowerCase();
        var bName = b.pilot.name.toLowerCase();
        return ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
      });
    };

    var updatePilot = function (name, loaded, message) {
      $scope.loadRequest.pilotsLoaded[name] = {loaded: loaded, message: message};
      $scope.loadRequest.current = $scope.loadRequest.current + 1;

      if (!loaded) {
        $scope.loadingErrorsFound = true;
      }
      if ($scope.loadRequest.current == $scope.loadRequest.total) {
        $scope.loading = false;
      }
    };

    $.each($scope.loadRequest.pilotNames, function (i, n) {
      var name = n.toLowerCase();
      var attempts = 0;

      function attempt() {
        $log.debug('Loading details for ' + name);
        $http.get(S3_URL + name).success(function (data) {
          $log.debug('Found details for ' + name);

          if (typeof data == 'string') {
            $log.error('Error loading details for ' + name);
            updatePilot(name, false, data);
            trackRequest('UserInitiated', 'PilotRetrieval', 'Retrieve', {attempts: attempts, exists: false});
          } else {
            updatePilot(name, true);
            if (data.killCount > 0) {
              addPilot(assignData(data));
            } else {
              addPilot(data);
            }
            trackRequest('UserInitiated', 'PilotRetrieval', 'Retrieve', {attempts: attempts, exists: true});
          }

        }).error(function () {
          if (attempts++ < MAX_ATTEMPTS) {
            $log.info('No details found for ' + name + ', retrying');
            $timeout(attempt, DELAY + generateRandomNumber(BACKOFF_MIN, BACKOFF_MAX));
          } else {
            $log.error('Could not get details for ' + name + ', timed out');
            updatePilot(name, false, 'Timed out loading pilot data');
            trackRequest('UserInitiated', 'PilotRetrieval', 'Success', name);
            trackRequest('UserInitiated', 'PilotRetrieval', 'Timeout', {attempts: attempts});
          }
        });
      }

      attempt();
    });
  };

  function assignData(data) {
    data.killedAlliancesGraph = convertToGraph(data.killedAlliances);
    data.assistedAlliancesGraph = convertToGraph(data.assistedAlliances);
    data.usedShipsGraph = convertToGraph(data.usedShips);
    data.killedShipsGraph = convertToGraph(data.killedShips);
    data.regionsGraph = convertToGraph(data.regions);
    data.killsPerDayGraph = data.killsPerDay;
    data.killsPerHourGraph = data.killsPerHour;
    data.loadComplete = true;
    return data;
  }

  function convertToGraph(data) {
    return $.map(data, function (v) {
      return {
        value: v.count,
        label: v.value.name
      }
    });
  }

  function getCurrentPath() {
    return location.hostname + (location.port != '' ? ':' + location.port : '') + location.pathname;
  }

  function generateRandomNumber(min, max) {
    return Math.floor(Math.random() * max) + min
  }

  function trackRequest(category, action, label, value) {
    if (window.ga == undefined) {
      $log.debug('Google Analytics is not loaded, retrying');
      $timeout(function () {trackRequest(category, action, label, value)}, 100);
    }
    $log.debug('Logging event to Google Analytics');
    ga('send', 'event', category, action, label, value);
  }

  if ($routeParams.pilot) {
    $scope.pilotName = $routeParams.pilot;
    $scope.search();
  }
});

