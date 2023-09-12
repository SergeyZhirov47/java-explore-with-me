package ru.practicum.user.service;

import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsersInfo(List<Long> ids, Integer from, Integer size);

    UserDto create(UserCreateDto userCreateDto);

    void delete(long id);
}
