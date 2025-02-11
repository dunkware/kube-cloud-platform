package com.dunkware.cloud.auth.starter.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "dunkware.security")
public class SecurityProperties {
    private List<String> permitAll = List.of("/login", "/validate");

    public List<String> getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(List<String> permitAll) {
        this.permitAll = permitAll;
    }
}