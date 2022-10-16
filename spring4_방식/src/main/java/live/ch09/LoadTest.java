package live.ch09;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class LoadTest {

  static AtomicInteger counter = new AtomicInteger(0);

  public static void main(String[] args) throws InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(100);
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8080/rest";

    StopWatch mainCounter = new StopWatch();
    mainCounter.start();

    for (int i = 0; i < 90; i++) {
//      restTemplate.getForObject(url, String.class);
      es.execute(() -> {
//        int idx = counter.incrementAndGet();
//        log.info("Thread {}", idx);
//
//        StopWatch sw = new StopWatch();
//        sw.start();

        String forObject = restTemplate.getForObject(url, String.class);
        log.info(forObject);
//        log.info("counter : " + counter.incrementAndGet());

//        sw.stop();
//        log.info("Elapsed: {} {}", idx, sw.getTotalTimeSeconds());

      });
    }

    es.shutdown();
    es.awaitTermination(100, TimeUnit.SECONDS);

    log.info("====3");

    mainCounter.stop();
    log.info("====종료 {}", mainCounter.getTotalTimeSeconds());
  }

}
