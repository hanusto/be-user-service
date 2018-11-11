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

import javax.validation.constraints.NotNull;

import reactor.core.publisher.Mono;

import com.github.hanusto.service.user.provider.model.User;
import com.github.hanusto.service.user.provider.model.exception.NotFoundException;

/**
 * Contract of {@link User} data provider.
 *
 * @author Tomas Hanus
 */
@FunctionalInterface
public interface UserDataProvider {

    /**
     * Gets {@link User} as Mono by its ID.
     *
     * @param id of user
     * @return publisher of {@link User}
     * @throws NotFoundException if {@code id} is not provided or user not found
     */
    Mono<User> getById(@NotNull Long id) throws NotFoundException;

}
