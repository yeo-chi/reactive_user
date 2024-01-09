package yeo.chi.reactive_user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
class ReactiveUserApplication

fun main(args: Array<String>) {
    runApplication<ReactiveUserApplication>(*args)
}
