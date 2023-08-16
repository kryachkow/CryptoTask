package com.task.crypto.advisor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "application_user")
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles"})
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "applicationUsers")
    @Setter(AccessLevel.PRIVATE)
    Set<Role> roles = new HashSet<>();

    public Set<Authority> retrieveAuthorities() {
        return roles.stream().flatMap(role -> role.getAuthorities().stream()).collect(Collectors.toSet());
    }
}
