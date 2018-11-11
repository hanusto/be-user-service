/*
 * Copyright 2004-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hanusto.service.user.test;

import java.io.File;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Abstract test with resource loader to simplify loading of JSON resources in tests.
 *
 * @author Tomáš Hanus
 */
public abstract class AbstractTest {

    protected final ResourceLoader resourceLoader;

    public AbstractTest() {
        resourceLoader = new ResourceLoader(getResourcesBase());
    }

    /**
     * Gets the path to directory with resources for this test.
     *
     * @return default resource base
     */
    protected String getResourcesBase() {
        return getClass().getSimpleName() + File.separator;
    }

    /**
     * Asserts that the {@code response} provided matches the expected payload from the path. It uses {@link JSONCompareMode#LENIENT}.
     *
     * @param expectedPayloadFilePath as Expected JSON string in path
     * @param response                as String type to compare
     * @see #assertEqualsPayload(String, String, JSONCompareMode)
     */
    protected void assertEqualsPayload(String expectedPayloadFilePath, String response) {
        assertEqualsPayload(expectedPayloadFilePath, response, JSONCompareMode.LENIENT);
    }

    /**
     * Asserts that the {@code response} provided matches the expected payload from the path.
     *
     * @param expectedPayloadFilePath as Expected JSON string in path
     * @param response                as String type to compare
     * @param compareMode             used to comparison
     * @see #assertEqualsPayload(String, String)
     */
    protected void assertEqualsPayload(
            final String expectedPayloadFilePath,
            final String response,
            final JSONCompareMode compareMode) {
        try {
            JSONAssert.assertEquals(
                    fileAsString(expectedPayloadFilePath),
                    response,
                    compareMode);
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Load file as string from resources.
     *
     * @param fileName the file, should start with slash.
     * @return string with file content.
     */
    protected String fileAsString(String fileName) {
        return resourceLoader.resource(fileName);
    }
}
