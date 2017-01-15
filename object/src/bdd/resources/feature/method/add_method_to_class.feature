Feature: Create field
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a base called Meta
    And there is a type called Item
    And the base Meta has a method named AddNumber

  Scenario: Create field
    When I weld method AddNumber to type Item
    Then the response is ok

