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
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "assignee_id", target = "assignee")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "getTaskStatusBySlug")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabelsByIds")
    public abstract Task map(TaskCreateDto dto);

    @Mapping(source = "assignee.id", target = "assignee_id")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "setToListLabelsId")
    public abstract TaskDto map(Task task);

    @Mapping(source = "assignee_id", target = "assignee")
    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "getTaskStatusBySlug")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabelsByIds")
    public abstract Task map(TaskDto taskDto);

    //@Mapping(target = "assignee", source = "assignee_id", qualifiedByName = "getUserById")
    @Mapping(target = "name", source = "title") //Reference mapper using
    @Mapping(target = "description", source = "content")
    @Mapping(target = "assignee", source = "assignee_id")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "getTaskStatusBySlug")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabelsByIds")
    public abstract void update(TaskUpdateDto dto, @MappingTarget Task model);

//    @Named("getUserById")
//    public User getUserById(Long id) {
//        if (id == null) {
//            return null;
//        } else {
//            return userRepository.findById(id)
//                    .orElseThrow(() ->
//                            new ResourceNotFoundException(String.format("User with id %s not found", id)));
//        }
//    }

    @Named("setToListLabelsId")
    public List<Long> setToListLabelsId(Set<Label> set){
        return set.stream().map(Label::getId).toList();
    }


    @Named("getTaskStatusBySlug")
    public TaskStatus getTaskStatusBySlug(String slug) {
        if (slug == null) {
            return null;
        } else {
            return taskStatusRepository.findBySlug(slug)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(String.format("Task Status with slug %s not found", slug)));
        }
    }

    @Named("getLabelsByIds")
    public Set<Label> getLabelsByIds(List<Long> list) {
        if (list == null) {
            return new HashSet<>();
        } else {
            //TODO replace with SimpleJpaRepository
            //TODO check if all Labels by id was found
            return labelRepository.findAllByIdIn(list.stream().filter(Objects::nonNull).toList());
        }
    }
}
