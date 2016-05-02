Feature: Create instance
  As part of building a program it must be able to create an instance.

  Background:
    Given there is an active transaction
    And there is a type called Order
    And there is a type called OrderRow
    And there is an owning relation from Order to OrderRow named OrderToOrderRow
    And there is an instance created from type: Order named: Order1
    And there is an instance created from type: OrderRow named: OrderRow1
    And I add OrderRow1 to Order1 with relation OrderToOrderRow
    And there is a relation OrderToOrderRow from Order1 to OrderRow1 named OrderToOrderRow1

  Scenario: Remove relation
    When I remove OrderRow1 from Order1 with relation OrderToOrderRow
    Then the response is ok
