package io.wannabit.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "io.wannabit")
public class AppConfig extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(AppConfig.class, args);
  }
}
