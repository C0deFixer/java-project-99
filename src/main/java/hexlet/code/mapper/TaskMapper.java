package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @Autowired
    private LabelRepository labelRepository;


    @Mapping(source = "assigneeId", target = "assignee.id")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "getLabelsById")
    public abstract Task map(TaskCreateDto dto);

    @Mapping(source = "assigneeId", target = "assignee.id")
    @Mapping(source = "taskStatusId", target = "taskStatus.id")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "getLabelsById")
    public abstract Task map(TaskDto taskDto);

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.id", target = "taskStatusId")
    @Mapping(source = "labels", target = "labels", qualifiedByName = "setToListLabelsId")
    public abstract TaskDto map(Task task);

    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "getUserById")
    @Mapping(target = "taskStatus", source = "taskStatusId", qualifiedByName = "getTaskStatusById")
    @Mapping(target = "labels", source = "labelsId", qualifiedByName = "getLabelsById")
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

    @Named("setToListLabelsId")
    public List<Long> setToListLabelsId(Set<Label> set){
        return set.stream().map(Label::getId).toList();
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

    @Named("getLabelsById")
    public Set<Label> getLabelsById(List<Long> list) {
        if (list == null) {
            return new HashSet<>();
        } else {
            //TODO replace with SimpleJpaRepository
            //TODO check if all Labels by id was found
            return labelRepository.findAllByIdIn(list.stream().filter(Objects::nonNull).toList());
        }
    }
}
