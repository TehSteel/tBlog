package dev.tehsteel.tblog.config;

import dev.tehsteel.tblog.config.filter.JwtAuthenticationFilter;
import dev.tehsteel.tblog.user.model.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	/* Create a custom UserDetailsService for managing user data */
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
		return http
				.cors(Customizer.withDefaults())
				/* Disable csrf idk why */
				.csrf(AbstractHttpConfigurer::disable)
				/* Allow anyone to access register and login endpoint */
				.authorizeHttpRequests(request ->
						request.requestMatchers("/api/user/register", "/api/user/login").permitAll()
								.anyRequest().authenticated()
				)
				/* Allow JWT stateless token login */
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				/* If there is any error that happening just return unauthorized */
				.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
						.authenticationEntryPoint((request, response, ex) -> {
							LOGGER.info(ex.getMessage());
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
						}))
				/* Add the JWT auth filter before accessing any endpoint */
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		/* Enable recommended password encoding */
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		/* Create a new DaoAuthenticationProvider used for UserDetails implementation */
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		/* Use user details service */
		authenticationProvider.setUserDetailsService(userDetailsService());
		/* Use our password encoder to encode and check passwords*/
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		/* Allow requests only from our current server port 8080 */
		configuration.setAllowedOrigins(List.of("http://localhost:8080"));
		/* Allow post and get request types*/
		configuration.setAllowedMethods(List.of("GET", "POST"));
		/* Allow authorization to authenticate yourself */
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
