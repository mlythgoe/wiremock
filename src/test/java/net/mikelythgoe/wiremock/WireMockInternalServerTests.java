package net.mikelythgoe.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.*;


// This class shows the setting up of a Mock Server and some tests
// that call the responses defined in the Mock Server.
// The class overall therefore has no real value, it shows coded requests to mock server calls.
// However, it DOES show how a REAL client could be configured and tested using a mock server
// and so serves as an example of how to test 'real' code
public class WireMockInternalServerTests {

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(PORT);

    @BeforeAll
    public static void initialize() {

        WIRE_MOCK_SERVER.start();
        WireMock.configureFor(HOST, PORT);

        // Build a response
        ResponseDefinitionBuilder emps1Response = new ResponseDefinitionBuilder();
        emps1Response.withStatus(201);
        emps1Response.withStatusMessage("I have just been created");
        emps1Response.withHeader("Content-Type", "text/json");
        emps1Response.withHeader("token", "11111");
        emps1Response.withHeader("Set-Cookie", "session-id=11111111");
        emps1Response.withHeader("Set-Cookie", "split_test_group=B");
        emps1Response.withBody("Text of the Body");

        // Map the response to a request url
        WireMock.givenThat(WireMock.get("/emps/1").willReturn(emps1Response));

        // Build a response
        ResponseDefinitionBuilder emps2Response = new ResponseDefinitionBuilder();
        emps2Response.withStatus(200);
        emps2Response.withStatusMessage("I Am Employee 2");
        emps2Response.withHeader("Content-Type", "text/json");
        emps2Response.withHeader("token", "22222");
        emps2Response.withHeader("Set-Cookie", "session-id=222222222");
        emps2Response.withHeader("Set-Cookie", "split_test_group=A");
        emps2Response.withBody("Text of the Body");

        // Map the response to a request url
        WireMock.givenThat(WireMock.get("/emps/2").willReturn(emps2Response));
    }


    @Test
    void testGetEmps1UsingRestAssured() {

        // RestAssured call - make a http get to the URL and assert the status code is 201
        RestAssured.given().
                get("http://" + HOST + ":" + PORT + "/emps/1").
                then().
                assertThat().
                statusCode(201);

    }

    @Test
    void testGetEmps2UsingRestAssured() {

        // RestAssured call - make a http get to the URL and assert the status code is 200
        RestAssured.given().
                get("http://" + HOST + ":" + PORT + "/emps/2").
                then().
                assertThat().
                statusCode(200);

    }

    @Test
    void testGetEmps2UsingRestTemplateAndAssertJ() {

        // Using RestTemplate and AssertJ
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + HOST + ":" + PORT + "/emps/2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }




}
