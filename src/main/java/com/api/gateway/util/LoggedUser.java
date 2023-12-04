package com.api.gateway.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoggedUser {
    private static final Logger logger = LoggerFactory.getLogger(LoggedUser.class);

    private static Map<String, String> loggedUsers = new HashMap<>();

    private LoggedUser() {
    }

    public static void logUser(String username, String token) {
        loggedUsers.put(username, token);
        logger.info("user {} logged", username);
    }

    public static boolean isUserLogged(String searchUser) {
        logger.info("logged users list- {}", loggedUsers.toString());
//        todo: what if username is of more length than 10??
        if (searchUser.length() < 10)
            return loggedUsers.containsKey(searchUser);
        else return loggedUsers.containsValue(searchUser);

    }

}
