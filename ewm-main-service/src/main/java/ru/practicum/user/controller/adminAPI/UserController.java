package ru.practicum.user.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info(String.format("GET /admin/users, ids = %s, from = %s, size = %s", ids, from, size));
        return userService.getUsersInfo(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info(String.format("POST /admin/users, body = %s", userCreateDto));
        return userService.create(userCreateDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info(String.format("DELETE /admin/users/{userId}, {userId} = %s", userId));
        userService.delete(userId);
    }
}
