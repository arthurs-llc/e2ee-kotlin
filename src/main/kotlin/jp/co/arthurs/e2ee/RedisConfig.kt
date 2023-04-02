package jp.co.arthurs.e2ee

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfig {
    @Bean
    fun jedisConnectionFactory(redisProperties: RedisProperties): JedisConnectionFactory {
        return JedisConnectionFactory(RedisStandaloneConfiguration(redisProperties.host, redisProperties.port))
    }

    @Bean
    fun redisTemplate(redisProperties: RedisProperties): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(jedisConnectionFactory(redisProperties))
        return template
    }
}