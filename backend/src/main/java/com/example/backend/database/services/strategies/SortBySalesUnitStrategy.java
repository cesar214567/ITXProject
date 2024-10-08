package com.example.backend.database.services.strategies;

import com.example.backend.database.entities.ProductDao;
import com.example.backend.database.services.commons.StrategiesHelper;
import com.example.backend.domain.models.Product;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
@Component("salesUnit")
@AllArgsConstructor
public class SortBySalesUnitStrategy implements SortingStrategy{
    private final ReactiveMongoTemplate mongoTemplate;
    private final StrategiesHelper strategiesHelper;
    private final String SALES_UNIT = "salesUnit";
    @Override
    public Flux<ProductDao> applySorting(Boolean asc) {
        var order = strategiesHelper.getOrder(asc,SALES_UNIT);
        return mongoTemplate.find(new Query().with(order),ProductDao.class);
    }

}
