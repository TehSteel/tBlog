package dev.tehsteel.tblog.blog.event;

import dev.tehsteel.tblog.blog.model.Blog;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/* An event for a blog getting updated */
@Getter
public class BlogUpdateEvent extends ApplicationEvent {
	private final Blog blog;

	public BlogUpdateEvent(final Object source, final Blog blog) {
		super(source);
		this.blog = blog;
	}
}
