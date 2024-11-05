package kr.husk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HuskApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuskApplication.class, args);
    }

}
