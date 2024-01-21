package dev.tehsteel.tblog.user;

import dev.tehsteel.tblog.user.model.User;
import dev.tehsteel.tblog.user.model.request.UserLoginRequest;
import dev.tehsteel.tblog.user.model.request.UserRegisterRequest;
import dev.tehsteel.tblog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private final UserService userService;
	
	@Autowired
	private final AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody final UserRegisterRequest request) {
		final User newUser = userService.insertUser(request);

		if (newUser != null) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {

			return new ResponseEntity<>("Email is already registered.", HttpStatus.CONFLICT);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody final UserLoginRequest request) {
		final Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		if (authenticate.isAuthenticated()) {
			return new ResponseEntity<>(JwtUtil.createToken(userService.getUserByEmail(request.email())), HttpStatus.OK);
		}

		return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final String username = authentication.getName();

		return new ResponseEntity<>(String.format("Hello %s, You are logged in successfully.", userService.getUserByEmail(username).getName()), HttpStatus.OK);
	}


}
