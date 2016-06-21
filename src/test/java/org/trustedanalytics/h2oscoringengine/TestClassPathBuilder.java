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
package org.trustedanalytics.h2oscoringengine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public enum TestClassPathBuilder {

    INSTANCE;

    private ClassLoader initialClassLoader = Thread.currentThread().getContextClassLoader();

    private static final String H2O_ONE_MODEL_TEST_LIB = "/h2o-one-model-test-lib.jar";

    private static final String H2O_TWO_MODELS_TEST_LIB = "/h2o-two-models-test-lib.jar";
    
    private static final String H2O_MODEL_3_1_AND_HIGHER_COMPATIBLE = "/h2o-model-3.6.0.jar";

    public void prepareClasspathWith3_1CompatibleModel() throws ClassNotFoundException, IOException{
      this.prepareClassPath(H2O_MODEL_3_1_AND_HIGHER_COMPATIBLE);
    }
    
    public void prepareClasspathWithOneModel() throws ClassNotFoundException, IOException {
        this.prepareClassPath(H2O_ONE_MODEL_TEST_LIB);
    }

    public void prepareClasspathWithMoreThanOneModel() throws ClassNotFoundException, IOException {
        this.prepareClassPath(H2O_TWO_MODELS_TEST_LIB);
    }

    public void prepareClassPathWithoutModel() {
        // default classpath contains no model
    }

    private void prepareClassPath(String jarResourcePath)
            throws ClassNotFoundException, IOException {
        URL[] jarUrl = {this.getClass().getResource(jarResourcePath)};
        URLClassLoader urlClassLoader =
                new URLClassLoader(jarUrl, Thread.currentThread().getContextClassLoader());

        Iterable<String> classNames = extractClassNamesFromJar(jarResourcePath);
        for (String className : classNames) {
            urlClassLoader.loadClass(className);
        }

        Thread.currentThread().setContextClassLoader(urlClassLoader);

        urlClassLoader.close();
    }

    private Iterable<String> extractClassNamesFromJar(String jarPath) throws IOException {

        List<String> classNames = new LinkedList<String>();

        JarInputStream jarInputStream = new JarInputStream(getClass().getResourceAsStream(jarPath));
        JarEntry jarEntry = jarInputStream.getNextJarEntry();

        while (jarEntry != null) {
            String jarEntryName = jarEntry.getName();
            if (!jarEntry.isDirectory() && jarEntryName.endsWith(".class")) {
                classNames.add(extractClassNameFromFilePath(jarEntryName));
            }

            jarEntry = jarInputStream.getNextJarEntry();
        }

        jarInputStream.close();
        return classNames;
    }

    private String extractClassNameFromFilePath(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf(".")).replaceAll(File.separator, ".");
    }

    public void restoreInitialClasspath() throws IOException {
        // restoring initial classPath
        Thread.currentThread().setContextClassLoader(initialClassLoader);
    }

}
