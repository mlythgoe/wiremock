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


public class WireMockInternalServerTests {
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(PORT);

    @BeforeAll
    public static void initialize() {

        WIRE_MOCK_SERVER.start();
        WireMock.configureFor(HOST, PORT);

        ResponseDefinitionBuilder emps1Response = new ResponseDefinitionBuilder();
        emps1Response.withStatus(201);
        emps1Response.withStatusMessage("I have just been created");
        emps1Response.withHeader("Content-Type", "text/json");
        emps1Response.withHeader("token", "11111");
        emps1Response.withHeader("Set-Cookie", "session-id=11111111");
        emps1Response.withHeader("Set-Cookie", "split_test_group=B");
        emps1Response.withBody("Text of the Body");

        WireMock.givenThat(WireMock.get("/emps/1").willReturn(emps1Response));

        ResponseDefinitionBuilder emps2Response = new ResponseDefinitionBuilder();
        emps2Response.withStatus(200);
        emps2Response.withStatusMessage("I Am Employee 2");
        emps2Response.withHeader("Content-Type", "text/json");
        emps2Response.withHeader("token", "22222");
        emps2Response.withHeader("Set-Cookie", "session-id=222222222");
        emps2Response.withHeader("Set-Cookie", "split_test_group=A");
        emps2Response.withBody("Text of the Body");

        WireMock.givenThat(WireMock.get("/emps/2").willReturn(emps2Response));
    }


    @Test
    void testOneUsingRestAssured() {

        RestAssured.given().
                get("http://" + HOST + ":" + PORT + "/emps/1").
                then().
                assertThat().
                statusCode(201);

    }

    @Test
    void testTwoUsingRestAssured() {

        RestAssured.given().
                get("http://" + HOST + ":" + PORT + "/emps/2").
                then().
                assertThat().
                statusCode(200);

    }

    @Test
    void testThreeUsingRestTemplateAndAssertJ() {

        ResponseEntity<String> response = restTemplate.getForEntity("http://" + HOST + ":" + PORT + "/emps/2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }




}
