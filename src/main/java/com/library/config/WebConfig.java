package com.library.config;

import java.nio.file.Path;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String allowedOriginsValue;
    private final boolean allowCredentials;
    private final String bookCoversPath;

    public WebConfig(
            @Value("${library.cors.allowed-origins:*}") String allowedOriginsValue,
            @Value("${library.cors.allow-credentials:false}") boolean allowCredentials,
            @Value("${library.book-covers.path:booksimages}") String bookCoversPath
    ) {
        this.allowedOriginsValue = allowedOriginsValue;
        this.allowCredentials = allowCredentials;
        this.bookCoversPath = bookCoversPath;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = parseAllowedOrigins();
        CorsRegistration registration = registry.addMapping("/api/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);

        if (hasOriginPattern(allowedOrigins)) {
            registration.allowedOriginPatterns(allowedOrigins);
        } else {
            registration.allowedOrigins(allowedOrigins);
        }

        registration.allowCredentials(allowCredentials && !isWildcard(allowedOrigins));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = Path.of(bookCoversPath)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry.addResourceHandler("/api/book-covers/**")
                .addResourceLocations(resourceLocation);
    }

    private String[] parseAllowedOrigins() {
        return Arrays.stream(allowedOriginsValue.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
    }

    private boolean isWildcard(String[] allowedOrigins) {
        return allowedOrigins.length == 0
                || Arrays.stream(allowedOrigins).anyMatch("*"::equals);
    }

    private boolean hasOriginPattern(String[] allowedOrigins) {
        return allowedOrigins.length == 0
                || Arrays.stream(allowedOrigins).anyMatch(origin -> origin.contains("*"));
    }
}
