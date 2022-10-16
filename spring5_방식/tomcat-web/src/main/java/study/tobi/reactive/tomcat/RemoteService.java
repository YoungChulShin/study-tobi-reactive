package study.tobi.reactive.tomcat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RemoteService {

  @GetMapping("/service")
  public String service(String req) throws InterruptedException {
    Thread.sleep(1000);
    return req + "/service1";
  }

  @GetMapping("/service2")
  public String service2(String req) throws InterruptedException {
    Thread.sleep(1000);
    return req + "/service2";
  }

}
