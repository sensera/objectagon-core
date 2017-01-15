Feature: Create field
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a type called Item

  Scenario: Create field
    When I add field ItemNumber to Item
    Then the response is ok

