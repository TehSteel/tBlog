package dev.tehsteel.tblog.blog;

import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.blog.model.BlogResponse;
import dev.tehsteel.tblog.blog.model.request.BlogCreationRequest;
import dev.tehsteel.tblog.blog.model.request.BlogUpdateRequest;
import dev.tehsteel.tblog.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/blog")
public class BlogController {

	@Autowired
	private final BlogService blogService;

	@Autowired
	private final UserService userService;

	/* Perform creation on user's blog post */
	@PostMapping("/create")
	public ResponseEntity<String> createBlog(@RequestBody final BlogCreationRequest request) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		final Blog blog = blogService.insertBlog(request, userService.getUserByEmail(authentication.getName()));

		if (blog == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}


		return new ResponseEntity<>("Created blog successfully", HttpStatus.OK);
	}
	
	/* Perform deletion on user's blog post */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteBlog(@PathVariable(name = "id") final long blogId) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Blog blog = blogService.getBlogById(blogId);

		if (blog == null) {
			return new ResponseEntity<>("Blog post wasn't found!", HttpStatus.BAD_REQUEST);
		}

		if (!blog.getPoster().getEmail().equals(authentication.getName())) {
			return new ResponseEntity<>("You are not owner of this blog.", HttpStatus.UNAUTHORIZED);
		}

		blogService.removeBlogPost(blogId);
		return new ResponseEntity<>("V", HttpStatus.OK);
	}


	/* Perform update on user's blog post */
	@PostMapping("/update/{id}")
	public ResponseEntity<BlogResponse> updateBlog(@PathVariable(name = "id") final long blogId, @RequestBody final BlogUpdateRequest request) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Blog blog = blogService.getBlogById(blogId);

		if (blog == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		if (!blog.getPoster().getEmail().equals(authentication.getName())) {
			return new ResponseEntity<>(BlogResponse.fromBlog(blog), HttpStatus.UNAUTHORIZED);
		}

		blog.setTitle(request.title());
		blog.setText(request.text());
		blogService.updateBlog(blog);

		return new ResponseEntity<>(BlogResponse.fromBlog(blog), HttpStatus.OK);
	}

	/* Look on a user's blog post */
	@GetMapping("/{id}")
	public ResponseEntity<BlogResponse> getBlog(@PathVariable(name = "id") final int blogId) {
		final Blog blog = blogService.getBlogById(blogId);

		if (blog == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(BlogResponse.fromBlog(blog), HttpStatus.OK);
	}

	/* Get blog posts by page number */
	@GetMapping("/blogs")
	public Page<BlogResponse> getBlogs(@RequestParam("pageNumber") final int pageNumber) {
		return blogService.getBlogs(PageRequest.of(pageNumber, 10));
	}
}
