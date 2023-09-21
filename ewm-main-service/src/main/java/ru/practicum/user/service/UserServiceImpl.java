package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.OffsetPageableValidator;
import ru.practicum.user.dto.UserCreateDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserDao;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsersInfo(List<Long> ids, Integer from, Integer size) {
        final Pageable pageRequest = OffsetPageableValidator.validateAndGet(from, size);

        List<User> users;
        if (isNull(ids) || ids.isEmpty()) {
            users = userDao.findAll(pageRequest);
        } else {
            users = userDao.getByIds(ids, pageRequest);
        }

        return users.stream().map(UserMapper::toUserDto).collect(toUnmodifiableList());
    }

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        final User createdUser = userDao.save(UserMapper.toUser(userCreateDto));
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public void delete(long id) {
        userDao.checkUserExists(id);
        userDao.delete(id);
    }
}
