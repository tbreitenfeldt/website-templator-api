package com.timothybreitenfeldt.templator.models;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "filename", "project_id" }) })
@Getter
@Setter
@AllArgsConstructor
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

    @Column(nullable = false, updatable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime createdOn;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime updatedOn;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Project project;

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProjectFile)) {
            return false;
        }
        ProjectFile other = (ProjectFile) obj;
        return this.id != null && Objects.equals(this.id, other.id);
    }

}
