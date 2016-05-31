Feature: Set method code
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a type called Meta
    And the base Meta has a method named addOne

  Scenario: Set method code
    When I set the code of the method addOne to "go get some"
    Then the response is ok
    And the code value for addOne is "go get some"
