package live.ch07;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxScEx {

  public static void main(String[] args) throws InterruptedException {
    /**
    Flux.range(1, 10)
        .publishOn(Schedulers.newSingle("pub")) // subscriber가 느릴 경우
        .log()
        .subscribeOn(Schedulers.newSingle("sub"))  // publisher가 느릴 경우
        .subscribe(System.out::println);  // onNext가 println이 된다
     **/

    // 유저가 생성한 스레드는 메인 스레드가 종료되어도 종료되지 않는다

    // Thread 종료
    // - user
    // - daemon: jvm이 daemon thread만 남아있으면 종료시킨다
    Flux.interval(Duration.ofMillis(500))
        .take(10)
        .subscribe(System.out::println);

    System.out.println("exit");

    TimeUnit.SECONDS.sleep(10);
  }
}
