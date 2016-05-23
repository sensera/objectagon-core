'use strict';

var demoModule = angular.module('demo.backend', []);


demoModule.factory('backend', ['$q', '$http', function($q, $http) {
    var service = {};

    service.url = 'http://localhost:9000/app/';
    
    service.items = [{
        name: 'Beluga katt',
        number: '6876876',
        price: '100SEK'
    },{
        name: 'Bulldog',
        number: '9869876',
        price: '200SEK'
    }];
    
    service.loadItems = function () {
        return $q(function(resolve, reject){
            resolve(service.items);
        });

    };

    return service;
}]);