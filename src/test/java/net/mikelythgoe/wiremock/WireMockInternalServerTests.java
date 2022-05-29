package net.mikelythgoe.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


// This class shows the setting up of a Mock Server and some tests
// that call the responses defined in the Mock Server.
// The class overall therefore has no real value, it shows coded requests to mock server calls.
// However, it DOES show how a REAL client could be configured and tested using a mock server
// and so serves as an example of how to test 'real' code
public class WireMockInternalServerTests {

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
        ResponseDefinitionBuilder postEmployee1Response = new ResponseDefinitionBuilder();
        postEmployee1Response.withStatus(201);
        postEmployee1Response.withStatusMessage("I have just been created");
        postEmployee1Response.withHeader("Content-Type", "text/json");
        postEmployee1Response.withHeader("token", "11111");
        postEmployee1Response.withHeader("Set-Cookie", "session-id=11111111");
        postEmployee1Response.withHeader("Set-Cookie", "split_test_group=B");
        postEmployee1Response.withBody("{ \"name\":\"Employee One\" }");

        // Map the response to a request url
        WireMock.givenThat(WireMock.post("/employees/1").willReturn(postEmployee1Response));

        // Build a response
        ResponseDefinitionBuilder employee2Response = new ResponseDefinitionBuilder();
        employee2Response.withStatus(200);
        employee2Response.withStatusMessage("I Am Employee 2");
        employee2Response.withHeader("Content-Type", "text/json");
        employee2Response.withHeader("token", "22222");
        employee2Response.withHeader("Set-Cookie", "session-id=222222222");
        employee2Response.withHeader("Set-Cookie", "split_test_group=A");
        employee2Response.withBody("{ \"name\":\"Employee Two\" }");

        // Map the response to a request url
        WireMock.givenThat(WireMock.get("/employees/2").willReturn(employee2Response));

        // Build a response
        ResponseDefinitionBuilder getEmployee999Response = new ResponseDefinitionBuilder();
        getEmployee999Response.withStatus(404);
        getEmployee999Response.withStatusMessage("Does Not Exists");

        // Map the response to a request url
        WireMock.givenThat(WireMock.get("/employees/999").willReturn(getEmployee999Response));
    }

    @Test
    void testPostEmployees1() {

        var request = new HttpEntity<>("{ \"name\":\"Employee One\" }");

        var response = restTemplate.postForEntity(PROTOCOL + HOST + ":" + PORT + "/employees/1", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    }

    @Test
    void testGetEmployees2() {

        ResponseEntity<String> response = restTemplate.getForEntity(PROTOCOL + HOST + ":" + PORT + "/employees/2", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(response.getBody()).isEqualTo("{ \"name\":\"Employee Two\" }");

    }

    @Test
    void testGetEmployeesThatDoesNotExist() {

        var message = "404 Does Not Exists: [no body]";

        var expectedException = assertThrows(

                HttpClientErrorException.class,
                () -> {
                    restTemplate.getForEntity(PROTOCOL + HOST + ":" + PORT + "/employees/999", null);

                });

        assertEquals(message, expectedException.getMessage());

    }

}
