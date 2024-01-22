package dev.tehsteel.tblog.blog.event;

import dev.tehsteel.tblog.blog.model.Blog;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/* An event for a blog getting deleted */
@Getter
public class BlogDeleteEvent extends ApplicationEvent {

	private final Blog blog;

	public BlogDeleteEvent(final Object source, final Blog blog) {
		super(source);
		this.blog = blog;
	}
}
