package hexlet.code.service.implementation;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LabelServiceImplementation implements LabelService {

    private final LabelRepository repository;

    private final LabelMapper mapper;

    @Override
    @Transactional
    public LabelDTO create(LabelCreateDTO data) {
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
    public LabelDTO findById(Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return mapper.map(item);
    }

    @Override
    public List<LabelDTO> getAll() {
        return repository.findAll().stream()
            .map(mapper::map)
            .toList();
    }

    @Override
    @Transactional
    public LabelDTO update(LabelUpdateDTO data, Long id) {
        var item = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found"));
        mapper.update(data, item);
        repository.save(item);
        return mapper.map(item);
    }
}
