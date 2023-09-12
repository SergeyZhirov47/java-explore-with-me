package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserDao {
    User save(User user);

    void delete(long id);

    List<User> findAll(Pageable pageable);

    List<User> getByIds(List<Long> ids, Pageable pageable);
}
