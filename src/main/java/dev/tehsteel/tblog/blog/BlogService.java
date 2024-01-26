package dev.tehsteel.tblog.blog;

import dev.tehsteel.tblog.blog.event.BlogCreationEvent;
import dev.tehsteel.tblog.blog.event.BlogDeleteEvent;
import dev.tehsteel.tblog.blog.event.BlogUpdateEvent;
import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.blog.model.BlogResponse;
import dev.tehsteel.tblog.blog.model.request.BlogCreationRequest;
import dev.tehsteel.tblog.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlogService {


	private final ApplicationEventPublisher eventPublisher;
	private final BlogRepository blogRepository;

	/**
	 * Inserts a new blog post into the system.
	 *
	 * @param request The request containing blog creation details.
	 * @param poster  The user who is the author of the blog post.
	 * @return The inserted blog entity.
	 */
	public Blog insertBlog(final BlogCreationRequest request, final User poster) {
		final Blog blog = new Blog();

		blog.setTitle(request.title());
		blog.setText(request.text());
		blog.setPoster(poster);

		blogRepository.save(blog);

		eventPublisher.publishEvent(new BlogCreationEvent(this, blog));

		return blog;
	}

	/**
	 * Updates an existing blog post in the system.
	 *
	 * @param blog The blog entity to be updated.
	 * @return The updated blog entity.
	 */
	@CachePut(value = "blogCache", key = "#blog.id")
	public Blog updateBlog(final Blog blog) {
		blog.setLastUpdated(new Date());
		blogRepository.save(blog);

		eventPublisher.publishEvent(new BlogUpdateEvent(this, blog));

		return blog;
	}

	/**
	 * Retrieves a paginated list of blog posts.
	 *
	 * @param pageable The pagination information.
	 * @return A page of {@link BlogResponse} objects representing blog posts.
	 */
	public Page<BlogResponse> getBlogs(final Pageable pageable) {
		return blogRepository.findAll(pageable).map(BlogResponse::fromBlog);
	}

	/**
	 * Retrieves a blog post by its id.
	 *
	 * @param id The id of the blog post.
	 * @return The blog entity corresponding to the given id, or {@code null} if not found.
	 */
	@Cacheable(value = "blogCache", key = "#id")
	public Blog getBlogById(final long id) {
		log.debug("Fetching blog by id: {}", id);
		return blogRepository.findById(id).orElse(null);
	}

	/**
	 * Removes a blog post from the system by its id.
	 *
	 * @param id The id of the blog post to be removed.
	 */
	@CacheEvict(value = "blogCache", key = "#id")
	public void removeBlogById(final long id) {
		log.debug("Deleting blog by id: {}", id);
		final Blog blog = blogRepository.findById(id).orElse(null);
		if (blog == null) return;
		blogRepository.deleteById(id);
		eventPublisher.publishEvent(new BlogDeleteEvent(this, blog));
	}
}
