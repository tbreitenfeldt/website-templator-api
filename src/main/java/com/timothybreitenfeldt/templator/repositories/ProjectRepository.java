package com.timothybreitenfeldt.templator.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.timothybreitenfeldt.templator.models.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    public abstract Optional<Project> findByName(String name);

}
