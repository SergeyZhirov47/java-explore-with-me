package ru.practicum.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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
        QEndpointHitInfo hitInfo = QEndpointHitInfo.endpointHitInfo;

        List<StringPath> groupByList = new ArrayList<>();
        groupByList.add(QEndpointHitInfo.endpointHitInfo.app);
        groupByList.add(QEndpointHitInfo.endpointHitInfo.uri);

        BooleanExpression finalExpression = getStartEndTimeExpression(start, end);

        if (nonNull(uris) && !uris.isEmpty()) {
            finalExpression = finalExpression.and(getUrisInExpression(uris));
        }

        if (nonNull(uniqueIP) && uniqueIP) {
            // Получить список уникальных посещений (app, uri, ip).

            groupByList.add(QEndpointHitInfo.endpointHitInfo.ip);
        }

        StringPath[] groupByFieldsArr = new StringPath[groupByList.size()];
        groupByFieldsArr = groupByList.toArray(groupByFieldsArr);

        List<EndpointStatsDto> result = queryFactory.select(finalExpression)
                .groupBy(groupByFieldsArr)
                .select(Projections.bean(EndpointStatsDto.class, hitInfo.app, hitInfo.uri, hitInfo.ip.count()))
                .fetch();

        return result;
    }

    private BooleanExpression getStartEndTimeExpression(LocalDateTime start, LocalDateTime end) {
        return QEndpointHitInfo.endpointHitInfo.timestamp.between(start, end);
    }

    private BooleanExpression getUrisInExpression(List<String> uris) {
        return QEndpointHitInfo.endpointHitInfo.uri.in(uris);
    }
}
