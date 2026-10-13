package edu.rit.swen262;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
@SpringBootApplication(scanBasePackages = {"edu.rit.swen262", "comix"})
public class MyApplication {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context =
                     SpringApplication.run(MyApplication.class, args)) {
            // The menu runner finishes before the application context is closed.
        }
    }
}
