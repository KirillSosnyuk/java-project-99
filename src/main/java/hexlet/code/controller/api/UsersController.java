package hexlet.code.controller.api;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@Valid @RequestBody UserCreateDTO data) {
        return userService.create(data);
    }

    @GetMapping("")
    ResponseEntity<List<UserDTO>> index() {
        var result = userService.getAll();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(result.size())).body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    UserDTO show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PreAuthorize("@userUtils.getCurrentUser().getId() == #id")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    UserDTO update(@Valid @RequestBody UserUpdateDTO data, @PathVariable Long id) {
        return userService.update(data, id);
    }

    @PreAuthorize("@userUtils.getCurrentUser().getId() == #id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
