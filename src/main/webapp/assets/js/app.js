var app = angular.module('eveintel', ['ngRoute', 'ui.bootstrap']).config(function ($routeProvider, $logProvider) {
  $logProvider.debugEnabled(false);
  $routeProvider.when('/:pilot?', {
    templateUrl: 'kills.html'
  }).otherwise({
    redirectTo: '/'
  });
});

app.filter('pilotFilter', function () {
  return function (input, filterOpts) {
    if (input) {
      var arr = input.slice(0);

      arr = $.grep(arr, function (v) {
        var show = true;
        if (filterOpts.pilotName) {
          show &= v.pilot.name.toLowerCase().indexOf(filterOpts.pilotName.toLowerCase()) > -1;
        }
        if (filterOpts.allianceName) {
          show &= v.pilot.corporation.alliance.name.toLowerCase().indexOf(filterOpts.allianceName.toLowerCase()) > -1;
        }
        if (filterOpts.pilotsWithKills) {
          show &= v.killCount > 0;
        }
        if (filterOpts.cynoPilots) {
          show &= v.flags && v.flags.cynoPilot;
        }
        if (filterOpts.fleetBoosters) {
          show &= v.flags && v.flags.fleetBooster;
        }
        return show;
      });
      return arr;
    }
  };
});

app.directive('graphDoughnut', function ($timeout) {
  return {
    restrict: 'E',
    scope: {
      data: '=',
      trigger: '='
    },
    link: function (scope, element, attrs) {
      var chart = $('<div class="graph graph-doughnut"></div>').appendTo(element);
      var draw = function () {
        $(chart).children().remove();
        $timeout(function () {
          Morris.Donut({
            element: chart,
            data: scope.data,
            resize: true
          });
        });
      };
      if (attrs.trigger) {
        scope.$watch('trigger', function (newValue, oldValue) {
          if (newValue) {
            draw();
          }
        }, false);
      } else {
        scope.$watch('data', function (newValue, oldValue) {
          if (newValue) {
            draw();
          }
        });
      }
    }
  }
});

app.directive('graphLine', function ($filter, $timeout) {
  return {
    restrict: 'E',
    scope: {
      data: '=',
      trigger: '='
    },
    link: function (scope, element, attrs) {
      var chart = $('<div class="graph graph-line"></div>').appendTo(element);
      var draw = function () {
        $(chart).children().remove();
        $timeout(function () {
          Morris.Line({
            element: chart,
            data: scope.data.data,
            xkey: 'x',
            ykeys: ['y'],
            labels: [scope.data.title],
            resize: true,
            dateFormat: function (x) {
              return $filter('date')(x, 'fullDate');
            }
          });
        });
      };

      if (attrs.trigger) {
        scope.$watch('trigger', function (newValue, oldValue) {
          if (newValue) {
            draw();
          }
        }, false);
      } else {
        scope.$watch('data', function (newValue, oldValue) {
          if (newValue) {
            draw();
          }
        });
      }
    }
  }
});

app.directive('graphBar', function ($filter, $timeout) {
  return {
    restrict: 'E',
    scope: {
      data: '=',
      trigger: '='
    },
    link: function (scope, element, attrs) {
      var chart = $('<div class="graph graph-bar"></div>').appendTo(element);
      var draw = function () {
        $(chart).children().remove();
        $timeout(function () {
          Morris.Bar({
            element: chart,
            data: scope.data.data,
            xkey: 'x',
            ykeys: ['y'],
            labels: [scope.data.title],
            resize: true,
            dateFormat: function (x) {
              return $filter('date')(x, 'fullDate');
            }
          });
        });
      };
      if (attrs.trigger) {
        scope.$watch('trigger', function (newValue, oldValue) {
          if (newValue) {
            draw();
          }
        }, false);
      } else {
        scope.$watch('data', function (newValue, oldValue) {
          if (newValue) {
            draw();
          }
        });
      }

    }
  }
});

app.directive('scrollOnTrue', function ($log) {
  return {
    restrict: 'A',
    link: function (scope, element, attributes) {
      var id = 'scroll';
      $(element).attr('id', id);
      scope.$watch(attributes.scrollOnTrue, function (newValue) {
        if (newValue) {
          $log.debug('scrolling');
          $("body").animate({scrollTop: element.offset().top}, "slow");
        }
      });
    }
  }
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