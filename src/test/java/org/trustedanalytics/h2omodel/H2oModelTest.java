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
package org.trustedanalytics.h2omodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.trustedanalytics.h2oscoringengine.h2omodel.H2oModel;

import hex.genmodel.GenModel;

public class H2oModelTest {

    private static int MODEL_FEATURES_NUMBER = 10;
    private static int MODEL_CLASSES_NUMBER = 45;

    private static int MODEL_VALID_INPUT_DATA_SIZE = MODEL_FEATURES_NUMBER;
    private static int MODEL_VALID_OUTPUT_DATA_SIZE = MODEL_CLASSES_NUMBER + 1;

    private static int MODEL_INVALID_INPUT_DATA_SIZE = MODEL_FEATURES_NUMBER + 42;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void score_ValidInpuDataSize_ValidOutputDataSizeReturned() {
        // given
        GenModel h2oGenModelMock = constructGenModelMock();

        // when
        H2oModel h2oModel = new H2oModel(h2oGenModelMock);
        double[] result = h2oModel.score(new double[MODEL_VALID_INPUT_DATA_SIZE]);

        // then
        assertThat("Test of result size: ", result.length, equalTo(MODEL_VALID_OUTPUT_DATA_SIZE));

        ArgumentCaptor<double[]> captorFirstArg = ArgumentCaptor.forClass(double[].class);
        ArgumentCaptor<double[]> captorSecongArg = ArgumentCaptor.forClass(double[].class);
        verify(h2oGenModelMock).score0(captorFirstArg.capture(), captorSecongArg.capture());

        assertThat("Test of first argument size: ", captorFirstArg.getValue().length,
                equalTo(MODEL_FEATURES_NUMBER));
        assertThat("Test of second argument size:", captorSecongArg.getValue().length,
                equalTo(MODEL_VALID_OUTPUT_DATA_SIZE));

    }

    @Test
    public void score_InvalidInputDataSize_ExceptionThrown() {
        // given
        GenModel h2oGenModelMock = constructGenModelMock();

        // when
        H2oModel h2oModel = new H2oModel(h2oGenModelMock);

        // then
        thrown.expect(IllegalArgumentException.class);
        h2oModel.score(new double[MODEL_INVALID_INPUT_DATA_SIZE]);

    }

    private GenModel constructGenModelMock() {
        GenModel mockedGenModel = mock(GenModel.class);
        when(mockedGenModel.nfeatures()).thenReturn(MODEL_FEATURES_NUMBER);
        when(mockedGenModel.nclasses()).thenReturn(MODEL_CLASSES_NUMBER);
        when(mockedGenModel.score0(any(double[].class), any(double[].class)))
                .thenReturn(new double[MODEL_VALID_OUTPUT_DATA_SIZE]);

        return mockedGenModel;
    }
}
