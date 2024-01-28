package dev.tehsteel.tblog.task;

import com.google.common.cache.Cache;
import dev.tehsteel.tblog.blog.BlogRepository;
import dev.tehsteel.tblog.blog.model.Blog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
@Slf4j
@Component
public class UpdateBlogCache {
	private final Cache<Long, Blog> blogCache;
	private final BlogRepository blogRepository;

	@Scheduled(fixedRate = 2, timeUnit = TimeUnit.HOURS)
	@Async
	public void runTask() {
		log.debug("Saving cached blogs size: {}", blogCache.size());
		blogCache.asMap().values().forEach(blogRepository::save);
		log.debug("Saved cached blogs size: {}", blogCache.size());
	}
}