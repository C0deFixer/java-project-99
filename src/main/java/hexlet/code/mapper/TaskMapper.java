package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(source = "assigneeId", target = "assignee.id")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    public abstract Task map(TaskCreateDto dto);

    @Mapping(source = "assigneeId", target = "assignee.id")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    public abstract Task map(TaskDto taskDto);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.id", target = "taskStatusId")
    public abstract TaskDto map(Task task);

    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "getUserById")
    @Mapping(target = "taskStatus", source = "taskStatusId", qualifiedByName = "getTaskStatusById")
    public abstract void update(TaskUpdateDto dto, @MappingTarget Task model);

    @Named("getUserById")
    public User getUserById(Long id) {
        if (id == null) {
            return null;
        } else {
            return userRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(String.format("User with id %s not found", id)));
        }
    }

    @Named("getTaskStatusById")
    public TaskStatus getTaskStatusById(Long id) {
        if (id == null) {
            return null;
        } else {
            return taskStatusRepository.findById(id)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(String.format("Task Status with id %s not found", id)));
        }
    }
}
