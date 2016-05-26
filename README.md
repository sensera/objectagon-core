objectagon
==========

4GL/5GL Aware System Builder

Continuous migration

Design, Development, Test & Production all integrated and simultaneous.
Continuous Integration and Development.
  
It's distributed, 100% load balanced and secure.

First came Open Source, then Open Data. This is Open Functionality.
  
##  To start server

Go to directory core
enter:
mvn clean install exec:java -Dexec.mainClass="org.objectagon.core.rest.HttpServerImpl" -DskipTests

#### Test server commands

se all server commands at: 
core/src/main/java/org/objectagon/core/rest/README.md


**Start new transaction add att to session**
curl -i -X PUT http://localhost:9900/transaction/

**Skapa en typ och koppla den skapade typens id till alias *Item***
curl -i -X PUT http://localhost:9900/class?alias=Item

**Sätt namn på typ till *Item* och använd alis *Item* i stället för address**
curl -i -X POST http://localhost:9900/class/Item/name?InstanceClassName=Item

**Hämta namnet på typen via alias *Item*** 
curl -i -X GET http://localhost:9900/class/Item/name

**Skapa en typ och koppla den skapade typens id till alias *Order***
curl -i -X PUT http://localhost:9900/class?alias=Order

**Skapa en relationstyp (mellan Order och Item) och koppla den skapade relationstypen till alias *OrderItem***
curl -i -X PUT http://localhost:9900/class/Order/relation/Item?alias=OrderItem


**Skapa en instans av Order och koppla till alias *Order1***
curl -i -X PUT http://localhost:9900/class/Order/instance?alias=Order1

**Skapa en instans av Item och koppla till alias *Item1***
curl -i -X PUT http://localhost:9900/class/Item/instance?alias=Item1

**Lägg till en relation från Order1 to Item1**
curl -i -X PUT http://localhost:9900/instance/Order1/relation/OrderItem/Item1

**Hämta relation *OrderItem* från **Order1***
curl -i -X GET http://localhost:9900/instance/Order1/relation/OrderItem

**Hämta relation *OrderItem* från **Item1***
curl -i -X GET http://localhost:9900/instance/Item1/relation/OrderItem

// ---
curl -i -X PUT http://localhost:9900/transaction/
curl -i -X PUT http://localhost:9900/class?alias=Item
curl -i -X POST http://localhost:9900/class/Item/name?InstanceClasName=Item
curl -i -X PUT http://localhost:9900/class?alias=Order
curl -i -X PUT http://localhost:9900/class/Order/relation/Item?alias=OrderItem
curl -i -X PUT http://localhost:9900/class/Order/instance?alias=Order1
curl -i -X PUT http://localhost:9900/class/Item/instance?alias=Item1
curl -i -X PUT http://localhost:9900/instance/Order1/relation/OrderItem/Item1
