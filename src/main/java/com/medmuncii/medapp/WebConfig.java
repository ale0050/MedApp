package com.medmuncii.medapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/companies.html").setViewName("companies");
        registry.addViewController("/employees.html").setViewName("employees");
        registry.addViewController("/aptitude.html").setViewName("aptitude");
        registry.addViewController("/login.html").setViewName("login"); // Explicitly map login.html
    }
}
