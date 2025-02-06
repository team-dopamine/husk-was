package kr.husk.application.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleConfig {
    private String clientId;
    private String clientSecret;
    private String scope;
    private String redirectUri;
    private String tokenUri;
    private String userInfoUri;
    private String revokeUrl;
}