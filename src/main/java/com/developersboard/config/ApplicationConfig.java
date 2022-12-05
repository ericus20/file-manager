package com.developersboard.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Configuration
public class ApplicationConfig {

    @Bean
    PromptProvider promptProvider() {
        return () -> new AttributedString("joe shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}
