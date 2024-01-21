package dev.tehsteel.tblog;

import dev.tehsteel.tblog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan
public class BlogApplication implements CommandLineRunner {
	@Autowired
	private Environment environment;

	public static void main(final String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@Override
	public void run(final String... args) {
		/* Setting the JWT private key on runtime */
		Constants.KEY = environment.getProperty("spring.security.jwt.private-key");
		JwtUtil.loadKey();
	}
}
