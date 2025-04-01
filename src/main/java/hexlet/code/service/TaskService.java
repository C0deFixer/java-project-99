package hexlet.code.service;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private TaskSpecification specBuilder;

    private static final int OFF_SET = 10;

    public List<TaskDto> getAll(TaskParamsDto taskParamsDto, int page) {
        var spec = specBuilder.build(taskParamsDto);
        Page<Task> tasks = repository.findAll(spec, PageRequest.of(page - 1, OFF_SET));
        var result = tasks.map(mapper::map);
        List<TaskDto> taskList = result.getContent();
        return taskList;
    }

    public TaskDto show(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        return mapper.map(task);
    }

    public TaskDto create(TaskCreateDto dto) {
        Task task = mapper.map(dto);
        repository.save(task);
        return mapper.map(task);
    }

    public TaskDto update(TaskUpdateDto dto, Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        mapper.update(dto, task);
        repository.save(task);
        return mapper.map(task);
    }

    public void delete(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        repository.deleteById(id);
    }

}
