package live.ch08;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Ch08Application {

  /**
   * Thread가 Blocking 되면 Context Switching이 일어나기 때문에 CPU를 많이 먹는다
   * HTTPServelet Request, Response는 input, output stream을 사용하는데, 이 연산은 Blocking 연산이다.
   *
   *
   *
   * 1          Servlete Thread 1  - req  - blocking io - response
   * 2          Servlete Thread 2
   * 3     NIO  Servlete Thread 3
   * 4          Servlete Thread 4
   * 5          Servlete Thread 5
   *
   * Thread를 계속 늘리면?
   * - 각 스레드가 stack trace와 data를 가지고 있어서 메모리를 많이 먹는다
   * - context switcing이 많이 발생하면서 CPU에 부하가 생긴다
   */

  @RestController
  public static class MyController {
    @GetMapping("/async")
    public String async() throws InterruptedException {
      Thread.sleep(2000);
      return Thread.currentThread().getName() + ": hello";
    }

    /**
     * 실행 로그
     * 2022-10-15 14:41:02.606  INFO 77240 --- [nio-8080-exec-1] live.ch08.Ch08Application                : callable
     * 2022-10-15 14:41:02.612  INFO 77240 --- [         task-1] live.ch08.Ch08App
     *
     * 처음 시작은 nio thread에서 진행되고, 이것은 바로 스레드 풀에 반납
     * callable의 실행은 spring이 해주는 것인데, 별도의 스레드풀에서 작업을 처리하고, 반납은 다시 일시적으로 스레드 풀에서 스레드를 가져와서 처리한다'
     * Throughtput이 증가한다
     */
    @GetMapping("/callable")
    public Callable<String> callable() throws InterruptedException {
      log.info("callable");
      return () -> {
        log.info("aysnc");
        Thread.sleep(2000);
        log.info("end");
        return "hello";
      };
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(Ch08Application.class, args);
  }
}
