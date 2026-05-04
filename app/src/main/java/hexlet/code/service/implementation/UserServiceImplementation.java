package hexlet.code.service.implementation;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import java.util.List;

import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository repository;

    private final TaskRepository taskRepository;

    private final UserMapper userMapper;

    @Override
    public UserDTO create(UserCreateDTO data) {
        var user = userMapper.map(data);
        repository.save(user);
        var userDTO = userMapper.map(user);
        return userDTO;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public UserDTO findById(Long id) {
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var userDTO = userMapper.map(user);
        return userDTO;
    }

    @Override
    public List<UserDTO> getAll() {
        var items = repository.findAll();
        var result = items.stream().map(userMapper::map).toList();
        return result;
    }

    @Override
    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        userMapper.update(data, user);
        repository.save(user);
        var userDTO = userMapper.map(user);
        return userDTO;
    }
}
