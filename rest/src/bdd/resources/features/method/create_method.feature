Feature: Create method
  # As part creating a domain model

  Scenario: Create simple method
    When I send method/create_domain_method.json to batch
    Then the response is ok

  Scenario: Create method with params
    When I send method/create_domain_method_with_params.json to batch
    Then the response is ok

  Scenario: Create simple method connected attached class
    When I send method/create_domain_simple_method_and_class_method.json to batch
    Then the response is ok

  Scenario: Create complex method connected attached class
    When I send method/create_domain_complex_method_and_class_method.json to batch
    Then the response is ok

  Scenario: Create simple method connected attached class with known bug
    When I send method/bug/create_domain_simple_method_and_class_method_bug_same_method_name.json to batch
    Then the response is fail with Server returned HTTP response code: 500 as error

