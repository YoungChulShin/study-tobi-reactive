package live.ch08;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FutureEx {

  interface SuccessCallback {
    void onSuccess(String result);
  }

  interface ExceptionCallback {
    void onError(Throwable t);
  }

  public static class CallbackFutureTask extends FutureTask<String> {
    SuccessCallback sc;
    ExceptionCallback ec;

    public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
      super(callable);
      this.sc = Objects.requireNonNull(sc);
      this.ec = Objects.requireNonNull(ec);
    }

    @Override
    protected void done() {
      try {
        sc.onSuccess(get());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        ec.onError(e.getCause());
      }
    }
  }

  // 비동기 작업의 결과
  // - future
  // - callback
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    Logger log = LoggerFactory.getLogger(FutureEx.class);
    ExecutorService es = Executors.newCachedThreadPool();

    CallbackFutureTask f = new CallbackFutureTask(() -> {
        Thread.sleep(2000);
        if (1 == 1) throw new RuntimeException("Async Error");
        log.debug("Async");
        return "Hello";},
        s -> System.out.println("Result: " + s),
        e -> System.out.println("Error: " + e.getMessage()));

    es.execute(f);
    es.shutdown();
  }
}
