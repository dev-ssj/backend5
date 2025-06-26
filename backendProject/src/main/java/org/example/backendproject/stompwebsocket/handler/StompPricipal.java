package org.example.backendproject.stompwebsocket.handler;

import java.security.Principal;

public class StompPricipal implements Principal {

    private final String name;

    public StompPricipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
