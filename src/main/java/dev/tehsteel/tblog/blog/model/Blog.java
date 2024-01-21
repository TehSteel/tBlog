package dev.tehsteel.tblog.blog.model;

import dev.tehsteel.tblog.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "blogs")
public class Blog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String text;

	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User poster;

	private Date lastUpdated = new Date();
	private Date creationDate = new Date();
}
