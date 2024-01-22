package dev.tehsteel.tblog.user.event;

import dev.tehsteel.tblog.user.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class UserRegistrationEvent extends ApplicationEvent {

	private final User user;

	public UserRegistrationEvent(final Object source, final User user) {
		super(source);
		this.user = user;
	}
}
