package dev.tehsteel.tblog.user;

import com.google.common.cache.Cache;
import dev.tehsteel.tblog.user.model.Role;
import dev.tehsteel.tblog.user.model.User;
import dev.tehsteel.tblog.user.model.request.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


	private final UserRepository userRepository;
	private final Cache<Long, User> userCache;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Inserts a new user into the system based on the provided UserRegisterRequest.
	 *
	 * @param userRegisterRequest The UserRegisterRequest containing user information.
	 * @return The inserted User object.
	 */
	public User insertUser(final UserRegisterRequest userRegisterRequest) {
		log.debug("Creating new user by: {}", userRegisterRequest.email());
		/* Checking if email is already registered. if so return null */
		if (userRepository.findByEmail(userRegisterRequest.email()).isPresent()) {
			log.debug("User is already created by email: {}", userRegisterRequest.email());
			return null;
		}

		final User user = new User();

		user.setRole(Role.USER);
		user.setEmail(userRegisterRequest.email());
		user.setName(userRegisterRequest.name());

		/* Hash & salt password before storing */
		user.setPassword(passwordEncoder.encode(userRegisterRequest.password()));

		userCache.put(user.getId(), user);
		userRepository.save(user);

		return user;
	}

	/**
	 * Fetch a user by his user id
	 *
	 * @param id The user id to fetch
	 * @return The fetched user or null
	 */
	public User getUserById(final Long id) {
		log.debug("Fetching user by id: {}", id);

		User user = userCache.getIfPresent(id);
		if (user != null) {
			log.debug("Found user in cache by id: {}", id);
			return user;
		}

		user = userRepository.findById(id).orElse(null);

		if (user != null) {
			userCache.put(user.getId(), user);
			log.debug("User fetched from repository and cached by id: {}", id);
		} else {
			log.debug("User not found for id: {}", id);
		}

		return user;
	}


	/**
	 * Fetch a user by his user email
	 *
	 * @param email The user email to fetch
	 * @return The fetched user or null
	 */
	public User getUserByEmail(final String email) {
		log.debug("Fetching user by email: {}", email);

		User user = userCache.asMap().values().stream().filter(userMap -> userMap.getEmail().equals(email)).findFirst().orElse(null);

		if (user != null) {
			log.debug("Found user in cache by email: {}", user.getEmail());
			return user;
		}

		user = userRepository.findByEmail(email).orElse(null);

		if (user != null) {
			userCache.put(user.getId(), user);
			log.debug("User fetched from repository and cached by email: {}", email);
		} else {
			log.debug("User not found for email: {}", email);
		}
		
		return userRepository.findByEmail(email).orElse(null);
	}
}
