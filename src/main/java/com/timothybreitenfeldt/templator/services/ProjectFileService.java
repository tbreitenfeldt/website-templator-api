package com.timothybreitenfeldt.templator.services;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.timothybreitenfeldt.templator.dtos.ProjectFileDto;
import com.timothybreitenfeldt.templator.exceptions.ArgumentMissingException;
import com.timothybreitenfeldt.templator.exceptions.DeletingFileException;
import com.timothybreitenfeldt.templator.exceptions.InvalidArgumentException;
import com.timothybreitenfeldt.templator.exceptions.MissingRequestBodyException;
import com.timothybreitenfeldt.templator.exceptions.ProjectFileAlreadyExistsException;
import com.timothybreitenfeldt.templator.exceptions.WritingToFileException;
import com.timothybreitenfeldt.templator.mappers.ProjectFileModelDtoMapper;
import com.timothybreitenfeldt.templator.models.Project;
import com.timothybreitenfeldt.templator.models.ProjectFile;
import com.timothybreitenfeldt.templator.repositories.ProjectFileRepository;
import com.timothybreitenfeldt.templator.repositories.ProjectRepository;

@Service
public class ProjectFileService {

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectFileModelDtoMapper projectFileModelDtoMapper;

    @Value("${projects.directory}")
    private String projectsDirectory;

    public List<ProjectFileDto> getFilesForProject(Integer projectId) {
        if (projectId == null) {
            throw new ArgumentMissingException("Project ID cannot be null.");
        }
        if (!this.projectRepository.existsById(projectId)) {
            throw new InvalidArgumentException("Unable to find a project with the ID " + projectId);
        }

        List<ProjectFile> projectFileModels = this.projectFileRepository.findAllByProjectId(projectId);
        List<ProjectFileDto> projectFiles = projectFileModels.stream()
                .map(model -> this.projectFileModelDtoMapper.projectFileModelToProjectFileDto(model))
                .collect(Collectors.toList());
        return projectFiles;
    }

    public ProjectFileDto createProjectFile(ProjectFileDto projectFileDto) {
        if (projectFileDto == null) {
            throw new MissingRequestBodyException("Missing request body for project file.");
        }
        if (projectFileDto.getFilename() == null || projectFileDto.getFilename().isEmpty()) {
            throw new InvalidArgumentException("Missing filename.");
        }
        if (projectFileDto.getPageTitle() == null || projectFileDto.getPageTitle().isEmpty()) {
            throw new InvalidArgumentException("Missing pageTitle.");
        }
        if (projectFileDto.getContent() == null || projectFileDto.getContent().isEmpty()) {
            throw new InvalidArgumentException("Missing content.");
        }
        if (projectFileDto.getProjectId() == null) {
            throw new ArgumentMissingException("Project ID cannot be null.");
        }
        if (!this.projectRepository.existsById(projectFileDto.getProjectId())) {
            throw new InvalidArgumentException("Unable to find project with ID " + projectFileDto.getProjectId());
        }
        if (this.projectFileRepository.existsByFilenameAndProjectId(projectFileDto.getFilename(),
                projectFileDto.getProjectId())) {
            throw new ProjectFileAlreadyExistsException("A project file with the name " + projectFileDto.getFilename()
                    + " already exists in the project which has an id of " + projectFileDto.getProjectId());
        }

        projectFileDto.setCreatedOn(null);
        projectFileDto.setUpdatedOn(null);

        ProjectFile projectFileModel = this.projectFileModelDtoMapper.projectFileDtoToProjectFileModel(projectFileDto);
        LocalDateTime createdOn = LocalDateTime.now();
        projectFileModel.setCreatedOn(createdOn);
        ProjectFile projectFileModelResult = this.projectFileRepository.save(projectFileModel);
        ProjectFileDto projectFileDtoResult = this.projectFileModelDtoMapper
                .projectFileModelToProjectFileDto(projectFileModelResult);
        return projectFileDtoResult;
    }

    public void updateProjectFile(ProjectFileDto projectFileDto) {
        if (projectFileDto == null) {
            throw new MissingRequestBodyException("Missing request body for project file.");
        }
        if (projectFileDto.getId() == null) {
            throw new ArgumentMissingException("Missing id");
        }

        projectFileDto.setCreatedOn(null);
        projectFileDto.setUpdatedOn(null);
        projectFileDto.setProjectId(null);

        ProjectFile projectFileModel = this.projectFileRepository.findById(projectFileDto.getId()).orElseThrow(
                () -> new InvalidArgumentException("Unable to find project file with id of " + projectFileDto.getId()));

        this.projectFileModelDtoMapper.updateProjectFileModelFromDto(projectFileDto, projectFileModel);
        LocalDateTime updatedOn = LocalDateTime.now();
        projectFileModel.setUpdatedOn(updatedOn);
        this.projectFileRepository.save(projectFileModel);
    }

    public void deleteProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("Missing id");
        }
        if (!this.projectFileRepository.existsById(id)) {
            throw new InvalidArgumentException("Unable to find project file with id of " + id);
        }

        this.projectFileRepository.deleteById(id);
    }

    public void publishProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("Missing id");
        }

        ProjectFile projectFile = this.projectFileRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project file with id of " + id));
        Project project = this.projectRepository.findById(projectFile.getProject().getId())
                .orElseThrow(() -> new InvalidArgumentException(
                        "Unable to find project with the ID " + projectFile.getProject().getId()));

        try {
            Path mainProjectDirectoryPath = Paths.get(this.projectsDirectory);
            Path projectDirectoryPath = mainProjectDirectoryPath.resolve(project.getName());
            Path projectFilePath = projectDirectoryPath.resolve(projectFile.getFilename());
            File mainProjectDirectory = mainProjectDirectoryPath.toFile();
            File projectDirectory = projectDirectoryPath.toFile();
            File file = projectFilePath.toFile();

            if (!mainProjectDirectory.exists()) {
                mainProjectDirectory.mkdir();
            }
            if (!projectDirectory.exists()) {
                projectDirectory.mkdir();
            }
            if (file.exists()) {
                file.createNewFile();
            }

            PrintStream fout = new PrintStream(file);
            fout.println(projectFile.getContent());
            fout.flush();
            fout.close();
        } catch (Exception e) {
            throw new WritingToFileException(e.getMessage());
        }
    }

    public void unpublishProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("Missing id");
        }

        ProjectFile projectFile = this.projectFileRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project file with id of " + id));
        Project project = this.projectRepository.findById(projectFile.getProject().getId())
                .orElseThrow(() -> new InvalidArgumentException(
                        "Unable to find project with the ID " + projectFile.getProject().getId()));

        try {
            Path projectDirectoryPath = Paths.get(this.projectsDirectory);
            Path projectFilePath = projectDirectoryPath.resolve(project.getName()).resolve(projectFile.getFilename());
            File fo = projectFilePath.toFile();
            fo.delete();
        } catch (Exception e) {
            throw new DeletingFileException(e.getMessage());
        }
    }

}
