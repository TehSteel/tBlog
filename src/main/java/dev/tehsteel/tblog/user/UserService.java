package dev.tehsteel.tblog.user;

import dev.tehsteel.tblog.user.model.Role;
import dev.tehsteel.tblog.user.model.User;
import dev.tehsteel.tblog.user.model.request.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	public User insertUser(final UserRegisterRequest userRegisterRequest) {
		if (userRepository.findByEmail(userRegisterRequest.email()).isPresent()) {
			return null;
		}

		final User user = new User();

		user.setRole(Role.USER);
		user.setEmail(userRegisterRequest.email());
		user.setName(userRegisterRequest.name());

		// Hash & salt password before storing
		user.setPassword(passwordEncoder.encode(userRegisterRequest.password()));

		userRepository.save(user);

		return user;
	}
	
	@Cacheable(value = "userCache", key = "#id")
	public User getUserById(final Long id) {
		LOGGER.debug("Fetching user by id: {}", id);
		return userRepository.findById(id).orElse(null);
	}

	@Cacheable(value = "userCache", key = "#email")
	public User getUserByEmail(final String email) {
		LOGGER.debug("Fetching user by email: {}", email);
		return userRepository.findByEmail(email).orElse(null);
	}
}
