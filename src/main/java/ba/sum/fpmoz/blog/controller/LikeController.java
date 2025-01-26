package ba.sum.fpmoz.blog.controller;

import ba.sum.fpmoz.blog.model.Like;
import ba.sum.fpmoz.blog.model.Post;
import ba.sum.fpmoz.blog.model.User;
import ba.sum.fpmoz.blog.model.UserDetails;
import ba.sum.fpmoz.blog.repositories.LikeRepository;
import ba.sum.fpmoz.blog.repositories.PostRepository;
import ba.sum.fpmoz.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class LikeController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @PostMapping("/like")
    @ResponseBody
    public ResponseEntity<?> likePost(@RequestParam Long postId, Authentication authentication) {

        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email);

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.badRequest().body("Post not found");
        }
        Post post = optionalPost.get();

        Optional<Like> existingLike = likeRepository.findByUserAndPost(currentUser, post);

        Map<String, Object> response = new HashMap<>();
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.removeLike(existingLike.get());
            response.put("liked", false);
        } else {
            Like like = new Like(currentUser, post);
            likeRepository.save(like);
            post.addLike(like);
            response.put("liked", true);
        }

        postRepository.save(post);

        long likeCount = likeRepository.countByPost(post);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}