package dev.tehsteel.tblog.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.user.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
	private final Cache<Long, Blog> blogCache = CacheBuilder.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build();

	private final Cache<Long, User> userCache = CacheBuilder.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build();

	@Bean
	public Cache<Long, User> userCache() {
		return userCache;
	}

	@Bean
	public Cache<Long, Blog> blogCache() {
		return blogCache;
	}
}
