/**
 * Copyright (c) 2016 Intel Corporation
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
package org.trustedanalytics.h2oscoringengine.rest;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket orgsApi() {
    return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(buildApiInfo())
            .select()
            .paths(paths())
            .build()
            .useDefaultResponseMessages(false);
  }

  private Predicate<String> paths() {
    return or(regex(H2oScoringEngineController.POST_H2O_MODEL_URL + ".*"));
  }

  private ApiInfo buildApiInfo() {
    return new ApiInfoBuilder()
            .title("H2O Scoring Engine Base")
            .description("Wrapper service for exposing H2O data models as REST services.")
            .version(H2oScoringEngineController.API_VERSION)
            .license("Apache License Version 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .build();
  }

}
