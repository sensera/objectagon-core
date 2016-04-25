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
    And there is an transaction named AdamsTransaction
    And there is an user named Adam with transaction AdamsTransaction

  Scenario: Different view of same value
    Given the active transaction is: SystemTransaction
    Then the value of ITEM1 field: ItemName is: Phone
    And user Adam get value Phone from ITEM1 field ItemName

  Scenario: If the value is changed, other views till se the same value
    Given the active transaction is: SystemTransaction
    When set instance: ITEM1 field: ItemName to value: Gurra
    Then the value of ITEM1 field: ItemName is: Gurra
    And user Adam get value Gurra from ITEM1 field ItemName

  Scenario: Change value in transaction
    Given the active transaction is: SystemTransaction
    And user Adam set value Max in ITEM1 field ItemName
    Then the value of ITEM1 field: ItemName is: Phone
    And user Adam get value Max from ITEM1 field ItemName
