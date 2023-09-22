package ru.practicum.compilation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.compilation.model.Compilation;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CompilationDaoImpl implements CompilationDao {
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation save(Compilation compilation) {
        return compilationRepository.save(compilation);
    }

    @Override
    public void delete(long id) {
        compilationRepository.deleteById(id);
    }

    @Override
    public void checkCompilationExists(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Подборка событий с id = %s не найдена!", id));
        }
    }

    @Override
    public Compilation getCompilation(long id) {
        final Optional<Compilation> optionalCompilation = compilationRepository.findById(id);
        return optionalCompilation.orElseThrow(() -> new NotFoundException(String.format("Подборка событий с id = %s не найдена!", id)));
    }

    @Override
    public List<Compilation> getFilteredCompilations(boolean pinned, Pageable pageable) {
        return compilationRepository.findAllByPinned(pinned, pageable);
    }
}
