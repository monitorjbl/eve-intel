var app = angular.module('eveintel', ['ngRoute', 'ui.bootstrap']).config(function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'kills.html'
    }).otherwise({
        redirectTo: '/'
    });
});

app.directive('graphDoughnut', function () {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        link: function (scope, element, attrs) {
            var chart = $('<div class="graph graph-doughnut"></div>').appendTo(element);
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

app.directive('graphLine', function ($filter) {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        link: function (scope, element, attrs) {
            var chart = $('<div class="graph graph-line"></div>').appendTo(element);
            scope.$watch('data', function (newValue, oldValue) {
                if (newValue) {
                    $(chart).children().remove();
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
                }
            }, false);

        }
    }
});

app.directive('graphBar', function ($filter) {
    return {
        restrict: 'E',
        scope: {
            data: '='
        },
        link: function (scope, element, attrs) {
            var chart = $('<div class="graph graph-bar"></div>').appendTo(element);
            scope.$watch('data', function (newValue, oldValue) {
                if (newValue) {
                    $(chart).children().remove();
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
                }
            }, false);

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