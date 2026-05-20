package hexlet.code.service.implementation;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TaskStatusServiceImplementation implements TaskStatusService {

    private final TaskStatusRepository repository;

    private final TaskRepository taskRepository;

    private final TaskStatusMapper mapper;

    @Override
    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO data) {
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
    public TaskStatusDTO findById(Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return mapper.map(item);
    }

    @Override
    public List<TaskStatusDTO> getAll() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }

    @Override
    @Transactional
    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        mapper.update(data, item);
        repository.save(item);
        return mapper.map(item);
    }
}
