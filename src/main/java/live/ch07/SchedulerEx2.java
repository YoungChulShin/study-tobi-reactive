package live.ch07;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerEx2 {

  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(SchedulerEx.class);

    Publisher<Integer> pub = sub -> {
      sub.onSubscribe(new Subscription() {
        @Override
        public void request(long n) {
          logger.debug("request-1");
          sub.onNext(1);
          logger.debug("request-2");
          sub.onNext(2);
          logger.debug("request-3");
          sub.onNext(3);
          logger.debug("request-4");
          sub.onNext(4);
          logger.debug("request-5");
          sub.onNext(5);
          sub.onComplete();
        }

        @Override
        public void cancel() {

        }
      });
    };

    // publisher 하는 쪽이 오래 걸린다면 이쪽을 비동기로 할 수 있다
    Publisher<Integer> subOnPub = sub -> {
      ExecutorService es = Executors.newSingleThreadExecutor();
      es.execute(() -> pub.subscribe(sub));
    };

    // 구독하는 곳에서 오래 걸린다면 비동기를 걸 수 있다
    Publisher<Integer> pubOnPub = sub -> {
      subOnPub.subscribe(new Subscriber<Integer>() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        @Override
        public void onSubscribe(Subscription s) {
          sub.onSubscribe(s);
        }

        @Override
        public void onNext(Integer integer) {
//          sub.onNext(calculateLong(integer));
          es.execute(() -> sub.onNext(calculateLong(integer)));
        }

        @Override
        public void onError(Throwable t) {
//          sub.onError(t);
          es.execute(() ->  sub.onError(t));
          es.shutdown();
        }

        @Override
        public void onComplete() {
//          sub.onComplete();
          es.execute(() -> sub.onComplete());
          es.shutdown();
        }
      });
    };

    pubOnPub.subscribe(new Subscriber<Integer>() {
      @Override
      public void onSubscribe(Subscription s) {
        logger.debug("onSubscribe");
        s.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(Integer integer) {
        logger.debug("onNext: {}", integer);
      }

      @Override
      public void onError(Throwable t) {
        logger.debug("onError: {}", t.getMessage(), t);
      }

      @Override
      public void onComplete() {
        logger.debug("onComplete");
      }
    });

    System.out.println("Exit");
  }

  private static Integer calculateLong(Integer source) {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return source * 1;
  }
}
