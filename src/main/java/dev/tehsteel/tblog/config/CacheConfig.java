package dev.tehsteel.tblog.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.user.model.verification.UserVerification;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
	private final Cache<Long, Blog> blogCache = CacheBuilder.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build();

	private final Cache<UUID, UserVerification> userVerificationCache = CacheBuilder.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(2, TimeUnit.HOURS)
			.build();

	@Bean
	public Cache<Long, Blog> blogCache() {
		return blogCache;
	}

	@Bean
	public Cache<UUID, UserVerification> userVerificationCache() {
		return userVerificationCache;
	}
}
