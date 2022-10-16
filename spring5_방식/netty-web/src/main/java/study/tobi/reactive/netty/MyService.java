package study.tobi.reactive.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class MyService {

  static final String URL1 = "http://localhost:8081/service?req={req}";
  static final String URL2 = "http://localhost:8081/service2?req={req}";

  WebClient webClient = WebClient.create();

  @GetMapping("/rest")
  public Mono<String> rest(int idx) {
    // 이 자체만으로는 호출되지 않는다. publisher이기 때문에.
    // subscrbier가 subscribe를 해야하는데, Mono 타입을 리턴하면 Spring이 이 작업을 해 준다
//    Mono<ClientResponse> res = webClient.get().uri(URL1, idx).exchange();
//    return res.flatMap(clientResponse -> clientResponse.bodyToMono(String.class));

    return webClient.get().uri(URL1, idx).exchange()
        .flatMap(c -> c.bodyToMono(String.class))
        .flatMap(res1 -> webClient.get().uri(URL2, res1).exchange())
        .flatMap(c -> c.bodyToMono(String.class));
  }
}
