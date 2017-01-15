Feature: Find type
  As part of building a program i must be able to search and find an type by name

  Background:
    Given there is an active transaction
    And there is a type called Item
    And there is an instance created from type: Item named: ITEM1

  Scenario: I want to be able to name this instance and find it later on with this name
    When I add type Item instance alias ITEM1_ALIAS for instance named: ITEM1
    Then the response is ok

