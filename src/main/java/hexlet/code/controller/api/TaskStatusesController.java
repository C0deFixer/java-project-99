package hexlet.code.controller.api;

import hexlet.code.dto.TaskStatusCreateDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskStatusUpdateDto;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/task_statuses")
public class TaskStatusesController {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper mapper;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDto show(@PathVariable Long id) {
        var taskStatus = taskStatusRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found!"));
        return mapper.map(taskStatus);
    }

/*    @GetMapping("/{slug}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDto showBySlug(@PathVariable String slug) {
        var taskStatus = taskStatusRepository
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Task with slug " + slug + " not found!"));
        return mapper.map(taskStatus);
    }*/

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskStatusDto>> index() {
        List<TaskStatus> taskStatusList = taskStatusRepository
                .findAll();
        var result = taskStatusList.stream()
                .map(mapper::map).toList();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", String.valueOf(result.size()))
                .body(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public TaskStatusDto create(@Valid @RequestBody TaskStatusCreateDto taskStatusCreateDto) {
        String slug = taskStatusCreateDto.getSlug();
        if (taskStatusRepository.findBySlug(slug).isPresent()) {
            throw new ResourceNotFoundException("Tsak status with slug " + slug + " already exist");
        }
        TaskStatus taskStatus = taskStatusRepository.save(mapper.map(taskStatusCreateDto));
        return mapper.map(taskStatus);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public TaskStatusDto update(@Valid @RequestBody TaskStatusUpdateDto taskStatusUpdateDto, @PathVariable Long id) {
        TaskStatus taskStatus = taskStatusRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + "not found"));

        String slug = taskStatusUpdateDto.getSlug();
        Optional<TaskStatus> taskStatusOptional = taskStatusRepository.findBySlug(slug);

        if (taskStatusOptional.isPresent() && !taskStatusOptional.get().equals(taskStatus)) {
            throw new ResourceNotFoundException("Task status with slug " + slug + " already exist");
        }
        mapper.map(taskStatusUpdateDto, taskStatus);
        taskStatusRepository.save(taskStatus);
        return mapper.map(taskStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable Long id) {
        TaskStatus taskStatus = taskStatusRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id " + id + "not found"));
        taskStatusRepository.deleteById(id);
    }
}
