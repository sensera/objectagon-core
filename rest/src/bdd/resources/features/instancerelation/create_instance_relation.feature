Feature: Create relation
  # As part creating a domain model

  Scenario: Create simple relation
    When I send instancerelation/create_instance_relation.json to batch
    Then the response is ok
    And there is an instance with alias main1 in main class
    And there is an instance with alias person1 in person.class class
    And there is a relation main.person.relation between main1 and person1

