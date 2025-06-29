package util;

import avro.model.OrderProductStock;
import dto.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapperUtil {
    public static List<ProductDto> toProductDto(OrderProductStock orderProductStock) {
        return orderProductStock.getProducts()
                .stream()
                .map(p -> {
                    ProductDto productDto = new ProductDto();
                    productDto.setSku(p.getSku());
                    productDto.setQuantityDiscount(p.getQuantity());
                    return productDto;
                }).collect(Collectors.toList());
    }
}
