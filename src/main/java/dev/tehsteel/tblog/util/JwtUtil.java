package dev.tehsteel.tblog.util;

import dev.tehsteel.tblog.Constants;
import dev.tehsteel.tblog.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;


/**
 * JwtUtil is a utility class for handling <a href="https://jwt.io/introduction">JWT</a>
 */
public final class JwtUtil {


	/* The key used to encrypt and decrypt */
	private static SecretKey KEY = null;

	/* Private constructor to prevent instantiation */
	private JwtUtil() {
	}

	/* A method used to load the JWT signing key */
	public static void loadKey() {
		KEY = Keys.hmacShaKeyFor(Constants.KEY.getBytes());
	}


	/**
	 * Create a JWT (JSON Web Token) for the specified user.
	 *
	 * @param user The user the JWT token is created for.
	 * @return A JWT string representing the jwt token use getClaims to get all claims.
	 */
	public static String createToken(final User user) {
		final Claims claims = Jwts.claims().subject(user.getEmail()).build();
		final long expMillis = 86400 * 1000 * 5;

		final Date now = new Date();
		final Date exp = new Date(now.getTime() + expMillis);

		return Jwts.builder()
				.claims(claims)
				.subject(user.getEmail())
				.issuedAt(now)
				.expiration(exp)
				.signWith(KEY)
				.compact();
	}

	/**
	 * Retrieve the claims from a JWT.
	 *
	 * @param token The JWT string from which to retrieve claims.
	 * @return The claims extracted from the JWT.
	 * @throws JwtException If there is an issue parsing or verifying the JWT.
	 */
	public static Claims getClaims(final String token) {
		return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * Check whether a JWT has expired.
	 *
	 * @param token The JWT string to check for expiration.
	 * @return true if the JWT has expired, false otherwise.
	 */
	public static boolean isExpired(final String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (final Exception e) {
			return false;
		}
	}
}
