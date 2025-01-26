package ba.sum.fpmoz.blog.controller;

import ba.sum.fpmoz.blog.model.Comment;
import ba.sum.fpmoz.blog.model.Post;
import ba.sum.fpmoz.blog.model.User;
import ba.sum.fpmoz.blog.model.UserDetails;
import ba.sum.fpmoz.blog.repositories.CommentRepository;
import ba.sum.fpmoz.blog.repositories.PostRepository;
import ba.sum.fpmoz.blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private UserRepository userRepo;

    private static String UPLOADED_FOLDER = "./uploads/";

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepo.findAllByOrderByCreatedAtDesc());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails user = (UserDetails) auth.getPrincipal();
            model.addAttribute("user", user);
        }

        return "posts/list";
    }

    @GetMapping("/new")
    public String showNewPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "posts/new";
    }

    @PostMapping
    public String createPost(@ModelAttribute Post post,
                             @RequestParam("photo") MultipartFile photo,
                             Authentication authentication) throws IOException {
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        post.setUser(user);

        post.setCreatedAt(new Date());

        if (photo != null && !photo.isEmpty()) {

            String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            Path path = Paths.get(UPLOADED_FOLDER + fileName);

            Files.createDirectories(path.getParent());

            Files.copy(photo.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            post.setPhotoUrl("/uploads/" + fileName);
        }

        postRepo.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Post> post = postRepo.findById(id);
        if (post.isEmpty() || !post.get().getUser().getEmail().equals(authentication.getName())) {
            return "redirect:/posts";
        }
        model.addAttribute("post", post.get());
        return "posts/edit";
    }

    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute Post post,
                             @RequestParam(value = "photo", required = false) MultipartFile photo,
                             Authentication authentication) throws IOException {
        Optional<Post> existingPost = postRepo.findById(id);
        if (existingPost.isPresent() && existingPost.get().getUser().getEmail().equals(authentication.getName())) {
            Post updatedPost = existingPost.get();
            updatedPost.setTitle(post.getTitle());
            updatedPost.setContent(post.getContent());

            if (photo != null && !photo.isEmpty()) {
                if (updatedPost.getPhotoUrl() != null) {
                    Path oldPath = Paths.get(UPLOADED_FOLDER + updatedPost.getPhotoUrl().replace("/uploads/", ""));
                    Files.deleteIfExists(oldPath);
                }

                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                Path path = Paths.get(UPLOADED_FOLDER + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(photo.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                updatedPost.setPhotoUrl("/uploads/" + fileName);
            }

            postRepo.save(updatedPost);
        }
        return "redirect:/posts";
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id, Authentication authentication) {
        Optional<Post> post = postRepo.findById(id);
        if (post.isPresent() && (post.get().getUser().getEmail().equals(authentication.getName()) ||
                authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))) {
            postRepo.delete(post.get());
        }
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Optional<Post> post = postRepo.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            model.addAttribute("comments", commentRepo.findByPostIdOrderByCreatedAtDesc(id));
            return "posts/view";
        }
        return "redirect:/posts";
    }

    @Autowired
    private CommentRepository commentRepo;

    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam("content") String content,
                             Authentication authentication) {
        Optional<Post> postOptional = postRepo.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            String email = authentication.getName();
            User user = userRepo.findByEmail(email);

            Comment comment = new Comment(content, post, user);
            commentRepo.save(comment);
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, Authentication authentication) {
        Optional<Comment> commentOptional = commentRepo.findById(commentId);

        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            if (comment.getUser().getEmail().equals(authentication.getName())) {
                Long postId = comment.getPost().getId();
                commentRepo.delete(comment);
                return "redirect:/posts/" + postId;
            }
        }
        return "redirect:/posts";
    }

}
