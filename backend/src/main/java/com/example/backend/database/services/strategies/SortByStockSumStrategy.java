package com.example.backend.database.services.strategies;

import com.example.backend.database.entities.ProductDao;
import com.example.backend.database.services.commons.StrategiesHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component("stockSum")
@AllArgsConstructor
public class SortByStockSumStrategy implements SortingStrategy{
    private final String SUM_STOCKS = "stockSum";
    private final String STOCK = "stock";
    private final String SALES_UNIT = "salesUnit";
    private final String NAME = "name";

    private final ReactiveMongoTemplate mongoTemplate;
    private final StrategiesHelper strategiesHelper;

    @Override
    public Flux<ProductDao> applySorting(Boolean asc) {
        var order = strategiesHelper.getOrder(asc,SUM_STOCKS);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project(NAME, STOCK,SALES_UNIT) // Project the fields you need
                        .andExpression("{$sum: {$map: {input: {$objectToArray: '$stock'}, as: 'item', in: '$$item.v'}}}")
                        .as(SUM_STOCKS), // Compute the total stock sum
                Aggregation.sort(order)
        );

        return mongoTemplate.aggregate(aggregation, "products", ProductDao.class);
    }
}
