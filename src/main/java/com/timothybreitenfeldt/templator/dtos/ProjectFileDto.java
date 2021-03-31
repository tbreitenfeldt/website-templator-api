package com.timothybreitenfeldt.templator.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFileDto {

    private Integer id;
    private String filename;
    private String pageTitle;
    private String content;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private Integer projectId;

}
