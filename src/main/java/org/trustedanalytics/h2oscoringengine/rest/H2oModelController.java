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

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.trustedanalytics.h2oscoringengine.h2omodel.H2OModel;
import org.trustedanalytics.h2oscoringengine.h2omodel.InvalidDataSizeException;
import org.trustedanalytics.h2oscoringengine.h2omodel.ModelLocator;
import org.trustedanalytics.h2oscoringengine.h2omodel.ModelNotFoundException;

@RestController
public class H2oModelController {
    public static final String POST_H2O_MODEL_URL = "/rest/h2o/score";

    /**
     * Using POST method due to potential large input data size.
     * 
     * @throws InvalidDataSizeException
     * @throws ModelNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST, value = POST_H2O_MODEL_URL,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
    public double[] score(@RequestBody(required = true) double[] data)
            throws InvalidDataSizeException, ModelNotFoundException {

        ModelLocator modelLocator = new ModelLocator();
        H2OModel model = modelLocator.getModel();

        return model.score(data);
    }

    @ExceptionHandler(InvalidDataSizeException.class)
    void handleInvalidInputDataSizeException(InvalidDataSizeException e,
            HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}
