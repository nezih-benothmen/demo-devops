# language: en
Feature: Product Creation
  As a catalog manager
  I want to create a new product
  So that it becomes available for sale

  Scenario: Create a product with valid data
    Given a product named "Mechanical Keyboard" priced at 89.90 with a stock of 25
    When I submit the product creation request
    Then the product is created successfully
    And the created product has the name "Mechanical Keyboard"