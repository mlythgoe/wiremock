package net.mikelythgoe.wiremock;


import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for simple App.
 */
public class SimpleConfigTests {

    private RestTemplate restTemplate;
    private WireMockServer wireMockServer;

    @BeforeEach
    void configureSystemUnderTest() {

        this.restTemplate = new RestTemplate();
        this.wireMockServer = new WireMockServer(options().port(8081));
        this.wireMockServer.start();

        configureFor("localhost", 8081);
    }

    @Test
    @DisplayName("Should ensure that WireMock server was started")
    void shouldEnsureThatServerWasStarted() {

        String responseBody = "This is the Response Body";

        givenThat(get(urlEqualTo("/")).willReturn(aResponse()
                .withStatus(200)
                .withBody(responseBody.getBytes())
        ));

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseBody);

    }

    @Test
    @DisplayName("Should respond with http status 200 if receiving request url of http://localhost:8081/justarandomurl")
    void shouldReturnSomething() {

        givenThat(get(urlEqualTo("/justarandomurl")).willReturn(aResponse()
                .withBody("Response Body".getBytes())
                .withStatus(200)
        ));

        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/justarandomurl", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @AfterEach
    void stopWireMockServer() {

        this.wireMockServer.stop();

    }
}

