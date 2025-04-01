package hexlet.code.controller.api;

import hexlet.code.dto.TaskCreateDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.dto.TaskUpdateDto;
import hexlet.code.service.TaskService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDto>> index(TaskParamsDto taskParamsDto,
                                               @RequestParam(defaultValue = "1") int page) {

        List<TaskDto> taskList = service.getAll(taskParamsDto, page);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Total-Count", String.valueOf(taskList.size()))
                .body(taskList);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto show(@PathVariable Long id) {
        return service.show(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public TaskDto create(@Valid @RequestBody TaskCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public TaskDto update(@Valid @RequestBody TaskUpdateDto dto, @PathVariable Long id) {
        return service.update(dto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
