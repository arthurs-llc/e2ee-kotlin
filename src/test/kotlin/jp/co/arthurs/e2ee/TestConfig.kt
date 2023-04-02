package jp.co.arthurs.e2ee

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisServer

@TestConfiguration
class TestConfig(redisProperties: RedisProperties) {
    private val redisServer: RedisServer
    init {
        redisServer = RedisServer(redisProperties.port)
    }

    @PostConstruct
    fun postConstruct(): Unit {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy(): Unit {
        redisServer.stop()
    }
}
