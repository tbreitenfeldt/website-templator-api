package com.timothybreitenfeldt.templator.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.timothybreitenfeldt.templator.exceptions.ArgumentMissingException;
import com.timothybreitenfeldt.templator.exceptions.InvalidArgumentException;
import com.timothybreitenfeldt.templator.exceptions.MissingRequestBodyException;
import com.timothybreitenfeldt.templator.exceptions.ProjectAlreadyExistsException;
import com.timothybreitenfeldt.templator.models.Project;
import com.timothybreitenfeldt.templator.repositories.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getProjects() {
        List<Project> projects = this.projectRepository.findAll();
        return projects;
    }

    public Project getProject(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("ID can not be null.");
        }

        Project project = this.projectRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project ID " + id));
        return project;
    }

    public Project createProject(Project request) {
        if (request == null) {
            throw new MissingRequestBodyException("project cannot be null.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ArgumentMissingException("Name cannot be null or empty.");
        }
        if (this.projectRepository.findByName(request.getName()).isPresent()) {
            throw new ProjectAlreadyExistsException(
                    "A project already exists with that name, please choose another project name.");
        }

        Project project = this.projectRepository.save(request);
        return project;
    }

    public void deleteProject(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("ID can not be null.");
        }
        if (!this.projectRepository.existsById(id)) {
            throw new InvalidArgumentException("Unable to find project with ID " + id);
        }

        this.projectRepository.deleteById(id);
    }

}
