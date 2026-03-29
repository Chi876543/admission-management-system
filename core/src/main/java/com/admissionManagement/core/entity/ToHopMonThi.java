package com.admissionManagement.core.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "xt_tohop_monthi")
@Data
public class ToHopMonThi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtohop")
    private int idToHop;

    @Column(name = "matohop", length = 45, nullable = false, unique = true)
    private String maToHop;

    @Column(name = "mon1", length = 10, nullable = false)
    private String mon1;

    @Column(name = "mon2", length = 10, nullable = false)
    private String mon2;

    @Column(name = "mon3", length = 10, nullable = false)
    private String mon3;

    @Column(name = "tentohop", length = 100)
    private String tenToHop;
}
