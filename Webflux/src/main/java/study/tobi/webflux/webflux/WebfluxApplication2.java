package study.tobi.webflux.webflux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@Slf4j
@RestController
public class WebfluxApplication2 {

  @GetMapping("/event/{id}")
  Mono<Event> hello(@PathVariable long id) {
    return Mono.just(new Event(id, "event" + id)).log();
  }

  /**
   * onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
   * request(unbounded)
   * onNext(WebfluxApplication2.Event(id=1, value=event1))
   * onNext(WebfluxApplication2.Event(id=1, value=event1))
   * onComplete
   */
  @GetMapping("/events")
  Flux<Event> events() {
    List<Event> events = Arrays.asList(new Event(1, "event1"), new Event(1, "event1"));
    return Flux.fromIterable(events).log();
  }

  @GetMapping("/events-stream")
  Flux<Event> eventsStream() {
    return Flux
        .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
        .take(10)
        .log();
  }

  @GetMapping(value = "/events-stream-step", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Event> eventsStreamStep() {
    return Flux
        .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
        .delayElements(Duration.ofSeconds(2))
        .take(10)
        .log();
  }

  @GetMapping(value = "/events-stream-from-flux")
  Flux<Event> eventsStreamFromFlux() {
    return Flux
        .<Event>generate(sink -> sink.next(new Event(System.currentTimeMillis(), "value")))
        .take(10)
        .log();
  }

  @GetMapping(value = "/events-stream-from-flux2")
  Flux<Event> eventsStreamFromFlux2() {
    return Flux
        .<Event, Long>generate(
            () -> 1L,
            (id, sink) -> {
              sink.next(new Event(id, "value"));
              return id + 1;
            }
        )
        .take(10)
        .log();
  }

  @GetMapping(value = "/events-stream-zip", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Event> eventsStreamZip() {
    Flux<Event> es = Flux.<Event, Long>generate(
        () -> 1L,
        (id, sink) -> {
          sink.next(new Event(id, "value"));
          return id + 1;
        }
    );

    Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

    return Flux.zip(es, interval).log().map(t -> t.getT1()).log().take(100);
  }


  public static void main(String[] args) {
    SpringApplication.run(WebfluxApplication2.class, args);
  }

  @Data
  @AllArgsConstructor
  public static class Event {
    long id;
    String value;
  }
}