package com.levantrung.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String username;
    String password;
    String firstname;
    String lastname;
    String email;
    String phone;
    String address;

    String roles;

    public Set<String> getRolesAsSet() {
        return (roles == null || roles.isEmpty()) ? new HashSet<>() : new HashSet<>(Arrays.asList(roles.split(",")));
    }

    public void setRolesFromSet(Set<String> rolesSet) {
        this.roles = (rolesSet == null || rolesSet.isEmpty()) ? "USER" : String.join(",", rolesSet);
    }
}
