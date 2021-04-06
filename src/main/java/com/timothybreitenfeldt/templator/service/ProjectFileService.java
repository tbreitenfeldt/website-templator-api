package com.timothybreitenfeldt.templator.service;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.timothybreitenfeldt.templator.dto.ProjectFileDto;
import com.timothybreitenfeldt.templator.exception.ArgumentMissingException;
import com.timothybreitenfeldt.templator.exception.DeletingFileException;
import com.timothybreitenfeldt.templator.exception.InvalidArgumentException;
import com.timothybreitenfeldt.templator.exception.MissingRequestBodyException;
import com.timothybreitenfeldt.templator.exception.ProjectFileAlreadyExistsException;
import com.timothybreitenfeldt.templator.exception.WritingToFileException;
import com.timothybreitenfeldt.templator.mapper.ProjectFileModelDtoMapper;
import com.timothybreitenfeldt.templator.model.Project;
import com.timothybreitenfeldt.templator.model.ProjectFile;
import com.timothybreitenfeldt.templator.repository.ProjectFileRepository;
import com.timothybreitenfeldt.templator.repository.ProjectRepository;

@Service
public class ProjectFileService {

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectFileModelDtoMapper projectFileModelDtoMapper;

    @Value("${app.timezone}")
    private String timezone;

    @Value("${app.projects.directory}")
    private String projectsDirectory;

    public List<ProjectFileDto> getFilesForProject(Integer projectId) {
        if (projectId == null) {
            throw new ArgumentMissingException("Project ID cannot be null.");
        }
        if (!this.projectRepository.existsById(projectId)) {
            throw new InvalidArgumentException("Unable to find a project with the ID " + projectId);
        }

        List<ProjectFile> projectFileModels = this.projectFileRepository.findAllByProjectId(projectId);
        Project project = this.projectRepository.findById(projectId).get();
        final String projectName = project.getName();

        List<ProjectFileDto> projectFiles = projectFileModels.stream().map(model -> {
            ProjectFileDto dto = this.projectFileModelDtoMapper.projectFileModelToProjectFileDto(model);
            boolean isPublished = this.isFilePublished(projectName, dto.getFilename());
            dto.setPublished(isPublished);
            return dto;
        }).collect(Collectors.toList());

        return projectFiles;
    }

    public ProjectFileDto getProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("Missing id");
        }

        ProjectFile projectFileModel = this.projectFileRepository.findById(id)
                .orElseThrow(() -> new InvalidArgumentException("Unable to find project file with id of " + id));
        Integer projectId = projectFileModel.getProject().getId();
        Project project = this.projectRepository.findById(projectId).get();
        final String projectName = project.getName();
        ProjectFileDto projectFileDto = this.projectFileModelDtoMapper
                .projectFileModelToProjectFileDto(projectFileModel);
        boolean isPublished = this.isFilePublished(projectName, projectFileDto.getFilename());
        projectFileDto.setPublished(isPublished);
        return projectFileDto;
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
        if (projectFileDto.getContent() == null) {
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
        ZonedDateTime createdOn = ZonedDateTime.now(ZoneId.of(this.timezone));
        projectFileModel.setCreatedOn(createdOn);
        ProjectFile projectFileModelResult = this.projectFileRepository.save(projectFileModel);
        Integer projectId = projectFileModelResult.getProject().getId();
        Project project = this.projectRepository.findById(projectId).get();
        final String projectName = project.getName();
        ProjectFileDto projectFileDtoResult = this.projectFileModelDtoMapper
                .projectFileModelToProjectFileDto(projectFileModelResult);
        boolean isPublished = this.isFilePublished(projectName, projectFileDtoResult.getFilename());
        projectFileDtoResult.setPublished(isPublished);
        return projectFileDtoResult;
    }

    public ProjectFileDto updateProjectFile(ProjectFileDto projectFileDto) {
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
        ZonedDateTime updatedOn = ZonedDateTime.now(ZoneId.of(this.timezone));
        projectFileModel.setUpdatedOn(updatedOn);
        ProjectFile projectFileModelResult = this.projectFileRepository.save(projectFileModel);
        Integer projectId = projectFileModelResult.getProject().getId();
        Project project = this.projectRepository.findById(projectId).get();
        final String projectName = project.getName();
        ProjectFileDto projectFileDtoResult = this.projectFileModelDtoMapper
                .projectFileModelToProjectFileDto(projectFileModelResult);
        boolean isPublished = this.isFilePublished(projectName, projectFileDtoResult.getFilename());
        projectFileDtoResult.setPublished(isPublished);
        return projectFileDtoResult;
    }

    public void deleteProjectFile(Integer id) {
        if (id == null) {
            throw new ArgumentMissingException("Missing id");
        }
        if (!this.projectFileRepository.existsById(id)) {
            throw new InvalidArgumentException("Unable to find project file with id of " + id);
        }

        this.unpublishProjectFile(id);
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

        final String content = "<!doctype html>\n" + "<html lang=\"en\">\n" + "<head>\n"
                + "  <meta charset=\"utf-8\" />\n"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" + "  <title>"
                + projectFile.getPageTitle() + "</title>\n" + "</head>\n" + "<body>\n" + projectFile.getContent() + "\n"
                + "</body>\n" + "</html>";

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
                file.delete();
                file.createNewFile();
            }

            PrintStream fout = new PrintStream(file);
            fout.println(content);
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

    private boolean isFilePublished(String projectName, String filename) {
        if (projectName == null) {
            throw new IllegalArgumentException("projectName cannot be null.");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }

        Path filePath = Paths.get(this.projectsDirectory).resolve(projectName).resolve(filename);
        File file = filePath.toFile();
        return file.exists();
    }

}
