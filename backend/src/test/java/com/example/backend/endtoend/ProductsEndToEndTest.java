package com.example.backend.endtoend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductsEndToEndTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateProduct() {
        String name = UUID.randomUUID().toString();
        String productJson = "{\n" +
                "    \"name\": \""+ name+"\",\n" +
                "    \"salesUnit\": 20,\n" +
                "    \"stock\": {\n" +
                "        \"S\": 9,\n" +
                "        \"M\": 2,\n" +
                "        \"L\": 5\n" +
                "    }\n" +
                "}";

        webTestClient.post()
                .uri("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productJson)
                .exchange()
                .expectStatus().isOk()  // Assuming you return 200 OK
                .expectBody()
                .jsonPath("$.name").isEqualTo(name)
                .jsonPath("$.salesUnit").isEqualTo(20);
    }

    @Test
    void shouldCreateProductFail() {
        // Simulating the POST request from Postman
        String name = UUID.randomUUID().toString();
        String productJson = "{\n" +
                "    \"name\": \""+ name+"\",\n" +
                "    \"salesUnit\": 20,\n" +
                "    \"stock\": {\n" +
                "        \"S\": 9,\n" +
                "        \"M\": 2,\n" +
                "        \"L\": 5\n" +
                "    }\n" +
                "}";

        webTestClient.post()
                .uri("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productJson)
                .exchange()
                .expectStatus().isOk();


        webTestClient.post()
                .uri("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(productJson)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldGetProductsSortedBySalesUnit() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/products/index")
                        .queryParam("sortBy", "salesUnit")
                        .queryParam("asc", "true")
                        .build())
                .exchange()
                .expectStatus().isOk()  // Assuming you return 200 OK
                .expectBody()
                .jsonPath("$.length()").isNumber()
                .jsonPath("$.length()").value(length -> assertTrue((Integer) length > 0))
                .jsonPath("$[*].salesUnit").value(salesUnit -> {
                    // Cast the list of stockSum values to a List of Integers
                    List<Integer> salesUnitList = (List<Integer>) salesUnit;
                    // Ensure each element's stockSum is greater than the previous one
                    for (int i = 1; i < salesUnitList.size(); i++) {
                        assertTrue(salesUnitList.get(i) >= salesUnitList.get(i - 1),
                                "The salesUnit at index " + i + " is not greater than the previous one.");
                    }
                });
    }


    @Test
    void shouldGetProductsSortedByStockSum() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/products/index")
                        .queryParam("sortBy", "stockSum")
                        .queryParam("asc", "true")
                        .build())
                .exchange()
                .expectStatus().isOk()  // Assuming you return 200 OK
                .expectBody()
                .jsonPath("$.length()").isNumber()
                .jsonPath("$.length()").value(length -> assertTrue((Integer) length > 0))
                .jsonPath("$[*].stockSum").value(stockSum -> {
                    // Cast the list of stockSum values to a List of Integers
                    List<Double> stockSumList = (List<Double>) stockSum;
                    // Ensure each element's stockSum is greater than the previous one
                    for (int i = 1; i < stockSumList.size(); i++) {
                        assertTrue(stockSumList.get(i) >= stockSumList.get(i - 1),
                                "The stockSum at index " + i + " is not greater than the previous one.");
                    }
                });
    }

    @Test
    void shouldPostMultiQuery() {
        // Simulating the POST request with a multi-query from Postman
        String multiQueryJson = "{\n" +
                "  \"queryAttributes\": [\n" +
                "    {\"attribute\": \"stock\", \"weight\": 0.2},\n" +
                "    {\"attribute\": \"stock\", \"weight\": 0.2}\n" +
                "  ],\n" +
                "  \"asc\": true\n" +
                "}";

        webTestClient.post()
                .uri("/v2/products/index")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(multiQueryJson)
                .exchange()
                .expectStatus().isOk()  // Assuming you return 200 OK
                .expectBody()
                .jsonPath("$.length()").isNumber()
                .jsonPath("$.length()").value(length -> assertTrue((Integer) length > 0))
                .jsonPath("$[*].weightedValue").value(weightedValue -> {
                    // Cast the list of stockSum values to a List of Integers
                    List<Double> weightedValueList = (List<Double>) weightedValue;
                    // Ensure each element's stockSum is greater than the previous one
                    for (int i = 1; i < weightedValueList.size(); i++) {
                        assertTrue(weightedValueList.get(i) >= weightedValueList.get(i - 1),
                                "The weightedValue at index " + i + " is not greater than the previous one.");
                    }
                });

    }
}
