package study.tobi.reactive.netty;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
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

  @Autowired
  private MyApplicationService myApplicationService;

  @GetMapping("/rest")
  public Mono<String> rest(int idx) {
    // 이 자체만으로는 호출되지 않는다. publisher이기 때문에.
    // subscrbier가 subscribe를 해야하는데, Mono 타입을 리턴하면 Spring이 이 작업을 해 준다
//    Mono<ClientResponse> res = webClient.get().uri(URL1, idx).exchange();
//    return res.flatMap(clientResponse -> clientResponse.bodyToMono(String.class));

    return webClient.get().uri(URL1, idx).exchange()  // Mono<ClientResponse>
        .flatMap(c -> c.bodyToMono(String.class))   // Mono<String>
        .doOnNext(log::info)
        .flatMap(res1 -> webClient.get().uri(URL2, res1).exchange())  // Mono<ClientResponse>>
        .flatMap(c -> c.bodyToMono(String.class)) // Mono<String>
        .doOnNext(log::info)
        .flatMap(rest2 -> Mono.fromCompletionStage(myApplicationService.work(rest2)))  // CompletableFuture<String> -> Mono<String>
        .doOnNext(log::info);
  }

  @Service
  public static class MyApplicationService {

    @Async
    public CompletableFuture<String> work(String req) {
      return CompletableFuture.completedFuture(req + "/asyncwork");
    }
  }

}
