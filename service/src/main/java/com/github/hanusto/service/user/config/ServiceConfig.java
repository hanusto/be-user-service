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

package com.github.hanusto.service.user.config;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import com.github.hanusto.service.user.provider.JSONPlaceholderUserDataProvider;
import com.github.hanusto.service.user.provider.UserDataProvider;
import com.github.hanusto.service.user.provider.model.User;

/**
 * Main configuration of service.
 *
 * @author Tomas Hanus
 */
@EnableCaching
@Configuration
public class ServiceConfig {

    /**
     * Exposes custom error handler.
     *
     * @return custom exception handler.
     */
    @Bean
    @Order(-2)
    public WebExceptionHandler customErrorHandler(final ErrorAttributes errorAttributes,
                                                  final ResourceProperties resourceProperties,
                                                  final ApplicationContext applicationContext) {

        GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(
                errorAttributes, resourceProperties, applicationContext);

        exceptionHandler.setMessageWriters(Collections.singletonList(new EncoderHttpMessageWriter<>(new Jackson2JsonEncoder())));

        return exceptionHandler;
    }

    /**
     * Exposes default implementation of {@link UserDataProvider}.
     *
     * @param config of
     * @return {@link JSONPlaceholderUserDataProvider} implementation
     */
    @Bean
    @ConditionalOnMissingBean
    public UserDataProvider userDataProvider(final UserDataProviderConfig config) {
        return new CacheableJSONPlaceholderUserDataProvider(config);
    }

    /**
     * Exposes configuration of {@link JSONPlaceholderUserDataProvider}.
     */
    @Configuration
    @ConfigurationProperties(prefix = "hanusto.user-service.data-provider")
    public class UserDataProviderConfig extends JSONPlaceholderUserDataProvider.Config {
    }

    /**
     * Expose cache manager.
     *
     * @return {@link SimpleCacheManager}
     */
    @ConditionalOnProperty(value = "hanusto.user-service.cache.enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Collections.singletonList(
                new ConcurrentMapCache(CacheableJSONPlaceholderUserDataProvider.CACHE_NAME,
                        CacheBuilder.newBuilder()
                                    .expireAfterWrite(15, TimeUnit.SECONDS)
                                    .maximumSize(10)
                                    .build()
                                    .asMap(), false)));
        return cacheManager;
    }

    /**
     * Cachable implementation of {@link JSONPlaceholderUserDataProvider}.
     */
    public class CacheableJSONPlaceholderUserDataProvider extends JSONPlaceholderUserDataProvider {

        static final String CACHE_NAME = "userProfiles";

        /**
         * Default initialization of provider with configuration.
         *
         * @param config of provider
         */
        CacheableJSONPlaceholderUserDataProvider(final Config config) {
            super(config);
        }

        @Override
        @Cacheable(CACHE_NAME)
        public Mono<User> getById(final Long id) {
            return super.getById(id);
        }
    }
}
