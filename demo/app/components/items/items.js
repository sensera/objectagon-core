'use strict';

var module = angular.module('demo.items', []);


module.factory('items', ['$q', function($q) {
    var service = {};

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