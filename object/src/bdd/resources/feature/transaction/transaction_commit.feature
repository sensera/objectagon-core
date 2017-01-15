Feature: Transaction commit
  As part of building a program i must be able to make changes within transactions

  Background:
    Given there is an active transaction
    And there is a type called Item


  Scenario: Commit a created type
    Given the active transaction contains Item
    When commit the active transaction
    Then the response is ok


  Scenario: Commit a created type and field
    And the type Item has a field named ItemName
    Given the active transaction contains ItemName
    When commit the active transaction
    Then the response is ok


  Scenario: Commit a created type and field and instance
    And the type Item has a field named ItemName
    And there is an instance created from type: Item named: ITEM1
    Given the active transaction contains ITEM1
    When commit the active transaction
    Then the response is ok
