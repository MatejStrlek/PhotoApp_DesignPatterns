package hr.algebra.photoapp_designpatterns_galic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Column(name = "role_user")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "package_type")
    @Enumerated(EnumType.STRING)
    private PackageType packageType;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    public User(String email, String password, Role role, PackageType packageType) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.packageType = packageType;
    }
}