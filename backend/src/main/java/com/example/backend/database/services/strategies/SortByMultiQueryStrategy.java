package com.example.backend.database.services.strategies;

import com.example.backend.commons.TechLogger;
import com.example.backend.database.entities.ProductDao;
import com.example.backend.database.services.commons.StrategiesHelper;
import com.example.backend.database.services.config.ProductQueryConfigMap;
import com.example.backend.domain.models.MultiQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component
@AllArgsConstructor
public class SortByMultiQueryStrategy{
    private final String WEIGHTED_VALUE = "weightedValue";

    private final ReactiveMongoTemplate mongoTemplate;
    private final StrategiesHelper strategiesHelper;
    private final TechLogger techLogger;
    public Flux<ProductDao> applySorting(MultiQuery multiQuery) {
        var order = strategiesHelper.getOrder(multiQuery.getAsc(),WEIGHTED_VALUE);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(strategiesHelper.generateProjectStage(multiQuery)),
                Aggregation.sort(order)
        );

        return mongoTemplate.aggregate(aggregation, "products", ProductDao.class)
                .doOnNext(techLogger::logInfo)
                .map(ProductDao.class::cast);
    }
}
