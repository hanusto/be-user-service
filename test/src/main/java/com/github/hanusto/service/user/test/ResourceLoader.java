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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Load resources as String from classpath resources.
 * <p>
 * Specify default base path in each test class with constructor.
 *
 * @author Tomáš Hanus
 */
class ResourceLoader {

    private static final char RESOURCE_PATH_SEPARATOR = '/';

    private String resourcesBase;

    public ResourceLoader() {
        this("/");
    }

    /**
     * New instance.
     *
     * @param resourcesBase base path to directory with resources
     */
    ResourceLoader(String resourcesBase) {
        this.resourcesBase = resourcesBase;
    }

    /**
     * Load a classpath resource into a String. Assume UTF-8 encoding.
     *
     * @param resource Path relative to `resourcesBase`
     * @return Resource contents as a String
     */
    public String resource(String resource) {
        try {
            return resource(resource, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load a classpath resource into a String.
     *
     * @param resource Path relative to `resourcesBase`
     * @param charset  Resource charset
     * @return Resource contents as a String
     * @throws IOException of resource does not exist
     */
    public String resource(String resource, Charset charset) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        InputStream inputStream = resourceStream(resource);
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] byteArray = buffer.toByteArray();
        buffer.close();
        return new String(byteArray, charset);
    }

    /**
     * Load a classpath resource as an {@link InputStream}.
     *
     * @param resource Path relative to `resourcesBase`
     * @return Resource stream
     * @throws IOException
     */
    private InputStream resourceStream(String resource) throws IOException {
        String resourcePath = getResourcesBaseInner() + resource;
        InputStream stream = getClass().getResourceAsStream(resourcePath);

        if (stream == null) {
            throw new IOException("resource `" + resourcePath + "` not found");
        }

        return stream;
    }

    private String getResourcesBaseInner() {
        String resourcesBase = this.resourcesBase;

        if (resourcesBase.length() == 0
                || resourcesBase.charAt(0) != RESOURCE_PATH_SEPARATOR)
            resourcesBase = RESOURCE_PATH_SEPARATOR + resourcesBase;

        if (resourcesBase.charAt(resourcesBase.length() - 1) != RESOURCE_PATH_SEPARATOR)
            resourcesBase += RESOURCE_PATH_SEPARATOR;

        return resourcesBase;
    }
}
