Feature: Transactions
  # As part creating a domain model

  Background:
    Given I send create_simple_domain.json to batch

  Scenario: Verify created domain in other transaction
    Given I shift to Kalle
    Then the value of instance person2 for field person.class.Age is 78

  Scenario: Verify created domain in other transaction with method test
    Given I shift to Kalle
    And I assign root transaction to current token
    When method addSum for instance person2 is invoked with params
      | addValue | 4 |
    Then the value of instance person2 for field person.class.Age is 82

  Scenario: Verify commit of value
    Given I shift to Kalle
    And I assign root transaction to current token
    And I extend current transaction
    And method addSum for instance person2 is invoked with params
      | addValue | 4 |
    And the value of instance person2 for field person.class.Age is 82
    And I shift to Arne
    And I assign root transaction to current token
    And the value of instance person2 for field person.class.Age is 78
    And I shift to Kalle
    And the value of instance person2 for field person.class.Age is 82
    And I commit current transaction
    And I shift to Arne
    And the value of instance person2 for field person.class.Age is 82

  Scenario: Verify commit of method
    Given I shift to Kalle
    And I assign root transaction to current token
    And I extend current transaction
    And Update method addSum to invokeWorker.setValue("sumValue").set(invokeWorker.getValue("sumValue").asNumber() - invokeWorker.getValue("addValue").asNumber());
    And the value of instance person2 for field person.class.Age is 78
    And method addSum for instance person2 is invoked with params
      | addValue | 4 |
    And the value of instance person2 for field person.class.Age is 74
    And I shift to Arne
    And I assign root transaction to current token
    And the value of instance person2 for field person.class.Age is 78
    And method addSum for instance person2 is invoked with params
      | addValue | 4 |
    And the value of instance person2 for field person.class.Age is 82
    And I shift to Kalle
    And I commit current transaction
    And I shift to Arne
    And the value of instance person2 for field person.class.Age is 74
    And method addSum for instance person2 is invoked with params
      | addValue | 4 |
    And the value of instance person2 for field person.class.Age is 70



