package com.alex.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    @Size(min = 4, max = 30, message = "name should be between 5 and 30 characters")
    private String username;

    @Column (name ="password")
    @Size(min = 5, message = "password should be minimum of 5 characters")
    private String password;

    @Column (name = "email")
    @Email(message = "enter a valid email")
    @NotEmpty(message = "email should not be empty")
    private String email;

    @Column(name = "locked")
    @NotNull
    private boolean locked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "user")
    private List<Task> tasks;


}
