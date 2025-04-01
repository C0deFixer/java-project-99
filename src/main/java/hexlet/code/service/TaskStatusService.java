package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskStatusMapper mapper;

    public List<TaskStatusDto> getAll() {
        return repository.findAll().stream().map(mapper::map).toList();
    }

    public TaskStatusDto show(Long id) {
        TaskStatus taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with " + id + " not found!"));
        return mapper.map(taskStatus);
    }

    public TaskStatusDto create(TaskStatusCreateDto dto) {
        TaskStatus taskStatus = mapper.map(dto);
        repository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    public TaskStatusDto update(TaskStatusUpdateDto dto, Long id) {
        TaskStatus taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with " + id + " not found!"));
        mapper.map(dto, taskStatus);
        repository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    public void delete(Long id) {
        TaskStatus taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with " + id + " not found!"));
        repository.deleteById(id);
    }
}
