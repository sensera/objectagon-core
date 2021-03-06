var objectagon_url = "http://localhost:9999/";

var headerToken = "1234567890";
var HEADER_TOKEN_NAME = "OBJECTAGON_REST_TOKEN";

var TRANSACTION_PATH="transaction/";
var INSTANCE_PATH="instance/";
var BATCH_PATH="batch/";
var INSTANCE_NAME_PATH="instancename/";
var CLASS_PATH="class/";
var META_PATH="meta/";
var SESSION_PATH="session/";
var RELATION_PATH="relation/";
var FIELD_PATH="field/";
var NAME_PATH="name/";
var ID_PATH="id/";
var METHOD_PATH="method/";

function getInstanceByName(className, instanceName) {
    console.log("getInstanceByName ",className, instanceName);
    return get(CLASS_PATH+className+"/"+INSTANCE_NAME_PATH+instanceName)
}

function getValue(id, fieldName) {
    return $.getJSON(objectagon_url+"instance/"+id+"/field/"+fieldName).fail(fail("setValue "+fieldName));
}

function setValue(id, fieldName, value) {
    return $.putJSON(objectagon_url+"instance/"+id+"/field/"+fieldName,{value:value}).fail(fail("setValue "+fieldName));
}
// ------------------- Use domain --------------------------

function nameSearch(name, alias) {
    return get(NAME_PATH+name+"/", "alias="+alias);
}

function indexName(name, alias) {
    return put(NAME_PATH+name+"/"+ID_PATH+alias);
}

// ----------------- Create domain --------------------------

function batchUpdate(data) {
    console.log("batch",data);
    return post(BATCH_PATH, data);
}


function useTransaction(alias) {
    console.log("useTransaction", alias);
    return put(SESSION_PATH, alias);
}

function createTransaction(alias) {
    console.log("createTransaction",alias);
    return put(TRANSACTION_PATH, alias);
}

function commitTransaction(alias) {
    console.log("commmitTransaction",alias);
    return post(TRANSACTION_PATH+alias+"/commit", alias);
}

function rollbackTransaction(alias) {
    console.log("rollbackTransaction",alias);
    return post(TRANSACTION_PATH+alias+"/rollback", alias);
}

function extendTransaction(targetAlias, newAlias) {
    console.log("",targetAlias, newAlias);
    return put(TRANSACTION_PATH+targetAlias+"/extend", alias);
}

function createMeta(alias) {
    console.log("createMeta",alias);
    return put(META_PATH, alias);
}

function createClass(alias) {
    console.log("createClass",alias);
    return put(CLASS_PATH, alias);
}

function addField(classAlias, alias) {
    console.log("addField", classAlias, alias);
    return put(CLASS_PATH+classAlias+"/"+FIELD_PATH, alias);
}

function createRelationClass(fromClass, toClass, alias) {
    console.log("createRelationClass",fromClass, toClass, alias);
    return put(CLASS_PATH+fromClass+"/"+RELATION_PATH+toClass, alias);
}

function setClassName(classAlias, className) {
    console.log("setClassName", classAlias, className);
    return post(CLASS_PATH+classAlias+"/name/"+className);
}

function createMethod(metaAlias, alias) {
    console.log("createMethod", metaAlias);
    return put(META_PATH+metaAlias+"/method/", alias);
}

function setMethodCode(alias, code) {
    return post(METHOD_PATH+alias, code);
}

function attacheMetaMethod(alias, method) {
    return post(CLASS_PATH+alias+"/"+METHOD_PATH+method);
}

function createInstanceOfClass(alias, instanceAlias) {
    return put(CLASS_PATH+alias+"/"+INSTANCE_PATH, instanceAlias);
}

function setNameInstanceOfClass(alias, instanceAlias, instanceName) {
    return post(CLASS_PATH+alias+"/"+INSTANCENAME_PATH+instanceAlias+"/name/"+instanceName);
}

function getInstanceRelations(instanceAlias, relationAlias) {
    return get(CLASS_PATH+alias+"/"+INSTANCENAME_PATH, instanceAlias+RELATION_PATH+relationAlias);
}

// ----------------- Create Objects --------------------------

function createTransactionObject(transactionId, alias) {
    return {
        id: transactionId,
        alias: alias ,
        commit: function () { return commitTransaction(alias); },
        rollback: function () { return rollbackTransaction(alias); },
        extend: function (newAlias) { return extendTransaction(alias, newAlias); }
    }
}

function createInstanceObject(id, alias) {
    return {
        id: id,
        alias: alias,
        getValue: function (name) { return getValue(alias, name); }
    }
}

function createMetaObject(id, alias) {
    return {
        id: id,
        alias: alias,
        addMethod: function (methodAlias) { return createMethod(alias, methodAlias); }
    }
}

function createClassObject(id, alias) {
    return {
        id: id,
        alias: alias,
        setName: function (name) { return setClassName(alias, name); },
        addField: function (fieldAlias) { return addField(alias, fieldAlias); },
        setInstanceName: function (instanceAlias, instanceName) { return setNameInstanceOfClass(alias, instanceAlias, instanceName); },
        getInstanceName: function (instanceAlias) { return getNameInstanceOfClass(alias, instanceAlias); },
        addRelation: function (instanceClass, newAlias) { return createRelationClass(alias, instanceClass.alias, newAlias); },
        attacheMethod: function (method) { return attacheMetaMethod(alias, method); },
        createInstance: function (instanceAlias) { return createInstanceOfClass(alias, instanceAlias); }
    }
}

function createRelationClassObject(id, alias) {
    return {
        id: id,
        alias: alias,
    }
}

function createMethodObject(id, alias) {
    return {
        id: id,
        alias: alias,
        setCode: function (code) {
            setMethodCode(alias, code);
        }
    }
}



// ------------------------ Utils -----------------------------

function ajax(type, path, params, data) {
    var ajaxParams = {
        beforeSend: function(request) {
            request.setRequestHeader(HEADER_TOKEN_NAME, headerToken);
        },
        type: type,
        dataType: "json",
        error: fail(type+" for path '"+path+"' failed", params, data),
        url: objectagon_url+path
    };
    if (typeof params !== 'undefined') {
        ajaxParams.url = ajaxParams.url + "?" + params;
    }
    if (typeof data !== 'undefined') {
        ajaxParams.data = data;
    }
    return $.ajax(ajaxParams);
}

function get(path, params) {
    return ajax('GET', path, params);
}

function put(path, alias) {
    if (typeof alias !== 'undefined') {
        return ajax('PUT', path, 'alias='+alias);
    } else {
        return ajax('PUT', path);
    }
}

function post(path, data) {
    return ajax('POST', path, undefined, data);
}

function fail(description) {
    return function(xhr, textStatus, errorThrown) {
        console.log("Request failed '"+description+"'! because ",xhr, textStatus, errorThrown);
    }
}

