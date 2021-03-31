package com.timothybreitenfeldt.templator.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.timothybreitenfeldt.templator.dtos.ProjectDto;
import com.timothybreitenfeldt.templator.exceptions.ArgumentMissingException;
import com.timothybreitenfeldt.templator.exceptions.InvalidArgumentException;
import com.timothybreitenfeldt.templator.exceptions.MissingRequestBodyException;
import com.timothybreitenfeldt.templator.exceptions.ProjectAlreadyExistsException;
import com.timothybreitenfeldt.templator.mappers.ProjectModelDtoMapper;
import com.timothybreitenfeldt.templator.models.Project;
import com.timothybreitenfeldt.templator.repositories.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectModelDtoMapper projectModelDtoMapper;

    public List<ProjectDto> getProjects() {
        List<Project> projectModels = this.projectRepository.findAll();
        List<ProjectDto> projects = projectModels.stream()
                .map(model -> this.projectModelDtoMapper.projectModelToProjectDto(model)).collect(Collectors.toList());
        return projects;
    }

    public ProjectDto getProject(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("ID can not be null.");
        }

        Project projectModel = this.projectRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project ID " + id));
        ProjectDto project = this.projectModelDtoMapper.projectModelToProjectDto(projectModel);
        return project;
    }

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new MissingRequestBodyException("project cannot be null.");
        }
        if (projectDto.getName() == null || projectDto.getName().isEmpty()) {
            throw new ArgumentMissingException("Name cannot be null or empty.");
        }
        if (this.projectRepository.findByName(projectDto.getName()).isPresent()) {
            throw new ProjectAlreadyExistsException(
                    "A project already exists with that name, please choose another project name.");
        }

        Project projectModel = this.projectModelDtoMapper.projectDtoToProjectModel(projectDto);
        Project projectModelResult = this.projectRepository.save(projectModel);
        ProjectDto projectDtoResult = this.projectModelDtoMapper.projectModelToProjectDto(projectModelResult);
        return projectDtoResult;
    }

    public void updateProject(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new MissingRequestBodyException("project cannot be null.");
        }
        if (projectDto.getId() == null) {
            throw new ArgumentMissingException("Missing ID");
        }

        Project projectModel = this.projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project ID " + projectDto.getId()));

        if (projectDto.getName() != null && !projectDto.getName().equals(projectModel.getName())
                && this.projectRepository.findByName(projectDto.getName()).isPresent()) {
            throw new ProjectAlreadyExistsException(
                    "A project already exists with that name, please choose another project name.");
        }

        this.projectModelDtoMapper.updateProjectModelFromDto(projectDto, projectModel);
        this.projectRepository.save(projectModel);
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
