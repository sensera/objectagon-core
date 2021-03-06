/*
Create domain

Main.Meta
    - Methods:
        * addAge
        * subtractAge

Main.class
    - Name: Main
    - relation to Person.class

Person.class
    - Name: Person
    - Fields:
        * Name
        * Age
        * Comment
    - relation to Main.class
    - Glued methods:
        * addAge (age)
        * subtractAge (age)

 */


var MAIN_TRANSACTION_ALIAS = "main.transaction";
var META_ALIAS = "main.meta";
var MAIN_CLASS_ALIAS = "main.class";
var CREATE_PERSON_METHOD_ALIAS = "create.person.method";
var PERSON_CLASS_ALIAS = "person.class";
var MAIN_PERSON_RELATION_ALIAS = "main.person.relation";

var MAIN_CLASS_NAME = "main.class"; //"Main";
var MAIN_CLASS_INSTANCE_NAME = "main.class.demo";
var PERSON_CLASS_NAME = "Person";
var PERSON_CLASS_AGE_FIELD_NAME = "Age";
var PERSON_CLASS_NAME_FIELD_NAME = "Name";
var PERSON_CLASS_COMMENT_FIELD_NAME = "Comment";

var main_transaction;
var main_meta;
var create_person_method;
var main_class;
var main_class_1;
var person_class;
var main_person_relation;

var create_person_method_code = 'invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() + invokeWorker.getValue("addValue").asNumber());';

var create_domain_json = [
    {
        "main.meta": {
            "type": "meta",
            "methods": [
                {
                    "create.person.method": {
                        "code": "create_person_method_code"
                    }
                }
            ]
        }
    },
    {
        "main.class": {
            "type": "class",
            "relations": [
                {
                    "main.person.relation": {
                        "targetClass": "person.class"
                    }
                }
            ]
        }
    },
    {
        "person.class": {
            "type": "class",
            "fields": [
                {
                    "person.class.Name": {
                        "type": "Text"
                    }
                },
                {
                    "person.class.Age": {
                        "type": "Number"
                    }
                },
                {
                    "person.class.Comment": {
                        "type": "Text"
                    }
                }
            ]
        }
    },
    {
        "main.class.demo": {
            "type": "instance",
            "class": "main.class",
            "alias": "main.class.demo",
            "relations": [
                {
                    "main.person.relation": [
                        {"person.1":"invalid"}
                    ]
                }
            ]
        }
    },
    {
        "person.1": {
            "type": "instance",
            "class": "person.class",
            "values": [
                {
                    "person.class.Name": {
                        "value": "Lars Gurra Aktersnurra"
                    }
                },
                {
                    "person.class.Age": {
                        "value": "44"
                    }
                },
                {
                    "person.class.Comment": {
                        "value": "Mera Gurra tack"
                    }
                }
            ]
        }
    },
    {
        "person.2": {
            "type": "instance",
            "class": "person.class",
            "values": [
                {
                    "person.class.Name": {
                        "value": "Svempa Snyltström"
                    }
                },
                {
                    "person.class.Age": {
                        "value": "78"
                    }
                },
                {
                    "person.class.Comment": {
                        "value": "Svempsson är en torsk"
                    }
                }
            ]
        }
    }
];

function createDomain(transaction, completed) {
    batchUpdate(JSON.stringify(create_domain_json)).done(function (data) {
        if (typeof completed !== 'undefined') {
            completed();
        }
    });

    /*if (typeof transaction !== 'undefined') {
        createTransaction(MAIN_TRANSACTION_ALIAS).done(function (transactionId) {
            main_transaction = createTransactionObject(transactionId, MAIN_TRANSACTION_ALIAS);
            createDomainMeta(completed);
        })
    } else {
        main_transaction = transaction;
        createDomainMeta(completed);
    } */
}

function createDomainMeta(completed) {
    createMeta(META_ALIAS).done(function (metaId) {
        main_meta = createMetaObject(metaId, META_ALIAS);
        main_meta.addMethod(CREATE_PERSON_METHOD_ALIAS).done(function (methodId) {
            create_person_method = createMethodObject(methodId, CREATE_PERSON_METHOD_ALIAS);
            create_person_method.setCode(CREATE_PERSON_METHOD_CODE);
        });
        createMainClass(completed);
    });
}

function createMainClass(completed) {
    createClass(MAIN_CLASS_ALIAS).done(function (classId) {
        main_class = createClassObject(classId, MAIN_CLASS_ALIAS);
        main_class.setName(MAIN_CLASS_NAME);
        indexName(MAIN_CLASS_NAME, MAIN_CLASS_ALIAS);
        createMainInstance(completed)
    });
}

function createMainInstance(completed) {
    main_class.createInstance(MAIN_CLASS_INSTANCE_NAME).done(function (instanceId) {
        main_class_1 = createInstanceObject(instanceId, MAIN_CLASS_INSTANCE_NAME);
        main_class.setInstanceName(MAIN_CLASS_INSTANCE_NAME,MAIN_CLASS_INSTANCE_NAME);
        createPersonClass(completed)
    });
}

function createPersonClass(completed) {
    createClass(PERSON_CLASS_ALIAS).done(function (classId) {
        person_class = createClassObject(classId, PERSON_CLASS_ALIAS);
        person_class.setName(PERSON_CLASS_NAME);
        indexName(PERSON_CLASS_NAME, PERSON_CLASS_ALIAS);
        person_class.addField(PERSON_CLASS_NAME_FIELD_NAME);
        person_class.addField(PERSON_CLASS_AGE_FIELD_NAME);
        person_class.addField(PERSON_CLASS_COMMENT_FIELD_NAME);
        person_class.addRelation(main_class, MAIN_PERSON_RELATION_ALIAS).done(function (relationClassId) {
            main_person_relation = createRelationClassObject(relationClassId, MAIN_PERSON_RELATION_ALIAS);
        });
        if (typeof completed !== 'undefined') {
            completed();
        }
        //commitDomain(completed); //TODO check for uncompleted changes
    })
}

function commitDomain(completed) {
    main_transaction.commit().done(function (data) {
        if (typeof completed !== 'undefined') {
            completed();
        }
    })
}


/*
 curl -i -X PUT http://localhost:9900/transaction/
 curl -i -X PUT http://localhost:9900/class?alias=Item
 curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemName
 curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemNumber
 curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemQuantity
 curl -i -X PUT http://localhost:9900/class?alias=Order
 curl -i -X PUT http://localhost:9900/class/Order/field?alias=OrderNumber
 curl -i -X PUT http://localhost:9900/class/Order/field?alias=CustomerName
 curl -i -X PUT http://localhost:9900/class/Order/field?alias=CustomerAddress
 curl -i -X PUT http://localhost:9900/class/Order/relation/Item?alias=OrderItem
 curl -i -X PUT http://localhost:9900/class/Order/instance?alias=Order1
 curl -i -X PUT http://localhost:9900/class/Item/instance?alias=Item1
 curl -i -X PUT http://localhost:9900/instance/Order1/relation/OrderItem/Item1
 curl -i -X PUT http://localhost:9900/meta?alias=Base
 curl -i -X PUT http://localhost:9900/meta/Base/method?alias=addValue echo 'invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() + invokeWorker.getValue("addValue").asNumber());' |
 curl -i -X POST -d @- http://localhost:9900/method/addValue/code
 curl -i -X PUT 'http://localhost:9900/method/addValue/param?paramName=sumValue&paramField=Number'
 curl -i -X PUT 'http://localhost:9900/method/addValue/param?paramName=addValue&paramField=Number'
 curl -i -X PUT http://localhost:9900/class/Item/method/addValue?sumValue=ItemQuantity
 curl -i -X GET http://localhost:9900/instance/Item1/method/addValue?addValue=10*
*
* */

