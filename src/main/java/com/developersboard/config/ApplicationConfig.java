package com.developersboard.config;

import com.developersboard.manager.file.TagManager;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ApplicationConfig {

    @Bean
    PromptProvider promptProvider() {
        return () -> new AttributedString("joe shell:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

    /**
     * Default path to get started. Not a valid path.
     *
     * @return path
     */
    @Bean
    Path defaultPath() {
        return Paths.get("files");
    }

    /**
     * Bean for the default tag name "untagged".
     *
     * @return the default tag name
     */
    @Bean
    String name() {
        return TagManager.DEFAULT_TAG_NAME;
    }
}
