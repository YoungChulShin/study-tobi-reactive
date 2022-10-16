package study.tobi.reactive.tomcat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TomcatWebApplication {

  public static void main(String[] args) {
    System.setProperty("server.port", "8081");
    SpringApplication.run(TomcatWebApplication.class, args);
  }

}
