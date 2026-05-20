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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TaskServiceImplementation implements TaskService {

    private final TaskRepository repository;

    private final TaskMapper mapper;

    private final TaskSpecification specBuilder;

    @Override
    @Transactional
    public TaskDTO create(TaskCreateDTO data) {
        var item = mapper.map(data);
        repository.save(item);
        return mapper.map(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public TaskDTO findById(Long id) {
        var item = repository.findFetchedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return mapper.map(item);
    }

    @Override
    public List<TaskDTO> getAll(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var tasks = repository.findAll(spec);
        return tasks.stream().map(mapper::map).toList();
    }

    @Override
    @Transactional
    public TaskDTO update(TaskUpdateDTO data, Long id) {
        var item = repository.findFetchedById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        mapper.update(data, item);
        repository.save(item);
        return mapper.map(item);
    }
}
