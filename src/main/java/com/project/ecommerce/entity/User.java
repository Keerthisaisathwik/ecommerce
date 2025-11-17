package com.project.ecommerce.entity;

import com.project.ecommerce.enums.AuthProviderType;
import com.project.ecommerce.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = true)
    private String password;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProviderType providerType;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String verificationToken;

    private Boolean isVerified = false;

    @Column(name = "reset_token")
    private String resetToken;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
