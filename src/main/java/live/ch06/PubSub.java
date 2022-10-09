package live.ch06;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * operator를 이용해서 data를 가공
 * publisher -> data1 -> operator -> data2 -> subscriber
 * 1. map (d1 -> f -> d2)
 */
public class PubSub {

  public static void main(String[] args) {
    Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
//    Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//    Publisher<Integer> map2Pub = mapPub(mapPub, s -> s * -1);
//    Publisher<Integer> sumPub = sumPub(pub);
    Publisher<Integer> reducePub = reducePub(pub, 0, (a, b) -> a + b);
    reducePub.subscribe(logSub());
  }

  private static Publisher<Integer> reducePub(
      Publisher<Integer> pub, int init,
      BiFunction<Integer, Integer, Integer> bf) {
    return new Publisher<Integer>() {
      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        pub.subscribe(new DelegateSub(sub) {
          int result = 0;
          @Override
          public void onNext(Integer i) {
            result = bf.apply(result, i);
          }

          @Override
          public void onComplete() {
            sub.onNext(result);
            sub.onComplete();
          }
        });
      }
    };
  }

  private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
    return new Publisher<Integer>() {
      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        pub.subscribe(new DelegateSub(sub) {
          int sum = 0;

          @Override
          public void onNext(Integer i) {
            sum += i;
          }

          @Override
          public void onComplete() {
            sub.onNext(sum);
            sub.onComplete();
          }
        });
      }
    };
  }

  private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
    return new Publisher<Integer>() {
      @Override
      public void subscribe(Subscriber<? super Integer> sub) {
        pub.subscribe(new DelegateSub(sub) {
          @Override
          public void onNext(Integer i) {
            sub.onNext(f.apply(i));
          }
        });
      }
    };
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
