package ru.practicum.user.controller.adminAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.common.Utils.DEFAULT_FROM_VALUE;
import static ru.practicum.common.Utils.DEFAULT_SIZE_VALUE;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                  @Positive @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info("GET /admin/users, ids = {}, from = {}, size = {}", ids, from, size);
        return userService.getUsersInfo(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("POST /admin/users, body = {}", userCreateDto);
        return userService.create(userCreateDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.info("DELETE /admin/users/{userId}, {userId} = {}", userId);
        userService.delete(userId);
    }
}
