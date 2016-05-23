'use strict';

var demoModule = angular.module('demo.class', []);


demoModule.factory('class', ['$q', '$http', 'backend', function($q, $http, backend) {
    var service = {};

    service.create = function (alias) {
        if (alias)
            return $http.put(backend.url+'class',{alias:alias});
        return $http.put(backend.url+'class');
    };

    service.addField = function (classAlias, fieldAlias) {
        if (alias)
            return $http.put(backend.url+'class/'+alias+'/field',{alias:alias});
        return $http.put(backend.url+'class');
    };

    service.addRelation = function (classAlias, relationClassAlias, instanceClassId) {
        if (alias)
            return $http.put(backend.url+'class/'+alias+'/relation',{relationType:'ASSOCIATION', instanceClassId: instanceClassId});
        return $http.put(backend.url+'class');
    };

    return service;
}]);


/*
 ###### Start transaction and add to new session
 curl -i -X PUT http://localhost:9900/transaction/
 ###### Create a new class and store the class address in alias named "Item"
 curl -i -X PUT http://localhost:9900/class?alias=Item
 ###### Set name of class with alias Item to Item
 curl -i -X POST http://localhost:9900/class/Item/name?INSTANCE_CLASS_NAME=Item
 ###### Check the new name of the class
 curl -i -X GET http://localhost:9900/class/Item/name

* */