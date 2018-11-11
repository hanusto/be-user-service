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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.BeforeAll;

/**
 * Parent for functional tests.
 * <p>
 * It is intended to be used for microservice "boundary" testing.
 * </p>
 *
 * @author Tomas Hanus
 */
public class AbstractFunctionalTest extends AbstractTest {

    /**
     * WireMock stub server instance, see https://github.com/tomakehurst/wiremock
     */
    protected static WireMockServer WIREMOCK;

    /**
     * Setup & start wiremock server before any test.
     */
    @BeforeAll
    public static void wireMockSetup() {
        WIREMOCK = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        WIREMOCK.start();
        WireMock.configureFor(WIREMOCK.port());
    }

    /**
     * Get URL for {@link WireMockServer} stub server.
     *
     * @return url.
     */
    protected static String getWireMockUrl() {
        return "http://localhost:" + WIREMOCK.port();
    }
}
