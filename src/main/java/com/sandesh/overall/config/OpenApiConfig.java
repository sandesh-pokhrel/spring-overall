package com.sandesh.overall.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        contact = @Contact(
                name = "Sandesh Pokhrel",
                email = "sandesh.pokhrel56@gmail.com",
                url = "https://sandesh.com"
        ),
        title = "Spring Overall Features",
        description = "Contains overall spring features information",
        license = @License(
                name = "Sandesh Licensed",
                url = "https://license.com"
        )
))
public class OpenApiConfig {
}
