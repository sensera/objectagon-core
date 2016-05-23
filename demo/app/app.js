'use strict';

// Declare app level module which depends on views, and components
var mainApp = angular.module('demo', [
  'ngRoute',
  'demo.admin',
  'demo.shop',
  'demo.class',
  'demo.create',
  'demo.items',
  'demo.backend'
]);

mainApp.config(['$routeProvider', function($routeProvider) {
  $routeProvider.otherwise({redirectTo: '/shop'});
}]);
