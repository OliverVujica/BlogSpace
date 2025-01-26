package ba.sum.fpmoz.blog.repositories;

import ba.sum.fpmoz.blog.model.Like;
import ba.sum.fpmoz.blog.model.Post;
import ba.sum.fpmoz.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByPost(Post post);
    Optional<Like> findByUserAndPost(User user, Post post);
    Long countByPost(Post post);
}