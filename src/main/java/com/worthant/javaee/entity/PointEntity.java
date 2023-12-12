package com.worthant.javaee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a result entity for storing point data.
 * This entity is mapped to a database table 'point_model' within the schema 's368090'.
 * It includes information about the point coordinates (x, y), radius (r) and whether the point is within a certain area (result).
 */
@Data
@Entity
@Table(name = "point_model", schema = "s368090")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "point-sequence-generator")
    @SequenceGenerator(name = "point-sequence-generator", sequenceName = "point_model_id_seq", allocationSize = 1)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserEntity user;

    private double x;
    private double y;
    private double r;
    private boolean result;
}

