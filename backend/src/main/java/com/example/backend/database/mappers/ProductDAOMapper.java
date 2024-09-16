package com.example.backend.database.mappers;

import com.example.backend.database.entities.ProductDao;
import com.example.backend.domain.models.Product;
import lombok.experimental.UtilityClass;

import java.util.UUID;


@UtilityClass
public class ProductDAOMapper {
    public static ProductDao mapProductDAO(Product product){
        return ProductDao.builder()
                .id(product.getId()!=null? product.getId().toString(): UUID.randomUUID().toString())
                .name(product.getName())
                .salesUnit(product.getSalesUnit())
                .stock(product.getStock())
                .build();
    }
    public static Product mapDAOToProduct(ProductDao productDao){
        return Product.builder()
                .id(productDao.getId())
                .name(productDao.getName())
                .salesUnit(productDao.getSalesUnit())
                .stock(productDao.getStock())
                .weightedValue(productDao.getWeightedValue())
                .stockSum(productDao.getStockSum())
                .build();
    }
}
