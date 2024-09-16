package com.example.backend.database.services.commons;

import com.example.backend.database.services.commons.StrategiesHelper;
import com.example.backend.database.services.config.ProductQueryConfigMap;
import com.example.backend.domain.models.MultiQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StrategiesHelperTest {

    private StrategiesHelper strategiesHelper;
    private ProductQueryConfigMap productQueryConfigMap;
    private static final String SALES_UNIT = "salesUnit";
    private static final String STOCK = "stock";
    private static final String LIST = "list";

    @BeforeEach
    void setUp() {
        productQueryConfigMap = mock(ProductQueryConfigMap.class);
        when(productQueryConfigMap.getKeys()).thenReturn(Map.of(
                SALES_UNIT,"value",
                STOCK,"hash",
                LIST,LIST
        ));
        strategiesHelper = new StrategiesHelper(productQueryConfigMap);
    }

    @Test
    void testGetOrderAscending() {
        Sort sort = strategiesHelper.getOrder(true, STOCK);

        assertNotNull(sort);
        assertEquals(Sort.by(Sort.Order.asc(STOCK)), sort);
    }

    @Test
    void testGetOrderDescending() {
        Sort sort = strategiesHelper.getOrder(false, STOCK);

        assertNotNull(sort);
        assertEquals(Sort.by(Sort.Order.desc(STOCK)), sort);
    }

    @Test
    void testGenerateProjectStageForValueAttribute() {
        MultiQuery multiQuery = new MultiQuery();
        multiQuery.setQueryAttributes(
            List.of(
                MultiQuery.QueryWeight.builder()
                    .attribute(SALES_UNIT)
                    .weight(0.5).build()
        ));


        String result = strategiesHelper.generateProjectStage(multiQuery);

        String expected = "{ '$project': { '_id': 1,stock:1, salesUnit:1,name: 1,weightedValue: { $add: [ { $multiply: [ '$salesUnit' ,0.5 ] }]}}}";
        assertEquals(expected, result);
    }

    @Test
    void testGenerateProjectStageForHashAttribute() {
        MultiQuery multiQuery = new MultiQuery();
        multiQuery.setQueryAttributes(
                List.of(
                        MultiQuery.QueryWeight.builder()
                                .attribute(STOCK)
                                .weight(0.8).build()
                ));

        String result = strategiesHelper.generateProjectStage(multiQuery);

        String expected = "{ '$project': { '_id': 1,stock:1, salesUnit:1,name: 1,weightedValue: { $add: [ { $multiply: [ { $sum: { $map: { input: { $objectToArray: '$stock' }, as: 'item', in: '$$item.v' } } } ,0.8 ] }]}}}";
        assertEquals(expected, result);
    }

    @Test
    void testGenerateProjectStageForListAttribute() {
        MultiQuery multiQuery = new MultiQuery();
        multiQuery.setQueryAttributes(
                List.of(
                        MultiQuery.QueryWeight.builder()
                                .attribute(LIST)
                                .weight(1.2).build()
                ));

        String result = strategiesHelper.generateProjectStage(multiQuery);

        String expected = "{ '$project': { '_id': 1,stock:1, salesUnit:1,name: 1,weightedValue: { $add: [ { $multiply: [ { $sum: { $map: { input: '$list', as: 'item', in: '$$item' } } } ,1.2 ] }]}}}";
        assertEquals(expected, result);
    }

    @Test
    void testGenerateProjectStageWithMultipleAttributes() {
        MultiQuery multiQuery = new MultiQuery();
        multiQuery.setQueryAttributes(
                List.of(
                        MultiQuery.QueryWeight.builder()
                                .attribute(SALES_UNIT)
                                .weight(1.2).build(),
                        MultiQuery.QueryWeight.builder()
                                .attribute(STOCK)
                                .weight(0.8).build()
                ));

        String result = strategiesHelper.generateProjectStage(multiQuery);

        String expected = "{ '$project': { '_id': 1,stock:1, salesUnit:1,name: 1,weightedValue: { $add: [ { $multiply: [ '$salesUnit' ,1.2 ] },{ $multiply: [ { $sum: { $map: { input: { $objectToArray: '$stock' }, as: 'item', in: '$$item.v' } } } ,0.8 ] }]}}}";
        assertEquals(expected, result);
    }

    @Test
    void testGenerateProjectStageWithNoAttributes() {
        MultiQuery multiQuery = new MultiQuery();
        multiQuery.setQueryAttributes(List.of()); // No attributes

        String result = strategiesHelper.generateProjectStage(multiQuery);

        String expected = "{ '$project': { '_id': 1,stock:1, salesUnit:1,name: 1,weightedValue: { $add: []}}}";
        assertEquals(expected, result);
    }
}
