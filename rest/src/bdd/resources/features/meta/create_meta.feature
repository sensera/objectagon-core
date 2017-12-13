Feature: Create meta
  # As part creating a domain model

  Scenario: Create simple meta
    When I send meta/create_domain_meta.json to batch
    Then the response is ok

