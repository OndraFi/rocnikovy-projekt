package cz.upce.fei.redsys.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "ix_users_username", columnList = "username", unique = true),
                @Index(name = "ix_users_email", columnList = "email", unique = true)
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String username;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name= "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}