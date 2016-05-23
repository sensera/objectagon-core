'use strict';

var demoModule = angular.module('demo.admin', []);

demoModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/admin', {
    templateUrl: 'admin/admin.html',
    controller: 'AdminCtrl'
  });
}]);

demoModule.controller('AdminCtrl', [function() {

}]);