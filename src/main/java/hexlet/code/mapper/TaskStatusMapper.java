package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusCreateDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskStatusMapper {

    public abstract TaskStatus map(TaskStatusDto taskStatusDto);

    public abstract TaskStatusDto map(TaskStatus taskStatus);

    public abstract TaskStatus map(TaskStatusCreateDto taskStatusCreateDto);

    public abstract void map(TaskStatusUpdateDto taskStatusCreateDto, @MappingTarget TaskStatus taskStatus);

}
