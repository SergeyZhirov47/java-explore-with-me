package ru.practicum.compilation.controller.publicAPI;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
@Slf4j
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getFiltered(@RequestParam boolean pinned,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info(String.format("GET /compilations?pinned={pinned}&from={from}&size={size}, {pinned} = %s, {from} = %s, {size} = %s", pinned, from, size));
        return compilationService.getFilteredCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        log.info(String.format("POST /compilations/{compId}, {compId} = %s", compId));
        return compilationService.getCompilation(compId);
    }
}
