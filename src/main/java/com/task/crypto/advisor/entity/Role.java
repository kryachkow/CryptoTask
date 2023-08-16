package com.task.crypto.advisor.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "role")
@EqualsAndHashCode(exclude = {"authorities", "applicationUsers"})
@ToString(exclude = {"authorities", "applicationUsers"})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    @Setter(AccessLevel.PRIVATE)
    private Set<Authority> authorities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "application_user_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "application_user_id")
    )
    @Setter(AccessLevel.PRIVATE)
    private Set<ApplicationUser> applicationUsers = new HashSet<>();

}
