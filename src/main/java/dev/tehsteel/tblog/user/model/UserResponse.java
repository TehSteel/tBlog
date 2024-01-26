package dev.tehsteel.tblog.user.model;

import dev.tehsteel.tblog.blog.model.Blog;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;


@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UserResponse {

	private final long id;
	private final String email;
	private final String name;
	private final List<Blog> postedBlogs;
	private final Role role;
	private final Date accountCreation;


	public static UserResponse fromUser(final User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getPostedBlogs(), user.getRole(), user.getAccountCreation());
	}
}
