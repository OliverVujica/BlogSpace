package ba.sum.fpmoz.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naslov ne smije biti prazan.")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Sadržaj ne smije biti prazan.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "photo_url")
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public void addLike(Like like) {
        likes.add(like);
        like.setPost(this);
    }

    public void removeLike(Like like) {
        likes.remove(like);
        like.setPost(null);
    }

    public int getLikeCount() {
        return likes.size();
    }

    public Post() {}

    public Post(String title, String content, User user, String photoUrl) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.photoUrl = photoUrl;
    }

    public Post(Long id, String title, String content, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Naslov ne smije biti prazan.") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Naslov ne smije biti prazan.") String title) {
        this.title = title;
    }

    public @NotBlank(message = "Sadržaj ne smije biti prazan.") String getContent() {
        return content;
    }

    public void setContent(@NotBlank(message = "Sadržaj ne smije biti prazan.") String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
