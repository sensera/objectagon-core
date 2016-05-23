'use strict';

var module = angular.module('demo.create', []);

module.factory('create', ['$q', 'class',  function($q, clazz) {
    var service = {};

    service.creates = [
        {   // Create class Petshop
            method: function() { return clazz.create('Petshop'); },
            onSuccess: function (resp) { service.petshopClassId = resp.data.address; }
        },
        {   // Create class Item
            method: function() { return clazz.create('Item'); },
            onSuccess: function (resp) { service.itemClassId = resp.data.address; }
        },
        {   // Create relation petshop
            method: function() { return clazz.addRelation('Petshop', service.itemClassId); },
            onSuccess: function (resp) { service.petshopItemsId = resp.data.address; }
        },
        {   // Create field Name on class Item
            method: function() { return clazz.addField('Item', 'Name'); },
            onSuccess: function (resp) { service.itemNameId = resp.data.address; }
        },
        {   // Create field Number on class Item
            method: function() { return clazz.addField('Item', 'Number'); },
            onSuccess: function (resp) { service.itemNumberId = resp.data.address; }
        }
    ];

    service.createShop = function () {
        createList(service.creates, 0);
    };

    function createList(list, index) {
        var at = list[index];
        at.method().success(function (resp) {
            if (at.onSuccess) at.onSuccess(resp);
            createList(list, index+1);
        })
    }

    return service;
}]);