package net.mikelythgoe.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// This class shows the setting up of a Mock Server and some tests
// that call the responses defined in the Mock Server.
// The class overall therefore has no real value, it shows coded requests to mock server calls.
// However, it DOES show how a REAL client could be configured and tested using a mock server
// and so serves as an example of how to test 'real' code
public class WireMockInternalGdprApiServerTests {

    private static final RestTemplate restTemplate = new RestTemplate();

    private static final String PROTOCOL = "http://";
    private static final String HOST = "localhost";
    private static final int PORT = 8081;
    private static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(PORT);

    @BeforeAll
    public static void initialize() {

        WIRE_MOCK_SERVER.start();
        WireMock.configureFor(HOST, PORT);

        // Build a response
        ResponseDefinitionBuilder gdprResponse = new ResponseDefinitionBuilder();
        gdprResponse.withStatus(200);
        gdprResponse.withStatusMessage("OK");
        gdprResponse.withHeader("Content-Type", "application/json");
        gdprResponse.withBody("" +
                "{\n" +
                "    \"has_consent\": true,\n" +
                "    \"time\": \"2022-12-12T12:29:00.473\"\n"
                + "}");

        // Map the response to a request url
        WireMock.givenThat(WireMock.get("/uat_public/v0/consent/status?" +
                "store_id=8302&" +
                "store_customer_id=151853&" +
                "source_system=Socrates")
                .willReturn(gdprResponse));

    }

    @Test
    void testCallGdpr() {

        var url = PROTOCOL + HOST + ":" + PORT + "/uat_public/v0/consent/status?" +
                "store_id=8302&store_customer_id=151853&source_system=Socrates";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isEqualTo(
                "{\n" + "    \"has_consent\": true,\n" +
                        "    \"time\": \"2022-12-12T12:29:00.473\"\n" +
                        "}");

    }

}
