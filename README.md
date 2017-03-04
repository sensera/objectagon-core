objectagon
==========

4GL/5GL Aware System Builder

Continuous migration

Design, Development, Test & Production all integrated and simultaneous.
Continuous Integration and Development.
  
It's distributed, 100% load balanced and secure.

First came Open Source, then Open Data. This is Open Functionality.
  
##  To start server

Via Docker

> docker run -d -p 9900:9900 sensera/objectagon

Or

Go to directory core
enter:
> mvn clean install exec:java -Dexec.mainClass="org.objectagon.core.rest.HttpServerImpl" -DskipTests

#### Test server commands

se all server commands at: 
core/src/main/java/org/objectagon/core/rest/README.md




**Start new transaction add att to session**
> curl -i -X PUT http://localhost:9900/transaction/


**Create meta and attach to Alias**
> curl -i -X PUT http://localhost:9900/meta?alias=Base

**Create a type and attach i to the type id and alias *Item***
> curl -i -X PUT http://localhost:9900/class?alias=Item

**Create a method**
> curl -i -X PUT http://localhost:9900/meta/Base/method?alias=addValue

**Add parameters to the Method**
> echo 'invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() + invokeWorker.getValue("addValue").asNumber());' | curl -i -X POST -d @- http://localhost:9900/method/addValue/code
> curl -i -X PUT 'http://localhost:9900/method/addValue/param?paramName=sumValue&paramField=Number' 
> curl -i -X PUT 'http://localhost:9900/method/addValue/param?paramName=addValue&paramField=Number'

**Add field to an Item**
> curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemName
> curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemNumber
> curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemQuantity

**Bind the method addValue to Item and the field ItemQuantity**
> curl -i -X PUT http://localhost:9900/class/Item/method/addValue?sumValue=ItemQuantity

**Create a type and attach it to the type id and alias *Order***
> curl -i -X PUT http://localhost:9900/class?alias=Order

**Add a field to Order**
> curl -i -X PUT http://localhost:9900/class/Order/field?alias=OrderNumber
> curl -i -X PUT http://localhost:9900/class/Order/field?alias=CustomerName
> curl -i -X PUT http://localhost:9900/class/Order/field?alias=CustomerAddress


**Create a relationtype (between Order and Item) and connect the created relation type to alias *OrderItem***
> curl -i -X PUT http://localhost:9900/class/Order/relation/Item?alias=OrderItem


**Create an instance of Order and connect it to alias *Order1***
> curl -i -X PUT http://localhost:9900/class/Order/instance?alias=Order1

**Create an instance of Item and connect it to alias *Item1***
> curl -i -X PUT http://localhost:9900/class/Item/instance?alias=Item1

**Set values on fields**
> curl -i -X POST http://localhost:9900/instance/Item1/field/ItemName?value=Hubbabubba
> curl -i -X POST http://localhost:9900/instance/Item1/field/ItemNumber?value=67897
> curl -i -X POST http://localhost:9900/instance/Order1/field/OrderNumber?value=44343
> curl -i -X POST http://localhost:9900/instance/Order1/field/CustomerName?value=Hubba%20AB
> curl -i -X POST http://localhost:9900/instance/Order1/field/CustomerAddress?value=Gone%20Street%201

**Add relation from *Order1* to *Item1***
> curl -i -X PUT http://localhost:9900/instance/Order1/relation/OrderItem/Item1

**Get relation *OrderItem* from **Order1***
> curl -i -X GET http://localhost:9900/instance/Order1/relation/OrderItem

**Get relation *Item1* from **OrderItem***
> curl -i -X GET http://localhost:9900/instance/Item1/relation/OrderItem

**Invkoke method**
> curl -i -X GET http://localhost:9900/instance/Item1/method/addValue?addValue=10

**Get values from fields**
> curl -i -X GET http://localhost:9900/instance/Item1/field/ItemName
> curl -i -X GET http://localhost:9900/instance/Item1/field/ItemNumber
> curl -i -X GET http://localhost:9900/instance/Item1/field/ItemQuantity
> curl -i -X GET http://localhost:9900/instance/Order1/field/OrderNumber
> curl -i -X GET http://localhost:9900/instance/Order1/field/CustomerName
> curl -i -X GET http://localhost:9900/instance/Order1/field/CustomerAddress


// --- All calls put together to create a base structure of Item and Order
> curl -i -X PUT http://localhost:9900/transaction/
> curl -i -X PUT http://localhost:9900/class?alias=Item
> curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemName
> curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemNumber
> curl -i -X PUT http://localhost:9900/class/Item/field?alias=ItemQuantity
> curl -i -X PUT http://localhost:9900/class?alias=Order
> curl -i -X PUT http://localhost:9900/class/Order/field?alias=OrderNumber
> curl -i -X PUT http://localhost:9900/class/Order/field?alias=CustomerName
> curl -i -X PUT http://localhost:9900/class/Order/field?alias=CustomerAddress
> curl -i -X PUT http://localhost:9900/class/Order/relation/Item?alias=OrderItem
> curl -i -X PUT http://localhost:9900/class/Order/instance?alias=Order1
> curl -i -X PUT http://localhost:9900/class/Item/instance?alias=Item1
> curl -i -X PUT http://localhost:9900/instance/Order1/relation/OrderItem/Item1
> curl -i -X PUT http://localhost:9900/meta?alias=Base
> curl -i -X PUT http://localhost:9900/meta/Base/method?alias=addValue
> echo 'invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() + invokeWorker.getValue("addValue").asNumber());' | curl -i -X POST -d @- http://localhost:9900/method/addValue/code
> curl -i -X PUT 'http://localhost:9900/method/addValue/param?paramName=sumValue&paramField=Number' 
> curl -i -X PUT 'http://localhost:9900/method/addValue/param?paramName=addValue&paramField=Number'
> curl -i -X PUT http://localhost:9900/class/Item/method/addValue?sumValue=ItemQuantity
> curl -i -X GET http://localhost:9900/instance/Item1/method/addValue?addValue=10