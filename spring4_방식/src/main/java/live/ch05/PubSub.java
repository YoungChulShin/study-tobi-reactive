package live.ch05;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class PubSub {

  public static void main(String[] args) {
    Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);

    Publisher p = new Publisher() {
      @Override
      public void subscribe(Subscriber subscriber) {
        Iterator<Integer> it = itr.iterator();
        subscriber.onSubscribe(new Subscription() {

          // Subscriber는 Subscription을 통해서 몇개를 Publisher로부터 받을지 알려줄 수 있다
          @Override
          public void request(long n) {
            while(n-- > 0) {
              if (it.hasNext()) {
                subscriber.onNext(it.next());
              } else {
                subscriber.onComplete();
                break;
              }
            }
          }

          @Override
          public void cancel() {

          }
        });
      }
    };

    Subscriber<Integer> s = new Subscriber<Integer>() {
      Subscription subscription;

      @Override
      public void onSubscribe(Subscription subscription) {
        System.out.println(Thread.currentThread().getName() + " :onSubscribe");
        this.subscription = subscription;
        this.subscription.request(2);
      }

      int bufferSize = 2;
      @Override
      public void onNext(Integer item) {
        System.out.println(Thread.currentThread().getName() + " :onNext : " + item);
        if (--bufferSize <= 0) {
          this.bufferSize = 2;
          this.subscription.request(2);
        }

      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println("onError");
      }

      @Override
      public void onComplete() {
        System.out.println(Thread.currentThread().getName() + " :onComplete");
      }
    };

    p.subscribe(s);
  }
}
