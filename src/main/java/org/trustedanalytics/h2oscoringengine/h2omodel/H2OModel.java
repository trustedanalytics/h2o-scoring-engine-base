/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.h2oscoringengine.h2omodel;

import hex.genmodel.GenModel;

public class H2OModel {
	GenModel model;

	public H2OModel(GenModel model) {
		super();
		this.model = model;
	}

	public double[] score(double[] data) throws InvalidDataSizeException {

		if (data.length != model.nfeatures()) {
			throw new InvalidDataSizeException(
					"Required input data size: " + model.nfeatures() + ", given: " + data.length);
		}

		double[] resultArray = new double[model.nclasses() + 1];
		return model.score0(data, resultArray);
	}

}
