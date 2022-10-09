package live.ch06;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PubSub {

  public static void main(String[] args) {
    Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
    pub.subscribe(logSub());
  }

  private static Publisher<Integer> iterPub(List<Integer> iter) {
    return new Publisher<>() {
      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        sub.onSubscribe(new Subscription() {
          @Override
          public void request(long n) {
            try {
              iter.forEach(s -> sub.onNext(s));
              sub.onComplete();
            } catch (Throwable t) {
              sub.onError(t);
            }
          }

          @Override
          public void cancel() {

          }
        });
      }
    };
  }

  private static Subscriber<Integer> logSub() {
    return new Subscriber<Integer>() {
      @Override
      public void onSubscribe(Subscription s) {
        System.out.println("onSubscription");
        s.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(Integer i) {
        System.out.println("onNext: " + i);
      }

      @Override
      public void onError(Throwable t) {
        System.out.println("onError");
      }

      @Override
      public void onComplete() {
        System.out.println("onComplete");
      }
    };
  }
}
