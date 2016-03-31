Feature: Create instance
  As part of building a program i must be able to create a instance.

  Background:
    Given there is an active transaction
    And there is a type called Car
    And the type Car has a field named Color

  Scenario: Create a instance from type Car
    When I create an instance from type: Car
    Then the response is ok
