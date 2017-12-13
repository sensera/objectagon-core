Feature: Create instance value
  # As part creating a domain model

  Scenario: Create single instance single value
    When I send instancevalue/create_instance_value.json to batch
    Then the response is ok
    And the value of instance person1 for field person.class.Name is Lars Gurra Aktersnurra

  Scenario: Create single instance multiple values
    When I send instancevalue/create_instance_values.json to batch
    Then the response is ok
    And the value of instance person1 for field person.class.Comment is Mera Gurra tack
    And the value of instance person1 for field person.class.Age is 44
    And the value of instance person1 for field person.class.Name is Lars Gurra Aktersnurra

