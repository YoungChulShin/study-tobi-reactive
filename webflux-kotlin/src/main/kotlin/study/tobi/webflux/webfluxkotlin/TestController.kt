package study.tobi.webflux.webfluxkotlin

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.stream.Stream

@RestController
class TestController {

    companion object {
        private val logger = LoggerFactory.getLogger(TestController::class.java)
    }

    @GetMapping("/event/{id}")
    fun hello(@PathVariable id: Long): Mono<Event> {
        return Mono.just(Event(id, "event $id")).log()
    }

    @GetMapping("/events")
    fun events(): Flux<Event> {
        val events: List<Event> = listOf(
            Event(1, "event 1"),
            Event(2, "event 2"),
        )
        return Flux.fromIterable(events).log()
    }

    @GetMapping("/events-stream")
    fun eventsStream(): Flux<Event> {
        return Flux
            .fromStream(Stream.generate { Event(1, "event") })
            .log()
            .take(10)
            .log()
    }

    @GetMapping("/events-stream-from-flux")
    fun eventStreamFromFlux(): Flux<Event> {
        return Flux
            .generate<Event?> { sink -> sink.next(Event(1, "value")) }
            .take(100)
//        return Flux
//            .generate<Event, Long>({ 1L }, { id, sink -> sink.next(Event(id, "value")); id + 1 })
//            .log()
//            .take(10)
    }

    @GetMapping("/events-stream-from-flux2")
    fun eventStreamFromFlux2(): Flux<Event> {
        return Flux
            .generate<Event?, Long?>({
                1L
            }, { id, sink ->
                sink.next(Event(id, "value"))
                id + 1
            })
            .take(10)
            .log()
    }

    @GetMapping(value = ["/events-stream-zip"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun eventStreamZip(): Flux<Int> {
        val flux1 = Flux.generate<Int> { sink -> sink.next(1) }
        val flux2 = Flux.interval(Duration.ofSeconds(1))

        return Flux.zip(flux1, flux2).map { t -> t.t1 }.take(10)
    }
}

class Event(
    val id: Long,
    val value: String,
)