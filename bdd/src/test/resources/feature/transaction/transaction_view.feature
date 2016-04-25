Feature: Transaction view
  As part of building a program i must be able to make changes within transactions

  Background:
    Given there is an transaction named SystemTransaction
    And there is a type called Item
    And the type Item has a field named ItemName
    And the field ItemName has default value Phone
    And there is an instance created from type: Item named: ITEM1
    And the active transaction has been commited
    And there is an user named Adam with role User
    And there is an user named Eva with role User
    And there is an transaction named AdamsTransaction
    And there is an transaction named EvasTransaction
    And there is an user named Adam with transaction AdamsTransaction
    And there is an user named Eva with transaction EvasTransaction

  Scenario: Different view of same value
    Given the active transaction is: SystemTransaction
    Then the value of ITEM1 field: ItemName is: Phone
    Then user Adam get value Phone from ITEM1 field ItemName
    Then user Eva get value Phone from ITEM1 field ItemName
