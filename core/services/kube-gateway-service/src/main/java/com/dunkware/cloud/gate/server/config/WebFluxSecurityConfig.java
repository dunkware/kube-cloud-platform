package com.dunkware.cloud.gate.server.config;

import java.util.Arrays;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableDiscoveryClient
public class WebFluxSecurityConfig {

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("dunkware")
                .password("{noop}dunkStreet@2022")
                .roles("ADMIN")
            
                .build();
        return new MapReactiveUserDetailsService(user);
    }
  //  antMatchers(HttpMethod.OPTIONS, "/your-url").permitAll()
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf().disable();
        http.cors().disable();

        http.authorizeExchange()
                // Allow all OPTIONS requests (preflight)
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Permit all other requests
                .anyExchange().permitAll();

        return http.build();
    }
    
  
 
    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
       configuration.applyPermitDefaultValues();
       
        configuration.setExposedHeaders(Arrays.asList("*"));
       configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
       configuration.setMaxAge((long)30);
       configuration.setAllowCredentials(true);
        //the below three lines will add the relevant CORS response headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;    	
    	
    	
        
        
        
        
    }
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        return new CorsWebFilter(corsConfiguration());
    }
    
    
}