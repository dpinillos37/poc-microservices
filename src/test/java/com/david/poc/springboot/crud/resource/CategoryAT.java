package com.david.poc.springboot.crud.resource;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.lang3.ArrayUtils.nullToEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.david.poc.springboot.crud.model.Category;
import io.cucumber.java8.En;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryAT implements En {

  public static final String URL_CATEGORY_PATTERN =
      "http://localhost:{0}/elections/v1/categories/{1}";
  private static final RestTemplate restTemplate = new RestTemplate();
  private static Log log = LogFactory.getLog(CategoryAT.class);
  @LocalServerPort
  private String port;
  private String categoryName;
  private Supplier<Category> createCategory = () -> new Category()
      .setCategoryName(categoryName);
  private UUID categoryId;
  private List<ErrorResponse> errors = new ArrayList<>();
  private int responseStatus;
  private List<Category> categoriesConsulted = Collections.emptyList();

  /**
   * Acceptance test for category module.
   */
  public CategoryAT() {
    Given("^the category (.*)$",
        (String category) -> this.categoryName = category);
    Given("^a not existent Id$", () -> this.categoryId = UUID.randomUUID());
    When(
        "I create this category",
        () -> {
          final URI uriResponse;
          try {
            ResponseEntity<String> response =
                restTemplate.exchange(
                    pathGeneric(),
                    HttpMethod.POST,
                    new HttpEntity<>(createCategory.get(),
                        headersForMethodsWithBody()),
                    String.class);
            uriResponse = response.getHeaders().getLocation();
            assertNotNull(uriResponse);

            this.categoryId = UUID.fromString(
                uriResponse.toString().replace(pathGeneric(), EMPTY));
            this.responseStatus = response.getStatusCodeValue();
          } catch (HttpStatusCodeException e) {
            manageHttpError(e);
          }
        });
    When(
        "I modify this category",
        () -> {
          final URI uriResponse;
          try {
            ResponseEntity<String> response =
                restTemplate.exchange(
                    pathToElement(),
                    HttpMethod.PUT,
                    new HttpEntity<>(createCategory.get(),
                        headersForMethodsWithBody()),
                    String.class);
            assertNotNull(response);

            this.responseStatus = response.getStatusCodeValue();
          } catch (HttpStatusCodeException e) {
            manageHttpError(e);
          }
        });
    When(
        "I delete this category",
        () -> {
          final URI uriResponse;
          try {
            ResponseEntity<String> response =
                restTemplate.exchange(pathToElement(), HttpMethod.DELETE, null,
                    String.class);
            assertNotNull(response);

            this.responseStatus = response.getStatusCodeValue();
          } catch (HttpStatusCodeException e) {
            manageHttpError(e);
          }
        });
    When(
        "I consult this category",
        () -> {
          try {
            ResponseEntity<Category> response =
                restTemplate.getForEntity(pathToElement(), Category.class);

            categoriesConsulted =
                Optional.ofNullable(response.getBody())
                    .map(Collections::singletonList)
                    .orElseGet(Collections::emptyList);
            this.responseStatus = response.getStatusCodeValue();
          } catch (HttpStatusCodeException e) {
            manageHttpError(e);
          }
        });
    When(
        "I consult all categories",
        () -> {
          try {
            ResponseEntity<Category[]> response =
                restTemplate.exchange(
                    pathGeneric(),
                    HttpMethod.GET,
                    new HttpEntity<>(null, headersForGet()),
                    Category[].class);

            this.categoriesConsulted = asList(
                nullToEmpty(response.getBody(), Category[].class));
            this.responseStatus = response.getStatusCodeValue();
          } catch (HttpStatusCodeException e) {
            manageHttpError(e);
          }
        });
    Then(
        "^(categories|category) (.*) exist[s]?$",
        (String ignoreThis, String categoriesJoined) -> {
          String[] categoriesRequested =
              Arrays.stream(categoriesJoined.split(","))
                  .map(String::trim)
                  .sorted()
                  .toArray(String[]::new);

          String[] categoriesExistent =
              emptyIfNull(this.categoriesConsulted).stream()
                  .map(Category::getCategoryName)
                  .sorted()
                  .toArray(String[]::new);
          assertThat(categoriesExistent).contains(categoriesRequested);
        });
    Then(
        "^category id is created$",
        () -> {
          assertNotNull(this.categoryId);
          assertEquals(HttpStatus.CREATED.value(), this.responseStatus);
        });
    Then(
        "^I don't get any category*$",
        () -> {
          assertThat(errors).isEmpty();
          assertThat(categoriesConsulted).isEmpty();
          assertEquals(HttpStatus.OK.value(), this.responseStatus);
        });
    Then(
        "^status: (\\d*)$",
        (Integer status) -> {
          assertEquals(status.intValue(), this.responseStatus);
        });
    Then(
        "^error( in field (\\w*))?: (.*)$",
        (String field, String errorMessage) -> {
          Optional<ErrorResponse> errorResponseStream =
              this.errors.stream()
                  .filter(errorEntry -> errorMessage
                      .equals(errorEntry.getMessage()))
                  .findAny();
          assertThat(errorResponseStream).isPresent();
          if (Objects.nonNull(field)) {
            assertEquals(field, errorResponseStream.get().getField());
          }
        });
  }

  private void manageHttpError(HttpStatusCodeException e) {
    log.error(e.getMessage(), e);
    this.responseStatus = e.getStatusCode().value();
    errorMapping(e);
  }

  private void errorMapping(HttpStatusCodeException e) {
    String responseBodyAsString = e.getResponseBodyAsString();
    if (isJsonObject(e)) {
      jsonErrorMapping(responseBodyAsString);
    } else {
      basicErrorMapping(responseBodyAsString);
    }
  }

  private void jsonErrorMapping(String errorMessage) {
    try {
      JSONObject jsonObject = new JSONObject(errorMessage);
      addErrorMessage(jsonObject.optString("message"),
          jsonObject.optString("path"));
    } catch (JSONException e1) {
      try {
        JSONArray jsonArray = new JSONArray(errorMessage);
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          addErrorMessage(jsonObject.optString("message"),
              jsonObject.optString("path"));
        }
      } catch (JSONException e2) {
        basicErrorMapping(errorMessage);
      }
    }
  }

  private void addErrorMessage(String message, String wrongFieldPath) {
    String wrongField = null;
    if (Objects.nonNull(wrongFieldPath)) {
      String[] pathSplit = wrongFieldPath.split("\\.");
      wrongField = pathSplit[pathSplit.length - 1];
    }
    this.errors.add(new ErrorResponse(message, wrongField));
  }

  private boolean isJsonObject(HttpStatusCodeException e) {
    return Objects.nonNull(e.getResponseHeaders())
        && MediaType.APPLICATION_JSON
        .equals(e.getResponseHeaders().getContentType());
  }

  private void basicErrorMapping(String errorMessage) {
    addErrorMessage(errorMessage, null);
  }

  private MultiValueMap<String, String> headersForMethodsWithBody() {
    MultiValueMap<String, String> objectObjectHashMap = new LinkedMultiValueMap<>();
    objectObjectHashMap.put(
        HttpHeaders.CONTENT_TYPE,
        Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
    objectObjectHashMap.put(
        HttpHeaders.ACCEPT,
        Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
    return objectObjectHashMap;
  }

  private MultiValueMap<String, String> headersForGet() {
    MultiValueMap<String, String> objectObjectHashMap = new LinkedMultiValueMap<>();
    objectObjectHashMap.put(
        HttpHeaders.ACCEPT,
        Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
    return objectObjectHashMap;
  }

  private String pathGeneric() {
    return format(URL_CATEGORY_PATTERN, port, EMPTY);
  }

  private String pathToElement() {
    return format(URL_CATEGORY_PATTERN, port, this.categoryId);
  }

  @AllArgsConstructor
  @Getter
  private static class ErrorResponse {

    private String message;
    private String field;
  }
}
