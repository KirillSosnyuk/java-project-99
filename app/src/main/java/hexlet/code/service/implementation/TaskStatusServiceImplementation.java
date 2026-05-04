package hexlet.code.service.implementation;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TaskStatusServiceImplementation implements TaskStatusService {

    private final TaskStatusRepository repository;

    private final TaskRepository taskRepository;

    private final TaskStatusMapper mapper;

    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var item = mapper.map(data);
        repository.save(item);
        var dto = mapper.map(item);
        return dto;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public TaskStatusDTO findById(Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var dto = mapper.map(item);
        return dto;
    }

    @Override
    public List<TaskStatusDTO> getAll() {
        var items = repository.findAll();
        var result = items.stream().map(mapper::map).toList();
        return result;
    }

    @Override
    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        mapper.update(data, item);
        repository.save(item);
        var dto = mapper.map(item);
        return dto;
    }
}
