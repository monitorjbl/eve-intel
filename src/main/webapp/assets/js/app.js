var app = angular.module('eveintel', ['ngRoute', 'ui.bootstrap']).config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'kills.html'
    }).otherwise({
        redirectTo: '/'
    });
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
                console.log(newValue);
                if (newValue) {
                    $log.debug('scrolling');
                    $("body").animate({scrollTop: element.offset().top}, "slow");
                }
            });
        }
    }
});