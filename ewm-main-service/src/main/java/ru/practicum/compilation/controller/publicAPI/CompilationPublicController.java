package ru.practicum.compilation.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

import static ru.practicum.common.Utils.DEFAULT_FROM_VALUE;
import static ru.practicum.common.Utils.DEFAULT_SIZE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getFiltered(@RequestParam(defaultValue = "false") Boolean pinned,
                                            @RequestParam(defaultValue = DEFAULT_FROM_VALUE) Integer from,
                                            @RequestParam(defaultValue = DEFAULT_SIZE_VALUE) Integer size) {
        log.info(String.format("GET /compilations?pinned={pinned}&from={from}&size={size}, {pinned} = %s, {from} = %s, {size} = %s", pinned, from, size));
        return compilationService.getFilteredCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        log.info(String.format("POST /compilations/{compId}, {compId} = %s", compId));
        return compilationService.getCompilation(compId);
    }
}
