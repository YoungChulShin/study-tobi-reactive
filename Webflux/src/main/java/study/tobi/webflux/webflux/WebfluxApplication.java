package study.tobi.webflux.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
@Slf4j
public class WebfluxApplication {

  @GetMapping("/")
  Mono<String> hello() {
    log.info("pos1");
    // just()는 이미 전달할 데이터가 만들어져있는 것이기 때문에, 이후에 콜백으로 실행되는 것은 아니다
    // Mono<String> m = Mono.just(generateHello()).log();
    Mono<String> m = Mono.fromSupplier(this::generateHello).log();
    log.info("pos2");
    // String message = m.block();
    // log.info("pos2: " + message);
    return m;
  }

  private String generateHello() {
    log.info("method generateHello()");
    return "Hello Mono";
  }

  // HTTP를 지원하는 웹서버라면 어떤 것이든지 사용 가능하다
  // - Netty
  //    - Servlet 위에서 스프링이 돌아가기 위해서 하는 작업들이 없어지기 때문에 시간이 빠르다
  public static void main(String[] args) {
    SpringApplication.run(WebfluxApplication.class, args);
  }

}
