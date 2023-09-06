package ru.practicum.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.EndpointStatsDto;
import ru.practicum.model.EndpointHitInfo;
import ru.practicum.model.QEndpointHitInfo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class DaoEndpointHitInfoImpl implements DaoEndpointHitInfo {
    private final EndpointHitInfoRepository endpointHitInfoRepository;
    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public DaoEndpointHitInfoImpl(EntityManager entityManager, EndpointHitInfoRepository endpointHitInfoRepository) {
        this.endpointHitInfoRepository = endpointHitInfoRepository;
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public EndpointHitInfo save(EndpointHitInfo endpointHitInfo) {
        return endpointHitInfoRepository.save(endpointHitInfo);
    }

    @Override
    public List<EndpointStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean uniqueIP) {
        final QEndpointHitInfo hitInfo = QEndpointHitInfo.endpointHitInfo;

        BooleanExpression finalExpression = getStartEndTimeExpression(start, end);

        if (nonNull(uris) && !uris.isEmpty()) {
            finalExpression = finalExpression.and(getUrisInExpression(uris));
        }

        var hits = hitInfo.ip.count();
        if (nonNull(uniqueIP) && uniqueIP) {
            hits = hitInfo.ip.countDistinct();
        }

        val queryResult = queryFactory.selectFrom(hitInfo)
                .where(finalExpression)
                .groupBy(QEndpointHitInfo.endpointHitInfo.app, QEndpointHitInfo.endpointHitInfo.uri)
                .select(hitInfo.app, hitInfo.uri, hits)
                .fetch();

        final List<EndpointStatsDto> finalResult = new ArrayList<>();
        for (val row : queryResult) {
            finalResult.add(new EndpointStatsDto(row.get(hitInfo.app), row.get(hitInfo.uri), row.get(2, Long.class)));
        }

        return finalResult;
    }

    private BooleanExpression getStartEndTimeExpression(LocalDateTime start, LocalDateTime end) {
        return QEndpointHitInfo.endpointHitInfo.timestamp.between(start, end);
    }

    private BooleanExpression getUrisInExpression(List<String> uris) {
        return QEndpointHitInfo.endpointHitInfo.uri.in(uris);
    }
}
