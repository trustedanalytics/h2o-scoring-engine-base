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
import java.util.Arrays;

public class H2oModel {

    private final GenModel model;
    private final String[] names;

    public H2oModel(GenModel model) {
        this.model = model;
        this.names = model.getNames();
    }

    public String[] score(HashMap<String, Object> data) {

        RowData row = new RowData();
        for (HashMap.Entry<String, Object> entry : data.entrySet())
        {
              String key = entry.getKey();
              String val = entry.getValue().toString();
              if (val != "" && Arrays.asList(this.names).contains(key)) {
                  row.put(key, val);
              }
        }

        EasyPredictModelWrapper predictor = new EasyPredictModelWrapper(model);

        try {
          BinomialModelPrediction pred = predictor.predictBinomial(row);
          String[] result = {pred.label, String.format("%.5f", pred.classProbabilities[0]), String.format("%.5f", pred.classProbabilities[1])};
          return result;
        }
        catch (Exception e) {
          System.out.println("Error: Prediction Exception");
          String message = e.getClass().toString() + ": " + e.getCause()
            + "; Message: " + e.getMessage();
          System.out.println(message);
          String[] error = {"Exception", message};
          return error;
        }

    }

    public double[] score(double[] data) {
      checkArgument(data.length == model.nfeatures(), "Required input data size: %s, given %s", model.nfeatures(), data.length);
      //H2O GenModel.score0 method requires an array of a size GenModel.classes + 1 as a second argument.
      double[] resultArray = new double[model.nclasses() + 1];
      return model.score0(data, resultArray);
    }

    public String[] getNames() {
      return this.names;
    }
}
