Feature: Create field
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a base called Meta
    And there is a type called Item
    And the base Meta has a method named AddNumber
    And the method AddNumber is welded to Item
    And there is an instance created from type: Item named: ITEM1
    And the method AddNumber has code value int a = 10;

  Scenario: Create field
    When I invoke method AddNumber of ITEM1
    Then the response is ok

