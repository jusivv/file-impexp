package org.coodex.file.impexp.boot;

import org.coodex.file.impexp.config.FileImpExpJerseyConfig;
import org.coodex.file.impexp.helper.SpringBeanTool;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
        "org.coodex.file.impexp.sample"
})
public class RESTServiceApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(RESTServiceApp.class, args);

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RESTServiceApp.class);
    }

    @Bean
    ResourceConfig resourceConfig() {
        return new FileImpExpJerseyConfig();
    }

    @Bean
    SpringBeanTool springBeanTool() {
        return new SpringBeanTool();
    }
}
