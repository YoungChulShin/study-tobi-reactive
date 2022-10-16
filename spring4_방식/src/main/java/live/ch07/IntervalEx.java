package live.ch07;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntervalEx {

  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(IntervalEx.class);

    Publisher<Integer> pub = sub -> {
      sub.onSubscribe(new Subscription() {
        int no = 0;
        volatile boolean cancelled = false;

        @Override
        public void request(long n) {
          ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
          exec.scheduleAtFixedRate(
              () -> {
                if (cancelled) {
                  exec.shutdown();
                  return;
                }
                sub.onNext(no++);
              },
              0,
              300,
              TimeUnit.MILLISECONDS
          );
        }

        @Override
        public void cancel() {
          cancelled = true;
        }
      });
    };

    Publisher<Integer> takePub = sub -> {
      pub.subscribe(new Subscriber<Integer>() {
        int count = 0;
        int takeCount = 10;
        Subscription subscription;
        @Override
        public void onSubscribe(Subscription s) {
          this.subscription = s;
          sub.onSubscribe(s);
        }

        @Override
        public void onNext(Integer integer) {
          if (count == takeCount) {
            subscription.cancel();
            sub.onComplete();
          } else {
            sub.onNext(integer);
            count++;
          }
        }

        @Override
        public void onError(Throwable t) {
          sub.onError(t);
        }

        @Override
        public void onComplete() {
          sub.onComplete();
        }
      });
    };

    takePub.subscribe(new Subscriber<Integer>() {
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
  }
}
