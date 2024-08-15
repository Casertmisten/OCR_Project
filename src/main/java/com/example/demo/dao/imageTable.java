package com.example.demo.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "OCRHISTORY")
public class imageTable {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "modelname")
    private String modelname;

    @Column(name = "imagedata")
    private String imagedata;

    @Column(name = "datetime")
    private String datetime;

    @Column(name = "text")
    private String text;

}