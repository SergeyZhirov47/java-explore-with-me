package ru.practicum.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(long id) {
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

    @Override
    public void checkUserExists(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден!", id));
        }
    }

    @Override
    public User getUser(long id) {
        final Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден!", id)));
    }
}
