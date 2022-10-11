package live.ch07;


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
      pub.subscribe(sub);
    };

    subOnPub.subscribe(new Subscriber<Integer>() {
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
  }

}
