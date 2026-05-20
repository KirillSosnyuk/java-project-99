package hexlet.code.service.implementation;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImplementation implements UserService {

    private final UserRepository repository;

    private final TaskRepository taskRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO create(UserCreateDTO data) {
        var user = userMapper.map(data);
        repository.save(user);
        return userMapper.map(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDTO findById(Long id) {
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return userMapper.map(user);
    }

    @Override
    public List<UserDTO> getAll() {
        return repository.findAllByOrderByIdAsc().stream()
            .map(userMapper::map)
            .toList();
    }

    @Override
    @Transactional
    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        userMapper.update(data, user);
        repository.save(user);
        return userMapper.map(user);
    }
}
