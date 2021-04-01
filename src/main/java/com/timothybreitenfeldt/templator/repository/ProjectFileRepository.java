package com.timothybreitenfeldt.templator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.timothybreitenfeldt.templator.model.ProjectFile;

@Repository
public interface ProjectFileRepository extends JpaRepository<ProjectFile, Integer> {

    public abstract List<ProjectFile> findAllByProjectId(Integer projectId);

    public abstract boolean existsByFilenameAndProjectId(String filename, Integer projectId);

}
