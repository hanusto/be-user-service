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

package com.github.hanusto.service.user.provider;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import com.github.hanusto.service.user.provider.model.Post;
import com.github.hanusto.service.user.provider.model.User;
import com.github.hanusto.service.user.test.AbstractFunctionalTest;

/**
 * Test suite for {@link JSONPlaceholderUserDataProvider}.
 *
 * @author Tomas Hanus
 */
class JSONPlaceholderUserDataProviderTest extends AbstractFunctionalTest {

    private JSONPlaceholderUserDataProvider TESTED;

    @BeforeEach
    void testedSetup() {
        TESTED = new JSONPlaceholderUserDataProvider(createConfig());
    }

    @Test
    void getById() {
        // prepare mock
        WireMock.stubFor(get(urlEqualTo(getPath() + "users/1"))
                .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(fileAsString("/user-1_response.json"))));
        WireMock.stubFor(get(urlEqualTo(getPath() + "posts?userId=1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(fileAsString("/posts-user-1_response.json"))));

        // execute
        final Mono<User> testedResult = TESTED.getById(1L);

        // verification
        assertEquals(testedResult.block(), new User(
                "Leanne Graham", "Bret", "Sincere@april.biz", Arrays.asList(
                        new Post(1L, "sunt aut facere repellat provident occaecati excepturi optio reprehenderit"),
                        new Post(2L, "qui est esse"))
        ));
    }

    private static String getPath() {
        return "/" + JSONPlaceholderUserDataProviderTest.class.getSimpleName() + "/";
    }

    private JSONPlaceholderUserDataProvider.Config createConfig() {
        final JSONPlaceholderUserDataProvider.Config config = new JSONPlaceholderUserDataProvider.Config();
        config.setBaseUrl(getWireMockUrl() + getPath());

        return config;
    }
}