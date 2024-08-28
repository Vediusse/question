package com.viancis.question.swagger;

import config.BaseSwaggerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BaseSwaggerConfig.class)
public class SwaggerConfig {
}
