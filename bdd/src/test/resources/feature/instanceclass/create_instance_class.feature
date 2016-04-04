Feature: Create type
  As part of building a program i must be able to create a Type.

  Background:
    Given there is an active transaction

  Scenario: Create a Type called Item
    When I create a type called: Item
    Then the response is ok
