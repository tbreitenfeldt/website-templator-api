package com.timothybreitenfeldt.templator.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @NotNull
    @Size(min = 3, max = 50)
    private String filename;

    @Column
    @NotNull
    @Size(min = 3, max = 50)
    private String pageTitle;

    @Column
    @NotNull
    @Lob
    private String content;

    @ManyToOne
    @NotNull
    @JsonIgnore
    private Project project;

}
