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

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import com.github.hanusto.service.user.provider.UserDataProvider;
import com.github.hanusto.service.user.provider.model.User;

/**
 * Router to provider user data.
 *
 * @author Tomas Hanus
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RouterConfig {

    private final UserDataProvider userDataProvider;

    /**
     * Exposes route for {@code /profiles/{id}}.
     *
     * @return route function
     */
    @Bean
    public RouterFunction<ServerResponse> userProfileRoute() {
        return RouterFunctions
                .route(RequestPredicates.GET("/profiles/{id}")
                                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                        this::profile);
    }

    private Mono<ServerResponse> profile(ServerRequest request) {
        final String id = request.pathVariable("id");

        Assert.hasText(id, "ID path variable must be present");

        long userId = Long.valueOf(id);

        LOG.trace("Getting data for user: {}", userId);

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userDataProvider.getById(userId), User.class)
                .switchIfEmpty(notFound().build());
    }
}
