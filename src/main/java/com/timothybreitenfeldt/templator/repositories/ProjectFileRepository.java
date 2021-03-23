package com.timothybreitenfeldt.templator.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.timothybreitenfeldt.templator.models.ProjectFile;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Integer> {

    public abstract List<ProjectFile> findAllByProjectId(Integer projectId);

}
