package net.mikelythgoe.wiremock;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@WireMockTest
public class WireMockStandaloneTests {


    @Test
    void testOne() {

        RestAssured.given().
                get("http://localhost:8080/users/1").
                then().
                assertThat().
                statusCode(200);

    }

    @Test
    void testTwo() {

        RestAssured.given().
                get("http://localhost:8080/users/1").
                then().
                assertThat().
                statusCode(200);

    }

    @Test
    void testNine() {

        String contentType = RestAssured.given().
                get("http://localhost:8080/users/9").
                then().
                assertThat().
                statusCode(404).
        extract().header("Content-Type");

        assertEquals("text/plain", contentType);

    }



}
