package dev.tehsteel.tblog.blog;

import com.google.common.cache.Cache;
import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.blog.model.BlogResponse;
import dev.tehsteel.tblog.blog.model.request.BlogCreationRequest;
import dev.tehsteel.tblog.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlogService {


	private final BlogRepository blogRepository;
	private final Cache<Long, Blog> blogCache;

	/**
	 * Inserts a new blog post into the system.
	 *
	 * @param request The request containing blog creation details.
	 * @param poster  The user who is the author of the blog post.
	 * @return The inserted blog entity.
	 */
	public Blog insertBlog(final BlogCreationRequest request, final User poster) {
		log.debug("Creating new blog by: {}", poster.getName());
		final Blog blog = new Blog();

		blog.setTitle(request.title());
		blog.setText(request.text());
		blog.setPoster(poster);
		
		blogCache.put(blog.getId(), blog);
		blogRepository.save(blog);

		return blog;
	}

	/**
	 * Updates an existing blog post in the system.
	 *
	 * @param blog The blog entity to be updated.
	 * @return The updated blog entity.
	 */
	public Blog updateBlog(final Blog blog) {
		blog.setLastUpdated(new Date());
		blogRepository.save(blog);

		blogCache.put(blog.getId(), blog);

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
	public Blog getBlogById(final long id) {
		log.debug("Fetching blog by id: {}", id);

		Blog blog = blogCache.getIfPresent(id);
		if (blog != null) {
			log.debug("Found blog in cache by id: {}", id);
			return blog;
		}

		blog = blogRepository.findById(id).orElse(null);

		if (blog != null) {
			blogCache.put(blog.getId(), blog);
			log.debug("Blog fetched from repository and cached by id: {}", id);
		} else {
			log.debug("Blog not found for id: {}", id);
		}

		return blog;
	}


	/**
	 * Removes a blog post from the system by its id.
	 *
	 * @param id The id of the blog post to be removed.
	 */
	public void removeBlogById(final long id) {
		log.debug("Deleting blog by id: {}", id);
		final Blog blog = blogRepository.findById(id).orElse(null);
		if (blog == null) return;
		log.debug("Deleted blog by id: {}", id);
		blogCache.invalidate(id);
		blogRepository.deleteById(id);
	}
}
