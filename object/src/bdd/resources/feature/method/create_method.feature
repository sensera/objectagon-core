Feature: Create method
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a base called Item

  Scenario: Create method
    When I add method AddNumber to Item
    Then the response is ok

