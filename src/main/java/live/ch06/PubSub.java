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
    Publisher<String> mapPub = mapPub(pub, s -> "[" + s + "]");
//    Publisher<Integer> map2Pub = mapPub(mapPub, s -> s * -1);
//    Publisher<Integer> sumPub = sumPub(pub);
//    Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a + "-" + b);
    Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b + ","));
    reducePub.subscribe(logSub());
  }

  private static <T,R> Publisher<R> reducePub(
      Publisher<T> pub,
      R init,
      BiFunction<R, T, R> bf) {
    return new Publisher<R>() {
      @Override
      public void subscribe(Subscriber<? super R> sub) {
        pub.subscribe(new DelegateSub<T, R>(sub) {
          R result = init;
          @Override
          public void onNext(T i) {
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
//
//  private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
//    return new Publisher<Integer>() {
//      @Override
//      public void subscribe(Subscriber<? super Integer> sub) {
//        pub.subscribe(new DelegateSub(sub) {
//          int sum = 0;
//
//          @Override
//          public void onNext(Integer i) {
//            sum += i;
//          }
//
//          @Override
//          public void onComplete() {
//            sub.onNext(sum);
//            sub.onComplete();
//          }
//        });
//      }
//    };
//  }

  private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
    return new Publisher<R>() {
      @Override
      public void subscribe(Subscriber<? super R> sub) {
        pub.subscribe(new DelegateSub<T, R>(sub) {
          @Override
          public void onNext(T i) {
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

  private static <T> Subscriber<T> logSub() {
    return new Subscriber<T>() {
      @Override
      public void onSubscribe(Subscription s) {
        System.out.println("onSubscription");
        s.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(T i) {
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
