package hexlet.code.service.implementation;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@AllArgsConstructor
@Service
public class TaskServiceImplementation implements TaskService {

    private final TaskRepository repository;

    private final TaskMapper mapper;

    private final TaskSpecification specBuilder;

    @Override
    public TaskDTO create(TaskCreateDTO data) {
        var item = mapper.map(data);
        repository.save(item);
        var dto = mapper.map(item);
        return dto;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public TaskDTO findById(Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var dto = mapper.map(item);
        return dto;
    }

    @Override
    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var tasks = repository.findAll(spec);
        var result = tasks.stream().map(mapper::map).toList();
        return result;
    }

    @Override
    public TaskDTO update(TaskUpdateDTO data, Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        mapper.update(data, item);
        repository.save(item);
        var dto = mapper.map(item);
        return dto;
    }
}
