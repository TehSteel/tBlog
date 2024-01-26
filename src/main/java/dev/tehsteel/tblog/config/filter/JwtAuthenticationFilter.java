package dev.tehsteel.tblog.config.filter;

import dev.tehsteel.tblog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		/* Here we are checking if there is headers and if there is any headers token */
		if (header == null || !header.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}


		/* Splitting to get the acutal token */
		final String token = header.split(" ")[1].trim();

		/* If token is empty just return */
		if (token.isEmpty()) {
			filterChain.doFilter(request, response);
			return;
		}

		final Claims claims = JwtUtil.getClaims(token);

		/* If the token is expired / null unlucky */
		if (JwtUtil.isExpired(token) || claims == null) {
			filterChain.doFilter(request, response);
			return;
		}

		/* Here we create a UsernamePasswordAuthenticationToken using the jwt email and credentials we set as null since there is no password we loggin as JWT */
		final Authentication authentication = new UsernamePasswordAuthenticationToken(JwtUtil.getClaims(token).getSubject(), null, List.of(new SimpleGrantedAuthority(claims.get("role", String.class))));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
