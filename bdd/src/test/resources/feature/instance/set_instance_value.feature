Feature: Create instance
  As part of building a program it must be able to set values to an instance

  Background:
    Given there is an active transaction
    And there is a type called Item
    And the type Item has a field named ItemName
    And there is an instance created from type: Item named: ITEM1

  Scenario: Set a value to an instance field
    When set instance: ITEM1 field: ItemName to value: Gurra
    Then the value of instance: ITEM1 field: ItemName is: Gurra
