package net.mikelythgoe.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// These tests assume a STANDALONE WireMock Server is running with the expected mappings and test
// These tests are useful for validating a standalone server
// The tests are disabled - we do NOT want them to run as part of the maven lifecycle
// because they depend on an external WireMock Server being up and running

@WireMockTest
@Disabled
public class WireMockStandaloneTests {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    @Test
    void testPostThatCreates() {

        HttpEntity<String> request = new HttpEntity<>("{ \"name\":\"Employee One\" }");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://" + HOST + ":" + PORT + "/employees/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    }

    @Test
    void testGetThatExists() {

        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://" + HOST + ":" + PORT + "/employees/2", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isEqualTo("{ \"name\":\"Employee Two\" }");

    }

    @Test
    void testGetThatDoesNotExist() {

        try {

            restTemplate.getForEntity(
                    "http://" + HOST + ":" + PORT + "/employees/99", String.class);

        } catch (HttpStatusCodeException ex) {

            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        }

    }

}
