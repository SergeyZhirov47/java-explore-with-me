package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationDao {
    Compilation save(Compilation compilation);

    void delete(long id);

    void checkCompilationExists(long id);

    Compilation getCompilation(long id);

    List<Compilation> getFilteredCompilations(boolean pinned, Pageable pageable);
}
