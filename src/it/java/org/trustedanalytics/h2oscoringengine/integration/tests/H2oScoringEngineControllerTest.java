/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.trustedanalytics.h2oscoringengine.integration.tests;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.trustedanalytics.h2oscoringengine.Application;
import org.trustedanalytics.h2oscoringengine.TestClassPathBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class H2oScoringEngineControllerTest {

    private static Double[] REQUEST = {1.2, 1.3, 1.4, 1.5};
    private static Double[] EXPECTED = {0.0, 0.9569522524478271, 0.0031503686405639146, 0.03989737891160907};

    @Value("${local.server.port}")
    int port;


    @BeforeClass
    public static void prepareClassPathWithTestModel() throws ClassNotFoundException, IOException {
        TestClassPathBuilder.INSTANCE.prepareClasspathWith3_1CompatibleModel();
    }

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void score_shouldReturnScore()
            throws JsonParseException, JsonMappingException, IOException {

        ExtractableResponse<Response> jsonResponse = 
                given().
                    contentType("application/json").
                    request().body(REQUEST).
                when().
                    post("/score").
                then().assertThat().
                    statusCode(HttpStatus.OK.value()).
                    contentType(ContentType.JSON).
                extract();

        Double[] response = convertJsonToDoubleArray(jsonResponse.asString());
        
        for (int i = 0; i<response.length; i++) {
            assertThat(response[i], closeTo(EXPECTED[i], 0.1));
        }
    }

    private Double[] convertJsonToDoubleArray(String json)
            throws JsonParseException, JsonMappingException, IOException {
    
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.readValue(json, Double[].class);
    }
  
}
