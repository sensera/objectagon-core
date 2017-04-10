var objectagon_url = "http://localhost:9999/";

var headerToken = "1234567890";
var HEADER_TOKEN_NAME = "OBJECTAGON_REST_TOKEN";

var TRANSACTION_PATH="transaction/";
var INSTANCE_PATH="instance/";
var CLASS_PATH="class/";
var META_PATH="meta/";
var SESSION_PATH="session/";
var RELATION_PATH="relation/";
var FIELD_PATH="field/";
var NAME_PATH="name/";
var METHOD_PATH="method/";

function getInstanceByName(instanceName) {



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
    return get(NAME_PATH+name+"/"+alias);
}

// ----------------- Create domain --------------------------

function useTransaction(alias) {
    console.log("useTransaction", alias);
    return put(SESSION_PATH, alias);
}

function createTransaction(alias) {
    console.log("createTransaction",alias);
    return put(TRANSACTION_PATH, alias);
}

function extendTransaction(targetAlias, newAlias) {
    console.log("",targetAlias, newAlias);
    return put(TRANSACTION_PATH+targetAlias+"/extend", alias);
}

function createMeta(alias) {
    console.log("extendTransaction",alias);
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

// ----------------- Create Objects --------------------------

function createTransactionObject(transactionId, alias) {
    return {
        id: transactionId,
        alias: alias ,
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
        addRelation: function (instanceClass, newAlias) { return createRelationClass(alias, instanceClass.alias, newAlias); },
        attacheMethod: function (method) { return attacheMetaMethod(alias, method); }
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

