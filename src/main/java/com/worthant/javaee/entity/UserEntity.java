package com.worthant.javaee.entity;

import com.worthant.javaee.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users", schema = "s368090")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user-sequence-generator")
    @SequenceGenerator(name = "user-sequence-generator", sequenceName = "users_id_seq", allocationSize = 1)
    private long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}

