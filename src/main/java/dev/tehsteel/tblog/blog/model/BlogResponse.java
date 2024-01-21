package dev.tehsteel.tblog.blog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
public final class BlogResponse {

	private long id;
	private String title;
	private String text;
	private String poster;
	private Date latestUpdate;
	private Date creationDate;


	public static BlogResponse fromBlog(final Blog blog) {
		return new BlogResponse(blog.getId(), blog.getTitle(), blog.getText(), blog.getPoster().getName(), blog.getLastUpdated(), blog.getCreationDate());
	}

}
