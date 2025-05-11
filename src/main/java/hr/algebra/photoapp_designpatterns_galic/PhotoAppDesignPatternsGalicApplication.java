package hr.algebra.photoapp_designpatterns_galic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PhotoAppDesignPatternsGalicApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoAppDesignPatternsGalicApplication.class, args);
    }

}
