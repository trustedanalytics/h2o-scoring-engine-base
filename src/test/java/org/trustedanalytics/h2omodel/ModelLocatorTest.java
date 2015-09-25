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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.trustedanalytics.h2oscoringengine.h2omodel.H2oModel;
import org.trustedanalytics.h2oscoringengine.h2omodel.ModelLocator;
import org.trustedanalytics.h2oscoringengine.h2omodel.ModelNotFoundException;

public class ModelLocatorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void getModel_ValidClasspathPrepared_ModelInstanceReturned()
            throws ModelNotFoundException, ClassNotFoundException, IOException {
        // given
        ModelLocator finder = new ModelLocator();

        // when
        TestClassPathBuilder.INSTANCE.prepareClasspathWithOneModel();
        H2oModel model = finder.getModel();

        // then
        assertThat(model, instanceOf(H2oModel.class));
    }

    @Test
    public void getModel_ClasspathWithoutModel_ExceptionThrown() throws ModelNotFoundException {
        // given
        ModelLocator finder = new ModelLocator();
        
        //when
        TestClassPathBuilder.INSTANCE.prepareClassPathWithoutModel();

        // then
        thrown.expect(ModelNotFoundException.class);
        finder.getModel();
    }

    @Test
    public void getModel_ClasspathWithMoreThanOneModel_ExceptionThrown()
            throws ClassNotFoundException, IOException, ModelNotFoundException {
        // given
        ModelLocator finder = new ModelLocator();

        // when
        TestClassPathBuilder.INSTANCE.prepareClasspathWithMoreThanOneModel();

        // then
        thrown.expect(ModelNotFoundException.class);
        finder.getModel();
    }
    
    @After
    public void restoreClassPath() throws IOException{
        TestClassPathBuilder.INSTANCE.restoreInitialClasspath();
    }
    
}
