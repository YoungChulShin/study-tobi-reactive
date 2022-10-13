package live.ch08;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FutureEx {

  // 비동기 작업의 결과
  // - future
  // - callback
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    Logger log = LoggerFactory.getLogger(FutureEx.class);
    ExecutorService es = Executors.newCachedThreadPool();

    // 다른 스레드에서 실행된 결과의 응답을 메인스레드로 가져온다
//    Future<String> f = es.submit(() -> {
//      Thread.sleep(2000);
//      log.debug("Async");
//      return "Hello";
//    });

    // 32 분
    FutureTask<String> f = new FutureTask<>(() -> {
      Thread.sleep(2000);
      log.debug("Async");
      return "Hello";
    });

    es.execute(f);
    es.shutdown();

//    System.out.println(f.isDone());
//    Thread.sleep(2100);
//    log.debug("Exit");
//    System.out.println(f.isDone());
//    System.out.println(f.get());
  }

}
