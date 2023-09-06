package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.EndpointHitInfo;


@Repository
public interface EndpointHitInfoRepository extends JpaRepository<EndpointHitInfo, Long>, QuerydslPredicateExecutor<EndpointHitInfo> {
}
