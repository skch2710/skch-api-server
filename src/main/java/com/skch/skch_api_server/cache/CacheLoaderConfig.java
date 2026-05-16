package com.skch.skch_api_server.cache;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CacheLoaderConfig {

    private final DataCacheService dataCacheService;

    @Bean
    ApplicationRunner loadCacheOnStartup() {
        return args -> {
            log.info(">>> Preloading roles cache...");
            dataCacheService.getAllRoles();
        };
    }
}
