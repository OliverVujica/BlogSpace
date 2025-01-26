package ba.sum.fpmoz.blog.repositories;

import ba.sum.fpmoz.blog.model.Post;
import ba.sum.fpmoz.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findAllByOrderByCreatedAtDesc();
}
