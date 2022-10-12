package live.ch07;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {

  public static void main(String[] args) {
    Flux.range(1, 10)
        .publishOn(Schedulers.newSingle("pub")) // subscriber가 느릴 경우
        .log()
        .subscribeOn(Schedulers.newSingle("sub"))  // publisher가 느릴 경우
        .subscribe(System.out::println);  // onNext가 println이 된다

    System.out.println("exit");
  }
}
