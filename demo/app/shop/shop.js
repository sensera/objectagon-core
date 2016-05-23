'use strict';

var demoModule = angular.module('demo.shop', []);

demoModule.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/shop', {
    templateUrl: 'shop/shop.html',
    controller: 'ShopCtrl'
  });
}]);

demoModule.controller('ShopCtrl', ['$scope', 'backend', function($scope, backend) {
  $scope.searchValue = '';
  $scope.items = [];

  $scope.addItemToCart = function(item) {
    console.log('Add item to Cart ',item);
  };

  function processLoadedItems(loadedItems) {
    console.log('Loaded '+loadedItems.length+' item(s)');
    $scope.items = loadedItems;
  }

  backend.loadItems().then(processLoadedItems);

}]);