package com.auth.application.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {

    @Id
    @SequenceGenerator(
            name = "roles_sequence",
            sequenceName = "roles_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "roles_sequence")
    private Long id;

    private String name;

}
