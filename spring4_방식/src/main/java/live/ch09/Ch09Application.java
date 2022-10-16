package live.ch09;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Ch09Application {

  /**
   * netty: system의 processor count * 2
   */
  @RestController
  public static class MyController {
    @GetMapping("/rest")
    public String rest() {
      return "rest";
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(Ch09Application.class, args);
  }
}
