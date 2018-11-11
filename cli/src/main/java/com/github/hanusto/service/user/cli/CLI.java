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

package com.github.hanusto.service.user.cli;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.DefaultApplicationArguments;

import com.github.hanusto.service.user.provider.JSONPlaceholderUserDataProvider;
import com.github.hanusto.service.user.provider.UserDataProvider;

/**
 * CLI as interface of {@link UserDataProvider}.
 *
 * @author Tomas Hanus
 */
@Slf4j
public class CLI {

    private static final UserDataProvider DATA_PROVIDER = new JSONPlaceholderUserDataProvider();
    private static final String USER_ID_OPT = "userId";

    /**
     * Start the CLI.
     *
     * @param args of CLI.
     */
    public static void main(String[] args) {
        final DefaultApplicationArguments arguments = new DefaultApplicationArguments(args);

        if (arguments.containsOption(USER_ID_OPT)) {
            final List<String> optionValues = arguments.getOptionValues(USER_ID_OPT);

            if (optionValues.isEmpty()) {
                System.err.println("Empty argument: userId");
            } else {
                final String userId = optionValues.get(0);
                LOG.debug("Resolved user ID: {}", userId);

                System.out.println(DATA_PROVIDER.getById(Long.valueOf(userId)).block());
            }
        } else {
            System.err.println("Missing required argument: userId (number)");
        }
    }
}
