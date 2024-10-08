package kr.husk.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "HUSK WAS", description = "HUSK API 명세입니다.", version = "v1")
)
@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi openApi() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("HUSK API v1")
                .pathsToMatch(paths)
                .build();
    }
}
