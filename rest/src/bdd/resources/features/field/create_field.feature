Feature: Create field
  # As part creating a domain model

  Scenario: Create single field
    When I send field/create_field.json to batch
    Then the response is ok
    And there is a class named person.class
    And there is an field with name field in person.class class

  Scenario: Create multiple fields
    When I send field/create_field.json to batch
    Then the response is ok
    And there is a class named person.class
    And there is an field with name field in person.class class

