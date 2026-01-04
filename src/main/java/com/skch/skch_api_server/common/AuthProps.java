package com.skch.skch_api_server.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProps {

    private Server server = new Server();
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;

    @Getter
    @Setter
    public static class Server {
        private String authorizeUrl;
        private String tokenUrl;
    }
}
