package com.timothybreitenfeldt.templator.services;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.timothybreitenfeldt.templator.exceptions.ArgumentMissingException;
import com.timothybreitenfeldt.templator.exceptions.DeletingFileException;
import com.timothybreitenfeldt.templator.exceptions.InvalidArgumentException;
import com.timothybreitenfeldt.templator.exceptions.MissingRequestBodyException;
import com.timothybreitenfeldt.templator.exceptions.WritingToFileException;
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

    @Value("${projects.directory}")
    private String projectsDirectory;

    public List<ProjectFile> getFilesForProject(Integer projectId) {
        if (projectId == null) {
            throw new ArgumentMissingException("Project ID cannot be null.");
        }
        if (!this.projectRepository.existsById(projectId)) {
            throw new InvalidArgumentException("Unable to find a project with the ID " + projectId);
        }

        List<ProjectFile> projectFiles = this.projectFileRepository.findAllByProjectId(projectId);
        return projectFiles;
    }

    public ProjectFile createProjectFile(ProjectFile request) {
        if (request == null) {
            throw new MissingRequestBodyException("Missing request body for project file.");
        }
        if (request.getFilename() == null || request.getFilename().isBlank()) {
            throw new InvalidArgumentException("Missing filename.");
        }
        if (request.getPageTitle() == null || request.getPageTitle().isBlank()) {
            throw new InvalidArgumentException("Missing pageTitle.");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new InvalidArgumentException("Missing content.");
        }
        if (request.getProject() == null || request.getProject().getId() == null) {
            throw new ArgumentMissingException("Project ID cannot be null.");
        }
        if (!this.projectRepository.existsById(request.getProject().getId())) {
            throw new InvalidArgumentException("Unable to find project with ID " + request.getProject().getId());
        }

        ProjectFile projectFile = this.projectFileRepository.save(request);
        return projectFile;
    }

    public void updateProjectFile(ProjectFile request) {
        if (request == null) {
            throw new MissingRequestBodyException("Missing request body for project file.");
        }
        if (request.getId() == null) {
            throw new ArgumentMissingException("ID cannot be null.");
        }
        if (!this.projectFileRepository.existsById(request.getId())) {
            throw new InvalidArgumentException("Unable to find project file with the ID " + request.getId());
        }
        if (request.getFilename() == null || request.getFilename().isBlank()) {
            throw new InvalidArgumentException("Missing filename.");
        }
        if (request.getPageTitle() == null || request.getPageTitle().isBlank()) {
            throw new InvalidArgumentException("Missing pageTitle.");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new InvalidArgumentException("Missing content.");
        }

        this.projectFileRepository.save(request);
    }

    public void deleteProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("ID cannot be null.");
        }
        if (!this.projectFileRepository.existsById(id)) {
            throw new InvalidArgumentException("Unable to find project file with the ID " + id);
        }

        this.projectFileRepository.deleteById(id);
    }

    public void publishProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("ID cannot be null.");
        }

        ProjectFile projectFile = this.projectFileRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project file with the ID " + id));
        Integer projectId = projectFile.getProject().getId();
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project with the ID " + projectId));

        try {
            Path projectDirectoryPath = Paths.get(this.projectsDirectory);
            Path projectFilePath = projectDirectoryPath.resolve(project.getName()).resolve(projectFile.getFilename());
            File fo = projectFilePath.toFile();
            PrintStream fout = new PrintStream(fo);
            fout.println(projectFile.getContent());
            fout.flush();
            fout.close();
        } catch (Exception e) {
            throw new WritingToFileException(e.getMessage());
        }
    }

    public void unpublishProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("ID cannot be null.");
        }

        ProjectFile projectFile = this.projectFileRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project file with the ID " + id));
        Integer projectId = projectFile.getProject().getId();
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project with the ID " + projectId));

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
