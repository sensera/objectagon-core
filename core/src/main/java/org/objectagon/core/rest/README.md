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
| /session/                     | Get session details | Create new transaction | -          | -                |   
| /session/login                | -                   | -                 | Login session   | -                |   
| /session/logout               | -                   | -                 | Logout session  | -                |   
| /session/transaction          | List transactions   | Create new Transaction | Update transaction | -        |   
| /session/transaction/{id}     | -                   | -                 | -            | Remove from session |   

###### REST - Transaction

| URL PATTERN                   | GET                 | PUT               | POST            | DELETE           |
|-------------------------------|:-------------------:|:-----------------:|:---------------:|:----------------:|
| /transaction/                 | List transactions   | Create new transaction | -          | -                |   
| /transaction/{id}             | Get transaction contents | -            | Update transaction | -             |   
| /transaction/{id}/extend      | -                   | Create new transaction| -           | -                |   
| /transaction/{id}/commit      | Commit transaction  | -                 | -               | -                |   
| /transaction/{id}/rollback    | Rollback transaction | -                | -               | -                |   
| /transaction/{id}/remove      | Remove transaction  | -                 | -               | -                |   

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
| /instance/                    | List instance       | -                 | -               | -                |   
| /instance/{name}              | List instance       | Create new instance | -             | -                |   
| /instance/{name}/{id}         | Get instance contents | -               | -               | Delete instance  |   
| /instance/{name}/{id}/field/{name} | Get field value | Update field value | Add field value | Delete value   |   
| /instance/{name}/{id}/relation/{name} | Get relations | -               | Add relation    | -                |   
| /instance/{name}/{id}/relation/{name}/{id} | Get relation | -           | -               | Delete relation  |   
| /instance/{name}/{id}/method  | Invokoe method      | -                 | Invoke method   | -                |   
 
 



