package dev.tehsteel.tblog.user;

import dev.tehsteel.tblog.user.event.UserRegistrationEvent;
import dev.tehsteel.tblog.user.model.Role;
import dev.tehsteel.tblog.user.model.User;
import dev.tehsteel.tblog.user.model.request.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private final ApplicationEventPublisher eventPublisher;

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	/**
	 * Inserts a new user into the system based on the provided UserRegisterRequest.
	 *
	 * @param userRegisterRequest The UserRegisterRequest containing user information.
	 * @return The inserted User object.
	 */
	public User insertUser(final UserRegisterRequest userRegisterRequest) {
		/* Checking if email is already registered. if so return null */
		if (userRepository.findByEmail(userRegisterRequest.email()).isPresent()) {
			return null;
		}

		final User user = new User();

		user.setRole(Role.USER);
		user.setEmail(userRegisterRequest.email());
		user.setName(userRegisterRequest.name());

		/* Hash & salt password before storing */
		user.setPassword(passwordEncoder.encode(userRegisterRequest.password()));

		userRepository.save(user);

		eventPublisher.publishEvent(new UserRegistrationEvent(this, user));

		return user;
	}

	/**
	 * Fetch a user by his user id
	 *
	 * @param id The user id to fetch
	 * @return The fetched user or null
	 */
	@Cacheable(value = "userCache", key = "#id")
	public User getUserById(final Long id) {
		LOGGER.debug("Fetching user by id: {}", id);
		return userRepository.findById(id).orElse(null);
	}


	/**
	 * Fetch a user by his user email
	 *
	 * @param email The user email to fetch
	 * @return The fetched user or null
	 */
	@Cacheable(value = "userCache", key = "#email")
	public User getUserByEmail(final String email) {
		LOGGER.debug("Fetching user by email: {}", email);
		return userRepository.findByEmail(email).orElse(null);
	}
}
