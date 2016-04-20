Feature: Find type
  As part of building a program i must be able to search and find an type by name

  Background:
    Given there is an active transaction
    And there is a type called Item

  Scenario: I want to find and item with a specific name
    When I search for an type called: Item
    Then the response is ok
