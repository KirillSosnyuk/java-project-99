package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    TaskDTO create(TaskCreateDTO data);
    List<TaskDTO> getAll(TaskParamsDTO params);
    TaskDTO findById(Long id);
    TaskDTO update(TaskUpdateDTO data, Long id);
    void delete(Long id);
}
