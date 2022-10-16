package live.ch06;

import reactor.core.publisher.Flux;

public class ReactorEx {

  // Flux: Publisher
  public static void main(String[] args) {
    Flux.<Integer>create(e -> {
          e.next(1);
          e.next(2);
          e.next(3);
          e.complete();
        })
        .log()
        .map(s -> s * 10) // operator 연산
        .log()
        .subscribe(System.out::println);
  }
}
