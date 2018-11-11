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

package com.github.hanusto.service.user.route;

import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import com.github.hanusto.service.user.provider.UserDataProvider;
import com.github.hanusto.service.user.provider.model.User;
import com.github.hanusto.service.user.provider.model.exception.NotFoundException;
import com.github.hanusto.service.user.test.AbstractTest;

/**
 * Test suite for {@link RouterConfig#profile(ServerRequest)}.
 *
 * @author Tomas Hanus
 */
@ExtendWith({
        SpringExtension.class
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouterConfigTest extends AbstractTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserDataProvider mockDataProvider;

    @Test
    void userProfileRoute() {
        final User data = new User("Tomas", "Hanus", "hanusto@gmail.com", Collections.emptyList());
        when(mockDataProvider.getById(1L))
                .thenReturn(Mono.just(data));

        final String testedResponse = webTestClient
                .get().uri("/profiles/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody().blockFirst();

        // verify
        assertEqualsPayload("user-profile-1_response.json", testedResponse);
    }

    @Test
    void userProfileRoute_notFound() {
        when(mockDataProvider.getById(1L))
                .thenThrow(new NotFoundException("User not found"));

        final String testedResponse = webTestClient
                .get().uri("/profiles/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isBadRequest()
                .returnResult(String.class)
                .getResponseBody().blockFirst();

        // verify
        assertEqualsPayload("user-profile-1_response-error.json", testedResponse);
    }
}