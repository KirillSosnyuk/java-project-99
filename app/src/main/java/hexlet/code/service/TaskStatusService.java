package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO create(TaskStatusCreateDTO data);
    List<TaskStatusDTO> getAll();
    TaskStatusDTO findById(Long id);
    TaskStatusDTO update(TaskStatusUpdateDTO data, Long id);
    void delete(Long id);
}
