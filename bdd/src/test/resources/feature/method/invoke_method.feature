Feature: Invoke method
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a base called Meta
    And there is a type called Item
    And the type Item has a field named Quantity
    And the base Meta has a method named AddNumber
    And there is an instance created from type: Item named: ITEM1

  Scenario: Invoke simple method
    Given the method AddNumber has code value int a = 10;
    And the method AddNumber is welded to Item
    When I invoke method AddNumber of ITEM1
    Then the response is ok

  Scenario: Invoke method with param
    Given the method AddNumber has code value int a = 10;
    And the method AddNumber has a parameter called sumValue
    And the method AddNumber has a parameter called addValue
    And the method AddNumber with parameter sumValue is welded to Item and field Quantity
    And the method AddNumber is welded to Item
    And set instance: ITEM1 field: Quantity to value: 15
    When I invoke method AddNumber of ITEM1 with param addValue and value 8
    Then the response is ok
    And the value of ITEM1 field: Quantity is: 23
