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


import static com.google.common.base.Preconditions.checkArgument;

import hex.genmodel.GenModel;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.prediction.*;

import java.util.HashMap;

public class H2oModel {

    private final GenModel model;

    public H2oModel(GenModel model) {
        this.model = model;
    }

    public double[] score(HashMap<String, Object> data) {

//checkArgument(data.length == model.nfeatures(), "Required input data size: %s, given %s", model.nfeatures(), data.length);

        RowData row = new RowData();
        for (HashMap.Entry<String, Object> entry : data.entrySet())
        {
              row.put(entry.getKey(), entry.getValue());
        }

        EasyPredictModelWrapper predictor = new EasyPredictModelWrapper(model);

        try {
          BinomialModelPrediction pred = predictor.predictBinomial(row);
          return pred.classProbabilities;
        }
        catch (Exception e) {
          System.out.println("Error: Prediction Exception");
          double[] nothing = {};
          return nothing;
        }

    }

    public double[] score(double[] data) {
      checkArgument(data.length == model.nfeatures(), "Required input data size: %s, given %s", model.nfeatures(), data.length);
      //H2O GenModel.score0 method requires an array of a size GenModel.classes + 1 as a second argument.
      double[] resultArray = new double[model.nclasses() + 1];
      return model.score0(data, resultArray);
    }
}
