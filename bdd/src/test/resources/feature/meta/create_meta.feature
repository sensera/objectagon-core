Feature: Create Base
  As part of building a program it must be able to create a Base.

  Background:
    Given there is an active transaction

  Scenario: Create a base
    When I create a base called:  Base
    Then the response is ok
