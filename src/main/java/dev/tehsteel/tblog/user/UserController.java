package dev.tehsteel.tblog.user;

import com.google.common.cache.Cache;
import dev.tehsteel.tblog.user.model.User;
import dev.tehsteel.tblog.user.model.request.UserLoginRequest;
import dev.tehsteel.tblog.user.model.request.UserRegisterRequest;
import dev.tehsteel.tblog.user.model.verification.UserVerification;
import dev.tehsteel.tblog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

	private final Cache<UUID, UserVerification> userVerificationCache;
	private final UserService userService;
	private final AuthenticationManager authenticationManager;

	/* Register a new user request */
	@PutMapping("/register")
	public ResponseEntity<String> register(@RequestBody final UserRegisterRequest request) {
		final User newUser = userService.insertUser(request);

		if (newUser != null) {
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {

			return new ResponseEntity<>("Email is already registered.", HttpStatus.CONFLICT);
		}
	}


	@PostMapping("/verify")
	public ResponseEntity<String> verify(@RequestParam("token") final UUID token) {
		final UserVerification userVerification = userVerificationCache.getIfPresent(token);
		if (userVerification == null) {
			return new ResponseEntity<>("Verification token is invalid.", HttpStatus.NOT_FOUND);
		}


		if (userVerification.isCodeExpired()) {
			return new ResponseEntity<>("Too late buddy.", HttpStatus.FORBIDDEN);
		}

		final User user = userService.getUserById(userVerification.getUserId());

		user.setVerified(true);
		user.setEnabled(true);

		userService.updateUser(user);

		return null;
	}


	/* Login as a user, Receives JWT token for stateless loggings */
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody final UserLoginRequest request) {
		final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		if (authenticate.isAuthenticated()) {
			return new ResponseEntity<>(JwtUtil.createToken(userService.getUserByEmail(request.email())), HttpStatus.OK);
		}

		return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
	}

	/* A test request to validate that everything is ok */
	@GetMapping("/test")
	public ResponseEntity<String> test() {
		/* Get current user email by jwt */
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final String username = authentication.getName();

		return new ResponseEntity<>(String.format("Hello %s, You are logged in successfully.", userService.getUserByEmail(username).getName()), HttpStatus.OK);
	}


}
