package com.example.backend.database.services.commons;

import com.example.backend.database.services.config.ProductQueryConfigMap;
import com.example.backend.domain.models.MultiQuery;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class StrategiesHelper {
    private final ProductQueryConfigMap productQueryConfigMap;
    private final String WEIGHTED_VALUE = "weightedValue";

    public Sort getOrder(Boolean asc, String attribute){
        Sort.Order order;
        if (Boolean.TRUE.equals(asc)){
            order = Sort.Order.asc(attribute);
        }else{
            order = Sort.Order.desc(attribute);
        }
        return Sort.by(order);
    }

    public String generateProjectStage(MultiQuery multiQuery){
        StringBuilder query = new StringBuilder("{ '$project': { '_id': 1,stock:1, salesUnit:1,name: 1,"
                + WEIGHTED_VALUE+": { $add: [ ");

        for(var it:multiQuery.getQueryAttributes()){
            String temp = "";
            switch (productQueryConfigMap.getKeys().get(it.getAttribute())) {
                case "value" -> temp  ="{ $multiply: [ '$<attribute>' ,<weight> ] },";
                case "hash" -> temp = "{ $multiply: [ { $sum: { $map: { input: { $objectToArray: '$<attribute>' }, as: 'item', in: '$$item.v' } } } ,<weight> ] },";
                case "list" -> temp = "{ $multiply: [ { $sum: { $map: { input: '$<attribute>', as: 'item', in: '$$item' } } } ,<weight> ] },";
                default -> {
                }
            }
            query.append(temp
                    .replace("<attribute>",it.getAttribute())
                    .replace("<weight>",it.getWeight().toString()));
        }
        query.deleteCharAt(query.length()-1);
        query.append("]}}}");
        Logger.getAnonymousLogger().info(query.toString());
        return query.toString();

    }

}
