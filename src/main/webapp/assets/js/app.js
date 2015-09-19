var app = angular.module('eveintel', ['ngRoute', 'ui.bootstrap']).config(function ($routeProvider, $logProvider) {
  $logProvider.debugEnabled(false);
  $routeProvider.when('/:pilot?', {
    templateUrl: 'kills.html'
  }).otherwise({
    redirectTo: '/'
  });
});

var NPC_CORPORATIONS = {
  1000002: "07_04", 1000050: "41_03", 1000051: "22_02", 1000052: "22_01", 1000053: "23_01", 1000054: "22_04", 1000055: "21_02", 1000056: "23_02",
  1000057: "21_03", 1000058: "23_03", 1000059: "21_04", 1000068: "18_03", 1000060: "22_03", 1000061: "23_04", 1000062: "21_01", 1000063: "14_03",
  1000064: "15_02", 1000065: "14_02", 1000066: "19_04", 1000067: "16_01", 1000069: "17_02", 1000070: "19_01", 1000079: "14_04", 1000080: "17_01",
  1000071: "17_04", 1000072: "16_02", 1000073: "13_02", 1000074: "15_04", 1000075: "17_03", 1000076: "13_03", 1000077: "39_04", 1000078: "18_04",
  1000082: "16_03", 1000083: "15_01", 1000084: "16_04", 1000085: "18_01", 1000086: "12_01", 1000087: "13_01", 1000088: "12_03", 1000089: "12_02",
  1000090: "12_04", 1000091: "15_03", 1000092: "13_04", 1000081: "18_02", 1000093: "19_02", 1000094: "33_01", 1000095: "33_03", 1000096: "26_01",
  1000097: "31_03", 1000098: "28_02", 1000099: "27_03", 1000100: "34_03", 1000101: "32_03", 1000102: "29_04", 1000103: "25_04", 1000114: "25_01",
  1000104: "25_02", 1000105: "38_02", 1000106: "25_03", 1000107: "31_02", 1000108: "32_01", 1000109: "32_04", 1000110: "33_02", 1000111: "30_01",
  1000112: "31_04", 1000113: "32_02", 1000124: "24_03", 1000125: "26_03", 1000115: "39_01", 1000116: "30_02", 1000117: "30_04", 1000118: "29_01",
  1000119: "29_02", 1000120: "29_03", 1000121: "30_03", 1000122: "31_01", 1000123: "35_02", 1000134: "19_03", 1000135: "38_01", 1000136: "24_02",
  1000126: "35_03", 1000127: "28_03", 1000128: "34_02", 1000129: "27_04", 1000130: "14_01", 1000131: "36_02", 1000132: "03_04", 1000133: "24_01",
  1000137: "04_01", 1000138: "24_04", 1000139: "35_04", 1000140: "40_02", 1000141: "28_04", 1000142: "40_04", 1000143: "26_04", 1000144: "26_02",
  1000145: "27_01", 1000146: "28_01", 1000147: "27_02", 1000148: "03_02", 1000149: "40_03", 1000150: "39_03", 1000151: "11_04", 1000152: "11_02",
  1000153: "11_03", 1000154: "35_01", 1000155: "41_01", 1000156: "11_01", 1000157: "33_04", 1000158: "40_01", 1000159: "36_03", 1000160: "36_01",
  1000161: "34_04", 1000162: "34_01", 1000163: "36_04", 1000164: "41_02", 1000003: "01_03", 1000004: "08_01", 1000005: "01_04", 1000006: "04_04",
  1000007: "38_03", 1000008: "39_02", 1000009: "03_03", 1000010: "03_01", 1000011: "06_02", 1000012: "10_04", 1000013: "09_04", 1000014: "07_02",
  1000015: "01_01", 1000016: "05_04", 1000017: "02_04", 1000018: "05_03", 1000019: "06_01", 1000020: "02_01", 1000021: "02_03", 1000022: "05_02",
  1000023: "09_01", 1000024: "07_03", 1000025: "10_03", 1000026: "08_04", 1000027: "09_02", 1000028: "09_03", 1000029: "04_02", 1000030: "05_01",
  1000031: "08_02", 1000032: "07_01", 1000033: "08_03", 1000034: "04_03", 1000035: "37_03", 1000036: "01_02", 1000037: "02_02", 1000038: "06_03",
  1000039: "37_02", 1000040: "37_01", 1000041: "38_04", 1000042: "06_04", 1000043: "37_04", 1000044: "10_01", 1000045: "10_02", 1000046: "20_03",
  1000047: "20_01", 1000048: "20_04", 1000049: "20_02", 1000165: "43_01", 1000166: "42_03", 1000167: "42_01", 1000168: "42_04", 1000169: "41_04",
  1000170: "43_02", 1000171: "43_03", 1000172: "43_04", 1000173: "44_01", 1000177: "45_04", 1000178: "46_01", 1000179: "47_01", 1000180: "47_02",
  1000181: "47_03", 1000182: "47_04"
};

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
        if (filterOpts.pilotsWithNoAlliance) {
          show &= v.pilot.corporation.alliance.name == "";
        }
        if (filterOpts.pilotsInNpcCorp) {
          show &= NPC_CORPORATIONS[v.pilot.corporation.id] != undefined;
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