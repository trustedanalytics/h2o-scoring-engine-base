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
package org.trustedanalytics.h2oscoringengine.h2omodel;

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import hex.genmodel.GenModel;

public class ModelLocator {

    private GenModel model;

    public H2OModel getModel() throws ModelNotFoundException {
        if (this.model == null) {
            this.model = findModel();
        }
        return new H2OModel(this.model);
    }

    private GenModel findModel() throws ModelNotFoundException {

        Reflections reflections =
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forManifest()));

        Set<Class<? extends GenModel>> models = reflections.getSubTypesOf(GenModel.class);

        if (models.size() != 1) {
            throw new ModelNotFoundException("Invalid number of models: " + models.size());
        }

        try {
            return models.iterator().next().newInstance();
        } catch (Throwable e) {
            throw new ModelNotFoundException(e);
        }
    }

}
