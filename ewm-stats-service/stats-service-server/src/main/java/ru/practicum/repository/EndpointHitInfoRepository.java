package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.model.EndpointHitInfo;

public interface EndpointHitInfoRepository extends JpaRepository<EndpointHitInfo, Long>, QuerydslPredicateExecutor<EndpointHitInfo> {
}
