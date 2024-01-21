package dev.tehsteel.tblog.blog;

import dev.tehsteel.tblog.blog.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
