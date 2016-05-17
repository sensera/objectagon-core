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
core/src/main/org/objectagon/core/rest/README.md

###### Start transaction and add to new session
curl -i -X PUT http://localhost:9900/transaction/
###### Create a new class and store the class address in alias named "Item" 
curl -i -X PUT http://localhost:9900/class?alias=Item
###### Set name of class with alias Item to Item
curl -i -X POST http://localhost:9900/class/Item/name?INSTANCE_CLASS_NAME=Item
###### Check the new name of the class
curl -i -X GET http://localhost:9900/class/Item/name

