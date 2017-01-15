Feature: Create field
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a type called Item
    And the type Item has a field named ItemName

  Scenario: Set default value
    Given the field ItemName has default value Phone
    Then the response is ok
    And the default value for ItemName is Phone
