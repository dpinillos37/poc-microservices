Feature: Category CRUD tests
  CRUD tests for the entity Category

  Scenario: create
    Given the category SENIOR
    When I create this category
    Then category id is created

  Scenario: create and obtain
    Given the category PRESIDENT
    And I create this category
    And category id is created
    When I consult this category
    Then category PRESIDENT exists

  Scenario: obtain all
    Given the category DIRECTOR
    And I create this category
    And the category SECRETARY
    And I create this category
    And the category SUB DIRECTOR
    And I create this category
    When I consult all categories
    Then categories DIRECTOR, SUB DIRECTOR, SECRETARY exist

  Scenario: obtain not existent
    Given a not existent Id
    When I consult this category
    Then I don't get any category

  Scenario: create existent
    Given the category DELEGATE OF CLASS
    And I create this category
    And category id is created
    When I create this category
    Then status: 409
    And error: Duplicated object

  Scenario: validation error: mandatory field
    When I create this category
    Then status: 400
    And error in field categoryName: must not be null

  Scenario: validation error: format error
    Given the category 123456789012345678901
    When I create this category
    Then status: 400
    And error in field categoryName: size must be between 1 and 20

  Scenario: create and update
    Given the category TO MODIFY
    And I create this category
    And the category MODIFIED VALUE
    And I modify this category
    Then status: 200
    And I consult this category
    And category MODIFIED VALUE exists

  Scenario: create and delete
    Given the category TO DELETE
    And I create this category
    And status: 201
    And I consult this category
    And status: 200
    And category TO DELETE exists
    And I delete this category
    And status: 200
    And I consult this category
    Then I don't get any category
