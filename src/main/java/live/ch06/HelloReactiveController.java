package live.ch06;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloReactiveController {

  @RequestMapping("/hello")
  public Publisher<String> hello(String name) {
    return new Publisher<String>() {
      @Override
      public void subscribe(Subscriber<? super String> s) {
        s.onSubscribe(new Subscription() {
          @Override
          public void request(long n) {
            s.onNext("hello " + name);
            s.onComplete(); // onComplete를 전달하지 않으면 스프링에서 계속 request를 하기 때문에 무한 응답이 만들어진다
          }

          @Override
          public void cancel() {

          }
        });
      }
    };
  }
}
