package hexlet.code.controller.api;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@AllArgsConstructor
public class LabelsController {

    private final LabelService labelService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    LabelDTO create(@Valid @RequestBody LabelCreateDTO data) {
        return labelService.create(data);
    }

    @GetMapping("")
    ResponseEntity<List<LabelDTO>> index() {
        var result = labelService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(result.size())).body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    LabelDTO show(@PathVariable Long id) {
        return labelService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    LabelDTO update(@Valid @RequestBody LabelUpdateDTO data, @PathVariable Long id) {
        return labelService.update(data, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
