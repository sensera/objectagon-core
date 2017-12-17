Feature: Create domain
  # As part creating a domain model

  Scenario: Create simple domain
    When I send create_simple_domain.json to batch
    Then the response is ok
    And there is an instance with alias main1 in main class
    And the value of instance person2 for field person.class.Age is 78
    And the value of instance person2 for field person.class.Comment is Svempsson är en torsk
    And the value of instance person2 for field person.class.Name is Svempa Snyltström
    And the value of instance person1 for field person.class.Age is 44
    And the value of instance person1 for field person.class.Comment is Mera Gurra tack
    And the value of instance person1 for field person.class.Name is Lars Gurra Aktersnurra

