package ba.sum.fpmoz.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 2, max = 50, message = "Polje mora sadržavati barem 2 znaka i manje od 50 znakova.")
    @Column(nullable = false, length = 50)
    private String firstname;

    @Size(min = 2, max = 50, message = "Polje mora sadržavati barem 2 znaka i manje od 50 znakova.")
    @Column(nullable = false, length = 50)
    private String lastname;
    @NotBlank(message="Molimo unesite Vašu email adresu.")
    @Email(message = "Unesite ispravnu email adresu.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message="Molimo unesite Vašu lozinku.")
    @Column(nullable = false)
    private String password;

    @NotBlank(message="Molimo ponovite Vašu lozinku.")
    @Transient
    private String passwordRepeat;

    @Column(nullable = false)
    private String role;

    public User(Long id, String firstname, String lastname, String email, String password, String role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
}
