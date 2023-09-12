package ru.practicum.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;

@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(long id) {
        final boolean exists = userRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден!", id));
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).getContent();
    }

    @Override
    public List<User> getByIds(List<Long> ids, Pageable pageable) {
        return userRepository.findAllByIdIn(ids, pageable);
    }
}
