Feature: Create field
  # As part of building a program i must be able to att fields to a class.

  Background:
    Given there is an active transaction
    And there is a type called Order
    And there is a type called OrderRow
    And there is a type called SalesOrder
    And there is a type called Item

  Scenario: Create owning relation
    When I add an owning relation from Order to OrderRow named OrderToOrderRow
    Then the response is ok

  Scenario: Create association relation
    When I add an knowing relation from OrderRow to Item named OrderRowToItem
    Then the response is ok

  Scenario: Create extending relation
    When I add an extending relation from SalesOrder to Order named SalesOrderToOrder
    Then the response is ok
