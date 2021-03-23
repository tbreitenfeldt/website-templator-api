package com.timothybreitenfeldt.templator.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @Column(nullable = true)
    @Null
    @Size(min = 0, max = 150)
    private String description;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private Set<ProjectFile> projectFiles = new HashSet<>();

}
