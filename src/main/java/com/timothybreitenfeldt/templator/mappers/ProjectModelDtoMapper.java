package com.timothybreitenfeldt.templator.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.timothybreitenfeldt.templator.dtos.ProjectDto;
import com.timothybreitenfeldt.templator.models.Project;

@Mapper(componentModel = "spring")
public interface ProjectModelDtoMapper {

    public abstract ProjectDto projectModelToProjectDto(Project model);

    @Mapping(target = "projectFiles", ignore = true)
    public abstract Project projectDtoToProjectModel(ProjectDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "projectFiles", ignore = true)
    public abstract void updateProjectModelFromDto(ProjectDto dto, @MappingTarget Project model);

}
