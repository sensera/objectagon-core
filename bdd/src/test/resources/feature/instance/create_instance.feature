Feature: Create instance
  As part of building a program it must be able to create an instance.

  Background:
    Given there is an active transaction
    And there is a type called Item
    And the type Item has a field named ItemName

  Scenario: Create a instance from type
    When I create an instance from type: Item
    Then the response is ok
