package com.timothybreitenfeldt.templator.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.timothybreitenfeldt.templator.dtos.ProjectFileDto;
import com.timothybreitenfeldt.templator.models.ProjectFile;
import com.timothybreitenfeldt.templator.services.ProjectFileService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/project")
public class ProjectFileController {

    @Autowired
    private ProjectFileService projectFileService;

    @GetMapping("/{projectId}/files")
    public ResponseEntity<List<ProjectFileDto>> getFilesForProject(@PathVariable Integer projectId) {
        List<ProjectFileDto> projectFiles = this.projectFileService.getFilesForProject(projectId);
        return new ResponseEntity<>(projectFiles, HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<ProjectFileDto> createProjectFile(@RequestBody ProjectFileDto request) {
        ProjectFileDto projectFile = this.projectFileService.createProjectFile(request);
        return new ResponseEntity<>(projectFile, HttpStatus.CREATED);
    }

    @PutMapping("/file")
    public ResponseEntity<ProjectFileDto> updateProjectFile(@RequestBody ProjectFileDto request) {
        this.projectFileService.updateProjectFile(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<ProjectFile> deleteProjectFile(@PathVariable Integer id) {
        this.projectFileService.deleteProjectFile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/file/{id}/publish")
    public ResponseEntity<ProjectFile> publishProjectFile(@PathVariable Integer id) {
        this.projectFileService.publishProjectFile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/file/{id}/unpublish")
    public ResponseEntity<ProjectFile> unpublishProjectFile(@PathVariable Integer id) {
        this.projectFileService.unpublishProjectFile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
