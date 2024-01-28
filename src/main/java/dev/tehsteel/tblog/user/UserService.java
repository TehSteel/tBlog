package dev.tehsteel.tblog.user;

import com.google.common.cache.Cache;
import dev.tehsteel.tblog.user.model.Role;
import dev.tehsteel.tblog.user.model.User;
import dev.tehsteel.tblog.user.model.request.UserRegisterRequest;
import dev.tehsteel.tblog.user.model.verification.UserActionType;
import dev.tehsteel.tblog.user.model.verification.UserVerification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


	private final UserRepository userRepository;
	private final Cache<UUID, UserVerification> userVerificationCache;
	private final PasswordEncoder passwordEncoder;
	private final MailSender mailSender;

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

		userRepository.save(user);


		final UserVerification verificationCode = new UserVerification(user.getId(), UserActionType.USER_CREATION);
		userVerificationCache.put(verificationCode.getCode(), verificationCode);

		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("admin@tehsteel.dev");
		mailMessage.setTo(user.getEmail());
		mailMessage.setText("Hey there! Please verify your email using this link: localhost:8080/api/user/verify?token=" + verificationCode.getCode());
		mailSender.send(mailMessage);

		log.info("Sending verification code via mail service: {}", verificationCode.getCode());

		return user;
	}

	public void updateUser(final User user) {
		userRepository.save(user);
	}

	/**
	 * Fetch a user by his user id
	 *
	 * @param id The user id to fetch
	 * @return The fetched user or null
	 */
	public User getUserById(final Long id) {
		log.debug("Fetching user by id: {}", id);
		return userRepository.findById(id).orElse(null);
	}


	/**
	 * Fetch a user by his user email
	 *
	 * @param email The user email to fetch
	 * @return The fetched user or null
	 */
	public User getUserByEmail(final String email) {
		log.debug("Fetching user by email: {}", email);
		return userRepository.findByEmail(email).orElse(null);
	}
}
