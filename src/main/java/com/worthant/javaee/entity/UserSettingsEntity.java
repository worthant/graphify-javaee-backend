package com.worthant.javaee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user_settings", schema = "s368090")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "settings-sequence-generator")
    @SequenceGenerator(name = "settings-sequence-generator", sequenceName = "user_settings_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    private String theme;
}
