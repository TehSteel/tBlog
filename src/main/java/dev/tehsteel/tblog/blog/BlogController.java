package dev.tehsteel.tblog.blog;

import dev.tehsteel.tblog.blog.model.Blog;
import dev.tehsteel.tblog.blog.model.BlogResponse;
import dev.tehsteel.tblog.blog.model.request.BlogCreationRequest;
import dev.tehsteel.tblog.blog.model.request.BlogUpdateRequest;
import dev.tehsteel.tblog.user.UserService;
import dev.tehsteel.tblog.user.model.Role;
import dev.tehsteel.tblog.user.model.User;
import lombok.RequiredArgsConstructor;
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

	private final BlogService blogService;
	private final UserService userService;

	/* Perform creation on user's blog post */
	@PutMapping("/create")
	public ResponseEntity<String> createBlog(@RequestBody final BlogCreationRequest request) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		final Blog blog = blogService.insertBlog(request, userService.getUserByEmail(authentication.getName()));

		if (blog == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}


		return new ResponseEntity<>("Created blog successfully", HttpStatus.CREATED);
	}

	/* Perform deletion on user's blog post */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBlog(@PathVariable(name = "id") final long blogId) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Blog blog = blogService.getBlogById(blogId);
		final User user = userService.getUserByEmail(authentication.getName());

		if (blog == null) {
			return new ResponseEntity<>("Blog post wasn't found!", HttpStatus.NOT_FOUND);
		}

		/* If user doesn't have admin role and not the owner of the blog just return */
		if (user.getRole() != Role.ADMIN) {
			if (!blog.getPoster().getEmail().equals(user.getEmail())) {
				return new ResponseEntity<>("You are not owner of this blog.", HttpStatus.FORBIDDEN);
			}
		}

		blogService.removeBlogById(blogId);
		return new ResponseEntity<>("V", HttpStatus.OK);
	}


	/* Perform update on user's blog post */
	@PostMapping("/{id}")
	public ResponseEntity<BlogResponse> updateBlog(@PathVariable(name = "id") final long blogId, @RequestBody final BlogUpdateRequest request) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final Blog blog = blogService.getBlogById(blogId);
		final User user = userService.getUserByEmail(authentication.getName());


		if (blog == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		/* If user doesn't have admin role and not the owner of the blog just return */
		if (user.getRole() != Role.ADMIN) {
			if (!blog.getPoster().getEmail().equals(user.getName())) {
				return new ResponseEntity<>(BlogResponse.fromBlog(blog), HttpStatus.FORBIDDEN);
			}
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
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(BlogResponse.fromBlog(blog), HttpStatus.OK);
	}

	/* Get blog posts by page number */
	@GetMapping("/blogs")
	public Page<BlogResponse> getBlogs(@RequestParam("pageNumber") final int pageNumber) {
		return blogService.getBlogs(PageRequest.of(pageNumber, 10));
	}

}
