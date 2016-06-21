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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trustedanalytics.h2oscoringengine.h2omodel.H2oModel;

@RestController
public class H2oModelNamesController {

    public static final String GET_H2O_MODEL_URL = "/names";

    private final H2oModel model;


    @Autowired
    public H2oModelNamesController(H2oModel model) {
        this.model = model;
    }


    /**
     * Using GET, since there are no inputs to this request.
     */
    @RequestMapping(value = GET_H2O_MODEL_URL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String[] getNames() {
        return model.getNames();
    }

}
