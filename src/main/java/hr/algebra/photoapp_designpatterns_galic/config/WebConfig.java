package hr.algebra.photoapp_designpatterns_galic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/thumbnails/**", "/uploads/**")
                .addResourceLocations("file:uploads/thumbnails/", "file:uploads/");
    }
}