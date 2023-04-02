package jp.co.arthurs.e2ee

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class E2eeApplication

fun main(args: Array<String>) {
	runApplication<E2eeApplication>(*args)
}
