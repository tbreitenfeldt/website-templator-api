package com.timothybreitenfeldt.templator.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.timothybreitenfeldt.templator.dtos.ProjectFileDto;
import com.timothybreitenfeldt.templator.models.ProjectFile;

@Mapper(componentModel = "spring")
public interface ProjectFileModelDtoMapper {

    @Mapping(target = "projectId", source = "project.id")
    public abstract ProjectFileDto projectFileModelToProjectFileDto(ProjectFile model);

    @Mapping(target = "project.id", source = "projectId")
    public abstract ProjectFile projectFileDtoToProjectFileModel(ProjectFileDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "project.id", source = "projectId")
    public abstract void updateProjectFileModelFromDto(ProjectFileDto dto, @MappingTarget ProjectFile model);

}
