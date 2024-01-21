package dev.tehsteel.tblog.blog;

import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.blog.model.BlogResponse;
import dev.tehsteel.tblog.blog.model.request.BlogCreationRequest;
import dev.tehsteel.tblog.user.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class BlogService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlogService.class);

	@Autowired
	private final BlogRepository blogRepository;

	public Blog insertBlog(final BlogCreationRequest request, final User poster) {
		final Blog blog = new Blog();

		blog.setTitle(request.title());
		blog.setText(request.text());
		blog.setPoster(poster);

		blogRepository.save(blog);

		return blog;
	}


	@CachePut(value = "blogCache", key = "#blog.id")
	public Blog updateBlog(final Blog blog) {
		blog.setLastUpdated(new Date());
		blogRepository.save(blog);

		return blog;
	}

	public Page<BlogResponse> getBlogs(final Pageable pageable) {
		return blogRepository.findAll(pageable).map(BlogResponse::fromBlog);
	}

	@Cacheable(value = "blogCache", key = "#id")
	public Blog getBlogById(final long id) {
		LOGGER.debug("Fetching blog by id: {}", id);
		return blogRepository.findById(id).orElse(null);
	}

	@CacheEvict(value = "blogCache", key = "#id")
	public void removeBlogPost(final long id) {
		LOGGER.debug("Deleting blog by id: {}", id);
		blogRepository.deleteById(id);
	}
}
