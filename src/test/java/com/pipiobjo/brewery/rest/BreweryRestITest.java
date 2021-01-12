package com.pipiobjo.brewery.rest;

import com.pipiobjo.brewery.adapters.spiextensionboard.SPIExtensionBoardAdapter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Tag("integration")
@QuarkusTest
@TestProfile(BreweryBasicITestProfile.class)
public class BreweryRestITest {

//    @BeforeEach
//    public void setup() {
//        Mockito.when(spiExtensionBoard.spi()).thenReturn();
//    }

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/swagger-ui")
                .then()
                .statusCode(200);
    }

}