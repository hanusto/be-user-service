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

import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import com.github.hanusto.service.user.provider.model.Post;
import com.github.hanusto.service.user.provider.model.User;
import com.github.hanusto.service.user.provider.model.exception.NotFoundException;

/**
 * JSONPlaceholder implementation of {@link UserDataProvider} that use <a href="https://jsonplaceholder.typicode.com/">public API</a>.
 *
 * @author Tomas Hanus
 */
@Slf4j
public class JSONPlaceholderUserDataProvider implements UserDataProvider {

    private final static String QUERY_PARAM_USER_ID = "userId";

    private final Config config;
    private final WebClient webClient;

    /**
     * Default initialization of provider.
     */
    public JSONPlaceholderUserDataProvider() {
        this(new Config());
    }

    /**
     * Initialization with custom configuration.
     *
     * @param config of provider
     */
    public JSONPlaceholderUserDataProvider(final Config config) {
        this.config = config;

        LOG.debug("Configuration of provider: {}", config);

        this.webClient = WebClient
                .create(config.getBaseUrl());
    }

    @Override
    public Mono<User> getById(final Long id) {
        Assert.notNull(id, "ID must not be null");

        final Mono<List<Post>> posts =
                execute(UriComponentsBuilder.fromUriString(config.getPostsRelativePath()).queryParam(QUERY_PARAM_USER_ID, id))
                        .flatMap(res -> res.bodyToMono(new ParameterizedTypeReference<List<Post>>() {
                        }));

        final Mono<User> user =
                execute(UriComponentsBuilder.fromUriString(config.getUsersRelativePath()).pathSegment("" + id))
                        .flatMap(res -> res.bodyToMono(User.class));

        return Mono
                .zip(user, posts, (user1, posts1) -> {
                    user1.setPosts(posts1);
                    return user1;
                })
                .subscribeOn(Schedulers.elastic())
                // cache predicate of mono block
                .cache();
    }

    /**
     * Executes call to URI.
     *
     * @param uri as builder of resource
     * @return response
     */
    private Mono<ClientResponse> execute(UriComponentsBuilder uri) {
        return webClient.get()
                        .uri(uri.toUriString())
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .exchange()
                        .doOnNext(response -> {
                            HttpStatus httpStatus = response.statusCode();
                            if (httpStatus == HttpStatus.NOT_FOUND) {
                                throw new NotFoundException("User not found");
                            }
                        });
    }

    /**
     * Configuration holder.
     */
    @Data
    public static class Config {
        @NonNull
        private String baseUrl = "https://jsonplaceholder.typicode.com";
        @NonNull
        private String usersRelativePath = "users";
        @NonNull
        private String postsRelativePath = "posts";
    }
}
