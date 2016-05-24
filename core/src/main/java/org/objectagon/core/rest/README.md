# Objectagon REST server

This document describest REST patterns and functions supported by the server
 
## Description

The implementation principles are focused on just relaying requests direct to the protocols.
URL request are translated into witch protocol to use and what parameters to send. 
URL responses are message response values translated directly to json.

## Disposition

This REST api is divided into several groups: 
1. Session
2. Transaction
2. Meta
3. Class
3. Field
3. Relation
3. Method
4. Instance

###### REST - Session

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /session/                     | Get session details | Create new sessoin | -              | -                |   
| /session/login                | -                   | -                 | Login session   | -                |   
| /session/logout               | -                   | -                 | Logout session  | -                |   
| /session/transaction          | List transactions   | Create new Transaction | Update transaction | -        |   
| /session/transaction/{id}     | -                   | -                 | -            | Remove from session |   

###### REST - Transaction

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /transaction/                 | List transactions   | Create new transaction | -          | -                |   
| /transaction/commit           | -                   | Commit session trans | -            | -                |   
| /transaction/rollback         | -                   | Rollback session trans | -          | -                |   
| /transaction/{id}             | Get contents        | -                 | Update          | Remove           |   
| /transaction/{id}/extend      | -                   | Create new transaction| -           | -                |   
| /transaction/{id}/commit      | -                   | -                 | Commit          | -                |   
| /transaction/{id}/rollback    | -                   | -                 | Rollback        | -                |   

###### REST - Meta
Future versions

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /meta/                        | -                   | -                 | -               | -                |   

###### REST - Class

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /class/                       | List classes        | Create new class  | -               | -                |   
| /class/{id}                   | Get class info      | -                 | Update class    | -                |   
| /class/{id}/name              | Get class name      | -                 | Set class name  | -                |   
| /class/{id}/field             | List fields         | Create new field  | -               | -                |   
| /class/{id}/relation          | Get relations       | Create new relation | -             | -                |   
| /class/{id}/method            | Not supported yet   | -                 | -               | -                |   
| /class/{id}/instance          | -                   | Create new instance | -             | -                |   
| /class/{id}/instance/{name}   | get instanceAlias   | Create new alias  | -               | remove alias     |

###### REST - Field

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /field/{id}                   | Field contents      | -                 | -               | Remove field     |   
| /field/{id}/name              | Get field name      | -                 | Set field name  | -                |   
| /field/{id}/type              | Get field type      | -                 | Set field type  | -                |   
| /field/{id}/default           | Get field default value  | -            | Set field default value | -        | 

###### REST - Relation

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /relation/{id}                | Get relation contents | -               | -               | Remove relation  |   
| /relation/{id}/name           | Get relation name   | -                 | Set relation name | -              |   
| /relation/{id}/type           | Get relation type   | -                 | -               | -                |   
| /relation/{id}/target         | Get relation target | -                 | -               | -                |   

###### REST - Method
Future versions

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /method/{id}                  | Not supported yet   | -                 | -               | -                |   

###### REST - Instance

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /instance/                    | List instances      | -                 | -               | -                |   
| /instance/{name}              | List instances      | Create new instance | -             | -                |   
| /instance/{id}                | Get instance contents | -               | -               | Delete instance  |   
| /instance/{id}/field/{name}   | Get field value     | Update field value | Add field value | Delete value   |   
| /instance/{id}/relation/{name}| Get relations       | -                 | Add relation    | -                |   
| /instance/{id}/relation/{name}/| Get relations       | -                 | Add relation    | -                |
| /instance/{id}/relation/{id}  | Get relation        | -                 | -               | Delete relation  |   
| /instance/{id}/method         | Invokoe method      | -                 | Invoke method   | -                |   
 
 
## Example

curl -i -X PUT http://localhost:9900/transaction/
curl -i -X PUT http://localhost:9900/class?alias=Item
curl -i -X POST http://localhost:9900/class/Item/name?INSTANCE_CLASS_NAME=Item
curl -i -X GET http://localhost:9900/class/Item/name?INSTANCE_CLASS_NAME=Item




