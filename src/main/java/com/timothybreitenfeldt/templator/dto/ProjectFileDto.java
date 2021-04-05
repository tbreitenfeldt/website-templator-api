package com.timothybreitenfeldt.templator.dto;

import java.time.ZonedDateTime;

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
    private ZonedDateTime createdOn;
    private ZonedDateTime updatedOn;
    private boolean published;
    private Integer projectId;

}
