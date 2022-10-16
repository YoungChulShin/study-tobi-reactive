package study.tobi.reactive.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NettyWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(NettyWebApplication.class, args);
  }

}
