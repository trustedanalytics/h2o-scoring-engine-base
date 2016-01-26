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
package org.trustedanalytics.h2oscoringengine.rest;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.trustedanalytics.h2oscoringengine.h2omodel.H2oModel;

@RestController
public class H2oArrayScoringEngineController {

    public static final String POST_H2O_MODEL_URL = "/score";
    private static final Logger LOGGER = LoggerFactory.getLogger(H2oArrayScoringEngineController.class);

    private final H2oModel model;


    @Autowired
    public H2oArrayScoringEngineController(H2oModel model) {
        this.model = model;
    }


    /**
     * Using POST method due to potential large input data size.
     */
    @RequestMapping(method = RequestMethod.POST, value = POST_H2O_MODEL_URL,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
    public double[] score(@RequestBody(required = true) double[] data) {
        return model.score(data);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response)
            throws IOException {
        LOGGER.error("Invalid input data size:", e);
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}
