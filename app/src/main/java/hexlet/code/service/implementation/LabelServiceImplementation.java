package hexlet.code.service.implementation;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class LabelServiceImplementation implements LabelService {

    private final LabelRepository repository;

    private final LabelMapper mapper;

    @Override
    public LabelDTO create(LabelCreateDTO data) {
        var item = mapper.map(data);
        repository.save(item);
        var dto = mapper.map(item);
        return dto;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public LabelDTO findById(Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        var dto = mapper.map(item);
        return dto;
    }

    @Override
    public List<LabelDTO> getAll() {
        var items = repository.findAll();
        var result = items.stream().map(mapper::map).toList();
        return result;
    }

    @Override
    public LabelDTO update(LabelUpdateDTO data, Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        mapper.update(data, item);
        repository.save(item);
        var dto = mapper.map(item);
        return dto;
    }
}
