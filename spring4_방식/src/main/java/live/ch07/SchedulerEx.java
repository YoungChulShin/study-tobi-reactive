package live.ch07;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerEx {

  public static void main(String[] args) {

    Logger logger = LoggerFactory.getLogger(SchedulerEx.class);

    Publisher<Integer> pub = sub -> {
      sub.onSubscribe(new Subscription() {
        @Override
        public void request(long n) {
          logger.debug("request");
          sub.onNext(1);
          sub.onNext(2);
          sub.onNext(3);
          sub.onNext(4);
          sub.onNext(5);
          sub.onComplete();
        }

        @Override
        public void cancel() {

        }
      });
    };

    // pub <-> subOnPub <-> sub
    Publisher<Integer> subOnPub = sub -> {
//      pub.subscribe(sub);
      ExecutorService es = Executors.newSingleThreadExecutor();
      es.execute(() -> pub.subscribe(sub));
      es.shutdown();
    };

    Publisher<Integer> pubOnPub = sub -> {
      subOnPub.subscribe(new Subscriber<Integer>() {
        ExecutorService es = Executors.newSingleThreadExecutor();

        @Override
        public void onSubscribe(Subscription s) {
          sub.onSubscribe(s);
        }

        @Override
        public void onNext(Integer integer) {
          es.execute(() -> sub.onNext(integer));
        }

        @Override
        public void onError(Throwable t) {
          es.execute(() -> sub.onError(t));
        }

        @Override
        public void onComplete() {
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

    System.out.println("exit");

    // 47:13
  }

}
